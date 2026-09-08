// src/domain/crochet/Lexer.ts
// Port of Kotlin Lexer — tokenizes crochet pattern text
// Uses substring matching instead of global regex to avoid state issues

export interface LexerToken {
  type: TokenType;
  value: string;
  position: number;
}

export type TokenType =
  | 'ROW_NUMBER'
  | 'STITCH_ABBREVIATION'
  | 'COUNT'
  | 'REPEAT_OPEN'      // [
  | 'REPEAT_CLOSE'     // ]
  | 'REPEAT_MULTIPLIER' // v (after ])
  | 'TOTAL_COUNT'      // (Np) or (N)
  | 'WHITESPACE'
  | 'EOF';

// Non-global regexes for matching at a specific position
const ROW_NUMBER_RE = /^\d+\)/;
const TOTAL_COUNT_RE = /^\(\d+p?\)/;
const REPEAT_MULTIPLIER_RE = /^v(?=\s|$|\))/;
const REPEAT_OPEN_RE = /^\[/;
const REPEAT_CLOSE_RE = /^\]/;
const COUNT_RE = /^\d+/;
const STITCH_RE = /^[padc]/i;
const WHITESPACE_RE = /^\s+/;

interface PatternEntry {
  type: TokenType;
  re: RegExp;
}

const PATTERNS: PatternEntry[] = [
  { type: 'TOTAL_COUNT', re: TOTAL_COUNT_RE },
  { type: 'ROW_NUMBER', re: ROW_NUMBER_RE },
  { type: 'REPEAT_MULTIPLIER', re: REPEAT_MULTIPLIER_RE },
  { type: 'REPEAT_OPEN', re: REPEAT_OPEN_RE },
  { type: 'REPEAT_CLOSE', re: REPEAT_CLOSE_RE },
  { type: 'COUNT', re: COUNT_RE },
  { type: 'STITCH_ABBREVIATION', re: STITCH_RE },
  { type: 'WHITESPACE', re: WHITESPACE_RE },
];

/**
 * Tokenize a crochet pattern line into a stream of tokens.
 * Uses anchored regexes (^) to match at the current position.
 */
export function tokenize(input: string): LexerToken[] {
  const tokens: LexerToken[] = [];
  let pos = 0;

  while (pos < input.length) {
    const remaining = input.slice(pos);
    let matched = false;

    for (const { type, re } of PATTERNS) {
      const match = re.exec(remaining);

      if (match) {
        // Skip whitespace tokens in output
        if (type !== 'WHITESPACE') {
          tokens.push({ type, value: match[0], position: pos });
        }
        pos += match[0].length;
        matched = true;
        break;
      }
    }

    if (!matched) {
      // Skip unrecognized character
      pos++;
    }
  }

  tokens.push({ type: 'EOF', value: '', position: pos });
  return tokens;
}
