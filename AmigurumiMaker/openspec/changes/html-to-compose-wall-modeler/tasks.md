# Tasks: Convierte HTML a Jetpack Compose — Modelador Geométrico 3D

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~900 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR1: Setup + Domain → PR2: Presentation MVI + UI → PR3: SceneView + Integration |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: pending
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Setup + Domain (models, tokenizer, expander, geometry, tests) | PR 1 | Base: main. ~300 lines. Self-verifiable via `./gradlew test`. |
| 2 | Presentation MVI + UI atoms/molecules/template | PR 2 | Base: main. ~300 lines. No domain deps at compile time, but consumes domain types. |
| 3 | SceneView organism + ViewModel wiring + MainActivity | PR 3 | Base: main. ~300 lines. Depends on both domain and presentation contracts. |

---

## Phase 1: Setup

- [ ] **T1.1** — `gradle/libs.versions.toml`: bump kotlin 2.2.10→2.4.0, add sceneview 4.23.0 + kotlin-test version refs and library entries
- [ ] **T1.2** — `app/build.gradle.kts`: add `implementation(libs.sceneview)` + `testImplementation(libs.kotlin.test)` to dependencies block
- [ ] **T1.3** — Create directory tree: `domain/model/`, `domain/`, `presentation/wallmodeler/`, `presentation/wallmodeler/ui/atoms/`, `ui/molecules/`, `ui/organisms/`, `ui/templates/`, `ui/pages/`, `theme/`, and test mirror dirs
- [ ] **T1.4** — Create `presentation/wallmodeler/theme/CadColor.kt` with light/dark color palette
- [ ] **T1.5** — Verify: `./gradlew assembleDebug` compiles cleanly

## Phase 2: Domain Models

- [ ] **T2.1** — Create `domain/model/TokenType.kt` (enum: CUBE, RAMP_LEFT, RAMP_RIGHT, PLACEHOLDER)
- [ ] **T2.2** — Create `domain/model/Token.kt` (data class: type, count)
- [ ] **T2.3** — Create `domain/model/RowDef.kt` (data class: startRow, endRow, tokens)
- [ ] **T2.4** — Create `domain/model/BlockGeometry.kt` (data classes: Float3, IntPair, IntTriple, BlockGeometry)
- [ ] **T2.5** — Create `domain/model/ParseResult.kt` (sealed class: Success(rows), Error(message))

## Phase 3: Domain Logic (TDD — write tests first)

- [ ] **T3.1 RED** — Write `app/src/test/.../domain/WallTokenizerTest.kt`: valid single row, mixed tokens, row ranges, invalid types, empty input, malformed rowSpec, multiple segments (all fail at this stage)
- [ ] **T3.2 GREEN** — Implement `domain/WallTokenizer.kt` regex parser: grammar `segment(','segment)*` with rowSpec + token+, returns ParseResult
- [ ] **T3.3 RED** — Write `app/src/test/.../domain/RowExpanderTest.kt`: single row passthrough, 2-row range, 5-row range, single-row range, invalid start>end
- [ ] **T3.4 GREEN** — Implement `domain/RowExpander.kt`: expand each RowDef's range into individual RowDefs
- [ ] **T3.5 RED** — Write `app/src/test/.../domain/GeometryComputerTest.kt`: cube 8v/12e, ramp_left 6v/9e diagonal(-0.5,-0.5)→(0.5,0.5), ramp_right 6v/9e diagonal(0.5,-0.5)→(-0.5,0.5), placeholder empty, multi-block centering
- [ ] **T3.6 GREEN** — Implement `domain/GeometryComputer.kt`: vertex math for CUBE (box), RAMP_LEFT/RAMP_RIGHT (triangular prism), PLACEHOLDER (empty); row centering via `startX = -floor(rowWidth/2)`
- [ ] **T3.7** — Run `./gradlew test --tests "com.example.amigurumimaker.domain.*"` — verify all domain tests pass and coverage >80%

## Phase 4: Presentation MVI

- [x] **T4.1** — Create `presentation/wallmodeler/WallModelerIntent.kt` sealed interface: `ParseSyntax(text)`, `Reset`
- [x] **T4.2** — Create `presentation/wallmodeler/WallModelerState.kt`: syntaxText, rows, geometries, isLoading, error, parsedBlockCount
- [x] **T4.3** — Create `presentation/wallmodeler/WallModelerEffect.kt` sealed interface: `ShowError(message)`, `CenterCamera`
- [x] **T4.4** — Implement `presentation/wallmodeler/WallModelerViewModel.kt`: `MutableStateFlow<State>`, `MutableSharedFlow<Effect>`, `process(intent)` launches coroutine calling domain pipeline, emits effects

## Phase 5: UI Atoms & Molecules

- [x] **T5.1** — Create atoms: `SyntaxInputField.kt` (OutlinedTextField), `AppButton.kt` (Button+Text, enabled param), `AppHeaderTitle.kt` (headline typography)
- [x] **T5.2** — Create molecules: `SyntaxInputPanel.kt` (field + build button wired), `HeaderBar.kt` (title with AppHeaderTitle)

## Phase 6: UI Organism & Integration

- [ ] **T6.1** — Create `GeometryViewport.kt` organism: SceneView with CubeNode (cubes), MeshNode (ramps: TRIANGLES primitive with vertex/index buffers), LineNode (edges); compute AABB → cameraManipulator.setLookAt(); use createUnlitColorInstance(CadColor.*)
- [ ] **T6.2** — Create `WallModelerTemplate.kt`: Column layout composing HeaderBar + SyntaxInputPanel + GeometryViewport
- [ ] **T6.3** — Create `WallModelerPage.kt`: ViewModel via `viewModel()`, collect StateFlow/SharedFlow, SnackbarHost for ShowError, feed geometries to GeometryViewport
- [ ] **T6.4** — Update `MainActivity.kt`: replace Greeting composable with `WallModelerPage()`, keep Scaffold + AmigurumiMakerTheme

## Phase 7: ViewModel Tests & Verify

- [x] **T7.1** — Write `app/src/test/.../presentation/wallmodeler/WallModelerViewModelTest.kt`: ParseSyntax valid → state update with geometries; ParseSyntax invalid → error in state + ShowError effect; Reset → initial state; use kotlinx-coroutines-test
- [x] **T7.2** — Run `./gradlew test` — all unit tests pass (domain + ViewModel)
- [x] **T7.3** — Run `./gradlew assembleDebug` — clean build

---

## Dependency Graph

```
T1.1 ──→ T1.2 ──→ T1.3 ──→ T1.4 ──→ T1.5
                                          │
                                          ▼
                                       T2.1 ──→ T2.5
                                       T2.2 ──→ T2.5
                                       T2.3 ──→ T2.5
                                       T2.4 ──→ T2.5
                                          │
                                          ▼
                ┌─────────────────────────┤
                ▼                         ▼
            T3.1 ─→ T3.2             T3.3 ─→ T3.4
            T3.5 ─→ T3.6
                │
                ▼
              T3.7
                │
                ▼
         ┌──────┴──────┐
         ▼             ▼
      T4.1─4         T5.1─2
         │             │
         └──────┬──────┘
                ▼
              T6.1 ─→ T6.2 ─→ T6.3 ─→ T6.4
                │
                ▼
           T7.1 ─→ T7.2 ─→ T7.3
```

### Parallelization Notes

- **T2.1–T2.5** (domain models) can be created in parallel — no interdependencies.
- **T3.1/T3.2, T3.3/T3.4, T3.5/T3.6** are sequential RED→GREEN pairs but the three pairs are independent of each other.
- **Phase 4 (MVI)** and **Phase 5 (UI atoms/molecules)** are independent and can be parallelized after domain is complete.
- **T6.1 (GeometryViewport)** is the riskiest task — SceneView API surface may differ from documented signatures; needs spike validation during implementation.
