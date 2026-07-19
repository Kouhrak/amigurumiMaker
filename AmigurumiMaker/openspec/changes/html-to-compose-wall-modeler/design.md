# Design: Convierte HTML a Jetpack Compose — Modelador Geométrico 3D

## Technical Approach

Replace the scaffold's Greeting screen with a Compose-native 3D block editor. A pure-Kotlin domain layer parses a custom wall syntax into tokens, expands row ranges, and computes block geometries (cubes, ramps). A Presentation layer with MVI drives a SceneView viewport that renders blocks with CAD-style white fill + black wireframe and orbit controls. This is a single-screen feature with deferred data layer — all state lives in the ViewModel.

## Architecture Decisions

### Decision: Domain as pure Kotlin module (no Android deps)

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Domain in `:app` with Android package restriction | Simple module structure, must enforce no `android.*` imports via code review | **Adopted** — NFR1 mandates pure Kotlin; we'll use a package-level convention and verify via test compilation on a JVM-only test source set |
| Separate `:domain` Gradle module | Cleaner boundary, but over-engineering for a single-screen v1 | Rejected — adds build complexity without benefit |

### Decision: Sealed class for ParseResult (Success/Error) over exceptions

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Throws exceptions on parse error | Forces try-catch in ViewModel, breaks StateFlow purity | Rejected |
| Sealed class with Success/Error | Explicit error handling, no surprises | **Adopted** — aligns with MVI where errors flow into State |
| Result\<List\<RowDef\>\> | Kotlin stdlib type, but no named fields for error messaging | Rejected — sealed class gives `message: String` on error |

### Decision: GeometryComputer produces vertices/edges/faces as flat lists

| Option | Tradeoff | Decision |
|--------|----------|----------|
| SceneNode tree (CubeNode, MeshNode) | Couples domain to SceneView types | Rejected — violates NFR1 |
| Float3/IntPair value objects | Pure data, zero platform deps, ViewModel converts to SceneView nodes | **Adopted** — domain returns `BlockGeometry` with vertex/edge/face lists; organisms layer does the SceneView mapping |

### Decision: Camera auto-fit via bounding box computation

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Fixed camera position | Works for small walls, breaks for large/single-block | Rejected |
| Compute axis-aligned bounding box from all vertices, then set camera distance | Works for any wall size, pure math | **Adopted** — compute in ViewModel or composable side-effect after geometry update |

### Decision: Snackbar via Effect.SharedFlow

| Option | Tradeoff | Decision |
|--------|----------|----------|
| State.error shown in Snackbar via LaunchedEffect | Snackbar dismisses when error clears; loses timing control | Rejected |
| SharedFlow\<Effect\> with SnackbarHostState | One-shot event survives state clearing | **Adopted** — MVI Effect pattern, one emission per error |

## Data Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│ User taps "Build"                                                   │
│   SyntaxInputField.currentText → WallModelerIntent.ParseSyntax(t)   │
└─────────────────────────┬───────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│ WallModelerViewModel                                                │
│   process(intent) in viewModelScope.launch {                        │
│     _state.update { it.copy(isLoading = true) }                     │
│     when (val result = WallTokenizer.parse(text)) {                 │
│       is ParseResult.Success -> {                                   │
│         val expanded = RowExpander.expand(result.rows)              │
│         val geometries = GeometryComputer.compute(expanded)         │
│         _state.update { it.copy(rows=expanded, geometries=geometries│
│                                isLoading=false, error=null) }       │
│         _effect.emit(Effect.CenterCamera)                           │
│       }                                                             │
│       is ParseResult.Error ->                                       │
│         _state.update { it.copy(isLoading=false, error=r.message) } │
│         _effect.emit(Effect.ShowError(r.message))                   │
│     }                                                               │
│   }                                                                 │
└─────────────────────────┬───────────────────────────────────────────┘
                          │
                    ┌─────┴───────────┐
                    ▼                 ▼
           StateFlow<State>    SharedFlow<Effect>
                    │                 │
                    ▼                 ▼
┌──────────────────────────────┐  ┌──────────────────┐
│ WallModelerTemplate          │  │ WallModelerPage  │
│   rows→ SyntaxInputPanel     │  │   ShowError →    │
│   geometries→ GeometryView‑  │  │   Snackbar       │
│   port (SceneView render)    │  │   CenterCamera → │
│                              │  │   camera.fit()   │
└──────────────────────────────┘  └──────────────────┘
```

### Component Tree (Atomic Design)

```
WallModelerPage                          ← Page: connects ViewModel
  └─ WallModelerTemplate                 ← Template: layout shell
       ├─ HeaderBar                      ← Molecule
       │    ├─ AppHeaderTitle            ← Atom
       │    └─ <optional back/icon>
       ├─ SyntaxInputPanel               ← Molecule
       │    ├─ SyntaxInputField           ← Atom: OutlinedTextField
       │    └─ AppButton ("Build")        ← Atom: Button + Text
       └─ GeometryViewport               ← Organism: SceneView node
            └─ SceneView (fill + wireframe nodes)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `gradle/libs.versions.toml` | Modify | Bump Kotlin 2.2.10→2.4.0, add sceneview 4.23.0 |
| `app/build.gradle.kts` | Modify | Add `implementation(libs.sceneview)` + `testImplementation(libs.kotlin.test)` |
| `domain/model/TokenType.kt` | Create | Enum: CUBE, RAMP_LEFT, RAMP_RIGHT, PLACEHOLDER |
| `domain/model/Token.kt` | Create | Data class: type, count |
| `domain/model/RowDef.kt` | Create | Data class: startRow, endRow, tokens |
| `domain/model/BlockGeometry.kt` | Create | Data class: vertices, edges, faceIndices |
| `domain/model/ParseResult.kt` | Create | Sealed class: Success / Error |
| `domain/WallTokenizer.kt` | Create | Regex-based parser, returns ParseResult |
| `domain/RowExpander.kt` | Create | Expands row ranges into RowDef list |
| `domain/GeometryComputer.kt` | Create | Computes vertices/edges from RowDef |
| `presentation/wallmodeler/WallModelerIntent.kt` | Create | Sealed interface: ParseSyntax, Reset |
| `presentation/wallmodeler/WallModelerState.kt` | Create | Data class for MVI state |
| `presentation/wallmodeler/WallModelerEffect.kt` | Create | Sealed interface: ShowError, CenterCamera |
| `presentation/wallmodeler/WallModelerViewModel.kt` | Create | ViewModel with process(), state/effect flows |
| `presentation/wallmodeler/ui/atoms/SyntaxInputField.kt` | Create | OutlinedTextField composable |
| `presentation/wallmodeler/ui/atoms/AppButton.kt` | Create | Material3 Button with label |
| `presentation/wallmodeler/ui/atoms/AppHeaderTitle.kt` | Create | Headline typography text |
| `presentation/wallmodeler/ui/molecules/SyntaxInputPanel.kt` | Create | Groups field + build button |
| `presentation/wallmodeler/ui/molecules/HeaderBar.kt` | Create | Title bar layout |
| `presentation/wallmodeler/ui/organisms/GeometryViewport.kt` | Create | SceneView + nodes from geometries |
| `presentation/wallmodeler/ui/templates/WallModelerTemplate.kt` | Create | Column layout of components |
| `presentation/wallmodeler/ui/pages/WallModelerPage.kt` | Create | ViewModel hookup + effect handling |
| `presentation/wallmodeler/theme/CadColor.kt` | Create | CAD color palette object |
| `MainActivity.kt` | Modify | Replace Greeting → WallModelerPage |
| `domain/...` (tests) | Create | Unit tests for domain classes |
| `presentation/wallmodeler/...` (tests) | Create | ViewModel unit tests |

## Interfaces / Contracts

### Domain Layer — Pure Kotlin (NFR1: zero Android imports)

```kotlin
// domain/model/TokenType.kt
enum class TokenType { CUBE, RAMP_LEFT, RAMP_RIGHT, PLACEHOLDER }

// domain/model/Token.kt
data class Token(val type: TokenType, val count: Int)

// domain/model/RowDef.kt
data class RowDef(val startRow: Int, val endRow: Int, val tokens: List<Token>)

// domain/model/BlockGeometry.kt
data class Float3(val x: Float, val y: Float, val z: Float)
data class IntPair(val first: Int, val second: Int)
data class IntTriple(val first: Int, val second: Int, val third: Int)
data class BlockGeometry(
    val vertices: List<Float3>,       // 8 for cube, 6 for ramp
    val edges: List<IntPair>,         // 12 for cube
    val faceIndices: List<IntTriple>  // triangles for fill rendering
)

// domain/model/ParseResult.kt
sealed class ParseResult {
    data class Success(val rows: List<RowDef>) : ParseResult()
    data class Error(val message: String) : ParseResult()
}

// domain/WallTokenizer.kt — Regex-based
object WallTokenizer {
    // Grammar: input := segment (',' segment)*
    // segment := rowSpec token+
    // rowSpec := digit+ ')' | digit+ '-' digit+ ')'
    // token := digit+ ('c' | 'd' | 'p')
    fun parse(input: String): ParseResult
}

// domain/RowExpander.kt
object RowExpander {
    fun expand(rows: List<RowDef>): List<RowDef>
    // For each RowDef where startRow != endRow, produce N individual RowDefs
}

// domain/GeometryComputer.kt
object GeometryComputer {
    fun compute(rows: List<RowDef>): List<BlockGeometry>
    // Block positioning: col = cumulative token offset, row = row number
    // CUBE: 8 vertices, 12 edges, 12 face indices (2 tris × 6 faces)
    // RAMP_LEFT: 6 vertices (triangular prism), 9 edges, 8 face indices (2 tris × 3 rect faces + 1 tri × 2 tri faces... need to verify)
    // RAMP_RIGHT: 6 vertices (triangular prism), 9 edges
    // PLACEHOLDER: empty BlockGeometry (no rendering)
}
```

### Presentation Layer — MVI Contracts

```kotlin
// WallModelerIntent.kt
sealed interface WallModelerIntent {
    data class ParseSyntax(val text: String) : WallModelerIntent
    data object Reset : WallModelerIntent
}

// WallModelerState.kt
data class WallModelerState(
    val syntaxText: String = "",
    val rows: List<RowDef> = emptyList(),
    val geometries: List<BlockGeometry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val parsedBlockCount: Int = 0
)

// WallModelerEffect.kt
sealed interface WallModelerEffect {
    data class ShowError(val message: String) : WallModelerEffect
    data object CenterCamera : WallModelerEffect
}

// WallModelerViewModel.kt
class WallModelerViewModel : ViewModel() {
    private val _state = MutableStateFlow(WallModelerState())
    val state: StateFlow<WallModelerState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WallModelerEffect>()
    val effect: SharedFlow<WallModelerEffect> = _effect.asSharedFlow()

    fun process(intent: WallModelerIntent) { /* launches coroutine, updates state */ }
}
```

### Composable Signatures

```kotlin
// Atoms
@Composable fun SyntaxInputField(
    text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier
)
@Composable fun AppButton(
    label: String, onClick: () -> Unit,
    enabled: Boolean = true, modifier: Modifier = Modifier
)
@Composable fun AppHeaderTitle(
    title: String, modifier: Modifier = Modifier
)

// Molecules
@Composable fun SyntaxInputPanel(
    text: String, onTextChange: (String) -> Unit, onBuildClick: () -> Unit,
    enabled: Boolean = true, modifier: Modifier = Modifier
)
@Composable fun HeaderBar(
    title: String, modifier: Modifier = Modifier
)

// Organism
@Composable fun GeometryViewport(
    geometries: List<BlockGeometry>,
    modifier: Modifier = Modifier
)

// Template
@Composable fun WallModelerTemplate(
    state: WallModelerState,
    onTextChange: (String) -> Unit,
    onBuildClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewport: @Composable (Modifier) -> Unit
)

// Page
@Composable fun WallModelerPage(
    viewModel: WallModelerViewModel = viewModel()
)
```

### SceneView Integration — Geometry to Node Mapping

```
BlockGeometry ──→ SceneView node tree (per block):

  CUBE:
    Fill:  CubeNode(size=Vec3(1f,1f,1f), center, createUnlitColorInstance(cadFill))
    Edges: LineNode from each IntPair → Vec3 position from vertices list

  RAMP_LEFT / RAMP_RIGHT:
    Fill:  MeshNode(
             primitiveType = PrimitiveType.TRIANGLES,
             vertexBuffer = vertices → FloatBuffer,
             indexBuffer  = faceIndices → ShortBuffer,
             materialInstance = createUnlitColorInstance(cadFill)
           )
    Edges: LineNode from each IntPair → Vec3 position

  PLACEHOLDER: No nodes added

  After all nodes: compute AABB via geometry vertices → cameraManipulator
    .setLookAt(center, furthestDistance * 1.5f)
```

Key SceneView types expected from v4.23.0:
- `io.github.sceneview.node.CubeNode`
- `io.github.sceneview.node.MeshNode`
- `io.github.sceneview.node.LineNode`
- `io.github.sceneview.loaders.MaterialLoader` → `createUnlitColorInstance(Color)`
- `io.github.sceneview.managers.CameraManipulator` → orbit controls + `setLookAt()`

## Theme Design

```kotlin
// presentation/wallmodeler/theme/CadColor.kt
package com.example.amigurumimaker.presentation.wallmodeler.theme

import androidx.compose.ui.graphics.Color

object CadColor {
    // Light mode
    val LightSurface   = Color(0xFFECEFF1)  // Blue Grey 50
    val LightFill      = Color(0xFFFAFAFA)  // Grey 50
    val LightWireframe = Color(0xFF212121)  // Grey 900
    val LightGrid      = Color(0xFFBDBDBD)  // Grey 400

    // Dark mode
    val DarkSurface   = Color(0xFF37474F)   // Blue Grey 800
    val DarkFill      = Color(0xFFBDBDBD)   // Grey 400
    val DarkWireframe = Color(0xFFEEEEEE)   // Grey 200
    val DarkGrid      = Color(0xFF616161)   // Grey 700
}
```

No changes to `Theme.kt` or `Color.kt`. The existing `AmigurumiMakerTheme` (MaterialTheme wrapper) is used as-is. CadColor is consumed directly where needed (viewport background, SceneView materials).

## Version Catalog Design

### gradle/libs.versions.toml — Changes

| Key | Before | After |
|-----|--------|-------|
| `kotlin` | `"2.2.10"` | `"2.4.0"` |
| *(new)* `sceneview` | — | `"4.23.0"` |

```toml
# New library entries
sceneview = { group = "io.github.sceneview", name = "sceneview", version.ref = "sceneview" }
kotlin-test = { group = "org.jetbrains.kotlin", name = "kotlin-test", version.ref = "kotlin" }
```

### app/build.gradle.kts — Changes

```kotlin
// Add to dependencies block (after existing deps):
    implementation(libs.sceneview)
    testImplementation(libs.kotlin.test)
```

Also the `kotlin` plugin version bump cascades: the `kotlin-compose` plugin reference in `[plugins]` uses `version.ref = "kotlin"`, so it picks up 2.4.0 automatically.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Domain — WallTokenizer | Valid syntax (all types), invalid types, empty input, malformed rowSpec, multiple segments, mixed tokens | JUnit 4 parameterized tests, pure Kotlin (no Android runner) |
| Domain — RowExpander | Single row passthrough, range expansion (1-3), single-row range (1-1), overflow bounds | JUnit 4, verify RowDef list count and properties |
| Domain — GeometryComputer | CUBE: 8 vertices, 12 edges, correct positions. RAMP_LEFT: 6 vertices, correct diagonal. RAMP_RIGHT: 6 vertices, correct diagonal. PLACEHOLDER: empty. Multi-block row positioning. | JUnit 4, verify vertex positions within epsilon (1e-6f) |
| Presentation — ViewModel | ParseSyntax → state update (rows, geometries), ParseSyntax invalid → error state + effect, Reset → initial state | ViewModel + kotlinx-coroutines-test, Turbine for StateFlow/SharedFlow assertions |
| Presentation — Compose | (Deferred to integration/UI tests) | Not in scope for v1 unit target |

### Domain coverage target: >80% line (NFR2)
All domain classes in pure Kotlin test source set, runnable via `./gradlew test` (JVM unit tests, no device needed).

## Implementation Order

### Phase 1: Setup (Tasks 1.1–1.5)
**Dependencies**: None. This is the foundation.

1.1 — Update version catalog: bump Kotlin, add sceneview + kotlin-test entries
1.2 — Create domain package directories and package-level `domain/package.md` (or empty marker files)
1.3 — Create presentation package directories
1.4 — Add `CadColor.kt` theme tokens
1.5 — Verify `./gradlew assembleDebug` passes

### Phase 2: Domain (Tasks 2.1–2.5)
**Depends on**: Phase 1 complete (build system ready)

2.1 — Create all domain model classes (TokenType, Token, RowDef, BlockGeometry, ParseResult)
2.2 — Implement `WallTokenizer.parse()` — regex-based tokenizer
2.3 — Implement `RowExpander.expand()` — range expansion
2.4 — Implement `GeometryComputer.compute()` — vertex math
2.5 — Write unit tests for all domain classes, verify coverage >80%

### Phase 3: Presentation (Tasks 3.1–3.9)
**Depends on**: Phase 2 complete (domain classes available)

3.1 — MVI contracts: Intent, State, Effect sealed classes
3.2 — WallModelerViewModel with process() + state/effect flows
3.3 — Atoms: SyntaxInputField, AppButton, AppHeaderTitle
3.4 — Molecules: SyntaxInputPanel, HeaderBar
3.5 — GeometryViewport: SceneView integration, node creation from BlockGeometry
3.6 — WallModelerTemplate: column layout combining molecules + viewport
3.7 — WallModelerPage: ViewModel injection, effect handling (Snackbar, camera center)
3.8 — Update MainActivity: replace Greeting with WallModelerPage
3.9 — ViewModel unit tests

### Phase 4: Data (Deferred)
Outlined only — future persistence for saved wall definitions (Room or DataStore).

### Phase 5: Polish (Deferred)
Outlined only — animations, error recovery, keyboard handling improvements.

## Open Questions

- [ ] SceneView v4.23.0 specific API surface: confirm `CubeNode`, `MeshNode`, `createUnlitColorInstance` signatures match v4.23.0 exactly. The spike confirmed compilation but actual API names may differ from expectation.
- [ ] Geometry.compute faceIndices for ramp prisms: need to confirm triangle winding order (counter-clockwise for SceneView backface culling). May need to verify with a test render.

## Architecture Diagram (Layered)

```
┌──────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION LAYER                            │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  WallModelerPage (Page)                                        │  │
│  │    ├── WallModelerTemplate (Template)                          │  │
│  │    │    ├── HeaderBar (Molecule)                               │  │
│  │    │    │    └── AppHeaderTitle (Atom)                         │  │
│  │    │    ├── SyntaxInputPanel (Molecule)                        │  │
│  │    │    │    ├── SyntaxInputField (Atom) — OutlinedTextField    │  │
│  │    │    │    └── AppButton (Atom) — "Build"                     │  │
│  │    │    └── GeometryViewport (Organism)                         │  │
│  │    │         └── SceneView + CameraManipulator                  │  │
│  │    └── SnackbarHost (effect → Snackbar)                         │  │
│  └───────────────────┬────────────────────────────────────────────┘  │
│                      │ StateFlow<State>                              │
│                      │ SharedFlow<Effect>                            │
│  ┌───────────────────▼────────────────────────────────────────────┐  │
│  │  WallModelerViewModel                                         │  │
│  │  - process(intent: WallModelerIntent)                          │  │
│  │  - state: StateFlow<WallModelerState>                          │  │
│  │  - effect: SharedFlow<WallModelerEffect>                       │  │
│  └───────────────────┬────────────────────────────────────────────┘  │
│                      │ calls domain objects                          │
├──────────────────────┼───────────────────────────────────────────────┤
│                      ▼                                               │
│                     DOMAIN LAYER (pure Kotlin — NFR1)                │
│  ┌────────────────────────────────────────────────────────────────┐  │
│  │  WallTokenizer.parse(String) → ParseResult                     │  │
│  │  RowExpander.expand(List<RowDef>) → List<RowDef>                │  │
│  │  GeometryComputer.compute(List<RowDef>) → List<BlockGeometry>   │  │
│  └────────────────────────────────────────────────────────────────┘  │
│                                                                      │
├──────────────────────────────────────────────────────────────────────┤
│                     DATA LAYER (Deferred — Phase 4)                  │
│                     (Room / DataStore — future)                      │
└──────────────────────────────────────────────────────────────────────┘
```
