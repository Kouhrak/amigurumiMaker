// src/domain/PatternParser.ts
// Port of Kotlin PatternParser — regex-based amigurumi pattern parser

import { ParsedRow, ParsedToken } from './model/models';
import { StitchType, STITCH_YIELD } from './model/types';

// Regex patterns — exact port from Kotlin
const ROW_HEADER_REGEX = /^(\d+)\)\s*/;
const TOTAL_REGEX = /\((\d+)p?\)\s*$/;
const TOKEN_REGEX = /(\d+)([padc])/g;
const REPEAT_GROUP_REGEX = /\[([^\[\]]*)\]\s*(\d+)v/g;

/**
 * Parse amigurumi pattern text into structured rows.
 * Each line is parsed independently with row header, tokens, and optional total.
 */
export function parse(text: string): ParsedRow[] {
  const lines = text.split('\n').map((l) => l.trim()).filter((l) => l.length > 0);
  if (lines.length === 0) return [];

  return lines.map((line, index) => parseLine(line, index + 1));
}

/**
 * Parse a single line of pattern text.
 */
function parseLine(line: string, defaultIndex: number): ParsedRow {
  let remaining = line;

  // Extract row header (e.g., "1) ")
  const rowHeaderMatch = ROW_HEADER_REGEX.exec(remaining);
  let rowIndex: number;
  if (rowHeaderMatch) {
    remaining = remaining.replace(ROW_HEADER_REGEX, '');
    rowIndex = parseInt(rowHeaderMatch[1], 10);
  } else {
    rowIndex = defaultIndex;
  }

  // Extract expected total (e.g., "(12p)" or "(12)")
  const totalMatch = TOTAL_REGEX.exec(remaining);
  let totalExpected: number | null;
  if (totalMatch) {
    remaining = remaining.replace(TOTAL_REGEX, '').trim();
    totalExpected = parseInt(totalMatch[1], 10);
  } else {
    totalExpected = null;
  }

  // Expand repeat groups (e.g., "[1p 1a] 6v" → 6 repetitions)
  const expandedText = expandRepeatGroups(remaining);

  // Extract tokens (e.g., "3p 1a" → [{count:3, type:'p'}, {count:1, type:'a'}])
  const tokens = parseTokens(expandedText);

  // Calculate totals
  let calculatedStitches = 0;
  let increaseCount = 0;
  let decreaseCount = 0;

  const parsedTokens: ParsedToken[] = tokens.map(([count, typeChar]) => {
    const stitchType = charToStitchType(typeChar);
    const yieldCount = STITCH_YIELD[stitchType] * count;
    calculatedStitches += yieldCount;
    if (stitchType === StitchType.INCREASE) increaseCount += count;
    if (stitchType === StitchType.DECREASE) decreaseCount += count;
    return { type: stitchType, count, yieldCount };
  });

  const isValid = totalExpected === null || totalExpected === calculatedStitches;
  const errorMsg = !isValid
    ? `Esperados ${totalExpected}p, pero calculados ${calculatedStitches}p.`
    : '';

  return {
    raw: line,
    rowIndex,
    tokens: parsedTokens,
    totalCalculated: calculatedStitches,
    totalExpected,
    isValid,
    errorMsg,
    increaseCount,
    decreaseCount,
  };
}

/**
 * Expand repeat groups: "[seq]Nv" → N repetitions of seq separated by spaces.
 * Handles nested brackets by replacing inner groups first.
 */
export function expandRepeatGroups(input: string): string {
  let result = input;
  let match: RegExpExecArray | null;

  // Keep expanding until no more repeat groups
  // Reset lastIndex for global regex
  REPEAT_GROUP_REGEX.lastIndex = 0;
  match = REPEAT_GROUP_REGEX.exec(result);

  while (match !== null) {
    const sequence = match[1];
    const multiplier = parseInt(match[2], 10);
    const expanded = Array.from({ length: multiplier }, () => sequence).join(' ');
    result = result.slice(0, match.index) + expanded + result.slice(match.index + match[0].length);

    // Reset and search again (string changed)
    REPEAT_GROUP_REGEX.lastIndex = 0;
    match = REPEAT_GROUP_REGEX.exec(result);
  }

  return result;
}

/**
 * Parse tokens from expanded text. Matches patterns like "3p", "1a", "2d", "1c".
 * Returns array of [count, typeChar] pairs.
 */
function parseTokens(text: string): [number, string][] {
  const tokens: [number, string][] = [];
  let match: RegExpExecArray | null;

  TOKEN_REGEX.lastIndex = 0;
  match = TOKEN_REGEX.exec(text);

  while (match !== null) {
    const count = parseInt(match[1], 10);
    const typeChar = match[2];
    tokens.push([count, typeChar]);
    match = TOKEN_REGEX.exec(text);
  }

  return tokens;
}

/**
 * Convert single character to StitchType.
 */
function charToStitchType(char: string): StitchType {
  switch (char) {
    case 'p': return StitchType.NORMAL;
    case 'a': return StitchType.INCREASE;
    case 'd': return StitchType.DECREASE;
    case 'c': return StitchType.CHAIN;
    default: return StitchType.NORMAL;
  }
}
