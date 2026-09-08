// src/__tests__/domain/EdgeCases.test.ts

import { parse } from '../../domain/PatternParser';
import { computeRoundAnalyses, computeSurfaceMetrics, classifySurface } from '../../domain/CurvatureComputer';
import { computeMesh } from '../../domain/RevolutionMeshComputer';
import { compute2DCells, compute3DProjection } from '../../domain/MeshComputer';

describe('Edge cases - PatternParser', () => {
  it('handles empty string', () => {
    expect(parse('')).toEqual([]);
  });

  it('handles only whitespace', () => {
    expect(parse('   \n  \n  ')).toEqual([]);
  });

  it('handles comments only (parser treats as row with 0 stitches)', () => {
    const rows = parse('// This is a comment');
    expect(rows).toHaveLength(1);
    expect(rows[0].totalCalculated).toBe(0);
  });

  it('handles mixed valid and invalid lines', () => {
    const rows = parse('1) 6c (6p)\ninvalid line\n2) 12p (12p)');
    expect(rows).toHaveLength(3); // all lines become rows
  });

  it('handles very large stitch counts', () => {
    const rows = parse('1) 50p (50p)');
    expect(rows[0].totalCalculated).toBe(50);
  });

  it('handles deep repeat nesting', () => {
    const rows = parse('1) [1p [1a 1p] 2v] 3v (18p)');
    expect(rows.length).toBeGreaterThan(0);
  });

  it('handles zero repeats', () => {
    const rows = parse('1) 0c (0p)');
    expect(rows.length).toBeGreaterThan(0);
  });

  it('handles malformed syntax gracefully', () => {
    expect(() => parse('1) c (p)')).not.toThrow();
  });

  it('handles multiple operations in one line', () => {
    const rows = parse('2) [1a 1p] 3v 2p (14p)');
    expect(rows.length).toBeGreaterThan(0);
  });
});

describe('Edge cases - CurvatureComputer', () => {
  it('handles single row analysis', () => {
    const rows = parse('1) 6c (6p)');
    const analyses = computeRoundAnalyses(rows);
    expect(analyses.length).toBeGreaterThan(0);
  });

  it('handles uniform stitch count', () => {
    const rows = parse('1) 6p (6p)\n2) 6p (6p)\n3) 6p (6p)');
    const analyses = computeRoundAnalyses(rows);
    // First row has no previous, deltaN = 0; others compare to previous
    expect(analyses.length).toBeGreaterThan(0);
  });

  it('handles surface metrics with minimal data', () => {
    const rows = parse('1) 6c (6p)');
    const analyses = computeRoundAnalyses(rows);
    const metrics = computeSurfaceMetrics(analyses);
    expect(metrics.totalArea).toBeGreaterThan(0);
  });

  it('classifies sphere correctly', () => {
    const rows = parse('1) 6c (6p)\n2) [1a] 6v (12p)\n3) [2p 1a] 6v (24p)');
    const analyses = computeRoundAnalyses(rows);
    const classification = classifySurface(analyses);
    expect(classification).toBe('SPHERE');
  });
});

describe('Edge cases - RevolutionMeshComputer', () => {
  it('generates mesh with minimal rows', () => {
    const rows = parse('1) 6c (6p)\n2) 12p (12p)');
    const analyses = computeRoundAnalyses(rows);
    const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');
    expect(mesh.segments.length).toBeGreaterThan(0);
  });

  it('generates mesh with large stitch count', () => {
    const rows = parse('1) 50c (50p)\n2) 50p (50p)');
    const analyses = computeRoundAnalyses(rows);
    const mesh = computeMesh(analyses, 'GAUSS_HEATMAP');
    expect(mesh.segments.length).toBeGreaterThan(0);
  });

  it('handles all color modes', () => {
    const rows = parse('1) 6c (6p)\n2) [1a] 6v (12p)');
    const analyses = computeRoundAnalyses(rows);

    const modes = ['GAUSS_HEATMAP', 'ROW_GRADIENT'] as const;
    modes.forEach((mode) => {
      const mesh = computeMesh(analyses, mode);
      expect(mesh.segments.length).toBeGreaterThan(0);
    });
  });
});

describe('Edge cases - MeshComputer', () => {
  it('handles empty parsed rows for 2D cells', () => {
    expect(compute2DCells([])).toEqual([]);
  });

  it('handles empty parsed rows for 3D projection', () => {
    expect(compute3DProjection([])).toEqual([]);
  });

  it('generates 2D cells for two rows', () => {
    const rows = parse('1) 6p (6p)\n2) 12p (12p)');
    const cells = compute2DCells(rows);
    expect(cells.length).toBeGreaterThan(0);
  });

  it('generates 3D projection for two rows', () => {
    const rows = parse('1) 6p (6p)\n2) 12p (12p)');
    const cells = compute3DProjection(rows);
    expect(cells.length).toBeGreaterThan(0);
  });
});
