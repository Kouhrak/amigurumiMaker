// src/__tests__/domain/RevolutionMeshComputer.test.ts

import { computeMesh, hslToRgb } from '../../domain/RevolutionMeshComputer';
import { RoundAnalysis } from '../../domain/model/models';

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

describe('RevolutionMeshComputer', () => {
  describe('computeMesh', () => {
    it('returns null for less than 2 analyses', () => {
      expect(computeMesh([], 'GAUSS_HEATMAP')).toBeNull();
      expect(computeMesh([makeAnalysis()], 'GAUSS_HEATMAP')).toBeNull();
    });

    it('generates segments for n-1 row pairs', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 0.5 }),
        makeAnalysis({ rowIndex: 3, stitchCount: 18, liftZ: 1.2 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      expect(mesh).not.toBeNull();
      expect(mesh!.segments).toHaveLength(2); // n-1 = 3-1 = 2
    });

    it('each segment has vertices and indices', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 6, liftZ: 0.5 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      expect(mesh!.segments[0].vertices.length).toBeGreaterThan(0);
      expect(mesh!.segments[0].indices.length).toBeGreaterThan(0);
      expect(mesh!.segments[0].indices.length % 3).toBe(0); // Divisible by 3 (triangles)
    });

    it('vertex count per segment is 4*S + 2 where S = max(N1, N2)', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 10, liftZ: 0.5 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      const S = Math.max(6, 10, 4); // MIN_SEGMENTS = 4
      // Each k: 2 vertices (vBot, vTop), plus 2 more if k < S (vBotNext, vTopNext)
      // Total: S * 4 + 2
      const expectedVertices = S * 4 + 2;
      expect(mesh!.segments[0].vertices).toHaveLength(expectedVertices);
    });

    it('mesh is centered (Y offset)', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 1.0 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      // Total height = 1.0, center offset = 0.5
      // First vertex Y should be 0 - 0.5 = -0.5
      const firstVertexY = mesh!.segments[0].vertices[0].y;
      expect(firstVertexY).toBeCloseTo(-0.5, 4);
    });

    it('totalHeight matches difference in liftZ', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 2.5 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      expect(mesh!.totalHeight).toBeCloseTo(2.5, 4);
    });

    it('GAUSS_HEATMAP colors based on curvature', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0, localCurvature: 0.5 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 0.5, localCurvature: 0.5 }),
      ];
      const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');

      // Positive curvature → green
      expect(mesh!.segments[0].color).toEqual({ x: 0.06, y: 0.72, z: 0.50 });
    });

    it('ROW_GRADIENT colors vary by row index', () => {
      const analyses = [
        makeAnalysis({ rowIndex: 1, stitchCount: 6, liftZ: 0 }),
        makeAnalysis({ rowIndex: 2, stitchCount: 12, liftZ: 0.5 }),
        makeAnalysis({ rowIndex: 3, stitchCount: 18, liftZ: 1.2 }),
      ];
      const mesh = computeMesh(analyses, 'ROW_GRADIENT');

      // Colors should be different for different rows
      const color0 = mesh!.segments[0].color;
      const color1 = mesh!.segments[1].color;
      expect(color0).not.toEqual(color1);
    });
  });

  describe('hslToRgb', () => {
    it('converts red (h=0)', () => {
      const rgb = hslToRgb(0, 1, 0.5);
      expect(rgb.x).toBeCloseTo(1.0, 1);
      expect(rgb.y).toBeCloseTo(0.0, 1);
      expect(rgb.z).toBeCloseTo(0.0, 1);
    });

    it('converts green (h=1/3)', () => {
      const rgb = hslToRgb(1 / 3, 1, 0.5);
      expect(rgb.x).toBeCloseTo(0.0, 1);
      expect(rgb.y).toBeCloseTo(1.0, 1);
      expect(rgb.z).toBeCloseTo(0.0, 1);
    });

    it('converts blue (h=2/3)', () => {
      const rgb = hslToRgb(2 / 3, 1, 0.5);
      expect(rgb.x).toBeCloseTo(0.0, 1);
      expect(rgb.y).toBeCloseTo(0.0, 1);
      expect(rgb.z).toBeCloseTo(1.0, 1);
    });

    it('converts gray (s=0)', () => {
      const rgb = hslToRgb(0, 0, 0.5);
      expect(rgb.x).toBeCloseTo(0.5, 1);
      expect(rgb.y).toBeCloseTo(0.5, 1);
      expect(rgb.z).toBeCloseTo(0.5, 1);
    });
  });
});
