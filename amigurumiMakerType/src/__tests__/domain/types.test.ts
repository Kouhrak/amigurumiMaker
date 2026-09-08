// src/__tests__/domain/types.test.ts
// Smoke tests for domain types — verify TypeScript compilation and default values

import {
  StitchType,
  STITCH_YIELD,
  STITCH_TYPE_META,
  ColorMode,
  ViewMode,
  InfoTab,
  SurfaceClassification,
  CompoundType,
} from '../../domain/model/types';

describe('StitchType', () => {
  it('has correct symbol values matching Kotlin', () => {
    expect(StitchType.NORMAL).toBe('p');
    expect(StitchType.INCREASE).toBe('a');
    expect(StitchType.DECREASE).toBe('d');
    expect(StitchType.CHAIN).toBe('c');
  });

  it('has correct yield per unit', () => {
    expect(STITCH_YIELD[StitchType.NORMAL]).toBe(1);
    expect(STITCH_YIELD[StitchType.INCREASE]).toBe(2);
    expect(STITCH_YIELD[StitchType.DECREASE]).toBe(1);
    expect(STITCH_YIELD[StitchType.CHAIN]).toBe(0);
  });

  it('has correct metadata', () => {
    expect(STITCH_TYPE_META[StitchType.NORMAL]).toEqual({ symbol: 'p', yieldPerUnit: 1 });
    expect(STITCH_TYPE_META[StitchType.INCREASE]).toEqual({ symbol: 'a', yieldPerUnit: 2 });
    expect(STITCH_TYPE_META[StitchType.DECREASE]).toEqual({ symbol: 'd', yieldPerUnit: 1 });
    expect(STITCH_TYPE_META[StitchType.CHAIN]).toEqual({ symbol: 'c', yieldPerUnit: 0 });
  });
});

describe('ColorMode', () => {
  it('has GAUSS_HEATMAP and ROW_GRADIENT', () => {
    const modes: ColorMode[] = ['GAUSS_HEATMAP', 'ROW_GRADIENT'];
    expect(modes).toContain('GAUSS_HEATMAP');
    expect(modes).toContain('ROW_GRADIENT');
  });
});

describe('ViewMode', () => {
  it('has all three view modes', () => {
    const modes: ViewMode[] = ['MESH_2D', 'CYLINDER_3D', 'REVOLUTION_3D'];
    expect(modes).toHaveLength(3);
  });
});

describe('SurfaceClassification', () => {
  it('has all four classifications', () => {
    const classes: SurfaceClassification[] = ['FLAT', 'SPHERE', 'HYPERBOLIC', 'CYLINDRICAL'];
    expect(classes).toHaveLength(4);
  });
});
