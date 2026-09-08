// src/domain/constants.ts
// Mathematical constants and color definitions

import { Float3 } from './model/models';
import { ColorMode } from './model/types';

// --- Geometry constants (from Kotlin CurvatureComputer) ---

/** Default stitch width in cm */
export const STITCH_WIDTH = 0.5;

/** Default stitch height in cm */
export const STITCH_HEIGHT = 0.5;

// --- Curvature thresholds ---

/** K > 0.1 → positive curvature (sphere-like) */
export const GAUSS_POSITIVE_THRESHOLD = 0.1;

/** K < -0.1 → negative curvature (hyperbolic) */
export const GAUSS_NEGATIVE_THRESHOLD = -0.1;

// --- Colors (RGB [0..1]) ---

/** Green — positive Gaussian curvature */
export const GAUSS_POSITIVE_COLOR: Float3 = { x: 0.06, y: 0.72, z: 0.50 };

/** Pink/Rose — negative Gaussian curvature */
export const GAUSS_NEGATIVE_COLOR: Float3 = { x: 0.95, y: 0.25, z: 0.37 };

/** Cyan — near-zero curvature (flat/cylindrical) */
export const GAUSS_NEUTRAL_COLOR: Float3 = { x: 0.02, y: 0.71, z: 0.83 };

// --- Viewport defaults ---

/** Default camera position (Z-axis, looking at origin) */
export const DEFAULT_CAMERA_POSITION: [number, number, number] = [0, 0, 12];

/** Default camera field of view */
export const DEFAULT_FOV = 45;

/** Min zoom scale */
export const MIN_SCALE = 0.3;

/** Max zoom scale */
export const MAX_SCALE = 4.0;

// --- Lighting ---

export const AMBIENT_LIGHT_COLOR = '#ffffff';
export const AMBIENT_LIGHT_INTENSITY = 0.7;

export const DIRECTIONAL_LIGHT_1 = {
  position: [10, 20, 15] as [number, number, number],
  intensity: 0.8,
  color: '#818cf8',
};

export const DIRECTIONAL_LIGHT_2 = {
  position: [-10, -10, -10] as [number, number, number],
  intensity: 0.4,
  color: '#f43f5e',
};

// --- Mesh defaults ---

/** Minimum segments per ring (prevents degenerate geometry) */
export const MIN_SEGMENTS = 4;
