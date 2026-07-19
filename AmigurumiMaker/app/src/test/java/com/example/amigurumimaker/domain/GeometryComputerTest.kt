package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import org.junit.Test
import kotlin.test.assertEquals

class GeometryComputerTest {

    private val epsilon = 1e-6f

    @Test
    fun `one cube row 1 returns 8 vertices 12 edges 12 face indices`() {
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.CUBE, 1))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(1, geometries.size)
        val geo = geometries[0]
        assertEquals(8, geo.vertices.size)
        assertEquals(12, geo.edges.size)
        assertEquals(12, geo.faceIndices.size)
    }

    @Test
    fun `one ramp_left returns 6 vertices 9 edges`() {
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.RAMP_LEFT, 1))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(1, geometries.size)
        val geo = geometries[0]
        assertEquals(6, geo.vertices.size)
        assertEquals(9, geo.edges.size)
    }

    @Test
    fun `one ramp_right returns 6 vertices 9 edges`() {
        // To get RAMP_RIGHT, we need 2 d-tokens so the second one becomes RAMP_RIGHT
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.RAMP_LEFT, 2))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(2, geometries.size)
        // First is RAMP_LEFT, second is RAMP_RIGHT
        assertEquals(6, geometries[0].vertices.size)
        assertEquals(9, geometries[0].edges.size)
        assertEquals(6, geometries[1].vertices.size)
        assertEquals(9, geometries[1].edges.size)
    }

    @Test
    fun `one placeholder returns empty geometry`() {
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.PLACEHOLDER, 1))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(1, geometries.size)
        val geo = geometries[0]
        assertEquals(0, geo.vertices.size)
        assertEquals(0, geo.edges.size)
        assertEquals(0, geo.faceIndices.size)
    }

    @Test
    fun `d tokens split correctly into ramp_left and ramp_right`() {
        // 2 d-tokens in a row of width 2: first is RAMP_LEFT, second is RAMP_RIGHT
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.RAMP_LEFT, 2))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(2, geometries.size)

        // First block is RAMP_LEFT: vertex 2 (right-top) aligned with vertex 1 (right-bottom) on x
        val rampLeft = geometries[0]
        assertEquals(rampLeft.vertices[1].x, rampLeft.vertices[2].x, epsilon)

        // Second block is RAMP_RIGHT: vertex 2 (left-top) aligned with vertex 0 (left-bottom) on x
        val rampRight = geometries[1]
        assertEquals(rampRight.vertices[0].x, rampRight.vertices[2].x, epsilon)
    }

    @Test
    fun `multi-block row centered correctly`() {
        // 10 cubes → blocks from x=-5 to x=4
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.CUBE, 10))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(10, geometries.size)

        // First block center x = -5
        assertEquals(-5f, geometries[0].vertices[0].x + 0.5f, epsilon)
        // Last block center x = 4
        assertEquals(4f, geometries[9].vertices[0].x + 0.5f, epsilon)
    }

    @Test
    fun `row numbering y equals row number minus 1`() {
        // Row 2 → y=1
        val rowDefs = listOf(RowDef(2, 2, listOf(Token(TokenType.CUBE, 1))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(1, geometries.size)
        val geo = geometries[0]
        // Vertex 0: (-0.5+cx, -0.5+cy, -0.5+cz) where cy = rowNumber - 1 = 1
        assertEquals(0.5f, geo.vertices[1].y, epsilon) // 0.5 + cy = 0.5 + 1 = 1.5 ... wait
        // cy = rowNumber - 1 = 2 - 1 = 1
        // Vertex 1: (0.5+0, -0.5+1, -0.5+0) = (0.5, 0.5, -0.5)
        assertEquals(0.5f, geo.vertices[1].y, epsilon)

        // Row 3 → y=2
        val rowDefs2 = listOf(RowDef(3, 3, listOf(Token(TokenType.CUBE, 1))))
        val geometries2 = GeometryComputer.compute(rowDefs2)

        assertEquals(1, geometries2.size)
        // Vertex 1: (0.5, -0.5+2, -0.5) = (0.5, 1.5, -0.5)
        assertEquals(1.5f, geometries2[0].vertices[1].y, epsilon)
    }

    @Test
    fun `ramp_left diagonal vertex at x equals 0_5`() {
        // Single RAMP_LEFT block at row 1
        val rowDefs = listOf(RowDef(1, 1, listOf(Token(TokenType.RAMP_LEFT, 1))))
        val geometries = GeometryComputer.compute(rowDefs)

        assertEquals(1, geometries.size)
        val geo = geometries[0]

        // RAMP_LEFT vertex 2: right-top-front at (0.5+cx, 0.5+cy, -0.5+cz)
        // With single block: cx = -floor(1/2) = 0, cy = 0, cz = 0
        // Vertex 2 should be at (0.5+0, 0.5+0, -0.5+0) = (0.5, 0.5, -0.5)
        assertEquals(0.5f, geo.vertices[2].x, epsilon)
        assertEquals(0.5f, geo.vertices[2].y, epsilon)
        assertEquals(-0.5f, geo.vertices[2].z, epsilon)
    }
}
