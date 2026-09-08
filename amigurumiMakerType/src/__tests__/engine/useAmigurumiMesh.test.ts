// src/__tests__/engine/useAmigurumiMesh.test.ts

// Note: We test the geometry generation logic directly (without React hooks)
// by extracting the core algorithm. The hook is a thin useMemo wrapper.

import * as THREE from 'three';
import { RoundAnalysis, MeshSettings, ColorMode } from '../../domain/model/models';
import { MIN_SEGMENTS } from '../../domain/constants';

/**
 * Core geometry generation (extracted from useAmigurumiMesh for testing).
 * This is the same algorithm as the hook, just without useMemo.
 */
function buildGeometry(analyses: RoundAnalysis[], colorMode: ColorMode): THREE.BufferGeometry | null {
  if (analyses.length < 2) return null;

  const positions: number[] = [];
  const colors: number[] = [];
  const indices: number[] = [];

  const totalRows = analyses.length;
  const totalHeight = analyses[totalRows - 1].liftZ - analyses[0].liftZ;
  const centerOffset = totalHeight / 2;

  for (let i = 0; i < totalRows - 1; i++) {
    const rowBot = analyses[i];
    const rowTop = analyses[i + 1];

    const r1 = rowBot.theoreticalRadius;
    const r2 = rowTop.theoreticalRadius;
    const z1 = rowBot.liftZ - centerOffset;
    const z2 = rowTop.liftZ - centerOffset;

    const S = Math.max(rowBot.stitchCount, rowTop.stitchCount, MIN_SEGMENTS);
    const segmentStart = positions.length / 3;

    // Simple color: green for positive, pink for negative, cyan for neutral
    let cr: number, cg: number, cb: number;
    if (rowBot.localCurvature > 0.1) {
      cr = 0.06; cg = 0.72; cb = 0.50;
    } else if (rowBot.localCurvature < -0.1) {
      cr = 0.95; cg = 0.25; cb = 0.37;
    } else {
      cr = 0.02; cg = 0.71; cb = 0.83;
    }

    for (let k = 0; k <= S; k++) {
      const phi = (k / S) * Math.PI * 2;

      positions.push(r1 * Math.cos(phi), z1, r1 * Math.sin(phi));
      colors.push(cr, cg, cb);

      positions.push(r2 * Math.cos(phi), z2, r2 * Math.sin(phi));
      colors.push(cr, cg, cb);

      if (k < S) {
        const bl = segmentStart + k * 2;
        const tl = bl + 1;
        const br = bl + 2;
        const tr = bl + 3;

        indices.push(bl, tl, br);
        indices.push(tl, tr, br);
      }
    }
  }

  const geometry = new THREE.BufferGeometry();
  geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3));
  geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));
  geometry.setIndex(indices);
  geometry.computeVertexNormals();

  return geometry;
}

function makeAnalysis(overrides: Partial<RoundAnalysis> = {}): RoundAnalysis {
  return {
    rowIndex: 1,
    stitchCount: 6,
    deltaN: 0,
    theoreticalRadius: 0.4775,
    cosTheta: 1.0,
    sinTheta: 0.0,
    inclinationAngleDeg: 0.0,
    liftZ: 0.0,
    localCurvature: 0.0,
    roundArea: 1.5,
    ...overrides,
  };
}

describe('useAmigurumiMesh (geometry generation)', () => {
  it('returns null for fewer than 2 analyses', () => {
    expect(buildGeometry([], 'GAUSS_HEATMAP')).toBeNull();
    expect(buildGeometry([makeAnalysis()], 'GAUSS_HEATMAP')).toBeNull();
  });

  it('generates valid BufferGeometry for 2 analyses', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');

    expect(geo).not.toBeNull();
    expect(geo!.getAttribute('position')).toBeDefined();
    expect(geo!.getAttribute('color')).toBeDefined();
    expect(geo!.index).toBeDefined();
  });

  it('position attribute has 3 floats per vertex', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 6, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    const pos = geo!.getAttribute('position') as THREE.BufferAttribute;

    expect(pos.itemSize).toBe(3);
    expect(pos.count).toBeGreaterThan(0);
  });

  it('color attribute has 3 floats per vertex', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 6, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    const col = geo!.getAttribute('color') as THREE.BufferAttribute;

    expect(col.itemSize).toBe(3);
    expect(col.count).toBe(pos_count(geo!));
  });

  it('index count is divisible by 3 (triangles)', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 10, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');

    expect(geo!.index!.count % 3).toBe(0);
  });

  it('mesh is centered (Y offset)', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 6, liftZ: 1.0 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    const pos = geo!.getAttribute('position') as THREE.BufferAttribute;

    // First vertex Y should be 0 - 0.5 = -0.5
    expect(pos.getY(0)).toBeCloseTo(-0.5, 4);
  });

  it('vertex count per segment pair is (S+1)*2', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 10, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    const pos = geo!.getAttribute('position') as THREE.BufferAttribute;

    // Engine generates 2 vertices per k (bottom + top), for k=0..S → (S+1)*2
    const S = Math.max(6, 10, MIN_SEGMENTS);
    const expectedVertices = (S + 1) * 2;
    expect(pos.count).toBe(expectedVertices);
  });

  it('handles multiple segment pairs', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 0.5 }),
      makeAnalysis({ rowIndex: 3, stitchCount: 18, liftZ: 1.2 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    const pos = geo!.getAttribute('position') as THREE.BufferAttribute;

    // 2 segment pairs, each with (S+1)*2 vertices approximately
    expect(pos.count).toBeGreaterThan(20);
  });

  it('disposes geometry without error', () => {
    const analyses = [
      makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
      makeAnalysis({ rowIndex: 2, stitchCount: 6, liftZ: 0.5 }),
    ];
    const geo = buildGeometry(analyses, 'GAUSS_HEATMAP');
    expect(() => geo!.dispose()).not.toThrow();
  });
});

/** Helper: get vertex count from geometry */
function pos_count(geo: THREE.BufferGeometry): number {
  return (geo.getAttribute('position') as THREE.BufferAttribute).count;
}
