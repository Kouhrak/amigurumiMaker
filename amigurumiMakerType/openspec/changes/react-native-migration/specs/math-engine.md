# Spec: Math Engine (Curvature & Mesh Computation)

## Capability
Pure TypeScript computation of discrete Gaussian curvature, surface metrics, and revolution mesh geometry. Equivalent to Kotlin CurvatureComputer, RevolutionMeshComputer, MeshComputer, GeometryComputer.

## Current Behavior (Kotlin)

### CurvatureComputer
- `computeRoundAnalyses(rows: List<ParsedRow>)`: For each row, computes:
  - `r_i = N_i * STITCH_WIDTH / (2π)`
  - `Δr = |r_i - r_{i-1}|`
  - `cos(θ_i) = min(1.0, Δr / STITCH_HEIGHT)`
  - `sin(θ_i) = sqrt(max(0, 1 - cos²(θ_i)))`
  - `liftZ = prev_liftZ + STITCH_HEIGHT * sin(θ_i)`
  - `K_i = (6 - ΔN_i) * π / 3` (local curvature)
  - `A_i = 2π * r_i * STITCH_HEIGHT` (round area)
- `computeSurfaceMetrics(analyses)`: totalArea, totalCurvature, eulerCharacteristic
- `classifySurface(analyses)`: FLAT / SPHERE / HYPERBOLIC / CYLINDRICAL
- `curvatureColor(k)`: >0.1 green, <-0.1 pink, else cyan (ARGB Long)
- `gaussCurvatureFromDelta(dN)`: (6 - dN) * π/3

### RevolutionMeshComputer
- `computeMesh(analyses, colorMode)`: For each adjacent row pair (i, i+1):
  - S = max(N_i, N_{i+1}) segments
  - φ_k = k/S * 2π
  - Bottom vertices: (r1*cos(φ), liftZ1, r1*sin(φ))
  - Top vertices: (r2*cos(φ), liftZ2, r2*sin(φ))
  - Two triangles per segment (4 vertices per quad)
  - Center Y by subtracting totalHeight/2
  - Color: GAUSS_HEATMAP (green/pink/cyan by K) or ROW_GRADIENT (HSL 240°→360°)

### MeshComputer
- `compute2DCells(rows)`: 2D grid cells for MESH_2D view
- `compute3DProjection(rows)`: Cylinder projection for CYLINDER_3D view

### GeometryComputer
- `compute(rowDefs)`: 2D block geometry (cubes, ramp_left, ramp_right) with vertices, edges, faceIndices

## Target Behavior (TypeScript)
Exact mathematical equivalents as pure functions in `src/domain/`. No side effects, no I/O.

## Interface

```typescript
// src/domain/CurvatureComputer.ts
export function computeRoundAnalyses(rows: ParsedRow[]): RoundAnalysis[];
export function computeSurfaceMetrics(analyses: RoundAnalysis[]): SurfaceMetrics;
export function classifySurface(analyses: RoundAnalysis[]): SurfaceClassification;
export function curvatureColor(k: number): Float3;  // returns RGB [0..1] instead of ARGB Long
export function gaussCurvatureFromDelta(dN: number): number;

// src/domain/RevolutionMeshComputer.ts
export function computeMesh(analyses: RoundAnalysis[], colorMode: ColorMode): RevolutionMesh | null;

// src/domain/MeshComputer.ts
export function compute2DCells(rows: ParsedRow[]): MeshCell[];
export function compute3DProjection(rows: ParsedRow[]): CylinderCell[];

// src/domain/GeometryComputer.ts
export function compute(rowDefs: RowDef[]): BlockGeometry[];
```

## Scenarios

### Scenario 1: Curvature analysis for a sphere pattern
- **Given** rows: [6c(6p), [1a]6v(12p), [1p 1a]6v(18p), [2p 1a]6v(24p)]
- **When** computeRoundAnalyses is called
- **Then** each RoundAnalysis has correct r_i, θ_i, K_i, liftZ values
- **And** K_i values are positive (sphere = positive curvature)
- **And** liftZ increases monotonically

### Scenario 2: Revolution mesh generation
- **Given** 5 RoundAnalyses with increasing stitch counts
- **When** computeMesh is called with GAUSS_HEATMAP
- **Then** returns RevolutionMesh with 4 segments (n-1 for n analyses)
- **And** each segment has vertices, indices, and color
- **And** totalHeight equals last.liftZ - first.liftZ
- **And** mesh is centered (Y offset = -totalHeight/2)

### Scenario 3: Surface classification
- **Given** analyses with all positive K_i (sphere pattern)
- **When** classifySurface is called
- **Then** returns 'SPHERE'

### Scenario 4: Curvature color mapping
- **Given** K = 0.5 (positive)
- **When** curvatureColor is called
- **Then** returns { x: 0.06, y: 0.72, z: 0.50 } (green)

### Scenario 5: Gauss curvature from delta
- **Given** dN = 6 (increase of 6)
- **When** gaussCurvatureFromDelta is called
- **Then** returns 0 (flat: (6-6)*π/3 = 0)

## Acceptance Criteria
- [ ] All mathematical formulas match Kotlin implementation exactly
- [ ] computeRoundAnalyses handles empty input (returns [])
- [ ] computeMesh returns null for < 2 analyses
- [ ] Vertex coordinates match Kotlin Float3 output
- [ ] Index triangulation matches Kotlin pattern (2 triangles per quad)
- [ ] Color mapping matches Kotlin curvatureColor thresholds
- [ ] ROW_GRADIENT uses HSL interpolation from 240° to 360°
- [ ] Mesh centering subtracts totalHeight/2 from Y
