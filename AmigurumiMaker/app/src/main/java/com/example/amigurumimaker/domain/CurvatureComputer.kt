package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

object CurvatureComputer {

    private const val STITCH_WIDTH = 0.5f
    private const val STITCH_HEIGHT = 0.5f

    fun computeRoundAnalyses(rows: List<ParsedRow>): List<RoundAnalysis> {
        if (rows.isEmpty()) return emptyList()

        val analyses = mutableListOf<RoundAnalysis>()

        for (row in rows) {
            val ni = row.totalCalculated.coerceAtLeast(1)
            val dN = if (analyses.isEmpty()) 0 else ni - analyses.last().stitchCount
            val ri = ni * STITCH_WIDTH / (2.0 * PI)
            val prevRi = analyses.lastOrNull()?.theoreticalRadius ?: 0.0
            val dr = abs(ri - prevRi).toFloat()
            val cosTheta = min(1.0, (dr / STITCH_HEIGHT).toDouble())
            val sinTheta = sqrt(max(0.0, 1.0 - cosTheta * cosTheta))
            val angleDeg = Math.toDegrees(acos(cosTheta.coerceIn(-1.0, 1.0)))
            val liftZ = analyses.lastOrNull()?.let { prev ->
                prev.liftZ + STITCH_HEIGHT.toDouble() * sinTheta
            } ?: 0.0

            val delta = if (analyses.isEmpty()) 0.0 else 2.0 * PI * (dN.toDouble() / ni)
            val ai = 2.0 * PI * ri * STITCH_HEIGHT
            val ki = if (ai > 0.0) delta / ai else 0.0

            analyses.add(
                RoundAnalysis(
                    rowIndex = row.rowIndex,
                    stitchCount = ni,
                    deltaN = dN,
                    theoreticalRadius = ri,
                    cosTheta = cosTheta,
                    sinTheta = sinTheta,
                    inclinationAngleDeg = angleDeg,
                    liftZ = liftZ,
                    localCurvature = ki,
                    roundArea = ai
                )
            )
        }

        return analyses
    }

    fun computeSurfaceMetrics(analyses: List<RoundAnalysis>): SurfaceMetrics {
        val totalArea = analyses.sumOf { it.roundArea }
        val totalCurvature = analyses.sumOf {
            val ni = it.stitchCount
            val dN = it.deltaN
            if (dN == 0) 0.0 else 2.0 * PI * (dN.toDouble() / ni)
        }
        val eulerChar = totalCurvature / (2.0 * PI)
        return SurfaceMetrics(
            totalArea = totalArea,
            totalCurvature = totalCurvature,
            eulerCharacteristic = eulerChar
        )
    }

    fun classifySurface(analyses: List<RoundAnalysis>): SurfaceClassification {
        if (analyses.isEmpty()) return SurfaceClassification.FLAT
        val nonZeroCurvatures = analyses.filter { it.localCurvature != 0.0 }
        if (nonZeroCurvatures.isEmpty()) return SurfaceClassification.FLAT
        val avgK = nonZeroCurvatures.sumOf { it.localCurvature } / nonZeroCurvatures.size
        return when {
            avgK > 1e-6 -> SurfaceClassification.SPHERE
            avgK < -1e-6 -> SurfaceClassification.HYPERBOLIC
            else -> SurfaceClassification.CYLINDRICAL
        }
    }

    fun curvatureColor(k: Double): Long {
        return when {
            k > 0.1 -> 0xFF10B981
            k < -0.1 -> 0xFFF43F5E
            else -> 0xFF06B6D4
        }
    }

    fun gaussCurvatureFromDelta(dN: Int): Float {
        return (6 - dN) * (PI.toFloat() / 3f)
    }
}
