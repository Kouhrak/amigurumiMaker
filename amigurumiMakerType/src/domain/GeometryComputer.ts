// src/domain/GeometryComputer.ts
// Port of Kotlin GeometryComputer — 2D block geometry (cubes, ramps)

import { BlockGeometry, Float3 } from './model/models';

type TokenType = 'CUBE' | 'RAMP_LEFT' | 'RAMP_RIGHT' | 'PLACEHOLDER';

interface RowToken {
  type: TokenType;
  count: number;
}

/**
 * Compute block geometries for a list of token rows.
 * Each token produces cube, ramp_left, or ramp_right geometry.
 */
export function compute(tokens: RowToken[]): BlockGeometry[] {
  const geometries: BlockGeometry[] = [];

  for (const row of tokens) {
    let blockIndex = 0;
    const totalBlocks = row.count;

    for (let i = 0; i < row.count; i++) {
      const cx = -Math.floor(totalBlocks / 2) + blockIndex;
      const cy = 0; // Single row, y=0
      const cz = 0;

      let actualType = row.type;
      // For ramp types, split by position relative to center
      if (row.type === 'RAMP_LEFT' || row.type === 'RAMP_RIGHT') {
        actualType = blockIndex < totalBlocks / 2 ? 'RAMP_LEFT' : 'RAMP_RIGHT';
      }

      let geometry: BlockGeometry;
      switch (actualType) {
        case 'CUBE':
          geometry = cubeGeometry(cx, cy, cz);
          break;
        case 'RAMP_LEFT':
          geometry = rampLeftGeometry(cx, cy, cz);
          break;
        case 'RAMP_RIGHT':
          geometry = rampRightGeometry(cx, cy, cz);
          break;
        default:
          geometry = { vertices: [], edges: [], faceIndices: [] };
          break;
      }

      geometries.push(geometry);
      blockIndex++;
    }
  }

  return geometries;
}

/**
 * Cube: 8 vertices, 12 edges, 12 face indices (6 faces × 2 triangles)
 */
function cubeGeometry(cx: number, cy: number, cz: number): BlockGeometry {
  const vertices: Float3[] = [
    { x: -0.5 + cx, y: -0.5 + cy, z: -0.5 + cz }, // 0: left-bottom-front
    { x: 0.5 + cx, y: -0.5 + cy, z: -0.5 + cz },  // 1: right-bottom-front
    { x: 0.5 + cx, y: 0.5 + cy, z: -0.5 + cz },   // 2: right-top-front
    { x: -0.5 + cx, y: 0.5 + cy, z: -0.5 + cz },  // 3: left-top-front
    { x: -0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },  // 4: left-bottom-back
    { x: 0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },   // 5: right-bottom-back
    { x: 0.5 + cx, y: 0.5 + cy, z: 0.5 + cz },    // 6: right-top-back
    { x: -0.5 + cx, y: 0.5 + cy, z: 0.5 + cz },   // 7: left-top-back
  ];

  const edges: [number, number][] = [
    [0, 1], [1, 2], [2, 3], [3, 0],
    [4, 5], [5, 6], [6, 7], [7, 4],
    [0, 4], [1, 5], [2, 6], [3, 7],
  ];

  const faceIndices: [number, number, number][] = [
    // Front
    [0, 2, 1], [0, 3, 2],
    // Back
    [4, 5, 6], [4, 6, 7],
    // Top
    [3, 2, 6], [3, 6, 7],
    // Bottom
    [0, 1, 5], [0, 5, 4],
    // Right
    [1, 2, 6], [1, 6, 5],
    // Left
    [0, 4, 7], [0, 7, 3],
  ];

  return { vertices, edges, faceIndices };
}

/**
 * RAMP_LEFT: triangular prism, diagonal from lower-left to upper-right.
 * 6 vertices, 9 edges, 8 face indices.
 */
function rampLeftGeometry(cx: number, cy: number, cz: number): BlockGeometry {
  const vertices: Float3[] = [
    { x: -0.5 + cx, y: -0.5 + cy, z: -0.5 + cz }, // 0
    { x: 0.5 + cx, y: -0.5 + cy, z: -0.5 + cz },  // 1
    { x: 0.5 + cx, y: 0.5 + cy, z: -0.5 + cz },   // 2
    { x: -0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },  // 3
    { x: 0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },   // 4
    { x: 0.5 + cx, y: 0.5 + cy, z: 0.5 + cz },    // 5
  ];

  const edges: [number, number][] = [
    [0, 1], [1, 2], [2, 0],
    [3, 4], [4, 5], [5, 3],
    [0, 3], [1, 4], [2, 5],
  ];

  const faceIndices: [number, number, number][] = [
    [0, 1, 2],              // Front
    [3, 5, 4],              // Back
    [0, 1, 4], [0, 4, 3],  // Bottom
    [1, 2, 5], [1, 5, 4],  // Right
    [2, 0, 3], [2, 3, 5],  // Diagonal
  ];

  return { vertices, edges, faceIndices };
}

/**
 * RAMP_RIGHT: triangular prism, diagonal from lower-right to upper-left.
 * 6 vertices, 9 edges, 8 face indices.
 */
function rampRightGeometry(cx: number, cy: number, cz: number): BlockGeometry {
  const vertices: Float3[] = [
    { x: -0.5 + cx, y: -0.5 + cy, z: -0.5 + cz }, // 0
    { x: 0.5 + cx, y: -0.5 + cy, z: -0.5 + cz },  // 1
    { x: -0.5 + cx, y: 0.5 + cy, z: -0.5 + cz },  // 2
    { x: -0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },  // 3
    { x: 0.5 + cx, y: -0.5 + cy, z: 0.5 + cz },   // 4
    { x: -0.5 + cx, y: 0.5 + cy, z: 0.5 + cz },   // 5
  ];

  const edges: [number, number][] = [
    [0, 1], [1, 2], [2, 0],
    [3, 4], [4, 5], [5, 3],
    [0, 3], [1, 4], [2, 5],
  ];

  const faceIndices: [number, number, number][] = [
    [0, 1, 2],              // Front
    [3, 5, 4],              // Back
    [0, 1, 4], [0, 4, 3],  // Bottom
    [0, 2, 5], [0, 5, 3],  // Left
    [1, 2, 5], [1, 5, 4],  // Diagonal
  ];

  return { vertices, edges, faceIndices };
}
