# Design: React Native Multiplatform Migration

## Architecture Overview

```
src/
├── domain/                    # Pure TypeScript — zero UI deps
│   ├── model/
│   │   ├── types.ts           # Enums: StitchType, ColorMode, ViewMode, etc.
│   │   └── models.ts          # Interfaces: ParsedRow, RoundAnalysis, Float3, etc.
│   ├── constants.ts           # STITCH_WIDTH, STITCH_HEIGHT, color constants
│   ├── CurvatureComputer.ts   # Pure math functions
│   ├── RevolutionMeshComputer.ts  # Mesh vertex/index generation
│   ├── MeshComputer.ts        # 2D cells + 3D cylinder projection
│   ├── GeometryComputer.ts    # Block geometry (cubes, ramps)
│   ├── PatternParser.ts       # Regex-based pattern parser
│   ├── RowExpander.ts         # Row expansion logic
│   ├── PresetPatterns.ts      # 5 preset patterns
│   └── crochet/
│       ├── AstNode.ts         # Discriminated union AST
│       ├── Lexer.ts           # Tokenizer
│       └── Parser.ts          # Recursive descent parser
├── engine/                    # Three.js / R3F integration
│   ├── useAmigurumiMesh.ts    # Hook: analyses → BufferGeometry
│   ├── AmigurumiCanvas.tsx    # R3F Canvas + Controls + Lighting
│   └── colorUtils.ts          # Color mapping functions
├── store/
│   └── useAmigurumiStore.ts   # Zustand store
├── components/
│   ├── atoms/                 # 8 atomic components
│   ├── molecules/             # 5 molecule components
│   ├── organisms/             # 4 organism components
│   └── screens/
│       └── WallModelerScreen.tsx
├── app/                       # Expo Router
│   ├── _layout.tsx
│   └── (tabs)/
│       ├── _layout.tsx
│       └── index.tsx
└── __tests__/                 # Jest tests
    ├── domain/
    ├── engine/
    └── store/
```

## Data Flow

```
User Input (text)
    ↓
PatternParser.parse(text) → ParsedRow[]
    ↓
CurvatureComputer.computeRoundAnalyses(rows) → RoundAnalysis[]
    ↓
RevolutionMeshComputer.computeMesh(analyses, colorMode) → RevolutionMesh
    ↓
useAmigurumiMesh(revolutionMesh) → THREE.BufferGeometry
    ↓
AmigurumiCanvas renders geometry via R3F
```

## Key Technical Decisions

### 1. State Management: Zustand over Redux/Context
**Decision**: Zustand
**Why**:
- Lightweight (~1KB), no providers needed
- Works in React Native without setup
- `useSyncExternalStore` under the hood (concurrent-safe)
- Selective re-rendering via selectors
- No boilerplate (unlike Redux Toolkit)

### 2. 3D Rendering: expo-gl + @react-three/fiber
**Decision**: @react-three/fiber with expo-gl
**Why**:
- R3F is the standard React 3D framework
- expo-gl provides GL context in React Native
- @react-three/drei adds OrbitControls, etc.
- Works on web, Android, iOS
- Declarative scene graph (JSX)

**Alternative considered**: Victory Native GL — rejected, no 3D mesh support.

### 3. Navigation: Expo Router (file-based)
**Decision**: Expo Router v4
**Why**:
- File-based routing (Next.js-like)
- Tab navigation built-in
- Deep linking automatic
- Works with EAS Build

### 4. Styling: StyleSheet (no NativeWind)
**Decision**: React Native StyleSheet
**Why**:
- Zero dependencies
- Works on all platforms
- Atomic Design maps cleanly
- Platform.select for edge cases
- No build step for styles

### 5. Testing: Jest + @testing-library/react-native
**Decision**: Jest for unit, RNTL for integration
**Why**:
- Jest is Expo default
- RNTL is RN testing standard
- Domain tests are pure Jest (no RN dependency)

## BufferGeometry Construction Detail

```typescript
// useAmigurumiMesh.ts — core algorithm
function buildGeometry(analyses: RoundAnalysis[], settings: MeshSettings): THREE.BufferGeometry {
  const positions: number[] = [];
  const colors: number[] = [];
  const indices: number[] = [];

  for (let i = 0; i < analyses.length - 1; i++) {
    const rowBot = analyses[i];
    const rowTop = analyses[i + 1];
    const r1 = rowBot.theoreticalRadius;
    const r2 = rowTop.theoreticalRadius;
    const z1 = rowBot.liftZ;
    const z2 = rowTop.liftZ;
    const S = Math.max(rowBot.stitchCount, rowTop.stitchCount, 4);

    const segmentStart = positions.length / 3;

    for (let k = 0; k <= S; k++) {
      const phi = (k / S) * Math.PI * 2;
      const phiNext = ((k + 1) / S) * Math.PI * 2;

      // Bottom ring vertex
      positions.push(r1 * Math.cos(phi), z1, r1 * Math.sin(phi));
      // Top ring vertex
      positions.push(r2 * Math.cos(phi), z2, r2 * Math.sin(phi));

      // Colors (same for both vertices in this column)
      const color = getSegmentColor(i, analyses.length - 1, rowBot.localCurvature, settings.colorMode);
      colors.push(color.r, color.g, color.b);
      colors.push(color.r, color.g, color.b);

      if (k < S) {
        // Indices for 2 triangles
        const bl = segmentStart + k * 2;
        const tl = segmentStart + k * 2 + 1;
        const br = segmentStart + (k + 1) * 2;
        const tr = segmentStart + (k + 1) * 2 + 1;

        indices.push(bl, tl, br);
        indices.push(tl, tr, br);
      }
    }
  }

  // Center Y
  const totalHeight = analyses[analyses.length - 1].liftZ - analyses[0].liftZ;
  for (let i = 1; i < positions.length; i += 3) {
    positions[i] -= totalHeight / 2;
  }

  const geometry = new THREE.BufferGeometry();
  geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3));
  geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));
  geometry.setIndex(indices);
  geometry.computeVertexNormals();

  return geometry;
}
```

## R3F Canvas Structure

```tsx
<Canvas camera={{ position: [0, 0, 12], fov: 45 }}>
  <ambientLight intensity={0.7} color="#ffffff" />
  <directionalLight position={[10, 20, 15]} intensity={0.8} color="#818cf8" />
  <directionalLight position={[-10, -10, -10]} intensity={0.4} color="#f43f5e" />

  {meshData && (
    <mesh geometry={meshData.geometry}>
      <meshStandardMaterial
        vertexColors
        side={THREE.DoubleSide}
        wireframe={settings.wireframe}
      />
    </mesh>
  )}

  <OrbitControls makeDefault />
</Canvas>
```

## Zustand Store Shape

```typescript
interface AmigurumiState {
  // Data
  syntaxText: string;
  parsedRows: ParsedRow[];
  roundAnalyses: RoundAnalysis[];
  revolutionMesh: RevolutionMesh | null;
  meshCells: MeshCell[];
  cylinderCells: CylinderCell[];
  surfaceMetrics: SurfaceMetrics | null;
  surfaceClassification: SurfaceClassification;

  // UI
  isLoading: boolean;
  error: string | null;
  viewMode: ViewMode;
  activeTab: InfoTab;
  colorMode: ColorMode;
  wireframeEnabled: boolean;

  // Viewport
  scale: number;
  offsetX: number;
  offsetY: number;

  // Computed
  totalStitches: number;
  totalIncreases: number;
  totalDecreases: number;
  logMessage: string;

  // Actions
  parseSyntax: (text: string) => void;
  reset: () => void;
  zoomBy: (factor: number) => void;
  resetView: () => void;
  setViewMode: (mode: ViewMode) => void;
  setActiveTab: (tab: InfoTab) => void;
  loadExample: (index: number) => void;
  dragBy: (dx: number, dy: number) => void;
  setColorMode: (mode: ColorMode) => void;
  setWireframe: (enabled: boolean) => void;
  loadPreset: (index: number) => void;
}
```

## Performance Considerations

| Concern | Mitigation |
|---|---|
| Re-parsing on every keystroke | Debounce 300ms in SyntaxInputPanel |
| Mesh recomputation on colorMode change | useMemo in useAmigurumiMesh (keyed on analyses + colorMode) |
| Geometry disposal | useEffect cleanup in AmigurumiCanvas |
| Large patterns (50+ rows) | S = max(N_i, N_{i+1}) is bounded by pattern; typically < 100 segments |
| Re-renders from Zustand | Selectors: `useAmigurumiStore(s => s.revolutionMesh)` |

## Platform-Specific Handling

| Platform | Consideration |
|---|---|
| Web | Canvas sizing via CSS, keyboard input works natively |
| Android/iOS | Gesture handling via OrbitControls (touch), keyboard avoidant view |
| Desktop (Electron) | Web mode, no additional changes needed |

## Testing Strategy

| Layer | Tool | What to test |
|---|---|---|
| Domain types | Jest | Type compilation, default values |
| Math engine | Jest | All formulas, edge cases (empty input, single row) |
| Parser | Jest | Regex matching, repeat expansion, validation |
| Store | Jest | State transitions, action dispatch |
| Components | RNTL | Render, user interaction, store wiring |
| 3D | Jest (mock) | Geometry generation, vertex counts |
