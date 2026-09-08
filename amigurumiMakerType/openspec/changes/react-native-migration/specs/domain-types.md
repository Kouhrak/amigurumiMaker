# Spec: Domain Types & Constants

## Capability
TypeScript type definitions and mathematical constants equivalent to the Kotlin domain model layer.

## Current Behavior (Kotlin)
- `StitchType` enum: NORMAL("p",1), INCREASE("a",2), DECREASE("d",1), CHAIN("c",0)
- `ColorMode` enum: GAUSS_HEATMAP, ROW_GRADIENT
- `ViewMode` enum: MESH_2D, CYLINDER_3D, REVOLUTION_3D
- `InfoTab` enum: CATALOG, RULES
- `SurfaceClassification` enum: FLAT, SPHERE, HYPERBOLIC, CYLINDRICAL
- `CompoundType` enum: V_STITCH, PUFF, POPCORN, FAN
- Data classes: ParsedRow, ParsedToken, RoundAnalysis, SurfaceMetrics, MeshSettings, Float3, RingSegment, RevolutionMesh, BlockGeometry, PresetPattern
- Constants: STITCH_WIDTH = 0.5f, STITCH_HEIGHT = 0.5f

## Target Behavior (TypeScript)
All types exported from `src/domain/model/` as TypeScript interfaces, types, and const enums. Constants in `src/domain/constants.ts`.

## Interface

```typescript
// src/domain/model/types.ts
export enum StitchType {
  NORMAL = 'p',
  INCREASE = 'a',
  DECREASE = 'd',
  CHAIN = 'c',
}

export const STITCH_YIELD: Record<StitchType, number> = {
  [StitchType.NORMAL]: 1,
  [StitchType.INCREASE]: 2,
  [StitchType.DECREASE]: 1,
  [StitchType.CHAIN]: 0,
};

export type ColorMode = 'GAUSS_HEATMAP' | 'ROW_GRADIENT';
export type ViewMode = 'MESH_2D' | 'CYLINDER_3D' | 'REVOLUTION_3D';
export type InfoTab = 'CATALOG' | 'RULES';
export type SurfaceClassification = 'FLAT' | 'SPHERE' | 'HYPERBOLIC' | 'CYLINDRICAL';
export type CompoundType = 'V_STITCH' | 'PUFF' | 'POPCORN' | 'FAN';

// src/domain/model/models.ts
export interface ParsedToken {
  type: StitchType;
  count: number;
  yieldCount: number;
}

export interface ParsedRow {
  raw: string;
  rowIndex: number;
  tokens: ParsedToken[];
  totalCalculated: number;
  totalExpected: number | null;
  isValid: boolean;
  errorMsg: string;
  increaseCount: number;
  decreaseCount: number;
}

export interface RoundAnalysis {
  rowIndex: number;
  stitchCount: number;
  deltaN: number;
  theoreticalRadius: number;
  cosTheta: number;
  sinTheta: number;
  inclinationAngleDeg: number;
  liftZ: number;
  localCurvature: number;
  roundArea: number;
}

export interface SurfaceMetrics {
  totalArea: number;
  totalCurvature: number;
  eulerCharacteristic: number;
}

export interface MeshSettings {
  stitchWidth: number;   // default 0.5
  stitchHeight: number;  // default 0.5
  wireframe: boolean;    // default false
  colorMode: ColorMode;  // default 'GAUSS_HEATMAP'
}

export interface Float3 {
  x: number;
  y: number;
  z: number;
}

export interface RingSegment {
  vertices: Float3[];
  indices: number[];
  color: Float3;
}

export interface RevolutionMesh {
  segments: RingSegment[];
  totalHeight: number;
}

export interface BlockGeometry {
  vertices: Float3[];
  edges: [number, number][];
  faceIndices: [number, number, number][];
}

export interface PresetPattern {
  name: string;
  description: string;
  pattern: string;
}
```

```typescript
// src/domain/constants.ts
export const STITCH_WIDTH = 0.5;
export const STITCH_HEIGHT = 0.5;
export const GAUSS_POSITIVE_COLOR: Float3 = { x: 0.06, y: 0.72, z: 0.50 };
export const GAUSS_NEGATIVE_COLOR: Float3 = { x: 0.95, y: 0.25, z: 0.37 };
export const GAUSS_NEUTRAL_COLOR: Float3 = { x: 0.02, y: 0.71, z: 0.83 };
```

## Scenarios

### Scenario 1: StitchType yield mapping
- **Given** StitchType.INCREASE
- **When** looking up yieldCount
- **Then** returns 2

### Scenario 2: Default MeshSettings
- **Given** no arguments
- **When** creating MeshSettings with defaults
- **Then** stitchWidth=0.5, stitchHeight=0.5, wireframe=false, colorMode='GAUSS_HEATMAP'

### Scenario 3: Float3 construction
- **Given** values x=1.0, y=2.0, z=3.0
- **When** creating Float3
- **Then** Float3 { x: 1.0, y: 2.0, z: 3.0 }

## Acceptance Criteria
- [ ] All types compile with TypeScript strict mode
- [ ] StitchType enum values match Kotlin ('p', 'a', 'd', 'c')
- [ ] STITCH_YIELD record covers all StitchType values
- [ ] MeshSettings has sensible defaults
- [ ] Float3, RingSegment, RevolutionMesh match Kotlin data class structure
