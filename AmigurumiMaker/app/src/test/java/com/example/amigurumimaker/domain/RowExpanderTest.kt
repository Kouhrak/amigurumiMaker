package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import org.junit.Test
import kotlin.test.assertEquals

class RowExpanderTest {

    @Test
    fun `single row returns same row`() {
        val rowDef = RowDef(startRow = 1, endRow = 1, tokens = listOf(Token(TokenType.CUBE, 10)))
        val expanded = RowExpander.expand(rowDef)
        assertEquals(1, expanded.size)
        assertEquals(1, expanded[0].startRow)
        assertEquals(1, expanded[0].endRow)
        assertEquals(rowDef.tokens, expanded[0].tokens)
    }

    @Test
    fun `range 1-3 returns three rows`() {
        val rowDef = RowDef(startRow = 1, endRow = 3, tokens = listOf(Token(TokenType.CUBE, 5)))
        val expanded = RowExpander.expand(rowDef)
        assertEquals(3, expanded.size)
        assertEquals(1, expanded[0].startRow)
        assertEquals(2, expanded[1].startRow)
        assertEquals(3, expanded[2].startRow)
    }

    @Test
    fun `all expanded rows have same tokens`() {
        val tokens = listOf(Token(TokenType.CUBE, 3), Token(TokenType.PLACEHOLDER, 2))
        val rowDef = RowDef(startRow = 1, endRow = 3, tokens = tokens)
        val expanded = RowExpander.expand(rowDef)
        assertEquals(3, expanded.size)
        for (row in expanded) {
            assertEquals(tokens, row.tokens)
        }
    }

    @Test
    fun `single element range 1-1 returns one row`() {
        val rowDef = RowDef(startRow = 1, endRow = 1, tokens = listOf(Token(TokenType.CUBE, 10)))
        val expanded = RowExpander.expand(rowDef)
        assertEquals(1, expanded.size)
        assertEquals(1, expanded[0].startRow)
    }
}
