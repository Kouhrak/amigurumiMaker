// src/domain/RowExpander.ts
// Port of Kotlin RowExpander — expands row definitions into individual rows

export interface RowDef {
  startRow: number;
  endRow: number;
  tokens: { type: string; count: number }[];
}

export interface ExpandedRow {
  startRow: number;
  tokens: { type: string; count: number }[];
}

/**
 * Expand a RowDef with a range of row numbers into individual ExpandedRows.
 * If startRow == endRow, returns a single row.
 */
export function expand(rowDef: RowDef): ExpandedRow[] {
  const rows: ExpandedRow[] = [];

  for (let row = rowDef.startRow; row <= rowDef.endRow; row++) {
    rows.push({
      startRow: row,
      tokens: rowDef.tokens.map((t) => ({ ...t })),
    });
  }

  return rows;
}
