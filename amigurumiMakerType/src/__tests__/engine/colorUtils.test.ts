// src/__tests__/engine/colorUtils.test.ts

import { getSegmentColor, hslToThreeColor } from '../../engine/colorUtils';
import * as THREE from 'three';

describe('colorUtils', () => {
  describe('getSegmentColor', () => {
    it('returns green for positive curvature (GAUSS_HEATMAP)', () => {
      const color = getSegmentColor(0, 5, 0.5, 'GAUSS_HEATMAP');
      expect(color.r).toBeCloseTo(0.06, 2);
      expect(color.g).toBeCloseTo(0.72, 2);
      expect(color.b).toBeCloseTo(0.50, 2);
    });

    it('returns pink for negative curvature (GAUSS_HEATMAP)', () => {
      const color = getSegmentColor(0, 5, -0.5, 'GAUSS_HEATMAP');
      expect(color.r).toBeCloseTo(0.95, 2);
      expect(color.g).toBeCloseTo(0.25, 2);
      expect(color.b).toBeCloseTo(0.37, 2);
    });

    it('returns cyan for near-zero curvature (GAUSS_HEATMAP)', () => {
      const color = getSegmentColor(0, 5, 0.0, 'GAUSS_HEATMAP');
      expect(color.r).toBeCloseTo(0.02, 2);
      expect(color.g).toBeCloseTo(0.71, 2);
      expect(color.b).toBeCloseTo(0.83, 2);
    });

    it('returns different colors for different rows (ROW_GRADIENT)', () => {
      const color0 = getSegmentColor(0, 5, 0, 'ROW_GRADIENT');
      const color4 = getSegmentColor(4, 5, 0, 'ROW_GRADIENT');
      // First row should be bluer, last row should be redder
      expect(color0.b).toBeGreaterThan(color4.b);
      expect(color0.r).toBeLessThan(color4.r);
    });

    it('handles single row (ROW_GRADIENT)', () => {
      const color = getSegmentColor(0, 1, 0, 'ROW_GRADIENT');
      expect(color).toBeInstanceOf(THREE.Color);
    });
  });

  describe('hslToThreeColor', () => {
    it('converts red (h=0)', () => {
      const color = hslToThreeColor(0, 1, 0.5);
      expect(color.r).toBeCloseTo(1.0, 1);
      expect(color.g).toBeCloseTo(0.0, 1);
      expect(color.b).toBeCloseTo(0.0, 1);
    });

    it('converts green (h=1/3)', () => {
      const color = hslToThreeColor(1 / 3, 1, 0.5);
      expect(color.r).toBeCloseTo(0.0, 1);
      expect(color.g).toBeCloseTo(1.0, 1);
      expect(color.b).toBeCloseTo(0.0, 1);
    });

    it('converts blue (h=2/3)', () => {
      const color = hslToThreeColor(2 / 3, 1, 0.5);
      expect(color.r).toBeCloseTo(0.0, 1);
      expect(color.g).toBeCloseTo(0.0, 1);
      expect(color.b).toBeCloseTo(1.0, 1);
    });

    it('converts gray (s=0)', () => {
      const color = hslToThreeColor(0, 0, 0.5);
      expect(color.r).toBeCloseTo(0.5, 1);
      expect(color.g).toBeCloseTo(0.5, 1);
      expect(color.b).toBeCloseTo(0.5, 1);
    });
  });
});
