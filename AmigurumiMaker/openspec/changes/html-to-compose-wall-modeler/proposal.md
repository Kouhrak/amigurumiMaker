# Proposal: Convierte HTML a Jetpack Compose — Modelador Geométrico 3D

## Intent

Port a Pure Geometric Wall Modeler (HTML/JS) to Android Compose. The original parses a custom syntax (`10c`, `1d 8p 1d`) and renders 3D wall blocks with orbit controls. We need the same functionality as a Compose-native feature within the AmigurumiMaker scaffold, using MVI + Clean Architecture + Atomic Design.

## Scope

### In Scope
- Syntax parser (tokenize custom wall syntax into structured models)
- 3D block geometry computation (cube vertices, triangular ramp prisms, wireframe edges)
- SceneView integration for 3D viewport with orbit controls (zoom/rotate)
- MVI state management: `WallModelerIntent`, `WallModelerState`, `WallModelerEffect`, `WallModelerViewModel`
- Atomic Design composable page: `WallModelerPage` + child atoms/molecules/organisms
- CAD-style theme palette (slate-gray)
- Unit tests for domain (parser, geometry) and ViewModel

### Out of Scope
- Preset/storage layer (deferred to later phase)
- Multi-window / landscape optimization
- Export/print functionality
- Animation system for block transitions

## Capabilities

### New Capabilities
- `wall-modeler`: Syntax parsing, 3D geometry generation, MVI UI for block modeling

### Modified Capabilities
- None (greenfield feature — no existing specs affected)

## Approach

**Architecture**: MVI + Clean Architecture + Atomic Design per project convention.

**Layers**:
- **Domain** (pure Kotlin, testable): `WallTokenizer` (tokenize syntax), `RowExpander` (expand ranges like `1-3)`), `GeometryComputer` (cube/ramp vertices + wireframe edges). Models: `Token`, `BlockGeometry`, `RowDef`.
- **Presentation**: `WallModelerViewModel` (MVI), `WallModelerPage` composable (page-level organism), supporting atoms (SyntaxInputField, AppButton), molecules (SyntaxInputPanel, GeometryViewport).
- **Data**: Deferred — Phase 4.

**3D Rendering**: SceneView (Filament-based) with `CameraManipulator` for orbit controls. Wireframe rendering via mesh modification (needs spike confirmation).

**Theme**: Extend current `AmigurumiMakerTheme` with CAD-specific color roles (slate surfaces, grid lines).

## Dependency Gate

> **⛔ BLOCKING DECISION REQUIRED BEFORE PROCEEDING**

| Option | Pros | Cons |
|--------|------|------|
| **SceneView** (`io.github.sceneview:sceneview`) | Compose-native, camera manipulator built-in, Filament-backed, maintained | Kotlin 2.4.0 target — project is on 2.2.10 (bump needed). Wireframe rendering unconfirmed. |
| **Raw OpenGL ES** (`GLSurfaceView` + AndroidView wrapper) | Full control over wireframe, no version conflict, zero new deps | Manual orbit math, no Compose integration (AndroidView bridge), significantly more code, no lighting/scene graph |

**Ask**: Proceed with SceneView (accept Kotlin bump risk and wireframe spike) or fall back to raw OpenGL ES?

## Phase Plan

| Phase | Focus | Key Deliverables | Est. Lines |
|-------|-------|-----------------|------------|
| **1** | Setup | Package structure (`domain/`, `presentation/`), MVI infra, theme extension, Gradle deps | ~100 |
| **2** | Domain | `WallTokenizer`, `RowExpander`, `GeometryComputer`, Token/BlockGeometry models, unit tests | ~150 |
| **3** | Presentation | `WallModelerViewModel`, all composables (atom→page), SceneView/OpenGL viewport, UI tests | ~500 |
| **4** | Data | Preset storage (deferred — no estimate yet) | TBD |
| **5** | Polish | Error states, loading indicators, edge-case handling, UX refinement | ~100 |

**Total estimated**: ~850 lines (excl. Phase 4).

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| SceneView Kotlin 2.4.0 requirement conflicts with project 2.2.10 | High | Phase 1 includes Kotlin version bump spike. If blocked, fallback to OpenGL ES. |
| SceneView wireframe rendering not supported | Medium | Phase 3 starts with SceneView spike on a proof-of-concept mesh. If fails, switch to OpenGL ES. |
| 3D geometry math incorrect (ramp prism vertex winding) | Low | Domain unit tests with known expected vertex sets from original HTML app. |

## Rollback Plan

- **Phase 1**: Revert `build.gradle.kts` deps and theme changes → project returns to scaffold state.
- **Phase 2**: Delete `domain/` package → no side effects (pure Kotlin, zero coupling to UI).
- **Phase 3**: Remove `WallModelerPage` from navigation + delete `presentation/` package → app returns to original Greeting screen.
- **Full rollback**: `git revert` the feature branch. No migration needed (greenfield feature with no data).

## Success Criteria

- [ ] Syntax parser tokenizes all valid inputs (`10c`, `1d 8p 1d`, `1-3) 10c`) and rejects invalid ones
- [ ] 3D viewport renders cubes and triangular prisms with visible wireframe edges
- [ ] Orbit controls (rotate, zoom) work via touch gestures
- [ ] Unit test coverage > 80% for domain layer
- [ ] `./gradlew assembleDebug` passes with no errors

## Dependencies

- **SceneView** (v4.23.0) — subject to dependency gate approval and Kotlin version compatibility
- Current: Kotlin 2.2.10, AGP 9.2.1, Compose BOM 2026.02.01
