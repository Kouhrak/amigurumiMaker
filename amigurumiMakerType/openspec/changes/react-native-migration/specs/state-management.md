# Spec: State Management (Zustand Store)

## Capability
Zustand store equivalent to Kotlin WallModelerViewModel + WallModelerState, managing app state, pattern parsing, and mesh computation.

## Current Behavior (Kotlin)

### WallModelerState
```kotlin
data class WallModelerState(
  syntaxText: String = "",
  parsedRows: List<ParsedRow> = emptyList(),
  meshCells: List<MeshCell> = emptyList(),
  cylinderCells: List<CylinderCell> = emptyList(),
  isLoading: Boolean = false,
  error: String? = null,
  viewMode: ViewMode = ViewMode.MESH_2D,
  activeTab: InfoTab = InfoTab.CATALOG,
  scale: Float = 1f,
  offsetX: Float = 0f,
  offsetY: Float = 0f,
  logMessage: String = "Listo para procesar patrón.",
  totalStitches: Int = 0,
  totalIncreases: Int = 0,
  totalDecreases: Int = 0,
  roundAnalyses: List<RoundAnalysis> = emptyList(),
  surfaceMetrics: SurfaceMetrics? = null,
  surfaceClassification: SurfaceClassification = SurfaceClassification.FLAT,
  colorMode: ColorMode = ColorMode.GAUSS_HEATMAP,
  wireframeEnabled: Boolean = false,
  revolutionMesh: RevolutionMesh? = null
)
```

### WallModelerIntent (Actions)
ParseSyntax, Reset, ZoomBy, ResetView, SetViewMode, SetActiveTab, LoadExample, DragBy, SetColorMode, SetWireframe, LoadPreset

### WallModelerViewModel
- process(intent) dispatches to private methods
- parseSyntax: PatternParser.parse → MeshComputer → CurvatureComputer → RevolutionMeshComputer → update state
- setColorMode: recomputes revolution mesh with new color mode
- loadPreset/loadExample: selects preset and calls parseSyntax

## Target Behavior (TypeScript)
Zustand store with same state shape and actions as Zustand slices.

## Interface

```typescript
// src/store/useAmigurumiStore.ts
import { StateCreator } from 'zustand';

export interface AmigurumiState {
  // State
  syntaxText: string;
  parsedRows: ParsedRow[];
  meshCells: MeshCell[];
  cylinderCells: CylinderCell[];
  isLoading: boolean;
  error: string | null;
  viewMode: ViewMode;
  activeTab: InfoTab;
  scale: number;
  offsetX: number;
  offsetY: number;
  logMessage: string;
  totalStitches: number;
  totalIncreases: number;
  totalDecreases: number;
  roundAnalyses: RoundAnalysis[];
  surfaceMetrics: SurfaceMetrics | null;
  surfaceClassification: SurfaceClassification;
  colorMode: ColorMode;
  wireframeEnabled: boolean;
  revolutionMesh: RevolutionMesh | null;

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

export const useAmigurumiStore: ReactStateCreator<AmigurumiState> = (set, get) => ({
  // ... initial state
  // ... action implementations
});
```

## Scenarios

### Scenario 1: Parse syntax updates full state
- **Given** store with empty state
- **When** parseSyntax("1) 6c (6p)\n2) [1a] 6v (12p)")
- **Then** parsedRows has 2 entries
- **And** roundAnalyses has 2 entries
- **And** revolutionMesh is not null
- **And** isLoading is false
- **And** error is null

### Scenario 2: Color mode change recomputes mesh
- **Given** store with parsed data and GAUSS_HEATMAP
- **When** setColorMode('ROW_GRADIENT')
- **Then** colorMode is 'ROW_GRADIENT'
- **And** revolutionMesh segments have different colors than before

### Scenario 3: Zoom bounded
- **Given** store with scale=1.0
- **When** zoomBy(2.0)
- **Then** scale is 2.0
- **When** zoomBy(10.0) (would exceed 4.0)
- **Then** scale is 4.0 (clamped)

### Scenario 4: Load preset triggers parse
- **Given** store with empty state
- **When** loadPreset(0) (Esfera)
- **Then** syntaxText matches Esfera pattern
- **And** parsedRows is not empty

### Scenario 5: Reset clears everything
- **Given** store with populated state
- **When** reset()
- **Then** all fields return to initial values

## Acceptance Criteria
- [ ] Store initial state matches Kotlin WallModelerState defaults
- [ ] parseSyntax calls all domain functions in correct order
- [ ] zoomBy clamps scale to [0.3, 4.0]
- [ ] setColorMode recomputes revolutionMesh (not just stores the mode)
- [ ] LoadExample loads correct pattern text
- [ ] LoadPreset indexes into PRESETS array
- [ ] Reset returns all fields to defaults
- [ ] No memory leaks (geometry disposal handled in 3D component)
