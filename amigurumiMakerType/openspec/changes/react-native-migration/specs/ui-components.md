# Spec: UI Components & Screens (Atomic Design)

## Capability
React Native components following Atomic Design, equivalent to Kotlin Compose atoms/molecules/organisms/pages. Includes Expo Router navigation and platform-specific adjustments.

## Current Behavior (Kotlin)

### Atoms (Compose)
- TabButton, SyntaxInputField, StitchSymbol, StatusBadge, StatCard, MetricCard, AppHeaderTitle, AppButton

### Molecules (Compose)
- SyntaxInputPanel (TextInput + parse button), StitchCatalog, StatsRow, PresetsBar, MetricsGrid, HeaderBar, GeometricRules

### Organisms (Compose)
- AnalysisTable (11-column table with header/rows)
- PatternEditorPanel (syntax input + presets + stats)
- StitchInfoPanel (stitch details)
- GeometryViewport (3D SceneView)
- CanvasView2D (2D mesh rendering)

### Pages/Templates
- WallModelerPage → WallModelerTemplate (combines all)

## Target Behavior (React Native)
Same Atomic Design structure with React Native StyleSheet. Expo Router for navigation.

## Interface

### File Structure
```
src/
├── app/
│   ├── _layout.tsx              # Root layout (Stack)
│   └── (tabs)/
│       ├── _layout.tsx          # Tab layout
│       ├── index.tsx            # Main editor (WallModelerScreen)
│       └── settings.tsx         # Settings (future)
├── components/
│   ├── atoms/
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Badge.tsx
│   │   ├── StatCard.tsx
│   │   ├── MetricCard.tsx
│   │   ├── HeaderTitle.tsx
│   │   ├── StitchSymbol.tsx
│   │   └── TabButton.tsx
│   ├── molecules/
│   │   ├── SyntaxInputPanel.tsx
│   │   ├── PresetsBar.tsx
│   │   ├── StatsRow.tsx
│   │   ├── MetricsGrid.tsx
│   │   └── HeaderBar.tsx
│   ├── organisms/
│   │   ├── AnalysisTable.tsx
│   │   ├── PatternEditorPanel.tsx
│   │   ├── StitchInfoPanel.tsx
│   │   └── GeometricRules.tsx
│   └── screens/
│       └── WallModelerScreen.tsx
└── engine/
    ├── AmigurumiCanvas.tsx
    └── useAmigurumiMesh.ts
```

### Component Interfaces
```tsx
// atoms
interface ButtonProps { title: string; onPress: () => void; variant?: 'primary' | 'secondary'; disabled?: boolean; }
interface InputProps { value: string; onChangeText: (text: string) => void; placeholder?: string; multiline?: boolean; }
interface BadgeProps { text: string; color: string; }
interface StatCardProps { label: string; value: string | number; icon?: string; }
interface MetricCardProps { label: string; value: string; unit?: string; color?: string; }
interface TabButtonProps { label: string; active: boolean; onPress: () => void; }

// molecules
interface SyntaxInputPanelProps { value: string; onChangeText: (text: string) => void; onParse: () => void; isLoading: boolean; }
interface PresetsBarProps { onPresetSelect: (index: number) => void; }
interface StatsRowProps { totalStitches: number; totalIncreases: number; totalDecreases: number; }

// organisms
interface AnalysisTableProps { rows: ParsedRow[]; roundAnalyses: RoundAnalysis[]; }
interface PatternEditorPanelProps { /* connects to store */ }

// screens
interface WallModelerScreenProps { /* no props — uses store */ }
```

## Scenarios

### Scenario 1: Main screen renders all sections
- **Given** app loads
- **When** WallModelerScreen mounts
- **Then** renders HeaderBar, SyntaxInputPanel, PresetsBar, StatsRow, AmigurumiCanvas, AnalysisTable

### Scenario 2: Syntax input triggers parse
- **Given** user types pattern in SyntaxInputPanel
- **When** user taps Parse button
- **Then** store.parseSyntax is called with input text
- **And** 3D canvas updates with new mesh

### Scenario 3: Preset selection loads pattern
- **Given** PresetsBar shows 5 presets
- **When** user taps "Esfera"
- **Then** store.loadPreset(0) is called
- **And** syntax input shows Esfera pattern
- **And** 3D canvas shows sphere mesh

### Scenario 4: Color mode toggle
- **Given** GAUSS_HEATMAP mode active
- **When** user toggles to ROW_GRADIENT
- **Then** 3D mesh colors update in real-time

### Scenario 5: Wireframe toggle
- **Given** wireframe disabled
- **When** user toggles wireframe on
- **Then** 3D mesh renders edges instead of filled faces

### Scenario 6: Platform-specific scrolling
- **Given** AnalysisTable with many rows
- **On** Android/iOS: uses ScrollView with bounce
- **On** Web: uses overflow-y: auto

## Acceptance Criteria
- [ ] All atoms are self-contained, no store dependency
- [ ] Molecules compose atoms only
- [ ] Organisms connect to Zustand store
- [ ] Screen composes organisms into full layout
- [ ] StyleSheet works on all platforms (no web-only CSS)
- [ ] Platform.select used for scroll/keyboard differences
- [ ] Expo Router navigation works (tab-based)
- [ ] 3D canvas re-renders only when analyses or settings change (useMemo)
