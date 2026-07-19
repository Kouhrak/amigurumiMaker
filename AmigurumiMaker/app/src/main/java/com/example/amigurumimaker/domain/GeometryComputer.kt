package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Computes [BlockGeometry] (vertices, edges, face indices) for a list of [RowDef]s.
 *
 * - Splits RAMP_LEFT tokens into RAMP_LEFT vs RAMP_RIGHT based on block position.
 * - Centers each row at x=0.
 * - Y = rowNumber - 1 (0-indexed).
 * - Z = 0 for all blocks.
 */
object GeometryComputer {

    fun compute(rowDefs: List<RowDef>): List<BlockGeometry> {
        val geometries = mutableListOf<BlockGeometry>()

        for (rowDef in rowDefs) {
            val expanded = RowExpander.expand(rowDef)
            for (singleRow in expanded) {
                val rowNumber = singleRow.startRow
                val totalBlocks = singleRow.tokens.sumOf { it.count }
                val startX = -floor(totalBlocks / 2f)

                var blockIndex = 0
                for (token in singleRow.tokens) {
                    for (i in 0 until token.count) {
                        val cx = startX + blockIndex
                        val cy = (rowNumber - 1).toFloat()
                        val cz = 0f

                        val actualType = if (token.type == TokenType.RAMP_LEFT) {
                            if (blockIndex < totalBlocks / 2f) TokenType.RAMP_LEFT else TokenType.RAMP_RIGHT
                        } else {
                            token.type
                        }

                        val geometry = when (actualType) {
                            TokenType.CUBE -> cubeGeometry(cx, cy, cz)
                            TokenType.RAMP_LEFT -> rampLeftGeometry(cx, cy, cz)
                            TokenType.RAMP_RIGHT -> rampRightGeometry(cx, cy, cz)
                            TokenType.PLACEHOLDER -> BlockGeometry(emptyList(), emptyList(), emptyList())
                        }

                        geometries.add(geometry)
                        blockIndex++
                    }
                }
            }
        }

        return geometries
    }

    private fun cubeGeometry(cx: Float, cy: Float, cz: Float): BlockGeometry {
        val vertices = listOf(
            Float3(-0.5f + cx, -0.5f + cy, -0.5f + cz),  // 0: left-bottom-front
            Float3(0.5f + cx, -0.5f + cy, -0.5f + cz),   // 1: right-bottom-front
            Float3(0.5f + cx, 0.5f + cy, -0.5f + cz),    // 2: right-top-front
            Float3(-0.5f + cx, 0.5f + cy, -0.5f + cz),   // 3: left-top-front
            Float3(-0.5f + cx, -0.5f + cy, 0.5f + cz),   // 4: left-bottom-back
            Float3(0.5f + cx, -0.5f + cy, 0.5f + cz),    // 5: right-bottom-back
            Float3(0.5f + cx, 0.5f + cy, 0.5f + cz),     // 6: right-top-back
            Float3(-0.5f + cx, 0.5f + cy, 0.5f + cz),    // 7: left-top-back
        )

        val edges = listOf(
            IntPair(0, 1), IntPair(1, 2), IntPair(2, 3), IntPair(3, 0),
            IntPair(4, 5), IntPair(5, 6), IntPair(6, 7), IntPair(7, 4),
            IntPair(0, 4), IntPair(1, 5), IntPair(2, 6), IntPair(3, 7),
        )

        val faceIndices = listOf(
            // Front: (0,2,1),(0,3,2)
            IntTriple(0, 2, 1), IntTriple(0, 3, 2),
            // Back: (4,5,6),(4,6,7)
            IntTriple(4, 5, 6), IntTriple(4, 6, 7),
            // Top: (3,2,6),(3,6,7)
            IntTriple(3, 2, 6), IntTriple(3, 6, 7),
            // Bottom: (0,1,5),(0,5,4)
            IntTriple(0, 1, 5), IntTriple(0, 5, 4),
            // Right: (1,2,6),(1,6,5)
            IntTriple(1, 2, 6), IntTriple(1, 6, 5),
            // Left: (0,4,7),(0,7,3)
            IntTriple(0, 4, 7), IntTriple(0, 7, 3),
        )

        return BlockGeometry(vertices, edges, faceIndices)
    }

    /**
     * RAMP_LEFT: triangular prism, diagonal from lower-left to upper-right.
     *
     * Vertices:
     *   0: left-bottom-front  (-0.5, -0.5, -0.5)
     *   1: right-bottom-front (0.5, -0.5, -0.5)
     *   2: right-top-front    (0.5, 0.5, -0.5)
     *   3: left-bottom-back   (-0.5, -0.5, 0.5)
     *   4: right-bottom-back  (0.5, -0.5, 0.5)
     *   5: right-top-back     (0.5, 0.5, 0.5)
     */
    private fun rampLeftGeometry(cx: Float, cy: Float, cz: Float): BlockGeometry {
        val vertices = listOf(
            Float3(-0.5f + cx, -0.5f + cy, -0.5f + cz),  // 0
            Float3(0.5f + cx, -0.5f + cy, -0.5f + cz),   // 1
            Float3(0.5f + cx, 0.5f + cy, -0.5f + cz),    // 2
            Float3(-0.5f + cx, -0.5f + cy, 0.5f + cz),   // 3
            Float3(0.5f + cx, -0.5f + cy, 0.5f + cz),    // 4
            Float3(0.5f + cx, 0.5f + cy, 0.5f + cz),     // 5
        )

        val edges = listOf(
            IntPair(0, 1), IntPair(1, 2), IntPair(2, 0),
            IntPair(3, 4), IntPair(4, 5), IntPair(5, 3),
            IntPair(0, 3), IntPair(1, 4), IntPair(2, 5),
        )

        val faceIndices = listOf(
            IntTriple(0, 1, 2),  // Front
            IntTriple(3, 5, 4),  // Back
            IntTriple(0, 1, 4), IntTriple(0, 4, 3),  // Bottom
            IntTriple(1, 2, 5), IntTriple(1, 5, 4),  // Right
            IntTriple(2, 0, 3), IntTriple(2, 3, 5),  // Diagonal
        )

        return BlockGeometry(vertices, edges, faceIndices)
    }

    /**
     * RAMP_RIGHT: triangular prism, diagonal from lower-right to upper-left.
     *
     * Vertices:
     *   0: left-bottom-front  (-0.5, -0.5, -0.5)
     *   1: right-bottom-front (0.5, -0.5, -0.5)
     *   2: left-top-front     (-0.5, 0.5, -0.5)
     *   3: left-bottom-back   (-0.5, -0.5, 0.5)
     *   4: right-bottom-back  (0.5, -0.5, 0.5)
     *   5: left-top-back      (-0.5, 0.5, 0.5)
     */
    private fun rampRightGeometry(cx: Float, cy: Float, cz: Float): BlockGeometry {
        val vertices = listOf(
            Float3(-0.5f + cx, -0.5f + cy, -0.5f + cz),  // 0
            Float3(0.5f + cx, -0.5f + cy, -0.5f + cz),   // 1
            Float3(-0.5f + cx, 0.5f + cy, -0.5f + cz),   // 2
            Float3(-0.5f + cx, -0.5f + cy, 0.5f + cz),   // 3
            Float3(0.5f + cx, -0.5f + cy, 0.5f + cz),    // 4
            Float3(-0.5f + cx, 0.5f + cy, 0.5f + cz),    // 5
        )

        val edges = listOf(
            IntPair(0, 1), IntPair(1, 2), IntPair(2, 0),
            IntPair(3, 4), IntPair(4, 5), IntPair(5, 3),
            IntPair(0, 3), IntPair(1, 4), IntPair(2, 5),
        )

        val faceIndices = listOf(
            IntTriple(0, 1, 2),  // Front
            IntTriple(3, 5, 4),  // Back
            IntTriple(0, 1, 4), IntTriple(0, 4, 3),  // Bottom
            IntTriple(0, 2, 5), IntTriple(0, 5, 3),  // Left
            IntTriple(1, 2, 5), IntTriple(1, 5, 4),  // Diagonal
        )

        return BlockGeometry(vertices, edges, faceIndices)
    }
}
