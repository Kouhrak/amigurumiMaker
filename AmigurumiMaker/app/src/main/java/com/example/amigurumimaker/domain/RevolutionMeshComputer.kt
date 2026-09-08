package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object RevolutionMeshComputer {

    fun computeMesh(
        analyses: List<RoundAnalysis>,
        colorMode: ColorMode
    ): RevolutionMesh? {
        if (analyses.size < 2) return null

        val segments = mutableListOf<RingSegment>()
        val totalRows = analyses.size

        for (i in 0 until totalRows - 1) {
            val rowBot = analyses[i]
            val rowTop = analyses[i + 1]

            val r1 = rowBot.theoreticalRadius.toFloat()
            val r2 = rowTop.theoreticalRadius.toFloat()
            val z1 = rowBot.liftZ.toFloat()
            val z2 = rowTop.liftZ.toFloat()
            val s = maxOf(rowBot.stitchCount, rowTop.stitchCount).coerceAtLeast(4)

            val color = segmentColor(i, totalRows, rowBot.localCurvature, colorMode)

            val vertices = mutableListOf<Float3>()
            val indices = mutableListOf<Int>()

            for (k in 0..s) {
                val phi = (k.toFloat() / s) * 2f * PI.toFloat()
                val phiNext = ((k + 1).toFloat() / s) * 2f * PI.toFloat()

                val vBot = Float3(r1 * cos(phi), z1, r1 * sin(phi))
                val vTop = Float3(r2 * cos(phi), z2, r2 * sin(phi))

                val idxBot = vertices.size
                vertices.add(vBot)
                val idxTop = vertices.size
                vertices.add(vTop)

                if (k < s) {
                    val vBotNext = Float3(r1 * cos(phiNext), z1, r1 * sin(phiNext))
                    val vTopNext = Float3(r2 * cos(phiNext), z2, r2 * sin(phiNext))
                    val idxBotNext = vertices.size
                    vertices.add(vBotNext)
                    val idxTopNext = vertices.size
                    vertices.add(vTopNext)

                    indices.add(idxBot)
                    indices.add(idxTop)
                    indices.add(idxBotNext)

                    indices.add(idxTop)
                    indices.add(idxTopNext)
                    indices.add(idxBotNext)
                }
            }

            segments.add(RingSegment(vertices, indices, color))
        }

        val totalHeight = (analyses.last().liftZ - analyses.first().liftZ).toFloat()
        val centerOffset = totalHeight / 2f

        val centered = segments.map { seg ->
            val movedVerts = seg.vertices.map { v ->
                Float3(v.x, v.y - centerOffset, v.z)
            }
            seg.copy(vertices = movedVerts)
        }

        return RevolutionMesh(centered, totalHeight)
    }

    private fun segmentColor(
        rowIndex: Int,
        totalRows: Int,
        curvature: Double,
        colorMode: ColorMode
    ): Float3 {
        return when (colorMode) {
            ColorMode.GAUSS_HEATMAP -> {
                when {
                    curvature > 0.1 -> Float3(0.06f, 0.72f, 0.50f)
                    curvature < -0.1 -> Float3(0.95f, 0.25f, 0.37f)
                    else -> Float3(0.02f, 0.71f, 0.83f)
                }
            }
            ColorMode.ROW_GRADIENT -> {
                val t = if (totalRows > 1) rowIndex.toFloat() / (totalRows - 1).toFloat() else 0f
                val hue = 240f + t * 120f
                hslToRgb(hue / 360f, 0.7f, 0.5f)
            }
        }
    }

    private fun hslToRgb(h: Float, s: Float, l: Float): Float3 {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs((h * 6f) % 2f - 1f))
        val m = l - c / 2f

        val (r1, g1, b1) = when {
            h < 1f / 6f -> Float3(c, x, 0f)
            h < 2f / 6f -> Float3(x, c, 0f)
            h < 3f / 6f -> Float3(0f, c, x)
            h < 4f / 6f -> Float3(0f, x, c)
            h < 5f / 6f -> Float3(x, 0f, c)
            else -> Float3(c, 0f, x)
        }

        return Float3(r1 + m, g1 + m, b1 + m)
    }
}
