# UI Validation Panel Specification

## Purpose

A new "Crochet Studio" screen providing a pattern input area, 3D mesh viewer, and an integrated validation panel for parse errors, row-level validation results, and spatial continuity warnings.

## Requirements

### Requirement: Separate Crochet Studio Screen

The system MUST provide a new "Crochet Studio" screen, separate from the existing Wall Modeler page, with pattern input and validation feedback.

#### Scenario: Navigate to Crochet Studio

- GIVEN the app is running and the user is on the main navigation
- WHEN the user selects "Crochet Studio"
- THEN a new screen appears with a pattern input field, a 3D viewport, and a validation panel

### Requirement: Parse Error Display

The validation panel MUST display parse errors per row, including the error message and affected row number.

#### Scenario: Parse error shown

- GIVEN the user enters pattern text containing a syntax error at row 3
- WHEN the parser runs and produces an error
- THEN the validation panel shows "Row 3: syntax error" with the specific message

#### Scenario: No errors — success state

- GIVEN the user enters a valid pattern
- WHEN parsing and validation complete without errors
- THEN the validation panel shows a success indicator (e.g., "All rows valid")

### Requirement: Row-by-Row Validation Feedback

The system MUST display row validation results (pass/fail per row) with the declared versus actual stitch count.

#### Scenario: Row validation failure

- GIVEN a row with declared count `(5p)` but actual count of 7
- WHEN row validation completes
- THEN the validation panel shows "Row 2: declared 5, actual 7 — MISMATCH"

### Requirement: Error Row Highlighting

The system SHOULD highlight the row containing the error in the pattern input text area.

#### Scenario: Editor highlight on error

- GIVEN a parse error at row 3
- WHEN the validation panel displays the error
- THEN the pattern input scrolls to or highlights the affected row

### Requirement: Non-blocking Warnings

The system MAY display non-blocking warnings (e.g., empty row, missing declared count) separately from blocking errors.

#### Scenario: Warning vs error distinction

- GIVEN a pattern with an empty row (warning) and a stitch count mismatch (error)
- WHEN the validation panel renders
- THEN errors are shown in red and warnings in yellow/amber
