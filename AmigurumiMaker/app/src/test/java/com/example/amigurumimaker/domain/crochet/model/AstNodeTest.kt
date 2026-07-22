package com.example.amigurumimaker.domain.crochet.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AstNodeTest {

    @Test
    fun `PatternNode holds RowNodes with StitchNodes`() {
        val stitch = AstNode.StitchNode("sc", 10)
        val row = AstNode.RowNode(1..1, listOf(stitch))
        val pattern = AstNode.PatternNode(listOf(row))

        assertEquals(1, pattern.rows.size)
        assertEquals(1..1, pattern.rows[0].rowNumbers)
        assertEquals(1, pattern.rows[0].stitches.size)
        assertEquals("sc", pattern.rows[0].stitches[0].abbreviation)
        assertEquals(10, pattern.rows[0].stitches[0].count)
    }

    @Test
    fun `sealed hierarchy includes all node types`() {
        val stitch = AstNode.StitchNode("dc", 6)
        val row = AstNode.RowNode(2..2, listOf(stitch))
        val repeat = AstNode.RepeatGroupNode(listOf(stitch), 3)
        val compound = AstNode.CompoundStitchNode(CompoundType.V_STITCH, 2)
        val pattern = AstNode.PatternNode(listOf(row))

        assertIs<AstNode>(stitch)
        assertIs<AstNode>(row)
        assertIs<AstNode>(repeat)
        assertIs<AstNode>(compound)
        assertIs<AstNode>(pattern)
    }

    @Test
    fun `RowNode allows declaredCount and stitch sequence`() {
        val sc = AstNode.StitchNode("sc", 1)
        val inc = AstNode.StitchNode("inc", 1)
        val row = AstNode.RowNode(3..3, listOf(sc, inc), declaredCount = 2)

        assertEquals(3..3, row.rowNumbers)
        assertEquals(2, row.stitches.size)
        assertEquals(2, row.declaredCount)
        assertEquals("sc", row.stitches[0].abbreviation)
        assertEquals("inc", row.stitches[1].abbreviation)
    }

    @Test
    fun `StitchNode defaults count to 1`() {
        val stitch = AstNode.StitchNode("ch")
        assertEquals("ch", stitch.abbreviation)
        assertEquals(1, stitch.count)
        assertNull(stitch.modifier)
    }

    @Test
    fun `StitchNode accepts optional modifier`() {
        val stitch = AstNode.StitchNode("sc", count = 6, modifier = "BLO")
        assertEquals("sc", stitch.abbreviation)
        assertEquals(6, stitch.count)
        assertEquals("BLO", stitch.modifier)
    }

    @Test
    fun `RepeatGroupNode holds sequence and repeats`() {
        val sc = AstNode.StitchNode("sc", 1)
        val inc = AstNode.StitchNode("inc", 1)
        val group = AstNode.RepeatGroupNode(listOf(sc, inc), 6)

        assertEquals(2, group.sequence.size)
        assertEquals(6, group.repeats)
        assertIs<AstNode>(group.sequence[0])
        assertIs<AstNode>(group.sequence[1])
    }

    @Test
    fun `CompoundStitchNode holds type and repeats`() {
        val vStitch = AstNode.CompoundStitchNode(CompoundType.V_STITCH, 1)
        assertEquals(CompoundType.V_STITCH, vStitch.type)
        assertEquals(1, vStitch.repeats)
    }
}
