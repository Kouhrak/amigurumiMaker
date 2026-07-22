# Proposal: Geometric Crochet Mesh Simulator

## Intent

The app has a Wall Modeler (4-token regex, wall blocks) — not a crochet engine. Crochet patterns are a DSL with ~20+ stitch types, repetition groups, nested modifiers, and spatial continuity rules. We need a proper compiler-style pipeline to parse crochet patterns, validate them, and render 3D mesh visualizations. Previous work on `geometric-crochet-engine` proved the approach but code was lost on a separate branch.

## Scope

### In Scope
- **Recursive Descent Parser + AST** — Lexer, Parser, AstNode types replacing `WallTokenizer`
- **Stitch Dictionary** — data-driven registry of stitch abbreviations → geometry parameters (6-8 MVP stitches: ch, sl st, sc, hdc, dc, inc, dec)
- **Repetition Groups** — `[count] v` syntax expansion in AST
- **Row Validation** — `([Total]p)` stitch count verification, structural error reporting
- **Spatial Continuity Engine** — block adjacency tracking (trapezoid/inverted-trapezoid for inc/dec)
- **Crochet Studio UI** — new screen/page (separate from Wall Modeler) with pattern input, mesh viewer, validation panel
- **Trapezoid geometry** — 8-vertex cuboid with variable base_top/base_bottom dimensions
- **Unit tests** — JUnit 4 for all domain layers

### Out of Scope
- Preset/storage for patterns
- Texture modifiers (FLO/BLO visual indicators) — deferred after Phase 6
- Export/print/pattern sharing
- Animation system for stitch construction

## Capabilities

> Each new capability maps to an existing `openspec/specs/<name>/` directory (empty, ready for spec writing).

### New Capabilities
- `stitch-dictionary`: Stitch metadata registry (abbreviations, spatial properties, base_top/base_bottom/height)
- `stitch-parser`: Recursive descent parser + AST for crochet pattern grammar
- `repetition-groups`: Nested repetition group expansion (`[1p 1a] 6v`)
- `row-validation`: Row-end stitch count validation and structure validation
- `compound-geometries`: Complex stitch geometries (V-stitch, puff, popcorn, fan)
- `spatial-continuity`: Block adjacency engine for stitch-to-stitch spatial relationships
- `ui-validation-panel`: UI for parse errors, row-by-row validation feedback

### Modified Capabilities
- None (greenfield — no existing specs affected)

## Approach

**Architecture**: MVI + Clean Architecture + Atomic Design per project convention. New "Crochet Studio" page (separate from Wall Modeler) to avoid token collision (`c` = CUBE vs `c` = Chain).

**Pipeline**: Input → Lexer → Parser (recursive descent) → AST → SemanticAnalyzer (StitchDictionary lookup, validation) → SpatialEngine (adjacency tracking) → GeometryComputer (trapezoid/cuboid generation) → SceneView MeshNode

**7 incremental phases** (each independently testable):

| Phase | Focus | Est. Lines |
|-------|-------|-----------|
| 1 | AST infrastructure: Lexer, Parser, AstNode sealed hierarchy | ~180 |
| 2 | StitchDictionary: StitchDef data class, built-in registry | ~100 |
| 3 | Repetition groups: AST + expansion visitor | ~120 |
| 4 | Row validation: stitch count checker + error reporting | ~100 |
| 5 | Geometry computer rewrite: trapezoids, spatial adjacency | ~200 |
| 6 | Compound geometries: V-stitch, puff, popcorn, fan | ~150 |
| 7 | Crochet Studio UI: pattern input, mesh viewer, validation panel | ~250 |

**Total estimated**: ~1,100 lines (domain ~700, presentation ~400).

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/model/AstNode.kt` | New | Sealed AST node hierarchy |
| `domain/lexer/Lexer.kt` | New | Tokenizer (replaces `WallTokenizer`) |
| `domain/parser/Parser.kt` | New | Recursive descent parser |
| `domain/StitchDictionary.kt` | New | Stitch definition registry |
| `domain/SpatialEngine.kt` | New | Block adjacency tracking |
| `domain/RowValidator.kt` | New | Row-end stitch count validation |
| `domain/GeometryComputer.kt` | Modified | Add trapezoid/inverted-trapezoid generation |
| `domain/model/BlockGeometry.kt` | Modified | Add base_top/base_bottom dimensions |
| `presentation/crochetstudio/` | New | MVI screen (CrochetStudioPage, ViewModel, state/intent/effect) |
| `presentation/wallmodeler/` | Unchanged | Existing wall modeler left intact |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| **Scope creep** — expanding stitch dictionary beyond MVP | Medium | Lock MVP to 7 stitches (ch, sl st, sc, hdc, dc, inc, dec) — compound stitches deferred to Phase 6 |
| **Spatial math edge cases** — staggered rows, off-center inc/dec | Medium | Test with known patterns from exploration; build engine incrementally |
| **SceneView trapezoid rendering** — unconfirmed for non-uniform cuboids | Low | GeometryViewport already handles `MeshNode` with custom buffers — trapezoids are vertex-position variants |
| **Lost code reimplementation** — rewriting Phase 1 from scratch | High (known) | Low impact — approach is proven by parity tests; rewrite is ~1 session |
| **Token collision** — `c` means CUBE in wall modeler, CHAIN in crochet | High | Separate page eliminates collision; shared infrastructure (SceneView, MVI patterns) reused |

## Rollback Plan

- **Per phase**: Each phase is a self-contained commit — revert individual commits with no cascade
- **Full rollback**: Delete `presentation/crochetstudio/` + revert changes to `domain/` models → wall modeler unaffected
- **Data safety**: No migrations needed (greenfield feature, no persistent data)

## Dependencies

- None external — SceneView already in project (v4.23.0)
- Existing MVI patterns (`WallModelerViewModel`, state/intent/effect) serve as reference

## Success Criteria

- [ ] Lexer tokenizes all MVP stitch abbreviations and rejects invalid input
- [ ] Parser builds correct AST for every exploration test pattern
- [ ] Row validator catches stitch count mismatches in `([Total]p)` annotations
- [ ] Spatial engine produces correct block adjacency for simple rows (inc/dec alignment verified)
- [ ] Geometry computer generates trapezoid meshes with correct vertex winding
- [ ] Crochet Studio page renders 3D mesh and accepts pattern input
- [ ] All existing wall modeler tests continue to pass
- [ ] `./gradlew test` passes with no errors
