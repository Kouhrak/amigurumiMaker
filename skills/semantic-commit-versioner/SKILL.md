---
name: semantic-commit-versioner
description: "Trigger: plan commits, git commit, version changes, commit message, sdd-apply commit. Calculate next semantic version (vX.Y.Z) during commit planning by analyzing change complexity."
license: Apache-2.0
metadata:
  author: gentleman-programming
  version: "1.0"
---

## Activation Contract

Calculate the next version tag (vX.Y.Z) during commit planning. The agent MUST analyze workspace changes and produce a version-bumped commit message with every commit.

## Hard Rules

1. Every commit message MUST start with `vX.Y.Z: ` prefix (e.g., `v1.0.4: Fix button alignment`).
2. Baseline for a new project is `v0.0.0`.
3. Increment exactly one segment:
   - **MAJOR** (v+1.0.0): breaking changes, complete releases, architectural overhauls.
   - **MINOR** (v0.+1.0): new modules, features, isolated services, new Atomic Design templates/pages.
   - **PATCH** (v0.0.+1): bug fixes, refactoring, minor style adjustments, dependency updates.
4. Reset rule: Major → reset Minor and Patch to 0. Minor → reset Patch to 0.
5. If the user specifies a version, use it. Otherwise calculate based on change analysis.

## Decision Gates

| Change type | Bump | Example |
|---|---|---|
| Breaking API, full release | MAJOR | v1.2.3 → v2.0.0 |
| New module, feature, screen | MINOR | v1.2.3 → v1.3.0 |
| Bug fix, refactor, deps | PATCH | v1.2.3 → v1.2.4 |

## Execution Steps

1. Read current version from latest git tag (`git describe --tags --abbrev=0`) or fall back to `v0.0.0`.
2. Analyze staged/unstaged changes to classify work type (major/minor/patch).
3. If user provided a specific version or bump type, honor it.
4. Calculate new version applying reset rules.
5. Prepend the version to the commit message: `vX.Y.Z: <message>`.

## Few-Shot Patterns

**Patch — layout fix:** Current v1.2.4, user asks to fix button alignment → v1.2.5. Commit: `v1.2.5: Fix button alignment in CardContent molecule`

**Minor — new module:** Current v0.4.1, user asks to implement auth module → v0.5.0. Commit: `v0.5.0: Add MVI authentication module and data repository`

**Major — breaking release:** Current v2.1.0, breaking API redesign → v3.0.0.

## Output Contract

Return a commit command with the calculated version:
```
git commit -m "vX.Y.Z: <commit message>"
```

If the version change is ambiguous, ask the user which segment to bump.

## References

- `skills/semantic-commit-versioner/` — this skill
