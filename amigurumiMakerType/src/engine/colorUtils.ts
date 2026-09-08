// src/engine/colorUtils.ts
// Color mapping for revolution mesh segments

import * as THREE from 'three';
import { ColorMode } from '../domain/model/types';
import {
  GAUSS_POSITIVE_COLOR,
  GAUSS_NEGATIVE_COLOR,
  GAUSS_NEUTRAL_COLOR,
  GAUSS_POSITIVE_THRESHOLD,
  GAUSS_NEGATIVE_THRESHOLD,
} from '../domain/constants';

/**
 * Map curvature value + row position to a THREE.Color based on color mode.
 *
 * GAUSS_HEATMAP:
 *   K > 0.1  → green  (0.06, 0.72, 0.50)
 *   K < -0.1 → pink   (0.95, 0.25, 0.37)
 *   else     → cyan   (0.02, 0.71, 0.83)
 *
 * ROW_GRADIENT:
 *   Interpolates HSL from 240° (blue) to 360° (red) based on row index.
 */
export function getSegmentColor(
  rowIndex: number,
  totalRows: number,
  curvature: number,
  colorMode: ColorMode
): THREE.Color {
  if (colorMode === 'GAUSS_HEATMAP') {
    if (curvature > GAUSS_POSITIVE_THRESHOLD) {
      return new THREE.Color(GAUSS_POSITIVE_COLOR.x, GAUSS_POSITIVE_COLOR.y, GAUSS_POSITIVE_COLOR.z);
    }
    if (curvature < GAUSS_NEGATIVE_THRESHOLD) {
      return new THREE.Color(GAUSS_NEGATIVE_COLOR.x, GAUSS_NEGATIVE_COLOR.y, GAUSS_NEGATIVE_COLOR.z);
    }
    return new THREE.Color(GAUSS_NEUTRAL_COLOR.x, GAUSS_NEUTRAL_COLOR.y, GAUSS_NEUTRAL_COLOR.z);
  }

  // ROW_GRADIENT
  const t = totalRows > 1 ? rowIndex / (totalRows - 1) : 0;
  const hue = (240 + t * 120) / 360; // 240° (blue) → 360° (red)
  return hslToThreeColor(hue, 0.7, 0.5);
}

/**
 * HSL to THREE.Color. h, s, l in [0..1].
 */
export function hslToThreeColor(h: number, s: number, l: number): THREE.Color {
  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - Math.abs(((h * 6) % 2) - 1));
  const m = l - c / 2;

  let r: number, g: number, b: number;

  if (h < 1 / 6) {
    r = c; g = x; b = 0;
  } else if (h < 2 / 6) {
    r = x; g = c; b = 0;
  } else if (h < 3 / 6) {
    r = 0; g = c; b = x;
  } else if (h < 4 / 6) {
    r = 0; g = x; b = c;
  } else if (h < 5 / 6) {
    r = x; g = 0; b = c;
  } else {
    r = c; g = 0; b = x;
  }

  return new THREE.Color(r + m, g + m, b + m);
}
