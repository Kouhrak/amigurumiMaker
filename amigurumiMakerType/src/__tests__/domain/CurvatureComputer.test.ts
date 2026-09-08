// src/__tests__/domain/CurvatureComputer.test.ts

import {
  computeRoundAnalyses,
  computeSurfaceMetrics,
  classifySurface,
  curvatureColor,
  gaussCurvatureFromDelta,
} from '../../domain/CurvatureComputer';
import { ParsedRow } from '../../domain/model/models';
import { StitchType } from '../../domain/model/types';

// Helper to create a ParsedRow
function makeRow(rowIndex: number, totalCalculated: number, deltaN: number = 0): ParsedRow {
  return {
    raw: `${rowIndex}) ${totalCalculated}p (${totalCalculated}p)`,
    rowIndex,
    tokens: [{ type: StitchType.NORMAL, count: totalCalculated, yieldCount: totalCalculated }],
    totalCalculated,
    totalExpected: totalCalculated,
    isValid: true,
    errorMsg: '',
    increaseCount: deltaN > 0 ? deltaN : 0,
    decreaseCount: deltaN < 0 ? Math.abs(deltaN) : 0,
  };
}

describe('CurvatureComputer', () => {
  describe('computeRoundAnalyses', () => {
    it('returns empty array for empty input', () => {
      expect(computeRoundAnalyses([])).toEqual([]);
    });

    it('computes correct radius for first row', () => {
      const rows = [makeRow(1, 6)];
      const analyses = computeRoundAnalyses(rows);
      expect(analyses).toHaveLength(1);

      // r_1 = N_1 * w / (2π) = 6 * 0.5 / (2π) ≈ 0.4775
      const expectedR = (6 * 0.5) / (2 * Math.PI);
      expect(analyses[0].theoreticalRadius).toBeCloseTo(expectedR, 4);
    });

    it('computes deltaN correctly', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12)];
      const analyses = computeRoundAnalyses(rows);

      expect(analyses[0].deltaN).toBe(0); // First row has no delta
      expect(analyses[1].deltaN).toBe(6); // 12 - 6 = 6
    });

    it('computes cosTheta and sinTheta', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12)];
      const analyses = computeRoundAnalyses(rows);

      // Second row: dr = |r2 - r1|, cosTheta = min(1, dr/h)
      const r1 = (6 * 0.5) / (2 * Math.PI);
      const r2 = (12 * 0.5) / (2 * Math.PI);
      const dr = Math.abs(r2 - r1);
      const expectedCos = Math.min(1.0, dr / 0.5);

      expect(analyses[1].cosTheta).toBeCloseTo(expectedCos, 4);
      expect(analyses[1].sinTheta).toBeCloseTo(Math.sqrt(1 - expectedCos * expectedCos), 4);
    });

    it('liftZ increases monotonically', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12), makeRow(3, 18)];
      const analyses = computeRoundAnalyses(rows);

      expect(analyses[1].liftZ).toBeGreaterThan(analyses[0].liftZ);
      expect(analyses[2].liftZ).toBeGreaterThan(analyses[1].liftZ);
    });

    it('computes local curvature K_i', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12)];
      const analyses = computeRoundAnalyses(rows);

      // K_2 = 2π * (ΔN / N) / (2π * r * h) = ΔN / (N * r * h)
      const ni = 12;
      const dN = 6;
      const r2 = (ni * 0.5) / (2 * Math.PI);
      const expectedK = (2 * Math.PI * dN / ni) / (2 * Math.PI * r2 * 0.5);

      expect(analyses[1].localCurvature).toBeCloseTo(expectedK, 4);
    });

    it('handles single row', () => {
      const rows = [makeRow(1, 6)];
      const analyses = computeRoundAnalyses(rows);

      expect(analyses).toHaveLength(1);
      expect(analyses[0].deltaN).toBe(0);
      expect(analyses[0].localCurvature).toBe(0);
    });
  });

  describe('computeSurfaceMetrics', () => {
    it('computes total area', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12)];
      const analyses = computeRoundAnalyses(rows);
      const metrics = computeSurfaceMetrics(analyses);

      expect(metrics.totalArea).toBeGreaterThan(0);
    });

    it('computes euler characteristic', () => {
      const rows = [makeRow(1, 6), makeRow(2, 12), makeRow(3, 18)];
      const analyses = computeRoundAnalyses(rows);
      const metrics = computeSurfaceMetrics(analyses);

      // Euler = totalCurvature / 2π
      expect(metrics.eulerCharacteristic).toBeCloseTo(metrics.totalCurvature / (2 * Math.PI), 4);
    });
  });

  describe('classifySurface', () => {
    it('returns FLAT for empty input', () => {
      expect(classifySurface([])).toBe('FLAT');
    });

    it('returns FLAT when all curvatures are zero', () => {
      const rows = [makeRow(1, 10), makeRow(2, 10)];
      const analyses = computeRoundAnalyses(rows);
      // When deltaN = 0, curvature = 0
      expect(classifySurface(analyses)).toBe('FLAT');
    });

    it('returns SPHERE for positive curvature pattern', () => {
      // Sphere: 6 → 12 → 18 → 24 (increasing by 6 each time)
      const rows = [
        makeRow(1, 6),
        makeRow(2, 12),
        makeRow(3, 18),
        makeRow(4, 24),
      ];
      const analyses = computeRoundAnalyses(rows);
      expect(classifySurface(analyses)).toBe('SPHERE');
    });
  });

  describe('curvatureColor', () => {
    it('returns green for positive curvature', () => {
      const color = curvatureColor(0.5);
      expect(color).toEqual({ x: 0.06, y: 0.72, z: 0.50 });
    });

    it('returns pink for negative curvature', () => {
      const color = curvatureColor(-0.5);
      expect(color).toEqual({ x: 0.95, y: 0.25, z: 0.37 });
    });

    it('returns cyan for near-zero curvature', () => {
      const color = curvatureColor(0.0);
      expect(color).toEqual({ x: 0.02, y: 0.71, z: 0.83 });
    });

    it('returns cyan for curvature at threshold boundary', () => {
      const colorPos = curvatureColor(0.1);
      const colorNeg = curvatureColor(-0.1);
      expect(colorPos).toEqual({ x: 0.02, y: 0.71, z: 0.83 });
      expect(colorNeg).toEqual({ x: 0.02, y: 0.71, z: 0.83 });
    });
  });

  describe('gaussCurvatureFromDelta', () => {
    it('returns 0 for ΔN = 6', () => {
      expect(gaussCurvatureFromDelta(6)).toBeCloseTo(0, 4);
    });

    it('returns positive for ΔN < 6', () => {
      const result = gaussCurvatureFromDelta(3);
      expect(result).toBeCloseTo((6 - 3) * (Math.PI / 3), 4);
    });

    it('returns negative for ΔN > 6', () => {
      const result = gaussCurvatureFromDelta(9);
      expect(result).toBeCloseTo((6 - 9) * (Math.PI / 3), 4);
    });
  });
});
