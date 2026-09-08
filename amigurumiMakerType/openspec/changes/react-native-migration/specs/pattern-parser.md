# Spec: Pattern Parser & Presets

## Capability
TypeScript regex-based parser for amigurumi pattern syntax, recursive descent parser with AST, and preset patterns. Equivalent to Kotlin PatternParser, Lexer, Parser, AstNode, PresetPatterns.

## Current Behavior (Kotlin)

### PatternParser
- Parses line-based pattern text: `"1) 8c (7p)\n2) 3p 1a 3p 1c (8p)"`
- Regex patterns:
  - `ROW_HEADER_REGEX = ^(\d+)\)\s*` — extracts row number
  - `TOTAL_REGEX = \((\d+)p?\)\s*$` — extracts expected total
  - `TOKEN_REGEX = (\d+)([padc])` — extracts count + stitch type
  - `REPEAT_GROUP_REGEX = \[(.*?)\]\s*(\d+)v` — expands `[seq]Nv` → N repetitions of seq
- Returns `List<ParsedRow>` with validation (isValid, errorMsg)

### AstNode (Sealed Interface)
- PatternNode(rows: List<RowNode>)
- RowNode(rowNumbers: IntRange, stitches: List<StitchNode>, declaredCount: Int?)
- StitchNode(abbreviation: String, count: Int, modifier: String?)
- RepeatGroupNode(sequence: List<AstNode>, repeats: Int)
- CompoundStitchNode(type: CompoundType, repeats: Int)

### PresetPatterns
5 presets: Esfera, Hiperbólica, Cono, Cilindro, Disco Plano

## Target Behavior (TypeScript)
Exact regex equivalents in TypeScript. AST as discriminated unions.

## Interface

```typescript
// src/domain/PatternParser.ts
export function parse(text: string): ParsedRow[];

// src/domain/crochet/AstNode.ts
export type AstNode =
  | { kind: 'Pattern'; rows: RowNode[] }
  | { kind: 'Row'; rowNumbers: [number, number]; stitches: StitchNode[]; declaredCount?: number }
  | { kind: 'Stitch'; abbreviation: string; count: number; modifier?: string }
  | { kind: 'Repeat'; sequence: AstNode[]; repeats: number }
  | { kind: 'Compound'; type: CompoundType; repeats: number };

// src/domain/crochet/Lexer.ts
export interface LexerToken { type: string; value: string; position: number; }
export function tokenize(input: string): LexerToken[];

// src/domain/crochet/Parser.ts
export function parseAst(tokens: LexerToken[]): AstNode;

// src/domain/PresetPatterns.ts
export const PRESETS: PresetPattern[];
```

## Scenarios

### Scenario 1: Parse simple row
- **Given** text: `"1) 8c (7p)"`
- **When** parse is called
- **Then** returns 1 ParsedRow with rowIndex=1, totalCalculated=0, totalExpected=7, isValid=false (chains yield 0)

### Scenario 2: Parse row with increases
- **Given** text: `"2) 3p 1a 3p 1c (8p)"`
- **When** parse is called
- **Then** returns ParsedRow with totalCalculated=8, totalExpected=8, isValid=true, increaseCount=1

### Scenario 3: Expand repeat groups
- **Given** text: `"1) [1p 1a] 6v (12p)"`
- **When** parse is called
- **Then** expands to 6 repetitions of "1p 1a"
- **And** totalCalculated=12

### Scenario 4: Validation error
- **Given** text: `"1) 8c (7p)"` where 8 chains yield 0 stitches but expected 7
- **When** parse is called
- **Then** isValid=false, errorMsg contains "Esperados 7p, calculados 0p"

### Scenario 5: AST discriminated union
- **Given** tokens from a pattern with repeat groups
- **When** parseAst is called
- **Then** returns Pattern node with Row children
- **And** Repeat nodes contain sequence arrays

### Scenario 6: Preset patterns load
- **Given** no arguments
- **When** accessing PRESETS
- **Then** returns 5 PresetPattern objects with name, description, pattern

## Acceptance Criteria
- [ ] All 4 regex patterns match Kotlin exactly
- [ ] Repeat group expansion handles nested patterns
- [ ] Row header extraction works with and without row numbers
- [ ] Total extraction handles optional 'p' suffix
- [ ] Token extraction captures count + type char
- [ ] Validation compares totalCalculated vs totalExpected
- [ ] AST discriminated union covers all 5 node kinds
- [ ] Presets match Kotlin PresetPatterns.presets exactly
