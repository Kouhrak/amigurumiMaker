// src/domain/RevolutionMeshComputer.ts
// Port of Kotlin RevolutionMeshComputer — generates 3D revolution mesh

import { RoundAnalysis, RevolutionMesh, RingSegment, Float3, ColorMode } from './model/models';
import { MIN_SEGMENTS } from './constants';

/**
 * Compute a 3D revolution mesh from round analyses.
 * For each adjacent row pair, generates S = max(N_i, N_{i+1}) radial segments.
 * Each segment has 4 vertices (bottom/top × current/next angle) and 2 triangles.
 * Mesh is centered by subtracting totalHeight/2 from Y.
 */
export function computeMesh(
  analyses: RoundAnalysis[],
  colorMode: ColorMode
): RevolutionMesh | null {
  if (analyses.length < 2) return null;

  const segments: RingSegment[] = [];
  const totalRows = analyses.length;

  for (let i = 0; i < totalRows - 1; i++) {
    const rowBot = analyses[i];
    const rowTop = analyses[i + 1];

    const r1 = rowBot.theoreticalRadius;
    const r2 = rowTop.theoreticalRadius;
    const z1 = rowBot.liftZ;
    const z2 = rowTop.liftZ;
    const s = Math.max(rowBot.stitchCount, rowTop.stitchCount, MIN_SEGMENTS);

    const color = segmentColor(i, totalRows, rowBot.localCurvature, colorMode);

    const vertices: Float3[] = [];
    const indices: number[] = [];

    for (let k = 0; k <= s; k++) {
      const phi = (k / s) * 2.0 * Math.PI;
      const phiNext = ((k + 1) / s) * 2.0 * Math.PI;

      // Bottom ring vertex
      const vBot: Float3 = { x: r1 * Math.cos(phi), y: z1, z: r1 * Math.sin(phi) };
      // Top ring vertex
      const vTop: Float3 = { x: r2 * Math.cos(phi), y: z2, z: r2 * Math.sin(phi) };

      const idxBot = vertices.length;
      vertices.push(vBot);
      const idxTop = vertices.length;
      vertices.push(vTop);

      if (k < s) {
        // Next column vertices
        const vBotNext: Float3 = { x: r1 * Math.cos(phiNext), y: z1, z: r1 * Math.sin(phiNext) };
        const vTopNext: Float3 = { x: r2 * Math.cos(phiNext), y: z2, z: r2 * Math.sin(phiNext) };
        const idxBotNext = vertices.length;
        vertices.push(vBotNext);
        const idxTopNext = vertices.length;
        vertices.push(vTopNext);

        // Two triangles per quad
        indices.push(idxBot, idxTop, idxBotNext);
        indices.push(idxTop, idxTopNext, idxBotNext);
      }
    }

    segments.push({ vertices, indices, color });
  }

  // Center mesh: subtract totalHeight/2 from Y coordinates
  const totalHeight = analyses[analyses.length - 1].liftZ - analyses[0].liftZ;
  const centerOffset = totalHeight / 2.0;

  const centered = segments.map((seg) => ({
    ...seg,
    vertices: seg.vertices.map((v) => ({
      x: v.x,
      y: v.y - centerOffset,
      z: v.z,
    })),
  }));

  return { segments: centered, totalHeight };
}

/**
 * Compute segment color based on color mode.
 * GAUSS_HEATMAP: green/pink/cyan based on curvature
 * ROW_GRADIENT: HSL interpolation from 240° (blue) to 360° (red)
 */
function segmentColor(
  rowIndex: number,
  totalRows: number,
  curvature: number,
  colorMode: ColorMode
): Float3 {
  switch (colorMode) {
    case 'GAUSS_HEATMAP': {
      if (curvature > 0.1) return { x: 0.06, y: 0.72, z: 0.50 };
      if (curvature < -0.1) return { x: 0.95, y: 0.25, z: 0.37 };
      return { x: 0.02, y: 0.71, z: 0.83 };
    }
    case 'ROW_GRADIENT': {
      const t = totalRows > 1 ? rowIndex / (totalRows - 1) : 0.0;
      const hue = 240.0 + t * 120.0; // 240° (blue) → 360° (red)
      return hslToRgb(hue / 360.0, 0.7, 0.5);
    }
  }
}

/**
 * HSL to RGB conversion. h, s, l in [0..1], returns Float3 with RGB in [0..1].
 */
export function hslToRgb(h: number, s: number, l: number): Float3 {
  const c = (1.0 - Math.abs(2.0 * l - 1.0)) * s;
  const x = c * (1.0 - Math.abs(((h * 6.0) % 2.0) - 1.0));
  const m = l - c / 2.0;

  let r1: number, g1: number, b1: number;

  if (h < 1.0 / 6.0) {
    r1 = c; g1 = x; b1 = 0;
  } else if (h < 2.0 / 6.0) {
    r1 = x; g1 = c; b1 = 0;
  } else if (h < 3.0 / 6.0) {
    r1 = 0; g1 = c; b1 = x;
  } else if (h < 4.0 / 6.0) {
    r1 = 0; g1 = x; b1 = c;
  } else if (h < 5.0 / 6.0) {
    r1 = x; g1 = 0; b1 = c;
  } else {
    r1 = c; g1 = 0; b1 = x;
  }

  return { x: r1 + m, y: g1 + m, z: b1 + m };
}
