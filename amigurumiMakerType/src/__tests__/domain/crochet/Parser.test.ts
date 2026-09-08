// src/__tests__/domain/crochet/Parser.test.ts

import { parseAst } from '../../../domain/crochet/Parser';
import { tokenize } from '../../../domain/crochet/Lexer';
import { AstNode } from '../../../domain/crochet/AstNode';

describe('Lexer', () => {
  it('tokenizes simple stitch', () => {
    const tokens = tokenize('3p');
    expect(tokens).toEqual([
      { type: 'COUNT', value: '3', position: 0 },
      { type: 'STITCH_ABBREVIATION', value: 'p', position: 1 },
      { type: 'EOF', value: '', position: 2 },
    ]);
  });

  it('tokenizes row with header', () => {
    const tokens = tokenize('1) 3p 1a');
    expect(tokens[0]).toEqual({ type: 'ROW_NUMBER', value: '1)', position: 0 });
    expect(tokens[1]).toEqual({ type: 'COUNT', value: '3', position: 3 });
    expect(tokens[2]).toEqual({ type: 'STITCH_ABBREVIATION', value: 'p', position: 4 });
    expect(tokens[3]).toEqual({ type: 'COUNT', value: '1', position: 6 });
    expect(tokens[4]).toEqual({ type: 'STITCH_ABBREVIATION', value: 'a', position: 7 });
  });

  it('tokenizes repeat group', () => {
    const tokens = tokenize('[1p 1a] 6v');
    expect(tokens[0]).toEqual({ type: 'REPEAT_OPEN', value: '[', position: 0 });
    expect(tokens[1]).toEqual({ type: 'COUNT', value: '1', position: 1 });
    expect(tokens[2]).toEqual({ type: 'STITCH_ABBREVIATION', value: 'p', position: 2 });
    expect(tokens[3]).toEqual({ type: 'COUNT', value: '1', position: 4 });
    expect(tokens[4]).toEqual({ type: 'STITCH_ABBREVIATION', value: 'a', position: 5 });
    expect(tokens[5]).toEqual({ type: 'REPEAT_CLOSE', value: ']', position: 6 });
    expect(tokens[6]).toEqual({ type: 'COUNT', value: '6', position: 8 });
    expect(tokens[7]).toEqual({ type: 'REPEAT_MULTIPLIER', value: 'v', position: 9 });
  });

  it('tokenizes total count', () => {
    const tokens = tokenize('(12p)');
    expect(tokens[0]).toEqual({ type: 'TOTAL_COUNT', value: '(12p)', position: 0 });
  });
});

describe('Parser', () => {
  it('parses simple stitches', () => {
    const tokens = tokenize('3p 1a 3p');
    const ast = parseAst(tokens);

    expect(ast.kind).toBe('Pattern');
    if (ast.kind === 'Pattern') {
      expect(ast.rows).toHaveLength(1);
      const row = ast.rows[0];
      expect(row.stitches).toHaveLength(3);
      expect(row.stitches[0].abbreviation).toBe('p');
      expect(row.stitches[0].count).toBe(3);
      expect(row.stitches[1].abbreviation).toBe('a');
      expect(row.stitches[1].count).toBe(1);
    }
  });

  it('parses row with header', () => {
    const tokens = tokenize('2) 3p 1a (8p)');
    const ast = parseAst(tokens);

    if (ast.kind === 'Pattern') {
      expect(ast.rows).toHaveLength(1);
      expect(ast.rows[0].rowNumbers).toEqual([2, 2]);
      expect(ast.rows[0].declaredCount).toBe(8);
    }
  });

  it('parses repeat group', () => {
    const tokens = tokenize('[1p 1a] 6v');
    const ast = parseAst(tokens);

    if (ast.kind === 'Pattern') {
      expect(ast.rows).toHaveLength(1);
      // [1p 1a] 6v → 6 × 2 = 12 stitches
      expect(ast.rows[0].stitches).toHaveLength(12);
    }
  });

  it('parses multiple rows', () => {
    const tokens = tokenize('1) 6c\n2) [1p 1a] 6v');
    const ast = parseAst(tokens);

    if (ast.kind === 'Pattern') {
      expect(ast.rows).toHaveLength(2);
      expect(ast.rows[0].rowNumbers).toEqual([1, 1]);
      expect(ast.rows[1].rowNumbers).toEqual([2, 2]);
    }
  });

  it('parses complex pattern', () => {
    const tokens = tokenize('3) [2p 1a] 6v (24p)');
    const ast = parseAst(tokens);

    if (ast.kind === 'Pattern') {
      expect(ast.rows).toHaveLength(1);
      // [2p 1a] 6v → 6 × 3 = 18 stitches
      expect(ast.rows[0].stitches).toHaveLength(18);
      expect(ast.rows[0].declaredCount).toBe(24);
    }
  });
});
