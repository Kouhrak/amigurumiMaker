# Tasks: React Native Multiplatform Migration

## Phase 1: Project Scaffold + Domain Types (~180 lines) — PR #1

- [ ] 1.1 Initialize Expo project: `npx create-expo-app@latest AmigurumiMakerRN --template blank-typescript`
- [ ] 1.2 Configure TypeScript strict mode in `tsconfig.json`
- [ ] 1.3 Create `src/domain/model/types.ts` — StitchType enum, ColorMode, ViewMode, InfoTab, SurfaceClassification, CompoundType
- [ ] 1.4 Create `src/domain/model/models.ts` — ParsedRow, ParsedToken, RoundAnalysis, SurfaceMetrics, MeshSettings, Float3, RingSegment, RevolutionMesh, BlockGeometry, PresetPattern
- [ ] 1.5 Create `src/domain/constants.ts` — STITCH_WIDTH, STITCH_HEIGHT, color constants
- [ ] 1.6 Create `src/__tests__/domain/types.test.ts` — smoke tests for type construction
- [ ] 1.7 Install dependencies: `npx expo install three @react-three/fiber @react-three/drei zustand expo-router`
- [ ] 1.8 Configure Expo Router in `app/_layout.tsx`

## Phase 2: Domain Logic — Math Engine (~250 lines) — PR #2

- [ ] 2.1 Create `src/domain/CurvatureComputer.ts` — port computeRoundAnalyses
- [ ] 2.2 Create `src/domain/CurvatureComputer.ts` — port computeSurfaceMetrics
- [ ] 2.3 Create `src/domain/CurvatureComputer.ts` — port classifySurface
- [ ] 2.4 Create `src/domain/CurvatureComputer.ts` — port curvatureColor (returns Float3 RGB instead of ARGB Long)
- [ ] 2.5 Create `src/domain/CurvatureComputer.ts` — port gaussCurvatureFromDelta
- [ ] 2.6 Create `src/domain/RevolutionMeshComputer.ts` — port computeMesh (vertex generation, triangulation, centering)
- [ ] 2.7 Create `src/domain/MeshComputer.ts` — port compute2DCells
- [ ] 2.8 Create `src/domain/MeshComputer.ts` — port compute3DProjection
- [ ] 2.9 Create `src/domain/GeometryComputer.ts` — port cubeGeometry, rampLeftGeometry, rampRightGeometry
- [ ] 2.10 Create `src/__tests__/domain/CurvatureComputer.test.ts` — test all functions with sphere/hyperbolic/cylinder patterns
- [ ] 2.11 Create `src/__tests__/domain/RevolutionMeshComputer.test.ts` — test mesh generation, vertex counts, centering

## Phase 3: Parser + Presets (~200 lines) — PR #3

- [ ] 3.1 Create `src/domain/PatternParser.ts` — port ROW_HEADER_REGEX, TOTAL_REGEX, TOKEN_REGEX, REPEAT_GROUP_REGEX
- [ ] 3.2 Create `src/domain/PatternParser.ts` — port expandRepeatGroups
- [ ] 3.3 Create `src/domain/PatternParser.ts` — port parseLine and parse
- [ ] 3.4 Create `src/domain/RowExpander.ts` — port row expansion logic
- [ ] 3.5 Create `src/domain/PresetPatterns.ts` — port 5 preset patterns (Esfera, Hiperbólica, Cono, Cilindro, Disco Plano)
- [ ] 3.6 Create `src/domain/crochet/AstNode.ts` — discriminated union types
- [ ] 3.7 Create `src/domain/crochet/Lexer.ts` — port tokenizer
- [ ] 3.8 Create `src/domain/crochet/Parser.ts` — port recursive descent parser
- [ ] 3.9 Create `src/__tests__/domain/PatternParser.test.ts` — test regex matching, repeat expansion, validation
- [ ] 3.10 Create `src/__tests__/domain/crochet/Parser.test.ts` — test AST generation

## Phase 4: 3D Rendering Engine (~300 lines) — PR #4

- [ ] 4.1 Create `src/engine/colorUtils.ts` — getSegmentColor (GAUSS_HEATMAP + ROW_GRADIENT), hslToRgb
- [ ] 4.2 Create `src/engine/useAmigurumiMesh.ts` — buildGeometry function (positions, colors, indices from RoundAnalysis[])
- [ ] 4.3 Create `src/engine/useAmigurumiMesh.ts` — React hook wrapping useMemo (keyed on analyses + settings)
- [ ] 4.4 Create `src/engine/AmigurumiCanvas.tsx` — R3F Canvas with camera, lighting (ambient + 2 directional)
- [ ] 4.5 Create `src/engine/AmigurumiCanvas.tsx` — mesh rendering with meshStandardMaterial (vertexColors, DoubleSide, wireframe)
- [ ] 4.6 Create `src/engine/AmigurumiCanvas.tsx` — OrbitControls from @react-three/drei
- [ ] 4.7 Create `src/engine/AmigurumiCanvas.tsx` — geometry disposal on unmount (useEffect cleanup)
- [ ] 4.8 Create `src/__tests__/engine/useAmigurumiMesh.test.ts` — test geometry generation, vertex counts, centering

## Phase 5: State Management + UI Shell (~350 lines) — PR #5

- [ ] 5.1 Create `src/store/useAmigurumiStore.ts` — Zustand store with initial state matching WallModelerState
- [ ] 5.2 Create `src/store/useAmigurumiStore.ts` — parseSyntax action (calls PatternParser → CurvatureComputer → RevolutionMeshComputer)
- [ ] 5.3 Create `src/store/useAmigurumiStore.ts` — zoomBy, resetView, dragBy actions (bounded scale [0.3, 4.0])
- [ ] 5.4 Create `src/store/useAmigurumiStore.ts` — setColorMode, setWireframe, loadPreset, loadExample actions
- [ ] 5.5 Create `src/app/_layout.tsx` — root Stack layout
- [ ] 5.6 Create `src/app/(tabs)/_layout.tsx` — tab navigation layout
- [ ] 5.7 Create `src/app/(tabs)/index.tsx` — main editor screen
- [ ] 5.8 Create `src/components/atoms/Button.tsx` — primary/secondary variants
- [ ] 5.9 Create `src/components/atoms/Input.tsx` — text input with multiline support
- [ ] 5.10 Create `src/components/atoms/Badge.tsx` — status badge
- [ ] 5.11 Create `src/components/atoms/StatCard.tsx` — statistic display card
- [ ] 5.12 Create `src/components/atoms/MetricCard.tsx` — metric display card
- [ ] 5.13 Create `src/components/atoms/HeaderTitle.tsx` — app header title
- [ ] 5.14 Create `src/components/atoms/StitchSymbol.tsx` — stitch type symbol display
- [ ] 5.15 Create `src/components/atoms/TabButton.tsx` — tab selection button
- [ ] 5.16 Create `src/components/molecules/SyntaxInputPanel.tsx` — text input + parse button
- [ ] 5.17 Create `src/components/molecules/PresetsBar.tsx` — horizontal preset buttons
- [ ] 5.18 Create `src/components/molecules/StatsRow.tsx` — stitches/increases/decreases stats
- [ ] 5.19 Create `src/components/molecules/MetricsGrid.tsx` — surface metrics grid
- [ ] 5.20 Create `src/components/molecules/HeaderBar.tsx` — top header bar
- [ ] 5.21 Create `src/__tests__/store/useAmigurumiStore.test.ts` — test state transitions

## Phase 6: Feature Screens (~350 lines) — PR #6

- [ ] 6.1 Create `src/components/organisms/AnalysisTable.tsx` — 11-column table with header/rows
- [ ] 6.2 Create `src/components/organisms/PatternEditorPanel.tsx` — syntax input + presets + stats
- [ ] 6.3 Create `src/components/organisms/StitchInfoPanel.tsx` — stitch details panel
- [ ] 6.4 Create `src/components/organisms/GeometricRules.tsx` — geometric rules display
- [ ] 6.5 Create `src/components/screens/WallModelerScreen.tsx` — compose all organisms into full layout
- [ ] 6.6 Wire Zustand store to all components via selectors
- [ ] 6.7 Add Platform.select for web vs native scroll/keyboard differences
- [ ] 6.8 Integrate AmigurumiCanvas into WallModelerScreen

## Phase 7: Polish + Tests + Config (~150 lines) — PR #7

- [ ] 7.1 Configure `app.json` — app name, icon, splash screen, platform settings
- [ ] 7.2 Configure `eas.json` — build profiles (development, preview, production)
- [ ] 7.3 Platform: Web — canvas sizing, CSS overrides if needed
- [ ] 7.4 Platform: Android/iOS — gesture handling, keyboard avoidant view
- [ ] 7.5 Create integration test — full parse → render → analyze flow
- [ ] 7.6 Update README.md — React Native setup instructions
- [ ] 7.7 Clean up: remove old Kotlin project files from repo root (keep in AmigurumiMaker/ for reference)

## Total: 76 tasks across 7 phases
## Estimated: ~1,780 lines | Avg ~255 lines/PR | All under 400-line budget
