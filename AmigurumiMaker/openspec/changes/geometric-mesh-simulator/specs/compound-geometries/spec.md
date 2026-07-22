# Compound Geometries Specification

## Purpose

Define complex stitch geometries (V-stitch, puff, popcorn, fan) as multi-stitch logical units with custom vertex generation rules.

## Requirements

### Requirement: V-Stitch Geometry

The system MUST support the V-stitch: `(dc, ch 1, dc)` in the same stitch, consuming 1 stitch and producing 2 stitches.

#### Scenario: V-stitch produces 2 stitches

- GIVEN a stitch dictionary containing a V-stitch compound definition
- WHEN the geometry computer processes a V-stitch at row N, position P
- THEN it produces 2 output stitches at row N+1, consuming 1 stitch below

### Requirement: Puff Stitch Geometry

The system MUST support the puff stitch: multiple incomplete double crochets joined at top, consuming 1 stitch and producing 1 decorative stitch.

#### Scenario: Puff stitch height

- GIVEN a puff stitch definition with 5 incomplete dc
- WHEN the geometry computer queries the puff stitch height
- THEN its height exceeds a single dc height (decorative bulge)

### Requirement: Popcorn Stitch Geometry

The system MUST support the popcorn stitch: 5 completed dc with hook reinserted into the first, consuming 1 stitch and producing 1 decorative stitch.

#### Scenario: Popcorn as single output

- GIVEN a popcorn stitch at row N, position P
- WHEN the spatial engine computes adjacency
- THEN it consumes 1 stitch from row N-1 and produces 1 stitch at row N+1

### Requirement: Fan Stitch Geometry

The system MUST support the fan stitch: `(dc, ch 1, dc)` repeated 3-5 times into the same stitch, consuming 1 stitch and producing 3-5 stitches.

#### Scenario: Fan stitch expansion

- GIVEN a fan stitch definition with 4 repeats
- WHEN the geometry computer processes the fan
- THEN it produces 4 output stitches at row N+1 from 1 input stitch

#### Scenario: Out-of-range fan repeat count

- GIVEN a fan stitch with 0 repeats
- WHEN the geometry computer processes the fan
- THEN it produces an error: "Fan stitch must have 3-5 repeats"
