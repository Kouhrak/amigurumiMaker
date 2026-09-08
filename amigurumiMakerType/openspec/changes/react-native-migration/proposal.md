# Proposal: React Native Multiplatform Migration

## Change
`react-native-migration` — Port AmigurumiMaker from Kotlin/Android to React Native (Expo) for multiplatform: Android, iOS, Web, Desktop.

## Intent
The current app is Android-only (Kotlin + Jetpack Compose + SceneView/Filament). The user wants a single codebase that runs on all platforms. React Native with Expo is the optimal choice: mature ecosystem, expo-gl for 3D via three.js, Expo Router for navigation, and EAS Build for native compilation.

## Current State
- **59 Kotlin files** in `AmigurumiMaker/app/src/main/`
- Android MVI + Clean Architecture + Atomic Design
- SceneView (Filament) for 3D revolution mesh rendering
- Regex-based PatternParser + Recursive Descent Parser (AST)
- CurvatureComputer, GeometryComputer, RevolutionMeshComputer, MeshComputer
- 5 unit tests + 1 instrumented test

## Target Stack
| Layer | Tech |
|---|---|
| Framework | Expo SDK 52+ (managed workflow) |
| Language | TypeScript (strict) |
| Navigation | Expo Router (file-based) |
| 3D | three.js + @react-three/fiber + @react-three/drei (via expo-gl) |
| State | Zustand (lightweight, RN-compatible) |
| UI | React Native StyleSheet (Atomic Design) |
| Testing | Jest + @testing-library/react-native |
| Build | EAS Build (Android, iOS, Web) |

## Approach: Domain-First Phased Migration

Port the pure domain logic first (zero UI dependencies), then 3D rendering, then UI. Each phase produces a testable, committable slice.

## Phase Breakdown

### Phase 1: Project Scaffold + Domain Types (~180 lines)
**PR #1** — Foundation
- Initialize Expo project with TypeScript strict mode
- Create `src/domain/model/` with all TypeScript equivalents:
  - `types.ts` — StitchType, ColorMode, ViewMode, InfoTab, SurfaceClassification, CompoundType
  - `models.ts` — ParsedRow, ParsedToken, RoundAnalysis, SurfaceMetrics, MeshSettings, Float3, RingSegment, RevolutionMesh, BlockGeometry, PresetPattern
- Create `src/domain/constants.ts` — STITCH_WIDTH, STITCH_HEIGHT, color thresholds
- Jest config + smoke tests for types

### Phase 2: Domain Logic — Math Engine (~250 lines)
**PR #2** — Pure computation, no I/O
- `src/domain/CurvatureComputer.ts` — port computeRoundAnalyses, computeSurfaceMetrics, classifySurface, curvatureColor, gaussCurvatureFromDelta
- `src/domain/RevolutionMeshComputer.ts` — port computeMesh (segment generation, vertex/index buffers, color computation, centering)
- `src/domain/MeshComputer.ts` — port compute2DCells, compute3DProjection
- `src/domain/GeometryComputer.ts` — port cubeGeometry, rampLeftGeometry, rampRightGeometry
- Unit tests for all math functions (Jest)

### Phase 3: Parser + Presets (~200 lines)
**PR #3** — Pattern parsing
- `src/domain/PatternParser.ts` — port regex-based parser (ROW_HEADER_REGEX, TOTAL_REGEX, TOKEN_REGEX, REPEAT_GROUP_REGEX)
- `src/domain/RowExpander.ts` — port row expansion logic
- `src/domain/PresetPatterns.ts` — port 5 preset patterns
- `src/domain/crochet/AstNode.ts` — port sealed interface to TypeScript discriminated unions
- `src/domain/crochet/Lexer.ts` — port lexer
- `src/domain/crochet/Parser.ts` — port recursive descent parser
- Unit tests for parser (Jest)

### Phase 4: 3D Rendering Engine (~300 lines)
**PR #4** — Three.js integration
- `src/engine/useAmigurumiMesh.ts` — custom hook: takes rows + settings, returns THREE.BufferGeometry (vertex positions, colors, indices)
- `src/engine/AmigurumiCanvas.tsx` — R3F Canvas component with OrbitControls, lighting (ambient + 2 directional), meshStandardMaterial with vertexColors
- `src/engine/colorUtils.ts` — GAUSS_HEATMAP / ROW_GRADIENT color mapping
- `src/engine/__tests__/useAmigurumiMesh.test.ts` — geometry generation tests

### Phase 5: State Management + UI Shell (~350 lines)
**PR #5** — App structure
- `src/store/useAmigurumiStore.ts` — Zustand store porting WallModelerState + WallModelerIntent
- `src/app/` — Expo Router file structure:
  - `_layout.tsx` — root layout with tab navigation
  - `(tabs)/index.tsx` — main editor screen
- `src/components/atoms/` — Button, Input, Badge, StatCard, MetricCard, HeaderTitle, StitchSymbol, TabButton
- `src/components/molecules/` — SyntaxInputPanel, PresetsBar, StatsRow, MetricsGrid, HeaderBar

### Phase 6: Feature Screens (~350 lines)
**PR #6** — Full UI
- `src/components/organisms/` — AnalysisTable, PatternEditorPanel, StitchInfoPanel, GeometricRules
- `src/components/screens/` — WallModelerScreen (combines all organisms)
- Wire Zustand store to components
- Platform-specific adjustments (web vs native scroll, keyboard)

### Phase 7: Polish + Tests + Config (~150 lines)
**PR #7** — Finalization
- EAS Build configuration (app.json, eas.json)
- Platform-specific: Web (canvas sizing), Android/iOS (gesture handling)
- Integration tests (full parse → render → analyze flow)
- Clean up: remove Kotlin project files, update README

## Estimated Total: ~1,780 lines across 7 PRs
Average ~255 lines/PR. All under 400-line budget.

## Risk Assessment

| Risk | Severity | Mitigation |
|---|---|---|
| 3D in RN (expo-gl) | Medium | expo-gl + three.js is proven; @react-three/fiber has RN examples |
| Mesh perf on low-end devices | Low | Data sizes are small (5-20 rows, ~200 vertices max) |
| Parser port accuracy | Low | Regex port is mechanical; existing tests validate |
| Platform UI differences | Medium | StyleSheet is consistent; use Platform.select for edge cases |
| Desktop via Electron | Low | Expo supports web; Electron wrapping is post-MVP |

## PR Strategy
**Stacked-to-main**: Each PR merges independently to main. Fast iteration, fix on the go.

## Migration Mapping

| Kotlin | TypeScript/React Native |
|---|---|
| `data class` | TypeScript `interface` / `type` |
| `enum class` | TypeScript `enum` or string union |
| `sealed interface` | Discriminated union (`type X = { kind: 'A' } \| { kind: 'B' }`) |
| `object` (singleton) | Module-level functions / exported functions |
| `ViewModel` + `StateFlow` | Zustand store |
| `@Composable` | React functional component |
| `Modifier` | StyleSheet + props |
| `SceneView` + Filament | @react-three/fiber Canvas + meshes |
| `VertexBuffer` / `IndexBuffer` | `THREE.BufferGeometry` + `setAttribute` |
| `LineNode` (wireframe) | `<lineSegments>` in R3F |
| `remember` | `useMemo` / `useCallback` |
| JUnit tests | Jest tests |
