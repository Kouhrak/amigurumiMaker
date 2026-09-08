// src/domain/model/types.ts
// Stitch type enumeration — maps directly from Kotlin StitchType enum

export enum StitchType {
  NORMAL = 'p',
  INCREASE = 'a',
  DECREASE = 'd',
  CHAIN = 'c',
}

/** Yield per unit for each stitch type (how many stitches it produces) */
export const STITCH_YIELD: Record<StitchType, number> = {
  [StitchType.NORMAL]: 1,
  [StitchType.INCREASE]: 2,
  [StitchType.DECREASE]: 1,
  [StitchType.CHAIN]: 0,
};

/** Stitch type metadata: symbol and yield */
export interface StitchTypeMeta {
  symbol: string;
  yieldPerUnit: number;
}

export const STITCH_TYPE_META: Record<StitchType, StitchTypeMeta> = {
  [StitchType.NORMAL]: { symbol: 'p', yieldPerUnit: 1 },
  [StitchType.INCREASE]: { symbol: 'a', yieldPerUnit: 2 },
  [StitchType.DECREASE]: { symbol: 'd', yieldPerUnit: 1 },
  [StitchType.CHAIN]: { symbol: 'c', yieldPerUnit: 0 },
};

// --- Color & display modes ---

export type ColorMode = 'GAUSS_HEATMAP' | 'ROW_GRADIENT';

export type ViewMode = 'MESH_2D' | 'CYLINDER_3D' | 'REVOLUTION_3D';

export type InfoTab = 'CATALOG' | 'RULES';

export type SurfaceClassification = 'FLAT' | 'SPHERE' | 'HYPERBOLIC' | 'CYLINDRICAL';

// --- Compound stitch types (for AST) ---

export type CompoundType = 'V_STITCH' | 'PUFF' | 'POPCORN' | 'FAN';
