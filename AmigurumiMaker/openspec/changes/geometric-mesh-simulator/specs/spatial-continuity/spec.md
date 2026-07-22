# Spatial Continuity Specification

## Purpose

Track stitch-to-stitch spatial relationships across rows using a block adjacency engine, enabling correct 3D mesh positioning for increases, decreases, and compound stitches.

## Requirements

### Requirement: Block Definition

Each stitch block MUST carry: `row`, `positionInRow`, `baseBottomWidth`, `baseTopWidth`, and `height`.

#### Scenario: Standard stitch block

- GIVEN a single crochet stitch at row 2, position 3
- WHEN a `StitchBlock` is created for it
- THEN `baseBottomWidth = 1`, `baseTopWidth = 1`, `height = 1`

#### Scenario: Increase block geometry

- GIVEN an increase stitch (inc) at row 2, position 3
- WHEN a `StitchBlock` is created for it
- THEN `baseBottomWidth = 1`, `baseTopWidth = 2` (inverted trapezoid)

#### Scenario: Decrease block geometry

- GIVEN a decrease stitch (dec) at row 2, position 3
- WHEN a `StitchBlock` is created for it
- THEN `baseBottomWidth = 2`, `baseTopWidth = 1` (trapezoid)

### Requirement: Row-Level Adjacency

The spatial engine MUST compute which stitch blocks in row N support which blocks in row N+1.

#### Scenario: 1-to-1 adjacency

- GIVEN row 1 with 3 single crochet blocks and row 2 with 3 single crochet blocks
- WHEN adjacency is computed
- THEN block at (1,0) supports block at (2,0); (1,1) supports (2,1); (1,2) supports (2,2)

#### Scenario: Increase adjacency (1-to-2)

- GIVEN row 1 has a single block (inc) at position 0 with `producesStitches = 2`
- WHEN adjacency is computed
- THEN block (1,0) supports blocks (2,0) AND (2,1)

#### Scenario: Decrease adjacency (2-to-1)

- GIVEN row 1 has 2 blocks (sc, dec with `consumesStitches = 2`)
- WHEN adjacency is computed
- THEN blocks (1,0) and (1,1) both support block (2,0)

### Requirement: Width Extrema Computation

The spatial engine MUST compute the minimum and maximum width at each row level.

#### Scenario: Row width after increase

- GIVEN a piece with 10 sc in row 1 and [10 inc] in row 2
- WHEN width extrema are computed
- THEN row 1 has `minWidth = 10, maxWidth = 10`; row 2 has `minWidth = 10, maxWidth = 20`

#### Scenario: Empty row width

- GIVEN a pattern with no stitches in row 3
- WHEN width extrema are computed
- THEN row 3 has `minWidth = 0, maxWidth = 0`
