# Wall Modeler Specification

## Purpose

The Wall Modeler is a Compose-native 3D geometric block editor that parses a custom wall syntax, computes 3D block geometries, and renders them via SceneView with a CAD aesthetic (white fill + black wireframe, orbit controls). It replaces the default Greeting screen in the AmigurumiMaker scaffold.

## Requirements

### FR1: Syntax Parsing

The system **MUST** tokenize a custom wall syntax string into structured `Token` models.

**Token grammar** (BNF-style):
```
input     := segment (',' segment)*
segment   := rowSpec token+
rowSpec   := digit+ ')'           (single row, e.g. "1)")
           | digit+ '-' digit+ ')'  (row range, e.g. "1-3)")
token     := digit+ type
type      := 'c'  (cube)
           | 'd'  (diagonal ramp)
           | 'p'  (placeholder, invisible)
```

#### Scenario: Parse simple row with cubes

- GIVEN the input `"1) 10c"`
- WHEN the `WallTokenizer` parses the string
- THEN it returns a list with one `Token(CUBE, 10)` for row 1

#### Scenario: Parse row with mixed tokens and placeholders

- GIVEN the input `"1) 1d 8p 1d (8p)"`
- WHEN the `WallTokenizer` parses the string
- THEN it returns `[Token(RAMP_LEFT, 1), Token(PLACEHOLDER, 8), Token(RAMP_RIGHT, 1)]`

#### Scenario: Parse row range with expansion

- GIVEN the input `"1-3) 10c"`
- WHEN the `RowExpander` processes the parsed tokens
- THEN it produces 3 `RowDef` entries (rows 1, 2, and 3), each with `Token(CUBE, 10)`

#### Scenario: Parse invalid syntax produces error

- GIVEN the input `"1) 10x"` (invalid token type `x`)
- WHEN the `WallTokenizer` parses the string
- THEN it returns a parse error indicating unrecognized token type at position

#### Scenario: Parse empty input

- GIVEN the input `""`
- WHEN the `WallTokenizer` parses the string
- THEN it returns a parse error for empty input

#### Scenario: Parse malformed row spec

- GIVEN the input `"abc) 5c"`
- WHEN the `WallTokenizer` parses the string
- THEN it returns a parse error for invalid row specifier

---

### FR2: Geometry Computation

The system **MUST** compute 3D block geometry (vertices, wireframe edges, face indices) for each token type.

**Block types**:
- **CUBE**: Standard cube, axis-aligned, 1×1×1 unit
- **RAMP_LEFT**: Right triangular prism — diagonal face slopes left-to-right (upper-left vertex pulled to center)
- **RAMP_RIGHT**: Right triangular prism — diagonal face slopes right-to-left (upper-right vertex pulled to center)
- **PLACEHOLDER**: No geometry rendered (invisible gap)

Each block occupies 1 unit width × 1 unit height × 1 unit depth. Blocks are positioned at `(col, row, 0)` where `col` is the cumulative position within the row and `row` is the vertical layer (Y-axis up).

#### Scenario: Cube has 8 vertices and 12 edges

- GIVEN a `GeometryComputer` with a CUBE token at position (0, 0, 0)
- WHEN geometry is computed
- THEN the `BlockGeometry` contains exactly 8 vertices forming a unit cube
- AND exactly 12 wireframe edges connecting adjacent vertices

#### Scenario: Ramp_LEFT has correct diagonal face

- GIVEN a `GeometryComputer` with a RAMP_LEFT token at position (0, 1, 0)
- WHEN geometry is computed
- THEN the upper-left-front vertex and upper-left-back vertex are positioned at `x = 0.5, y = 1.0, z = ...` instead of `x = 0.0, y = 1.0`
- AND the block has 6 vertices (triangular prism, not a full cube)

#### Scenario: RAMP_RIGHT has correct diagonal face

- GIVEN a `GeometryComputer` with a RAMP_RIGHT token at position (0, 1, 0)
- WHEN geometry is computed
- THEN the upper-right-front vertex and upper-right-back vertex are positioned at `x = 0.5, y = 1.0` instead of `x = 1.0, y = 1.0`

#### Scenario: Placeholder produces nil geometry

- GIVEN a `GeometryComputer` with a PLACEHOLDER token
- WHEN geometry is computed
- THEN the result is empty (no vertices, no edges, no faces)

---

### FR3: 3D Rendering via SceneView

The system **MUST** render computed block geometries in a SceneView viewport with:
- Opaque white fill (CAD aesthetic) via unlit materials
- Black wireframe edges visible on top of fill
- Camera auto-centered on the wall bounding box after each parse

#### Scenario: Blocks render with fill and wireframe

- GIVEN a list of `BlockGeometry` objects from a valid parse
- WHEN the `GeometryViewport` receives them and renders via SceneView
- THEN each block appears with a white opaque surface
- AND each block has visible black edge lines

#### Scenario: Empty geometry clears the viewport

- GIVEN an empty geometry list (no blocks to render)
- WHEN the `GeometryViewport` renders
- THEN the viewport shows an empty 3D scene with no rendered blocks

---

### FR4: Orbit Controls

The system **MUST** provide touch-based orbit controls via SceneView's `CameraManipulator`:
- **Rotate**: Single-finger drag rotates the camera around the scene center
- **Zoom**: Pinch gesture or scroll zooms in/out
- **Auto-center**: Camera auto-orients to frame the full wall after each rebuild

#### Scenario: Drag rotates the camera

- GIVEN a rendered wall with 10 cubes
- WHEN the user drags left on the viewport
- THEN the camera rotates to reveal the left side of the wall

#### Scenario: Pinch zoom changes view distance

- GIVEN a rendered wall
- WHEN the user performs a pinch-to-zoom gesture
- THEN the camera distance from the scene center changes proportionally

---

### FR5: MVI State Management

The system **MUST** manage UI state via MVI:
- `WallModelerIntent`: sealed interface with `ParseSyntax(text: String)`, `Draw`, `Reset`
- `WallModelerState`: data class with `syntaxText: String`, `rows: List<RowDef>`, `geometries: List<BlockGeometry>`, `isLoading: Boolean`, `error: String?`
- `WallModelerEffect`: sealed interface with `ShowError(message: String)`, `CenterCamera`
- `WallModelerViewModel`: extends `ViewModel`, exposes `StateFlow<WallModelerState>` and `SharedFlow<WallModelerEffect>`, processes intents via `reduce(intent: WallModelerIntent)`

#### Scenario: ParseSyntax intent produces row data

- GIVEN the ViewModel in initial state
- WHEN `WallModelerIntent.ParseSyntax("1) 10c")` is dispatched
- THEN state updates with `isLoading = false`, `error = null`, and `rows` containing one `RowDef` with 10 CUBE tokens
- AND `geometries` contains 10 `BlockGeometry` entries

#### Scenario: Invalid syntax shows error

- GIVEN the ViewModel in initial state
- WHEN `WallModelerIntent.ParseSyntax("1) xyz")` is dispatched
- THEN state updates with `error` set to a non-null error message
- AND a `ShowError` effect is emitted

#### Scenario: Reset clears state

- GIVEN the ViewModel with parsed data and geometries
- WHEN `WallModelerIntent.Reset` is dispatched
- THEN state returns to defaults (`syntaxText = ""`, `rows = emptyList()`, `geometries = emptyList()`, `error = null`)

---

### FR6: UI Components (Atomic Design)

The system **MUST** implement the following composable hierarchy:

| Level | Component | Responsibility |
|-------|-----------|---------------|
| **Atom** | `SyntaxInputField` | Outlined text field for wall syntax input, single-line or multi-line |
| **Atom** | `AppButton` | Material3 Button with Text label, reusable |
| **Atom** | `AppHeaderTitle` | Typography headline for section headers |
| **Molecule** | `SyntaxInputPanel` | Groups SyntaxInputField + AppButton ("Build") with layout + label |
| **Molecule** | `HeaderBar` | AppHeaderTitle + optional app icon/back button |
| **Organism** | `GeometryViewport` | Wraps SceneView with CameraManipulator, receives geometry list |
| **Template** | `WallModelerTemplate` | Arranges HeaderBar, SyntaxInputPanel, GeometryViewport in a column layout |
| **Page** | `WallModelerPage` | Connects ViewModel state to WallModelerTemplate; handles effects (ShowError → Snackbar, CenterCamera → camera callback) |

#### Scenario: Syntax entry triggers parse on Build tap

- GIVEN the user has typed `"1) 5c 5c"` into SyntaxInputField
- WHEN the user taps the "Build" AppButton
- THEN `WallModelerIntent.ParseSyntax("1) 5c 5c")` is dispatched to the ViewModel

#### Scenario: Error state shows Snackbar

- GIVEN an invalid syntax has been entered
- WHEN `WallModelerEffect.ShowError("Error: invalid token")` is emitted
- THEN a Snackbar appears at the bottom of `WallModelerPage` displaying the error message

---

### FR7: CAD Theme Palette

The system **MUST** extend `AmigurumiMakerTheme` with CAD-specific colors:

| Role | Light | Dark | Usage |
|------|-------|------|-------|
| `cadSurface` | `#ECEFF1` (Blue Grey 50) | `#37474F` (Blue Grey 800) | Viewport background |
| `cadFill` | `#FAFAFA` (Grey 50) | `#BDBDBD` (Grey 400) | Block fill color |
| `cadWireframe` | `#212121` (Grey 900) | `#EEEEEE` (Grey 200) | Block edge lines |
| `cadGrid` | `#BDBDBD` (Grey 400) | `#616161` (Grey 700) | Ground grid lines |

#### Scenario: Page renders with CAD theme tokens

- GIVEN the app is using `AmigurumiMakerTheme`
- WHEN `WallModelerPage` renders
- THEN the 3D viewport background uses `cadSurface` from the current color scheme
- AND rendered blocks use `cadFill` for surfaces and `cadWireframe` for edges

---

## Non-Functional Requirements

### NFR1: Domain Purity

The `domain` package (models + `WallTokenizer` + `RowExpander` + `GeometryComputer`) **MUST** compile as pure Kotlin with zero Android, Compose, or SceneView dependencies. It **MUST NOT** import `android.*`, `androidx.*`, or any platform-specific packages.

### NFR2: Test Coverage

Unit tests **MUST** achieve >80% line coverage for the `domain` package, verified by JaCoCo or Kotlin test coverage tool. At minimum:
- `WallTokenizer`: all valid token types, invalid types, empty input, mixed segments
- `RowExpander`: single rows, range expansion, overflow (>99 rows)
- `GeometryComputer`: all three block types, edge count, vertex positions for ramps

### NFR3: No Experimental APIs

All Compose and SceneView APIs used **MUST** be stable (not annotated `@ExperimentalMaterial3Api`, `@ExperimentalComposeUiApi`, or similar). If a stable alternative exists, it **SHALL** be preferred.

### NFR4: Unlit CAD Materials

SceneView rendering **MUST** use `createUnlitColorInstance(Color)` for block surfaces to achieve flat, non-shaded CAD appearance. No dynamic lighting or shadow projections.

### NFR5: Build Requirement

`./gradlew assembleDebug` **MUST** succeed with no compilation or resource errors after all changes are applied.

---

## Architecture Constraints

### Package Structure

```
com.example.amigurumimaker/
├── domain/
│   ├── model/
│   │   ├── TokenType.kt          (enum: CUBE, RAMP_LEFT, RAMP_RIGHT, PLACEHOLDER)
│   │   ├── Token.kt              (data class: type: TokenType, count: Int)
│   │   ├── RowDef.kt             (data class: startRow, endRow: Int, tokens: List<Token>)
│   │   ├── BlockGeometry.kt      (data class: vertices: List<Float3>, edges: List<IntPair>, faceIndices: List<IntTriple>)
│   │   └── ParseResult.kt        (sealed: Success/Error)
│   ├── WallTokenizer.kt          (object/class: fun parse(input: String): ParseResult)
│   ├── RowExpander.kt            (object/class: fun expand(rows: List<RowDef>): List<RowDef>)
│   └── GeometryComputer.kt       (object/class: fun compute(rows: List<RowDef>): List<BlockGeometry>)
│
├── presentation/
│   └── wallmodeler/
│       ├── WallModelerIntent.kt   (sealed interface)
│       ├── WallModelerState.kt    (data class)
│       ├── WallModelerEffect.kt   (sealed interface)
│       ├── WallModelerViewModel.kt (extends ViewModel)
│       ├── ui/
│       │   ├── atoms/
│       │   │   ├── SyntaxInputField.kt
│       │   │   ├── AppButton.kt
│       │   │   └── AppHeaderTitle.kt
│       │   ├── molecules/
│       │   │   ├── SyntaxInputPanel.kt
│       │   │   └── HeaderBar.kt
│       │   ├── organisms/
│       │   │   └── GeometryViewport.kt
│       │   ├── templates/
│       │   │   └── WallModelerTemplate.kt
│       │   └── pages/
│       │       └── WallModelerPage.kt
│       └── theme/
│           └── CadColor.kt        (CAD color palette tokens)
```

### Data Flow

```
User types syntax → SyntaxInputField → AppButton tap →
  WallModelerIntent.ParseSyntax(text) →
    WallModelerViewModel.process() →
      WallTokenizer.parse() → RowExpander.expand() → GeometryComputer.compute() →
        WallModelerState updated →
          SyntaxInputPanel (shows parsed rows count)
          GeometryViewport (SceneView) → renders BlockGeometries with CAD materials
          Effect.ShowError → Snackbar (on error)
```

---

## Version Catalog Updates

### Modified Entry

| Catalog Key | Current | New |
|---|---|---|
| `kotlin` | `"2.2.10"` | `"2.4.0"` |

### Added Entry

```toml
[versions]
sceneview = "4.23.0"

[libraries]
sceneview = { group = "io.github.sceneview", name = "sceneview", version.ref = "sceneview" }
```

### Build Dependency (app/build.gradle.kts)

```kotlin
implementation(libs.sceneview)
```

### Repository (settings.gradle.kts)

No additional repositories required — SceneView is published to Maven Central, which is already configured.

---

## Acceptance Criteria

- [ ] AC1: Syntax parser tokenizes `10c`, `1d 8p 1d`, `1-3) 10c` correctly into `Token` models
- [ ] AC2: Invalid syntax (`xyz`, `10x`, empty string) produces a parse error with descriptive message
- [ ] AC3: Row range `1-3)` expands to three individual row definitions
- [ ] AC4: `GeometryComputer` produces correct vertex counts: cube (8), ramp LEFT (6), ramp RIGHT (6), placeholder (none)
- [ ] AC5: SceneView renders white-filled blocks with visible black wireframe edges
- [ ] AC6: Camera auto-centers on the wall bounding box after parse
- [ ] AC7: Touch drag rotates the viewport, pinch zoom changes camera distance
- [ ] AC8: `WallModelerViewModel.process()` updates state correctly for ParseSyntax and Reset intents
- [ ] AC9: `WallModelerPage` shows Snackbar on error effects from ViewModel
- [ ] AC10: Domain layer has zero Android imports and compiles as pure Kotlin
- [ ] AC11: Unit test coverage >80% for `domain` package
- [ ] AC12: `./gradlew assembleDebug` succeeds
- [ ] AC13: App displays `WallModelerPage` instead of the default `Greeting` composable
