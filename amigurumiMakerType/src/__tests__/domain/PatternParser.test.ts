// src/__tests__/domain/PatternParser.test.ts

import { parse, expandRepeatGroups } from '../../domain/PatternParser';

describe('PatternParser', () => {
  describe('expandRepeatGroups', () => {
    it('expands simple repeat group', () => {
      const result = expandRepeatGroups('[1p 1a] 6v');
      expect(result).toBe('1p 1a 1p 1a 1p 1a 1p 1a 1p 1a 1p 1a');
    });

    it('expands single repeat', () => {
      const result = expandRepeatGroups('[1a] 6v');
      expect(result).toBe('1a 1a 1a 1a 1a 1a');
    });

    it('returns unchanged text without repeat groups', () => {
      const result = expandRepeatGroups('3p 1a 3p');
      expect(result).toBe('3p 1a 3p');
    });

    it('handles nested repeat groups (inner first)', () => {
      const result = expandRepeatGroups('[2p [1a] 2v] 3v');
      // First inner: [1a] 2v → 1a 1a
      // Then outer: [2p 1a 1a] 3v → 2p 1a 1a 2p 1a 1a 2p 1a 1a
      expect(result).toBe('2p 1a 1a 2p 1a 1a 2p 1a 1a');
    });
  });

  describe('parse', () => {
    it('returns empty for empty input', () => {
      expect(parse('')).toEqual([]);
      expect(parse('  \n  \n  ')).toEqual([]);
    });

    it('parses a single row with header', () => {
      const rows = parse('1) 6c (6p)');
      expect(rows).toHaveLength(1);
      expect(rows[0].rowIndex).toBe(1);
      expect(rows[0].totalExpected).toBe(6);
      expect(rows[0].totalCalculated).toBe(0); // chains yield 0
      expect(rows[0].isValid).toBe(false);
    });

    it('parses row without header (uses line index)', () => {
      const rows = parse('6c (6p)');
      expect(rows).toHaveLength(1);
      expect(rows[0].rowIndex).toBe(1);
    });

    it('parses multiple rows', () => {
      const rows = parse('1) 6c (6p)\n2) [1a] 6v (12p)');
      expect(rows).toHaveLength(2);
      expect(rows[0].rowIndex).toBe(1);
      expect(rows[1].rowIndex).toBe(2);
    });

    it('expands repeat groups and calculates stitches', () => {
      const rows = parse('2) [1p 1a] 6v (12p)');
      expect(rows).toHaveLength(1);
      // [1p 1a] 6v → 6 × (1p + 1a) = 6 × (1 + 2) = 18
      expect(rows[0].totalCalculated).toBe(18);
    });

    it('validates total expected vs calculated', () => {
      const validRow = parse('2) [1a] 6v (12p)');
      expect(validRow[0].isValid).toBe(true);

      const invalidRow = parse('1) 6c (7p)');
      expect(invalidRow[0].isValid).toBe(false);
      expect(invalidRow[0].errorMsg).toContain('Esperados 7p');
    });

    it('counts increases and decreases', () => {
      const rows = parse('2) 3p 1a 3p 1d (7p)');
      expect(rows[0].increaseCount).toBe(1);
      expect(rows[0].decreaseCount).toBe(1);
    });

    it('parses row with declared count in parentheses', () => {
      const rows = parse('3) 8p (8p)');
      expect(rows[0].totalCalculated).toBe(8);
      expect(rows[0].totalExpected).toBe(8);
      expect(rows[0].isValid).toBe(true);
    });

    it('handles total without p suffix', () => {
      const rows = parse('3) 8p (8)');
      expect(rows[0].totalExpected).toBe(8);
    });

    it('parses complex pattern with multiple repeat groups', () => {
      const rows = parse('3) [2p 1a] 6v (24p)');
      expect(rows).toHaveLength(1);
      // [2p 1a] 6v → 6 × (2 + 2) = 24
      expect(rows[0].totalCalculated).toBe(24);
      expect(rows[0].totalExpected).toBe(24);
      expect(rows[0].isValid).toBe(true);
    });

    it('parses preset sphere pattern', () => {
      const text = '1) 6c (6p)\n2) [1a] 6v (12p)\n3) [1p 1a] 6v (18p)\n4) [2p 1a] 6v (24p)\n5) [3p 1a] 6v (30p)\n6) [9p 1a] 3v (33p)';
      const rows = parse(text);
      expect(rows).toHaveLength(6);

      // Row 1: 6 chains → 0 stitches (valid=false since expected 6)
      expect(rows[0].totalCalculated).toBe(0);

      // Row 2: [1a] 6v → 6 × 2 = 12
      expect(rows[1].totalCalculated).toBe(12);

      // Row 3: [1p 1a] 6v → 6 × (1 + 2) = 18
      expect(rows[2].totalCalculated).toBe(18);

      // Row 6: [9p 1a] 3v → 3 × (9 + 2) = 33
      expect(rows[5].totalCalculated).toBe(33);
    });
  });
});
