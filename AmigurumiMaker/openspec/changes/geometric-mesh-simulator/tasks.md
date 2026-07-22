# Tasks: Geometric Crochet Mesh Simulator

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~1,100 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1: Infrastructure → PR 2: Semantics → PR 3: Geometry → PR 4: UI |
| Delivery strategy | ask-on-risk |
| Chain strategy | stacked-to-main |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | AST + Lexer + Parser + StitchDictionary | PR 1 | Foundation: ~280 lines, merges to main |
| 2 | RepeatExpander + RowValidator | PR 2 | Semantics: ~220 lines, merges to main |
| 3 | SpatialEngine + GeometryMapper + BlockGeometry mod + GeometryComputer overload | PR 3 | Geometry: ~200 lines, merges to main |
| 4 | Compound geometries + Crochet Studio UI + MainActivity routing | PR 4 | UI: ~400 lines, merges to main |

## Phase 1: AST Infrastructure + StitchDictionary

- [ ] 1.1 **RED** Write `domain/crochet/model/AstNodeTest.kt` — assert sealed hierarchy compiles, PatternNode holds RowNodes
- [ ] 1.2 **GREEN** Create `domain/crochet/model/AstNode.kt` — `AstNode` sealed interface with `PatternNode`, `RowNode`, `StitchNode`, `RepeatGroupNode`, `CompoundStitchNode`
- [ ] 1.3 **RED** Write `domain/crochet/lexer/CrochetTokenTest.kt` — tokenize valid stitch abbreviations and reject `"zz"`
- [ ] 1.4 **GREEN** Create `domain/crochet/lexer/TokenType.kt`, `CrochetToken.kt`, `CrochetLexer.kt` — tokenizer returning `Result<List<CrochetToken>>`
- [ ] 1.5 **RED** Write `domain/crochet/parser/CrochetParserTest.kt` — simple row, stitch sequence, range notation, malformed row
- [ ] 1.6 **GREEN** Create `domain/crochet/parser/CrochetParser.kt` — recursive descent parser consuming tokens → `PatternNode`
- [ ] 1.7 **RED** Write `domain/crochet/StitchDictionaryTest.kt` — lookup `"sc"` returns StitchDef, `"zz"` returns null, inc/dec geometry
- [ ] 1.8 **GREEN** Create `domain/crochet/model/StitchDef.kt` and `domain/crochet/StitchDictionary.kt` — MVP 7-stitch registry

## Phase 2: Repetition Groups + Row Validation

- [ ] 2.1 **RED** Write `domain/crochet/RepeatExpanderTest.kt` — simple group, nested groups, modifier preservation (per repetition-groups spec)
- [ ] 2.2 **GREEN** Create `domain/crochet/RepeatExpander.kt` — visitor flattening `RepeatGroupNode` into sequential `StitchNode` list
- [ ] 2.3 **RED** Write `domain/crochet/RowValidatorTest.kt` — matching counts, mismatch, empty row, missing declared count (per row-validation spec)
- [ ] 2.4 **GREEN** Create `domain/crochet/model/ParseError.kt` and `domain/crochet/RowValidator.kt` — sealed error types, count validation

## Phase 3: Spatial Engine + Geometry Pipeline

- [ ] 3.1 **RED** Write `domain/crochet/SpatialEngineTest.kt` — 1-to-1 adjacency, inc (1→2), dec (2→1), width extrema (per spatial-continuity spec)
- [ ] 3.2 **GREEN** Create `domain/crochet/model/StitchBlock.kt` and `domain/crochet/SpatialEngine.kt` — adjacency tracker outputting `List<StitchBlock>`
- [ ] 3.3 **RED** Write `domain/crochet/GeometryMapperTest.kt` — cube, trapezoid (dec), inverted-trapezoid (inc), compound vertex counts
- [ ] 3.4 **GREEN** Create `domain/crochet/GeometryMapper.kt` — maps `StitchBlock` → `BlockGeometry` with `baseTop`/`baseBottom`
- [ ] 3.5 Modify `domain/model/BlockGeometry.kt` — add optional `baseBottom: Float`, `baseTop: Float` fields (default to unit)
- [ ] 3.6 Add `fun compute(stitchBlocks: List<StitchBlock>): List<BlockGeometry>` overload to `domain/GeometryComputer.kt`
- [ ] 3.7 **RED** Write existing GeometryComputer tests pass verification — run `./gradlew test` on modified files

## Phase 4: Compound Geometries

- [ ] 4.1 **RED** Write `domain/crochet/CompoundGeometryTest.kt` — V-stitch produces 2 outputs, puff height, fan repeat range (per compound-geometries spec)
- [ ] 4.2 **GREEN** Extend `CompoundStitchNode` geometry in `GeometryComputer` — vertex generation for V-stitch, puff, popcorn, fan
- [ ] 4.3 Verify `CompoundType` enum cases covered exhaustively in AST matches

## Phase 5: Crochet Studio UI

- [ ] 5.1 Create `presentation/crochetstudio/CrochetStudioIntent.kt` — sealed interface with `ParsePattern`, `Reset`
- [ ] 5.2 Create `presentation/crochetstudio/CrochetStudioState.kt` — `patternText`, `rows`, `geometries`, `validationErrors`, `isLoading`
- [ ] 5.3 Create `presentation/crochetstudio/CrochetStudioEffect.kt` — sealed: `ShowError`, `CenterCamera`
- [ ] 5.4 **RED** Write `presentation/crochetstudio/CrochetStudioViewModelTest.kt` — ParsePattern → state update; invalid → error + effect; Reset → initial
- [ ] 5.5 **GREEN** Create `presentation/crochetstudio/CrochetStudioViewModel.kt` — MVI pipeline wiring Lexer→Parser→Expander→Validator→SpatialEngine→GeometryMapper
- [ ] 5.6 Create `presentation/crochetstudio/theme/CrochetColor.kt` — error red, warning amber tokens
- [ ] 5.7 Create `presentation/crochetstudio/ui/atoms/PatternInputField.kt` — multi-line text field
- [ ] 5.8 Create `presentation/crochetstudio/ui/molecules/PatternInputPanel.kt` — input + "Render" button
- [ ] 5.9 Create `presentation/crochetstudio/ui/molecules/ValidationPanel.kt` — row-by-row error/warning list (per ui-validation-panel spec)
- [ ] 5.10 Create `presentation/crochetstudio/ui/organisms/CrochetViewport.kt` — trapezoid MeshNode rendering
- [ ] 5.11 Create `presentation/crochetstudio/ui/templates/CrochetStudioTemplate.kt` — split layout panel
- [ ] 5.12 Create `presentation/crochetstudio/ui/pages/CrochetStudioPage.kt` — ViewModel wiring + effect handling
- [ ] 5.13 Modify `MainActivity.kt` — add BottomNavItem for Crochet Studio route
- [ ] 5.14 Run `./gradlew test` — all existing tests still pass
