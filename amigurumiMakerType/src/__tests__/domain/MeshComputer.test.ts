// src/__tests__/domain/MeshComputer.test.ts

import { compute2DCells, compute3DProjection } from '../../domain/MeshComputer';
import { ParsedRow } from '../../domain/model/models';
import { StitchType } from '../../domain/model/types';

function makeRow(rowIndex: number, tokens: { type: StitchType; count: number; yieldCount: number }[]): ParsedRow {
  const totalCalculated = tokens.reduce((sum, t) => sum + t.yieldCount, 0);
  return {
    raw: `test`,
    rowIndex,
    tokens,
    totalCalculated,
    totalExpected: totalCalculated,
    isValid: true,
    errorMsg: '',
    increaseCount: tokens.filter((t) => t.type === StitchType.INCREASE).reduce((s, t) => s + t.count, 0),
    decreaseCount: tokens.filter((t) => t.type === StitchType.DECREASE).reduce((s, t) => s + t.count, 0),
  };
}

describe('MeshComputer', () => {
  describe('compute2DCells', () => {
    it('returns empty for empty input', () => {
      expect(compute2DCells([])).toEqual([]);
    });

    it('creates cells for each stitch', () => {
      const rows = [
        makeRow(1, [
          { type: StitchType.NORMAL, count: 3, yieldCount: 3 },
        ]),
      ];
      const cells = compute2DCells(rows);
      expect(cells).toHaveLength(3);
    });

    it('positions cells centered at x=0', () => {
      const rows = [
        makeRow(1, [
          { type: StitchType.NORMAL, count: 4, yieldCount: 4 },
        ]),
      ];
      const cells = compute2DCells(rows);

      // startX = -floor(4/2) = -2
      expect(cells[0].x).toBe(-2);
      expect(cells[1].x).toBe(-1);
      expect(cells[2].x).toBe(0);
      expect(cells[3].x).toBe(1);
    });

    it('handles increases (yieldCount > count)', () => {
      const rows = [
        makeRow(1, [
          { type: StitchType.NORMAL, count: 2, yieldCount: 2 },
          { type: StitchType.INCREASE, count: 1, yieldCount: 2 },
        ]),
      ];
      const cells = compute2DCells(rows);
      expect(cells).toHaveLength(4); // 2 normal + 2 from increase
    });
  });

  describe('compute3DProjection', () => {
    it('returns empty for empty input', () => {
      expect(compute3DProjection([])).toEqual([]);
    });

    it('creates cylindrical cells', () => {
      const rows = [
        makeRow(1, [
          { type: StitchType.NORMAL, count: 6, yieldCount: 6 },
        ]),
      ];
      const cells = compute3DProjection(rows);
      expect(cells).toHaveLength(6);
    });

    it('angles are evenly distributed around 2π', () => {
      const rows = [
        makeRow(1, [
          { type: StitchType.NORMAL, count: 4, yieldCount: 4 },
        ]),
      ];
      const cells = compute3DProjection(rows);

      expect(cells[0].angle).toBeCloseTo(0, 4);
      expect(cells[1].angle).toBeCloseTo(Math.PI / 2, 4);
      expect(cells[2].angle).toBeCloseTo(Math.PI, 4);
      expect(cells[3].angle).toBeCloseTo((3 * Math.PI) / 2, 4);
    });
  });
});
