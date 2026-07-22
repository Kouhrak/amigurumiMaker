# Repetition Groups Specification

## Purpose

Parse and expand nested repetition group syntax (`[1sc 1inc] 6v`) within crochet patterns, producing the equivalent flat sequence of stitches.

## Requirements

### Requirement: Simple Repetition Expansion

The system MUST expand a repetition group `[sequence] Nv` into N consecutive copies of the sequence.

#### Scenario: Single group expansion

- GIVEN a parsed AST with `RepeatGroupNode(sequence=[StitchNode("sc",1), StitchNode("inc",1)], repeats=6)`
- WHEN the expansion visitor processes the AST
- THEN it produces 12 flat StitchNodes: `[sc, inc]` repeated 6 times

### Requirement: Nested Repetition Groups

The system MUST support repetition groups nested within other repetition groups.

#### Scenario: Two-level nesting

- GIVEN a parsed AST for pattern `"1) [[1sc] 2v 1inc] 3v"`
- WHEN the expansion visitor processes the AST
- THEN the inner group `[1sc] 2v` expands first to `[sc, sc]`, then the outer group expands to 3 copies of `[sc, sc, inc]` = 9 flat stitches

### Requirement: Repetition with Modifiers

The system MUST preserve any texture modifiers (FLO, BLO) applied to stitches inside repetition groups.

#### Scenario: Modifiers preserved in expansion

- GIVEN a parsed AST for pattern `"[1sc_FLO 1inc] 3v"`
- WHEN the expansion visitor processes the AST
- THEN each `sc` in the output retains the `FLO` modifier
