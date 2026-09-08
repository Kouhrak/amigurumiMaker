// src/engine/useAmigurumiMesh.ts
// Custom hook: generates THREE.BufferGeometry from RoundAnalysis[]

import { useMemo } from 'react';
import * as THREE from 'three';
import { RoundAnalysis, MeshSettings, ColorMode } from '../domain/model/models';
import { MIN_SEGMENTS } from '../domain/constants';
import { getSegmentColor } from './colorUtils';

export interface MeshData {
  geometry: THREE.BufferGeometry;
  segmentCount: number;
  vertexCount: number;
  indexCount: number;
}

/**
 * Generate a revolution mesh geometry from round analyses.
 *
 * For each adjacent row pair (i, i+1):
 *   S = max(N_i, N_{i+1}, MIN_SEGMENTS) radial segments
 *   φ_k = k/S * 2π
 *   Bottom vertices: (r1*cos(φ), liftZ1, r1*sin(φ))
 *   Top vertices:     (r2*cos(φ), liftZ2, r2*sin(φ))
 *   2 triangles per quad
 *   Center Y by subtracting totalHeight/2
 *
 * Returns null if fewer than 2 analyses.
 */
export function useAmigurumiMesh(
  analyses: RoundAnalysis[],
  settings: MeshSettings
): MeshData | null {
  return useMemo(() => {
    if (analyses.length < 2) return null;

    const positions: number[] = [];
    const colors: number[] = [];
    const indices: number[] = [];

    const totalRows = analyses.length;
    const totalHeight = analyses[totalRows - 1].liftZ - analyses[0].liftZ;
    const centerOffset = totalHeight / 2;

    for (let i = 0; i < totalRows - 1; i++) {
      const rowBot = analyses[i];
      const rowTop = analyses[i + 1];

      const r1 = rowBot.theoreticalRadius;
      const r2 = rowTop.theoreticalRadius;
      const z1 = rowBot.liftZ - centerOffset; // Pre-center
      const z2 = rowTop.liftZ - centerOffset;

      const S = Math.max(rowBot.stitchCount, rowTop.stitchCount, MIN_SEGMENTS);
      const segmentStart = positions.length / 3;
      const color = getSegmentColor(i, totalRows - 1, rowBot.localCurvature, settings.colorMode);

      for (let k = 0; k <= S; k++) {
        const phi = (k / S) * Math.PI * 2;

        // Bottom ring vertex
        positions.push(r1 * Math.cos(phi), z1, r1 * Math.sin(phi));
        colors.push(color.r, color.g, color.b);

        // Top ring vertex
        positions.push(r2 * Math.cos(phi), z2, r2 * Math.sin(phi));
        colors.push(color.r, color.g, color.b);

        if (k < S) {
          // Indices for 2 triangles (4 vertices: bl, tl, br, tr)
          const bl = segmentStart + k * 2;
          const tl = bl + 1;
          const br = bl + 2;
          const tr = bl + 3;

          indices.push(bl, tl, br);
          indices.push(tl, tr, br);
        }
      }
    }

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.Float32BufferAttribute(positions, 3));
    geometry.setAttribute('color', new THREE.Float32BufferAttribute(colors, 3));
    geometry.setIndex(indices);
    geometry.computeVertexNormals();

    return {
      geometry,
      segmentCount: totalRows - 1,
      vertexCount: positions.length / 3,
      indexCount: indices.length,
    };
  }, [analyses, settings.colorMode]);
}
