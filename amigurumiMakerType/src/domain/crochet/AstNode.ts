// src/domain/crochet/AstNode.ts
// Port of Kotlin AstNode sealed interface — TypeScript discriminated unions

import { CompoundType } from '../model/types';

/**
 * AST node types for crochet pattern parsing.
 * Discriminated union on `kind` field.
 */
export type AstNode =
  | PatternNode
  | RowNode
  | StitchNode
  | RepeatGroupNode
  | CompoundStitchNode;

export interface PatternNode {
  kind: 'Pattern';
  rows: RowNode[];
}

export interface RowNode {
  kind: 'Row';
  rowNumbers: [number, number]; // [start, end] inclusive range
  stitches: StitchNode[];
  declaredCount?: number;
}

export interface StitchNode {
  kind: 'Stitch';
  abbreviation: string;
  count: number;
  modifier?: string;
}

export interface RepeatGroupNode {
  kind: 'Repeat';
  sequence: AstNode[];
  repeats: number;
}

export interface CompoundStitchNode {
  kind: 'Compound';
  type: CompoundType;
  repeats: number;
}

// --- Factory functions ---

export function patternNode(rows: RowNode[]): PatternNode {
  return { kind: 'Pattern', rows };
}

export function rowNode(
  rowNumbers: [number, number],
  stitches: StitchNode[],
  declaredCount?: number
): RowNode {
  return { kind: 'Row', rowNumbers, stitches, declaredCount };
}

export function stitchNode(abbreviation: string, count: number, modifier?: string): StitchNode {
  return { kind: 'Stitch', abbreviation, count, modifier };
}

export function repeatGroupNode(sequence: AstNode[], repeats: number): RepeatGroupNode {
  return { kind: 'Repeat', sequence, repeats };
}

export function compoundStitchNode(type: CompoundType, repeats: number): CompoundStitchNode {
  return { kind: 'Compound', type, repeats };
}
