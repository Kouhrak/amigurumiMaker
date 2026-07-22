## Exploration: geometric-mesh-simulator

### Current State

The codebase has a **Wall Modeler** feature — a 3D block editor that parses a 4-token grammar (`c`, `d`, `p` + `ma`/`ca` modifiers) and renders cubes/triangular prisms via SceneView. The full pipeline:

```
Syntax text → WallTokenizer.tokenize() → RowExpander.expand() → GeometryComputer.compute() → SceneView (GeometryViewport)
```

**Domain layer (pure Kotlin):**
- `TokenType` enum: `CUBE`, `RAMP_LEFT`, `RAMP_RIGHT`, `PLACEHOLDER` — only 4 types
- `WallTokenizer`: regex-based, grammar `(rowSpec token+)` with `c`→CUBE, `d`→RAMP_LEFT, `p`→PLACEHOLDER. No support for repetition groups, parentheses, or stitch abbreviations.
- `GeometryComputer`: produces unit cubes (8 vertices) or triangular prisms (6 vertices). No trapezoid geometry, no width variation.
- `BlockGeometry`: generic vertex/edge/faceIndices container — no semantics about stitch type, no stitch metadata.

**Presentation layer (MVI):**
- `WallModelerViewModel` with `ParseSyntax` / `Reset` intents
- `GeometryViewport` organism: renders cubes via `CubeNode`, ramps via `MeshNode` with Filament vertex/index buffers, wireframe via `LineNode`
- Files: `WallModelerPage`, `WallModelerTemplate`, atoms/molecules per Atomic Design

**Test patterns:** JUnit 4 for domain (pure assertion), `kotlinx-coroutines-test` + `StandardTestDispatcher` for ViewModel.

### Limitations for Crochet Simulation

1. **4 token types** vs ~20+ required stitches (single crochet, double crochet, treble, etc.)
2. **No increase/decrease geometry** — current ramps are triangular prisms, not trapezoids. An increase (a) needs an inverted trapezoid (base_bottom < base_top). A decrease (d) needs a regular trapezoid (base_bottom > base_top).
3. **No repetition groups** — pattern grammar `[1p 1a] 6v` (repeat a block 6 times) is not parseable by the current regex.
4. **No stitch dictionary** — each stitch abbreviation (`p.b.`, `p.a.`, `p.m.a.`, etc.) must map to geometry parameters (base width, height, stitch consumption/production).
5. **No spatial continuity** — no tracking of which blocks below support which blocks above. Current system centers rows independently.
6. **No row validation** — the `([Total]p)` annotation at end of row is parsed as a PLACEHOLDER token, not validated against actual stitch count.
7. **No texture modifiers** — FLO/BLO (front/back loop only) and post stitches have no visual representation.

### Previous Related Work

An earlier change (`geometric-crochet-engine`) explored the same problem and completed:
- **Exploration**: recommended Recursive Descent Parser + AST pipeline
- **Proposal**: 7-phase migration plan
- **Design**: architecture for AST, StitchDictionary, SpatialEngine
- **7 specs**: stitch-parser, stitch-dictionary, repetition-groups, row-validation, compound-geometries, spatial-continuity, ui-validation-panel
- **Phase 1 implementation** (AST + Lexer + Parser) — parity-verified with 16 tests but **never committed to the `develop` branch**

The code files (`AstNode.kt`, `Lexer.kt`, `Parser.kt`) and openspec artifacts no longer exist on the current branch. The learnings are preserved in Engram (memory #59, #60, #65).

### Affected Areas

| Area | Files | Impact |
|------|-------|--------|
| **Domain models** | `TokenType.kt`, `Token.kt`, `BlockGeometry.kt`, `RowDef.kt`, `ParseResult.kt` | Current models are too generic. Need stitch-type semantics, geometry parameters (base_top, base_bottom), spatial adjacency data. |
| **Domain parsing** | `WallTokenizer.kt` | Must be replaced — regex cannot handle ~20 stitches, repetition groups, nested structures. Need Recursive Descent Parser + AST. |
| **Domain geometry** | `GeometryComputer.kt` | Must be rewritten — needs cuboid/trapezoid/inverted-trapezoid generators, stitch-specific geometry parameters, spatial adjacency tracking. |
| **Domain new** | `StitchDictionary.kt`, `SpatialEngine.kt`, `AST.kt` (AstNode, Lexer, Parser), `RowValidator.kt` | New files for stitch definitions, spatial continuity, parsing, and validation. |
| **Presentation ViewModel** | `WallModelerViewModel.kt`, `WallModelerState.kt`, `WallModelerIntent.kt`, `WallModelerEffect.kt` | Extend or create new crochet-specific ViewModel. State needs stitch-type info, validation results, spatial graph. |
| **Presentation viewport** | `GeometryViewport.kt` | Must render trapezoids, texture modifiers (FLO/BLO visual indicators), color-coded stitch types, adjacency lines. |
| **Presentation UI** | `SyntaxInputPanel.kt`, template/page | Pattern input needs stitch suggestions, validation feedback panel, row-by-row display. |
| **Presentation new** | Validation panel, stitch palette, row navigator | New UI components for the crochet domain. |
| **Theme** | `CadColor.kt` | New colors for stitch-type differentiation, validation status (pass/fail). |
| **Tests** | `WallTokenizerTest.kt`, `GeometryComputerTest.kt`, `WallModelerViewModelTest.kt` | Test suites must be rewritten or extended for the new pipeline. |

### Approaches

#### Approach A: Recursive Descent Parser + AST + StitchDictionary + SpatialEngine

Build a proper compiler-style pipeline: Lexer → Parser (recursive descent) → AST → Semantic Analysis (StitchDictionary lookup, row validation) → Spatial Continuity Engine → Geometry Generation.

**Pipeline:**
```
Input → Lexer (tokenize) → Parser (build AST) → SemanticAnalyzer (resolve stitches, validate) → SpatialEngine (track adjacency) → GeometryComputer (generate cuboids) → SceneView
```

- **StitchDictionary**: data-driven mapping of stitch abbreviations to geometry parameters (base_top, base_bottom, height, stitches_consumed, stitches_produced, texture_modifiers)
- **AST** nodes: `RowNode`, `StitchNode`, `RepeatGroupNode`, `ModifierNode`
- **SpatialEngine**: tracks which blocks below align to which blocks above, computes offset for increases/decreases

**Pros:**
- Correct solution for a DSL — crochet patterns ARE a language
- StitchDictionary makes new stitches data additions, not code changes
- AST enables rich error reporting (position-aware, "expected stitch but found X")
- SpatialEngine can model real crochet physics (tension, stitch compression)
- Clean separation of concerns — each phase independently testable
- Reuses the existing SceneView rendering pipeline (only adds new geometry types)
- Previous work validated this approach (Phase 1 was parity-verified with 16 tests)

**Cons:**
- Higher upfront effort (~900-1200 lines across 7 phases)
- More complex than extending the regex
- Over-engineered if only a few stitches are needed
- AST infrastructure (Lexer, Parser, Visitor) is new code with learning curve

**Effort: High** (7 phases, ~900-1200 lines)

#### Approach B: Extended Regex + Geometry Parameter Table

Extend the current regex tokenizer to support more stitch abbreviations and add a geometry parameter table. Replace `TokenType` with a richer stitch model that carries geometric parameters. Add repetition group expansion in a new pre-processing step.

**Pipeline:**
```
Input → PreProcessor (expand repetitions) → ExtendedWallTokenizer (regex, 20+ stitches) → GeometryParameterTable → GeometryComputer (trapezoid aware) → SceneView
```

**Pros:**
- Leverages existing code — minimal refactoring
- Faster initial implementation (~400-600 lines)
- Simpler mental model — no AST overhead
- Good enough for flat, non-nested patterns

**Cons:**
- Regex hits complexity limits fast — patterns like `[2p.b. 1p.a.] 3v` with nested parens become unmaintainable
- Error messages are poor (regex match just fails, no position-aware feedback)
- Adding a new stitch often means updating the regex pattern AND the parameter table
- No AST means no semantic validation (e.g., "this row has 6 stitches but should have 5")
- Spatial continuity requires ad-hoc hacks rather than a dedicated engine
- Previous work explicitly rejected this approach for these reasons

**Effort: Medium** (~400-600 lines)

### Recommendation

**Approach A: Recursive Descent Parser + AST + StitchDictionary + SpatialEngine**

The previous exploration already validated this choice, and Phase 1 was implemented and parity-tested (though the code was lost). The reasons remain solid:

1. **Crochet patterns ARE a DSL** — treating them as one justifies a proper parser. Regex will fail on nested repetitions, compound stitches (e.g. `p.V` V-stitch), and texture modifiers with parenthesized groups.
2. **Scalability** — StitchDictionary adds stitches as data rows, not code. The dictionary can live as a Kotlin `Map<String, StitchDef>` or even a config file.
3. **Validation** — AST enables `([Total]p)` validation at row end, cross-row stitch count validation, and structured error messages.
4. **Spatial continuity** — the current `RAMP_LEFT`/`RAMP_RIGHT` heuristic cannot model real increases/decreases. An inverted trapezoid (increase) widens upward, a trapezoid (decrease) narrows upward. This requires per-block adjacency math.
5. **Previous investment** — the parity-verified Phase 1 tests validated that the approach compiles and produces correct output. The lost code is time regained (the approach is proven).

**Migration strategy:** 7 incremental phases (matching the previous proposal), each independently testable:

1. **AST infrastructure** — Lexer, Parser, AstNode types (replaces WallTokenizer, preserves existing grammar initially)
2. **StitchDictionary** — Map of abbreviation → StitchDef geometry parameters
3. **Repetition groups** — AST handling for `[count] v` syntax
4. **Row validation** — `([Total]p)` stitch count verification
5. **Geometry computer rewrite** — trapezoid/inverted-trapezoid generation, spatial adjacency
6. **Compound geometries** — V-stitch, puff, popcorn, fan, picot
7. **UI validation panel** — error display, row-by-row visualization

### Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| **Scope creep** — 20+ stitch types with compound variants is a large dictionary | Medium | High | Define MVP stitch set (6-8 basic) first, add compound stitches in Phase 6 |
| **Spatial continuity math** — computing which blocks below support which blocks above may reveal edge cases (staggered rows, center increases) | Medium | High | Build spatial engine incrementally, test with known patterns from the user requirements (Row 1→5 example) |
| **Lost Phase 1 code** — no code to salvage, must rewrite AST/Lexer/Parser from scratch | High | Medium | The approach is proven by previous parity tests — rewrite time is ~1 session |
| **SceneView trapezoid rendering** — SceneView may not support arbitrary mesh geometry as easily as cubes and prisms | Low | Medium | GeometryViewport already handles MeshNode with custom vertex/index buffers. Trapezoids are 8 vertices (same as cubes with different positions) — no new SceneView capability needed |
| **Naming collision** — `c` currently means CUBE in the wall modeler, but `c` means "chain" in crochet | High | Low | The wall modeler syntax and crochet syntax are separate domains. Decide: extend the wall modeler or create a new crochet page. Recommend a new page/screen. |
| **Over-engineering for v1** — full AST may be more than needed for initial crochet simulation | Medium | Low | Start with a lean AST (Row, Stitch, RepeatGroup) — add CompoundStitch, Modifier nodes later. Each phase ships independently. |

### Ready for Proposal

**Yes** — exploration is complete with sufficient understanding of the codebase, previous work, and tradeoffs.

The orchestrator should tell the user:
1. There are two clear approaches, with **Approach A (Recursive Descent + AST)** recommended
2. The previous `geometric-crochet-engine` Phase 1 was validated but the code is lost — approach is proven, needs reimplementation
3. The 7-phase incremental plan from the previous work can be adapted directly
4. A key open question: **should this be a new screen/page (crochet simulator) or an extension of the existing wall modeler?** The grammar and semantics differ enough to warrant a separate page, but the rendering pipeline (SceneView) and MVI patterns can be reused.
5. The MVP stitch set should be agreed before spec writing
