// src/domain/MeshComputer.ts
// Port of Kotlin MeshComputer — 2D grid cells and 3D cylinder projection

import { ParsedRow, MeshCell, CylinderCell } from './model/models';

/**
 * Compute 2D grid cells for MESH_2D view mode.
 * Each token produces a block at (col, row) coordinates.
 */
export function compute2DCells(rows: ParsedRow[]): MeshCell[] {
  const cells: MeshCell[] = [];

  for (const row of rows) {
    let col = 0;
    const totalBlocks = row.tokens.reduce((sum, t) => sum + t.yieldCount, 0);
    const startX = -Math.floor(totalBlocks / 2);

    for (const token of row.tokens) {
      for (let i = 0; i < token.yieldCount; i++) {
        cells.push({
          row: row.rowIndex,
          col,
          type: token.type,
          x: startX + col,
          y: row.rowIndex - 1,
        });
        col++;
      }
    }
  }

  return cells;
}

/**
 * Compute 3D cylinder projection for CYLINDER_3D view mode.
 * Maps 2D cells to cylindrical coordinates (angle, y, radius).
 */
export function compute3DProjection(rows: ParsedRow[]): CylinderCell[] {
  const cells: CylinderCell[] = [];

  for (const row of rows) {
    const totalBlocks = row.tokens.reduce((sum, t) => sum + t.yieldCount, 0);
    if (totalBlocks === 0) continue;

    let col = 0;
    const baseRadius = 2.0; // Fixed radius for cylinder view

    for (const token of row.tokens) {
      for (let i = 0; i < token.yieldCount; i++) {
        const angle = (col / totalBlocks) * 2.0 * Math.PI;
        cells.push({
          row: row.rowIndex,
          col,
          type: token.type,
          angle,
          y: row.rowIndex - 1,
          radius: baseRadius,
        });
        col++;
      }
    }
  }

  return cells;
}
