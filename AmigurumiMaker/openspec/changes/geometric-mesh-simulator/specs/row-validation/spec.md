# Row Validation Specification

## Purpose

Validate row-end stitch counts against declared totals, and detect structural errors in parsed crochet patterns.

## Requirements

### Requirement: Stitch Count Validation

The system MUST validate that the actual stitch count (sum of `producesStitches` for each stitch in the row) matches the declared `([N]p)` annotation.

#### Scenario: Matching counts — valid row

- GIVEN a parsed row with stitches `[sc, sc, sc]` and declared count `(3p)`
- WHEN the row validator checks the row
- THEN validation passes with no errors

#### Scenario: Mismatched counts — error

- GIVEN a parsed row with stitches `[sc, inc]` (produces 1 + 2 = 3) and declared count `(2p)`
- WHEN the row validator checks the row
- THEN it produces a `RowValidationError` with message matching "declared 2 but actual count is 3"

### Requirement: Partial Validation

The system MUST support validating a single row without requiring the full pattern.

#### Scenario: Single row validation

- GIVEN a single `RowNode` with stitch count mismatch
- WHEN validateRow(rowNode) is called
- THEN it returns the validation result for that row only, without processing other rows

### Requirement: Structural Validation

The system MUST detect rows with zero stitches and rows with unreferenced declared counts.

#### Scenario: Empty row

- GIVEN a parsed row with zero stitches and declared count `(0p)`
- WHEN the row validator checks the row
- THEN it produces a warning or error indicating an empty row

#### Scenario: Missing declared count

- GIVEN a parsed row with `[sc, inc]` but no `([N]p)` annotation
- WHEN the row validator checks the row
- THEN it produces a `MissingDeclaredCount` warning
