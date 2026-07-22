# Design: Geometric Crochet Mesh Simulator

## Technical Approach

New recursive-descent parser + AST pipeline in `domain.crochet`, driving a trapezoid-capable `GeometryComputer` that preserves backward compatibility. New "Crochet Studio" screen in `presentation.crochetstudio` follows the exact MVI + Atomic Design pattern from `WallModelerPage`. Feature flag `useNewCrochetParser` guards the pipeline switch. 7 incremental phases, each independently testable via JUnit 4.

## Architecture Decisions

### Decision: New `domain.crochet` package (separate from existing domain)

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Extend `WallTokenizer` for crochet grammar | Regex-based — can't handle nested groups or [N]v syntax | Rejected |
| New package `domain.crochet` | Parallel domain that won't touch existing wall modeler | **Adopted** — avoids `c`=CUBE vs `c`=Chain collision; both pipelines coexist |

### Decision: Sealed AST hierarchy over visitor-only pattern

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Visitor-only (no sealed nodes) | Type safety lost, casting everywhere | Rejected |
| Sealed `AstNode` with `RowNode`, `StitchNode`, `RepeatGroupNode`, `CompoundStitchNode` | Compiler-enforced exhaustiveness, easy expansion | **Adopted** — follows Kotlin idioms, match statements guarantee coverage |

### Decision: `GeometryComputer.compute()` overloaded — not replaced

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Replace signature with `List<StitchBlock>` | Breaks WallModelerPage contract | Rejected |
| Add `fun compute(stitchBlocks: List<StitchBlock>): List<BlockGeometry>` | Both pipelines work; old signature untouched | **Adopted** — zero risk to existing wall modeler |

### Decision: Trapezoid via `baseBottom`/`baseTop` fields on `BlockGeometry`

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Separate `TrapezoidGeometry` class | Duplicate rendering logic in GeometryViewport | Rejected |
| Add `baseBottom`, `baseTop` optional fields to `BlockGeometry` | Viewport reads fields to decide MeshNode (cuboid vs custom buffer) vs CubeNode | **Adopted** — minimal diff, cheap pattern match in viewport |

### Decision: Crochet Studio as `BottomNavItem` route in `MainActivity`

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Replace WallModelerPage entirely | Too risky for existing users | Rejected |
| Bottom navigation with two tabs (Wall Modeler / Crochet Studio) | Both pages accessible, clean separation | **Adopted** — `mainActivityNavigation` composable switches on selected tab |

## Data Flow

```
┌──────────────────────────────────────────────────────────────────────┐
│ CrochetStudioPage (Page)                                             │
│  patternInput ──→ CrochetStudioIntent.ParsePattern(text)             │
└────────────────────────────────┬─────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────────┐
│ CrochetStudioViewModel                                               │
│  process(intent) {                                                   │
│    val tokens = CrochetLexer.tokenize(text)                          │
│    val ast    = CrochetParser.parse(tokens)                          │
│    val flat   = RepeatExpander.expand(ast)                           │
│    val errors = RowValidator.validate(flat, stitchRegistry)           │
│    val blocks = SpatialEngine.track(flat, stitchRegistry)             │
│    val meshes = GeometryComputer.compute(blocks)                     │
│    _state.update { copy(geometries=meshes, errors=errors) }          │
│  }                                                                    │
└────────────────────────────────┬─────────────────────────────────────┘
                                 │
                    ┌────────────┴───────────┐
                    ▼                        ▼
           StateFlow<State>          SharedFlow<Effect>
                    │                        │
                    ▼                        ▼
┌──────────────────────────────────┐ ┌──────────────────┐
│ CrochetStudioTemplate            │ │ CrochetStudioPage│
│  PatternInputPanel               │ │  ShowError →     │
│  ValidationPanel (errors/warns)  │ │  Snackbar        │
│  GeometryViewport (reused)       │ │                  │
└──────────────────────────────────┘ └──────────────────┘
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/crochet/model/AstNode.kt` | Create | Sealed hierarchy: PatternNode, RowNode, StitchNode, RepeatGroupNode, CompoundStitchNode |
| `domain/crochet/model/StitchDef.kt` | Create | Data class: abbreviation, displayName, baseBottom, baseTop, height, consumesStitches, producesStitches |
| `domain/crochet/model/StitchBlock.kt` | Create | Data class: row, posInRow, baseBottom, baseTop, height, stitchAbbr |
| `domain/crochet/model/CrochetToken.kt` | Create | Crochet-specific token types (NUMBER, ABBREVIATION, ROW_MARKER, BRACKET_OPEN/CLOSE, REPEAT_MARKER, LPAREN/RPAREN) |
| `domain/crochet/model/ParseError.kt` | Create | Sealed: LexerError, ParserError, ValidationError with rowNumber/actual/expected/message |
| `domain/crochet/StitchDictionary.kt` | Create | Object with MVP registry (ch, sl st, sc, hdc, dc, inc, dec), lookup function |
| `domain/crochet/CrochetLexer.kt` | Create | Tokenizer returning List<CrochetToken> or LexerError |
| `domain/crochet/CrochetParser.kt` | Create | Recursive descent: parse tokens → AstNode |
| `domain/crochet/RepeatExpander.kt` | Create | Flattens RepeatGroupNode into sequential StitchNode list |
| `domain/crochet/RowValidator.kt` | Create | Validates declared vs actual count per row using StitchDictionary |
| `domain/crochet/SpatialEngine.kt` | Create | Computes StitchBlock adjacency, min/max width per row |
| `domain/crochet/GeometryMapper.kt` | Create | Converts StitchBlock → trapezoid BlockGeometry with baseTop/baseBottom |
| `domain/model/BlockGeometry.kt` | Modify | Add optional baseBottom, baseTop fields |
| `domain/GeometryComputer.kt` | Modify | Add `compute(blocks: List<StitchBlock>)` overload |
| `presentation/crochetstudio/CrochetStudioIntent.kt` | Create | Sealed interface: ParsePattern, Reset |
| `presentation/crochetstudio/CrochetStudioState.kt` | Create | patternText, astRows, geometries, validationErrors, isLoading |
| `presentation/crochetstudio/CrochetStudioEffect.kt` | Create | Sealed: ShowError, CenterCamera |
| `presentation/crochetstudio/CrochetStudioViewModel.kt` | Create | MVI ViewModel: process → pipeline → state update |
| `presentation/crochetstudio/ui/atoms/PatternInputField.kt` | Create | Multi-line text field for pattern input |
| `presentation/crochetstudio/ui/molecules/PatternInputPanel.kt` | Create | Input field + "Render" button |
| `presentation/crochetstudio/ui/molecules/ValidationPanel.kt` | Create | Error/warning list per row with color coding |
| `presentation/crochetstudio/ui/organisms/CrochetViewport.kt` | Create | Reuses GeometryViewport pattern; handles trapezoid MeshNode |
| `presentation/crochetstudio/ui/templates/CrochetStudioTemplate.kt` | Create | Split layout: input panel + viewport + validation panel |
| `presentation/crochetstudio/ui/pages/CrochetStudioPage.kt` | Create | ViewModel wiring, effect handling |
| `presentation/crochetstudio/theme/CrochetColor.kt` | Create | Crochet-specific color tokens (error red, warning amber) |
| `MainActivity.kt` | Modify | Add BottomNavItem for Crochet Studio |
| `openspec/specs/crochet-studio/spec.md` | Create | Main spec for crochet studio capability |

## Interfaces / Contracts

### Domain — AST Nodes (sealed hierarchy)

```kotlin
sealed interface AstNode {
    data class PatternNode(val rows: List<RowNode>) : AstNode
    data class RowNode(val rowNumbers: IntRange, val stitches: List<StitchNode>,
                       val declaredCount: Int? = null) : AstNode
    data class StitchNode(val abbreviation: String, val count: Int = 1,
                          val modifier: String? = null) : AstNode
    data class RepeatGroupNode(val sequence: List<AstNode>, val repeats: Int) : AstNode
    data class CompoundStitchNode(val type: CompoundType, val repeats: Int) : AstNode
}
enum class CompoundType { V_STITCH, PUFF, POPCORN, FAN }
```

### Domain — Stitch Block (SpatialEngine output)

```kotlin
data class StitchBlock(
    val row: Int, val positionInRow: Int,
    val baseBottomWidth: Float, val baseTopWidth: Float,
    val height: Float, val abbreviation: String
)
```

### Domain — Pipeline objects

```kotlin
object StitchDictionary {
    fun lookup(abbr: String): StitchDef?  // null if unknown
}
object CrochetLexer {
    fun tokenize(input: String): Result<List<CrochetToken>>  // Result.success or Result.failure(LexerError)
}
object CrochetParser {
    fun parse(tokens: List<CrochetToken>): Result<AstNode.PatternNode>
}
```

### Presentation — MVI (follows WallModeler pattern exactly)

```kotlin
sealed interface CrochetStudioIntent {
    data class ParsePattern(val text: String) : CrochetStudioIntent
    data object Reset : CrochetStudioIntent
}
data class CrochetStudioState(
    val patternText: String = "",
    val rows: List<AstNode.RowNode> = emptyList(),
    val geometries: List<BlockGeometry> = emptyList(),
    val validationErrors: List<ParseError> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Domain — CrochetLexer | Valid/invalid abbreviations, row markers, bracket syntax, edge cases | JUnit 4, parametrized, 100% pure Kotlin |
| Domain — CrochetParser | Simple rows, sequences, range notation, repeat groups, nested groups, declared counts | JUnit 4, assert AST tree structure |
| Domain — RepeatExpander | Flat expansion, nested expansion, modifier preservation | JUnit 4, verify stitch ordering and count |
| Domain — RowValidator | Matching counts, mismatches, empty rows, missing declared counts | JUnit 4, assert ParseError types |
| Domain — StitchDictionary | Lookup success/failure, inc/dec geometry properties | JUnit 4 |
| Domain — SpatialEngine | 1-to-1 adjacency, inc (1→2), dec (2→1), width extrema | JUnit 4, verify StitchBlock fields |
| Domain — GeometryMapper | Cube (1:1), trapezoid (dec), inverted-trapezoid (inc), compound geometry vertex counts | JUnit 4, epsilon-comparison of vertex positions |
| Domain — GeometryComputer (modified) | Existing tests still pass; new overload produces correct BlockGeometry | JUnit 4, backward compat verified |
| Presentation — CrochetStudioViewModel | ParsePattern → state update; invalid → error state + effect; Reset → initial state | ViewModelTest + kotlinx-coroutines-test + Turbine |

## Migration / Rollout

No migration required — greenfield feature. Feature flag `useNewCrochetParser` (boolean in CrochetStudioViewModel init) enables A/B toggle during development. Wall modeler is completely unaffected.

## Open Questions

- [ ] Compound stitch geometry vertex generation (V-stitch, puff, popcorn, fan) — deferred to Phase 6 design refinement
- [ ] SceneView `CubeNode` vs custom `MeshNode` for trapezoid rendering — need to verify custom vertex buffer handles non-uniform cuboids correctly (existing ramp code confirms custom MeshNode works)
