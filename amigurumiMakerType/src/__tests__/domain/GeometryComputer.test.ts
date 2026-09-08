// src/__tests__/domain/GeometryComputer.test.ts

import { compute } from '../../domain/GeometryComputer';

describe('GeometryComputer', () => {
  describe('compute', () => {
    it('returns empty for empty input', () => {
      expect(compute([])).toEqual([]);
    });

    it('generates cube geometry', () => {
      const geometries = compute([{ type: 'CUBE', count: 1 }]);

      expect(geometries).toHaveLength(1);
      expect(geometries[0].vertices).toHaveLength(8); // 8 vertices
      expect(geometries[0].edges).toHaveLength(12);   // 12 edges
      expect(geometries[0].faceIndices).toHaveLength(12); // 6 faces × 2 triangles
    });

    it('generates multiple cubes', () => {
      const geometries = compute([{ type: 'CUBE', count: 3 }]);

      expect(geometries).toHaveLength(3);
    });

    it('centers blocks at x=0', () => {
      const geometries = compute([{ type: 'CUBE', count: 3 }]);

      // Block 0: cx = -floor(3/2) + 0 = -1
      // Block 1: cx = -1 + 1 = 0
      // Block 2: cx = -1 + 2 = 1
      const block0CenterX = geometries[0].vertices[0].x + 0.5; // Vertex 0 is at -0.5 + cx
      const block1CenterX = geometries[1].vertices[0].x + 0.5;
      const block2CenterX = geometries[2].vertices[0].x + 0.5;

      expect(block0CenterX).toBeCloseTo(-1, 1);
      expect(block1CenterX).toBeCloseTo(0, 1);
      expect(block2CenterX).toBeCloseTo(1, 1);
    });

    it('generates ramp_left geometry', () => {
      const geometries = compute([{ type: 'RAMP_LEFT', count: 1 }]);

      expect(geometries).toHaveLength(1);
      expect(geometries[0].vertices).toHaveLength(6); // 6 vertices (triangular prism)
      expect(geometries[0].edges).toHaveLength(9);
      expect(geometries[0].faceIndices).toHaveLength(8); // 4 faces × 2 triangles
    });

    it('generates ramp_right geometry', () => {
      const geometries = compute([{ type: 'RAMP_RIGHT', count: 1 }]);

      expect(geometries).toHaveLength(1);
      expect(geometries[0].vertices).toHaveLength(6);
      expect(geometries[0].edges).toHaveLength(9);
      expect(geometries[0].faceIndices).toHaveLength(8);
    });

    it('splits ramps by position relative to center', () => {
      const geometries = compute([{ type: 'RAMP_LEFT', count: 4 }]);

      // First 2 blocks → RAMP_LEFT, last 2 → RAMP_RIGHT
      // RAMP_LEFT has 6 vertices, RAMP_RIGHT has 6 vertices
      expect(geometries[0].vertices).toHaveLength(6); // RAMP_LEFT
      expect(geometries[1].vertices).toHaveLength(6); // RAMP_LEFT
      expect(geometries[2].vertices).toHaveLength(6); // RAMP_RIGHT
      expect(geometries[3].vertices).toHaveLength(6); // RAMP_RIGHT
    });

    it('PLACEHOLDER returns empty geometry', () => {
      const geometries = compute([{ type: 'PLACEHOLDER', count: 1 }]);

      expect(geometries).toHaveLength(1);
      expect(geometries[0].vertices).toHaveLength(0);
      expect(geometries[0].edges).toHaveLength(0);
      expect(geometries[0].faceIndices).toHaveLength(0);
    });

    it('face indices reference valid vertices', () => {
      const geometries = compute([{ type: 'CUBE', count: 1 }]);
      const geo = geometries[0];

      for (const [i0, i1, i2] of geo.faceIndices) {
        expect(i0).toBeGreaterThanOrEqual(0);
        expect(i0).toBeLessThan(geo.vertices.length);
        expect(i1).toBeGreaterThanOrEqual(0);
        expect(i1).toBeLessThan(geo.vertices.length);
        expect(i2).toBeGreaterThanOrEqual(0);
        expect(i2).toBeLessThan(geo.vertices.length);
      }
    });
  });
});
