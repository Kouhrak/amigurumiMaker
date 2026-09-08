# Spec: 3D Rendering Engine (React Three Fiber)

## Capability
Three.js-based 3D revolution mesh rendering via @react-three/fiber, replacing Android SceneView/Filament. Includes procedural BufferGeometry generation, orbit controls, and dual color modes.

## Current Behavior (Kotlin — GeometryViewport.kt)
- Uses SceneView with Filament engine
- Per RingSegment: creates FloatBuffer (vertices), ShortBuffer (indices)
- VertexBuffer with POSITION attribute (3 floats)
- IndexBuffer with USHORT type
- Material: createColorInstance(argb) per segment
- Wireframe: extracts unique edges from triangle indices, renders LineNode per edge
- Camera: OrbitCamera with rememberCameraManipulator

## Target Behavior (TypeScript/R3F)
- Custom hook `useAmigurumiMesh` generates THREE.BufferGeometry from RoundAnalysis[]
- `<AmigurumiCanvas>` wraps R3F Canvas with controls, lighting, mesh rendering
- Vertex colors attribute for per-vertex coloring
- Wireframe via `<meshStandardMaterial wireframe={true}>`
- OrbitControls from @react-three/drei

## Interface

```typescript
// src/engine/useAmigurumiMesh.ts
import * as THREE from 'three';

export interface MeshData {
  geometry: THREE.BufferGeometry;
  segmentCount: number;
  vertexCount: number;
  indexCount: number;
}

export function useAmigurumiMesh(
  analyses: RoundAnalysis[],
  settings: MeshSettings
): MeshData | null;

// src/engine/AmigurumiCanvas.tsx
export interface AmigurumiCanvasProps {
  analyses: RoundAnalysis[];
  settings: MeshSettings;
}

export function AmigurumiCanvas({ analyses, settings }: AmigurumiCanvasProps): JSX.Element;

// src/engine/colorUtils.ts
export function getSegmentColor(
  rowIndex: number,
  totalRows: number,
  curvature: number,
  colorMode: ColorMode
): THREE.Color;

export function hslToRgb(h: number, s: number, l: number): THREE.Color;
```

## Geometry Generation Algorithm

```
For each adjacent row pair (i, i+1):
  r1 = analyses[i].theoreticalRadius
  r2 = analyses[i+1].theoreticalRadius
  z1 = analyses[i].liftZ
  z2 = analyses[i+1].liftZ
  S = max(analyses[i].stitchCount, analyses[i+1].stitchCount)  // min 4 segments

  For k = 0 to S-1:
    φ = k / S * 2π
    φNext = (k+1) / S * 2π

    // 4 vertices per quad
    vBot     = (r1*cos(φ),   z1, r1*sin(φ))
    vTop     = (r2*cos(φ),   z2, r2*sin(φ))
    vBotNext = (r1*cos(φNext), z1, r1*sin(φNext))
    vTopNext = (r2*cos(φNext), z2, r2*sin(φNext))

    // 2 triangles
    indices: [vBot, vTop, vBotNext], [vTop, vTopNext, vBotNext]

Center Y: subtract totalHeight/2 from all Y coordinates
```

## Lighting Setup
```tsx
<ambientLight intensity={0.7} color="#ffffff" />
<directionalLight position={[10, 20, 15]} intensity={0.8} color="#818cf8" />
<directionalLight position={[-10, -10, -10]} intensity={0.4} color="#f43f5e" />
```

## Camera Setup
```tsx
<Canvas camera={{ position: [0, 0, 12], fov: 45 }}>
```

## Scenarios

### Scenario 1: Geometry generation from analyses
- **Given** 5 RoundAnalyses with increasing stitch counts
- **When** useAmigurumiMesh is called
- **Then** returns MeshData with valid BufferGeometry
- **And** geometry has position attribute with 3 floats per vertex
- **And** geometry has color attribute with 3 floats per vertex
- **And** index count is divisible by 3 (triangles)

### Scenario 2: Vertex centering
- **Given** analyses with totalHeight = 4.0
- **When** mesh is generated
- **Then** all Y coordinates are offset by -2.0

### Scenario 3: Color mode switching
- **Given** analyses with mixed positive/negative curvature
- **When** colorMode is GAUSS_HEATMAP
- **Then** vertices get green/pink/cyan based on local K_i
- **When** colorMode is ROW_GRADIENT
- **Then** vertices get HSL-interpolated color based on row index

### Scenario 4: Wireframe toggle
- **Given** a generated mesh
- **When** settings.wireframe is true
- **Then** meshStandardMaterial renders with wireframe=true

### Scenario 5: Empty input
- **Given** empty analyses array
- **When** useAmigurumiMesh is called
- **Then** returns null

## Acceptance Criteria
- [ ] BufferGeometry has position, color, and normal attributes
- [ ] Index buffer uses correct triangle winding (counter-clockwise)
- [ ] Vertex count matches (S+1) * 2 per segment pair
- [ ] Colors match curvature thresholds exactly
- [ ] OrbitControls allows 360° rotation, zoom, pan
- [ ] Canvas renders on all platforms (web, Android, iOS via expo-gl)
- [ ] Wireframe toggle works in real-time
- [ ] Memory: geometry is properly disposed on unmount
