// src/domain/crochet/Parser.ts
// Port of Kotlin Parser — recursive descent parser for crochet AST

import {
  AstNode,
  PatternNode,
  RowNode,
  StitchNode,
  RepeatGroupNode,
  patternNode,
  rowNode,
  stitchNode,
  repeatGroupNode,
} from './AstNode';
import { LexerToken } from './Lexer';

/**
 * Parse a stream of lexer tokens into an AST.
 */
export function parseAst(tokens: LexerToken[]): AstNode {
  const parser = new ParserImpl(tokens);
  return parser.parsePattern();
}

class ParserImpl {
  private tokens: LexerToken[];
  private pos: number;

  constructor(tokens: LexerToken[]) {
    this.tokens = tokens;
    this.pos = 0;
  }

  private peek(): LexerToken {
    return this.tokens[this.pos] ?? { type: 'EOF', value: '', position: -1 };
  }

  private advance(): LexerToken {
    const token = this.tokens[this.pos];
    if (token && token.type !== 'EOF') {
      this.pos++;
    }
    return token ?? { type: 'EOF', value: '', position: -1 };
  }

  private expect(type: string): LexerToken {
    const token = this.peek();
    if (token.type !== type) {
      throw new Error(`Expected ${type}, got ${token.type} at position ${token.position}`);
    }
    return this.advance();
  }

  private isStitchStart(): boolean {
    const t = this.peek().type;
    return t === 'STITCH_ABBREVIATION' || t === 'COUNT';
  }

  private isRowStart(): boolean {
    const t = this.peek().type;
    return t === 'ROW_NUMBER' || t === 'STITCH_ABBREVIATION' || t === 'COUNT' || t === 'REPEAT_OPEN';
  }

  /**
   * Pattern := Row*
   */
  parsePattern(): PatternNode {
    const rows: RowNode[] = [];

    while (this.peek().type !== 'EOF') {
      if (this.isRowStart()) {
        rows.push(this.parseRow());
      } else {
        this.advance(); // skip unexpected token
      }
    }

    return patternNode(rows);
  }

  /**
   * Row := ROW_NUMBER? (Stitch | RepeatGroup)* TOTAL_COUNT?
   */
  private parseRow(): RowNode {
    let rowStart = 1;
    let rowEnd = 1;

    // Parse optional row number
    if (this.peek().type === 'ROW_NUMBER') {
      const rowToken = this.advance();
      const num = parseInt(rowToken.value.replace(')', ''), 10);
      rowStart = num;
      rowEnd = num;
    }

    // Parse stitches and repeat groups
    const stitches: StitchNode[] = [];

    while (
      this.peek().type !== 'EOF' &&
      this.peek().type !== 'TOTAL_COUNT' &&
      this.peek().type !== 'ROW_NUMBER'
    ) {
      if (this.peek().type === 'REPEAT_OPEN') {
        const group = this.parseRepeatGroup();
        // Unroll repeat group into individual stitch nodes
        for (let i = 0; i < group.repeats; i++) {
          for (const node of group.sequence) {
            if (node.kind === 'Stitch') {
              // Expand count: stitchNode('p', 2) → 2 individual stitches
              for (let j = 0; j < node.count; j++) {
                stitches.push(stitchNode(node.abbreviation, 1));
              }
            }
          }
        }
      } else if (this.isStitchStart()) {
        stitches.push(this.parseStitch());
      } else {
        this.advance(); // skip unexpected
      }
    }

    // Parse optional total count
    let declaredCount: number | undefined;
    if (this.peek().type === 'TOTAL_COUNT') {
      const totalToken = this.advance();
      const match = totalToken.value.match(/\((\d+)/);
      if (match) {
        declaredCount = parseInt(match[1], 10);
      }
    }

    return rowNode([rowStart, rowEnd], stitches, declaredCount);
  }

  /**
   * Stitch := COUNT? STITCH_ABBREVIATION
   */
  private parseStitch(): StitchNode {
    let count = 1;

    if (this.peek().type === 'COUNT') {
      count = parseInt(this.advance().value, 10);
    }

    const abbr = this.expect('STITCH_ABBREVIATION');
    return stitchNode(abbr.value, count);
  }

  /**
   * RepeatGroup := '[' (Stitch | RepeatGroup)* ']' COUNT? 'v'
   */
  private parseRepeatGroup(): RepeatGroupNode {
    this.expect('REPEAT_OPEN');

    const sequence: AstNode[] = [];

    while (this.peek().type !== 'REPEAT_CLOSE' && this.peek().type !== 'EOF') {
      if (this.peek().type === 'REPEAT_OPEN') {
        sequence.push(this.parseRepeatGroup());
      } else if (this.isStitchStart()) {
        sequence.push(this.parseStitch());
      } else {
        this.advance(); // skip unexpected
      }
    }

    this.expect('REPEAT_CLOSE');

    // Parse optional multiplier before 'v'
    let repeats = 1;
    if (this.peek().type === 'COUNT') {
      repeats = parseInt(this.advance().value, 10);
    }

    // Consume 'v'
    if (this.peek().type === 'REPEAT_MULTIPLIER') {
      this.advance();
    }

    return repeatGroupNode(sequence, repeats);
  }
}
