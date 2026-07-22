# Stitch Parser Specification

## Purpose

A recursive descent parser that tokenizes crochet pattern text into an Abstract Syntax Tree (AST) of typed nodes, replacing the current regex-based WallTokenizer.

## Requirements

### Requirement: Lexer Tokenization

The lexer MUST tokenize stitch abbreviations from the stitch dictionary and reject unrecognized tokens.

#### Scenario: Valid abbreviation tokens

- GIVEN a pattern input `"1) 10sc"`
- WHEN the lexer processes the input
- THEN it produces tokens: `RowNumber(1)`, `Stitch("sc")`, `Count(10)`

#### Scenario: Invalid token rejection

- GIVEN a pattern input containing `"1) 10zz"`
- WHEN the lexer processes the input
- THEN it produces a tokenization error at position of `"zz"`

### Requirement: Recursive Descent Parsing

The parser MUST consume tokens from the lexer and produce a typed AST node hierarchy (RowNode, StitchNode, RepeatGroupNode, RangeNode).

#### Scenario: Simple row parsing

- GIVEN tokenized input for row `"1) 10sc"`
- WHEN the parser builds the AST
- THEN it produces a `RowNode` with `rowNumber = 1` containing a single `StitchNode("sc")` with count `10`

#### Scenario: Row with stitch sequence

- GIVEN tokenized input `"2) 1sc 1inc"`
- WHEN the parser builds the AST
- THEN it produces a `RowNode` with two `StitchNode` children: `StitchNode("sc", 1)` and `StitchNode("inc", 1)`

#### Scenario: Range notation

- GIVEN tokenized input `"1-5) 10sc"`
- WHEN the parser builds the AST
- THEN it produces five `RowNode` entries (rows 1 through 5), each containing `StitchNode("sc", 10)`

#### Scenario: Malformed row number

- GIVEN tokenized input `"abc) 10sc"`
- WHEN the parser attempts to build the AST
- THEN it produces a parse error: "Expected row number at position 0"

### Requirement: Declared count annotation

The parser MUST capture the declared stitch count `([N]p)` at end of row as metadata on the `RowNode`.

#### Scenario: Row with declared count

- GIVEN tokenized input `"1) 10sc (10p)"`
- WHEN the parser builds the AST
- THEN the `RowNode` has `declaredCount = 10`
