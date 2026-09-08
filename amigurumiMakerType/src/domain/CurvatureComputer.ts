// src/domain/CurvatureComputer.ts
// Port of Kotlin CurvatureComputer — pure math, zero I/O

import { ParsedRow, RoundAnalysis, SurfaceMetrics, SurfaceClassification, Float3 } from './model/models';
import { STITCH_WIDTH, STITCH_HEIGHT, GAUSS_POSITIVE_THRESHOLD, GAUSS_NEGATIVE_THRESHOLD, GAUSS_POSITIVE_COLOR, GAUSS_NEGATIVE_COLOR, GAUSS_NEUTRAL_COLOR } from './constants';

/**
 * Compute round-by-round geometric analysis from parsed rows.
 * Formula: r_i = N_i * w / (2π), cos(θ) = min(1, Δr/h), K = (6 - ΔN) * π/3
 */
export function computeRoundAnalyses(rows: ParsedRow[]): RoundAnalysis[] {
  if (rows.length === 0) return [];

  const analyses: RoundAnalysis[] = [];

  for (const row of rows) {
    const ni = Math.max(row.totalCalculated, 1);
    const dN = analyses.length === 0 ? 0 : ni - analyses[analyses.length - 1].stitchCount;
    const ri = (ni * STITCH_WIDTH) / (2.0 * Math.PI);
    const prevRi = analyses.length > 0 ? analyses[analyses.length - 1].theoreticalRadius : 0.0;
    const dr = Math.abs(ri - prevRi);
    const cosTheta = Math.min(1.0, dr / STITCH_HEIGHT);
    const sinTheta = Math.sqrt(Math.max(0.0, 1.0 - cosTheta * cosTheta));
    const angleDeg = (Math.acos(Math.max(-1.0, Math.min(1.0, cosTheta))) * 180.0) / Math.PI;

    const prevLiftZ = analyses.length > 0 ? analyses[analyses.length - 1].liftZ : 0.0;
    const liftZ = prevLiftZ + STITCH_HEIGHT * sinTheta;

    // Local curvature: δ = 2π * (ΔN / N), A = 2π * r * h, K = δ / A
    const delta = analyses.length === 0 ? 0.0 : (2.0 * Math.PI * dN) / ni;
    const ai = 2.0 * Math.PI * ri * STITCH_HEIGHT;
    const ki = ai > 0.0 ? delta / ai : 0.0;

    analyses.push({
      rowIndex: row.rowIndex,
      stitchCount: ni,
      deltaN: dN,
      theoreticalRadius: ri,
      cosTheta,
      sinTheta,
      inclinationAngleDeg: angleDeg,
      liftZ,
      localCurvature: ki,
      roundArea: ai,
    });
  }

  return analyses;
}

/**
 * Compute aggregate surface metrics from round analyses.
 * Total curvature → Euler characteristic: χ = ΣK / 2π
 */
export function computeSurfaceMetrics(analyses: RoundAnalysis[]): SurfaceMetrics {
  const totalArea = analyses.reduce((sum, a) => sum + a.roundArea, 0.0);

  const totalCurvature = analyses.reduce((sum, a) => {
    const ni = a.stitchCount;
    const dN = a.deltaN;
    if (dN === 0) return sum;
    return sum + (2.0 * Math.PI * dN) / ni;
  }, 0.0);

  const eulerCharacteristic = totalCurvature / (2.0 * Math.PI);

  return { totalArea, totalCurvature, eulerCharacteristic };
}

/**
 * Classify the surface based on average non-zero curvature.
 * K > 0 → SPHERE, K < 0 → HYPERBOLIC, else → CYLINDRICAL or FLAT
 */
export function classifySurface(analyses: RoundAnalysis[]): SurfaceClassification {
  if (analyses.length === 0) return 'FLAT';

  const nonZero = analyses.filter((a) => a.localCurvature !== 0.0);
  if (nonZero.length === 0) return 'FLAT';

  const avgK = nonZero.reduce((sum, a) => sum + a.localCurvature, 0.0) / nonZero.length;

  if (avgK > 1e-6) return 'SPHERE';
  if (avgK < -1e-6) return 'HYPERBOLIC';
  return 'CYLINDRICAL';
}

/**
 * Map curvature value to RGB color.
 * K > 0.1 → green, K < -0.1 → pink, else → cyan
 * Returns Float3 with RGB in [0..1] range.
 */
export function curvatureColor(k: number): Float3 {
  if (k > GAUSS_POSITIVE_THRESHOLD) return { ...GAUSS_POSITIVE_COLOR };
  if (k < GAUSS_NEGATIVE_THRESHOLD) return { ...GAUSS_NEGATIVE_COLOR };
  return { ...GAUSS_NEUTRAL_COLOR };
}

/**
 * Compute Gauss curvature from stitch delta.
 * K = (6 - ΔN) * π / 3
 */
export function gaussCurvatureFromDelta(dN: number): number {
  return (6 - dN) * (Math.PI / 3);
}
