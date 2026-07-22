package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import kotlin.math.cos
import kotlin.math.sin

object MeshComputer {

    private const val ROW_HEIGHT = 36f
    private const val BASE_STITCH_WIDTH = 32f
    private const val DEFORMATION_FACTOR = 0.25f

    fun compute2DCells(rows: List<ParsedRow>): List<MeshCell> {
        val cells = mutableListOf<MeshCell>()
        val totalRows = rows.size
        val totalHeight = totalRows * ROW_HEIGHT
        val startY = totalHeight / 2f

        rows.forEachIndexed { rowIdx, row ->
            val yBottom = startY - (rowIdx * ROW_HEIGHT)
            val yTop = yBottom - ROW_HEIGHT

            val blocks = mutableListOf<StitchType>()
            row.tokens.forEach { token ->
                repeat(token.count) { blocks.add(token.type) }
            }

            val rowStitchCount = blocks.size.coerceAtLeast(1)
            val totalRowWidth = rowStitchCount * BASE_STITCH_WIDTH
            var currentX = -totalRowWidth / 2f

            blocks.forEachIndexed { bIdx, stitchType ->
                var xBL = currentX
                var xBR = currentX + BASE_STITCH_WIDTH
                var xTL = currentX
                var xTR = currentX + BASE_STITCH_WIDTH

                when (stitchType) {
                    StitchType.INCREASE -> {
                        xTL -= BASE_STITCH_WIDTH * DEFORMATION_FACTOR
                        xTR += BASE_STITCH_WIDTH * DEFORMATION_FACTOR
                    }
                    StitchType.DECREASE -> {
                        xTL += BASE_STITCH_WIDTH * DEFORMATION_FACTOR
                        xTR -= BASE_STITCH_WIDTH * DEFORMATION_FACTOR
                    }
                    StitchType.CHAIN -> {
                        xTL += BASE_STITCH_WIDTH * 0.1f
                        xTR -= BASE_STITCH_WIDTH * 0.1f
                    }
                    StitchType.NORMAL -> {}
                }

                cells.add(
                    MeshCell(
                        rowIndex = row.rowIndex,
                        stitchIndex = bIdx + 1,
                        type = stitchType,
                        bottomLeft = Point2D(xBL, yBottom),
                        bottomRight = Point2D(xBR, yBottom),
                        topRight = Point2D(xTR, yTop),
                        topLeft = Point2D(xTL, yTop)
                    )
                )

                currentX += BASE_STITCH_WIDTH
            }
        }

        return cells
    }

    fun compute3DProjection(rows: List<ParsedRow>): List<CylinderCell> {
        val cells = mutableListOf<CylinderCell>()
        val rowHeight = 30f
        val totalRows = rows.size
        val startY = (totalRows * rowHeight) / 2f

        rows.forEachIndexed { rowIdx, row ->
            val y = startY - (rowIdx * rowHeight)

            val blocks = mutableListOf<StitchType>()
            row.tokens.forEach { token ->
                repeat(token.count) { blocks.add(token.type) }
            }

            val count = blocks.size.coerceAtLeast(8)
            val radius = (40f + count * 7f).coerceAtMost(200f)

            for (i in blocks.indices) {
                val angle = (i.toFloat() / count) * kotlin.math.PI.toFloat() * 2f
                val nextAngle = ((i + 1).toFloat() / count) * kotlin.math.PI.toFloat() * 2f

                val x1 = cos(angle) * radius
                val z1 = sin(angle) * radius
                val x2 = cos(nextAngle) * radius
                val z2 = sin(nextAngle) * radius

                val depth = (z1 + radius) / (radius * 2f)

                cells.add(
                    CylinderCell(
                        x1 = x1, y1 = y - z1 * 0.2f,
                        x2 = x2, y2 = y - z2 * 0.2f,
                        x3 = x2, y3 = y - rowHeight - z2 * 0.2f,
                        x4 = x1, y4 = y - rowHeight - z1 * 0.2f,
                        type = if (i < blocks.size) blocks[i] else StitchType.NORMAL,
                        depth = depth
                    )
                )
            }
        }

        return cells
    }
}
