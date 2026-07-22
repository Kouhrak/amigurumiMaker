# Stitch Dictionary Specification

## Purpose

A data-driven registry mapping crochet stitch abbreviations to their spatial geometry parameters, enabling the parser and geometry engine to resolve stitch types without hardcoding.

## Requirements

### Requirement: Stitch Registry

The system MUST provide a read-only registry of stitch definitions, each containing: `abbreviation`, `displayName`, `baseBottom`, `baseTop`, `height`, `consumesStitches`, `producesStitches`, and `allowedTextureModifiers`.

#### Scenario: Standard stitch lookup

- GIVEN a stitch registry initialized with the MVP set (ch, sl st, sc, hdc, dc, inc, dec)
- WHEN the registry is queried with abbreviation `"sc"`
- THEN it returns a `StitchDef` with `displayName = "Single Crochet"`, `baseBottom = 1`, `baseTop = 1`, `height = 1`, `consumesStitches = 1`, `producesStitches = 1`

#### Scenario: Increase stitch properties

- GIVEN a stitch registry containing `"inc"`
- WHEN the registry is queried with `"inc"`
- THEN it returns a StitchDef where `producesStitches = 2`, `consumesStitches = 1`, and `baseTop = 2 * baseBottom`

#### Scenario: Decrease stitch properties

- GIVEN a stitch registry containing `"dec"`
- WHEN the registry is queried with `"dec"`
- THEN it returns a StitchDef where `producesStitches = 1`, `consumesStitches = 2`, and `baseBottom = 2 * baseTop`

#### Scenario: Unknown abbreviation returns null

- GIVEN a stitch registry with the MVP set
- WHEN the registry is queried with `"unknown_stitch"`
- THEN it returns null or throws a controlled error

### Requirement: Texture Modifier Support

The system SHOULD define allowed texture modifiers per stitch (FLO, BLO, front-post, back-post), though visual rendering MAY be deferred.

#### Scenario: Texture modifier listing

- GIVEN a stitch registry containing `"sc"`
- WHEN `allowedTextureModifiers` is inspected for `"sc"`
- THEN it returns a non-empty set including `"FLO"` and `"BLO"`

#### Scenario: Stitch without texture modifiers

- GIVEN a stitch registry containing `"ch"`
- WHEN `allowedTextureModifiers` is inspected for `"ch"`
- THEN it returns an empty set (chain stitches have no loop-only variant)
