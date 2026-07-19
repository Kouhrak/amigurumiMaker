package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertEquals

class WallTokenizerTest {

    @Test
    fun `single row single token`() {
        val result = WallTokenizer.tokenize("1) 10c")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(1, success.rows.size)
        assertEquals(1, success.rows[0].startRow)
        assertEquals(1, success.rows[0].endRow)
        assertEquals(1, success.rows[0].tokens.size)
        assertEquals(TokenType.CUBE, success.rows[0].tokens[0].type)
        assertEquals(10, success.rows[0].tokens[0].count)
    }

    @Test
    fun `single row multiple token types`() {
        val result = WallTokenizer.tokenize("1) 1d 8p 1d")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(1, success.rows.size)
        assertEquals(3, success.rows[0].tokens.size)
        assertEquals(Token(TokenType.RAMP_LEFT, 1), success.rows[0].tokens[0])
        assertEquals(Token(TokenType.PLACEHOLDER, 8), success.rows[0].tokens[1])
        assertEquals(Token(TokenType.RAMP_LEFT, 1), success.rows[0].tokens[2])
    }

    @Test
    fun `row range parses correctly`() {
        val result = WallTokenizer.tokenize("1-3) 10c")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(1, success.rows.size)
        assertEquals(1, success.rows[0].startRow)
        assertEquals(3, success.rows[0].endRow)
        assertEquals(1, success.rows[0].tokens.size)
        assertEquals(TokenType.CUBE, success.rows[0].tokens[0].type)
        assertEquals(10, success.rows[0].tokens[0].count)
    }

    @Test
    fun `empty input returns error`() {
        val result = WallTokenizer.tokenize("")
        assertIs<ParseResult.Error>(result)
    }

    @Test
    fun `blank input returns error`() {
        val result = WallTokenizer.tokenize("   ")
        assertIs<ParseResult.Error>(result)
    }

    @Test
    fun `invalid token type returns error`() {
        val result = WallTokenizer.tokenize("1) 10x")
        assertIs<ParseResult.Error>(result)
    }

    @Test
    fun `malformed row spec returns error`() {
        val result = WallTokenizer.tokenize("abc) 5c")
        assertIs<ParseResult.Error>(result)
    }

    @Test
    fun `parenthesized placeholder token`() {
        val result = WallTokenizer.tokenize("1) 10c (10p)")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(1, success.rows.size)
        assertEquals(2, success.rows[0].tokens.size)
        assertEquals(Token(TokenType.CUBE, 10), success.rows[0].tokens[0])
        assertEquals(Token(TokenType.PLACEHOLDER, 10), success.rows[0].tokens[1])
    }

    @Test
    fun `full example with parenthesized placeholders`() {
        val result = WallTokenizer.tokenize("1) 10c (10p), 2) 1d 8p 1d (8p)")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(2, success.rows.size)
        // Row 1: 10c + (10p)
        assertEquals(2, success.rows[0].tokens.size)
        assertEquals(Token(TokenType.CUBE, 10), success.rows[0].tokens[0])
        assertEquals(Token(TokenType.PLACEHOLDER, 10), success.rows[0].tokens[1])
        // Row 2: 1d + 8p + 1d + (8p)
        assertEquals(4, success.rows[1].tokens.size)
        assertEquals(Token(TokenType.RAMP_LEFT, 1), success.rows[1].tokens[0])
        assertEquals(Token(TokenType.PLACEHOLDER, 8), success.rows[1].tokens[1])
        assertEquals(Token(TokenType.RAMP_LEFT, 1), success.rows[1].tokens[2])
        assertEquals(Token(TokenType.PLACEHOLDER, 8), success.rows[1].tokens[3])
    }

    @Test
    fun `multiple segments`() {
        val result = WallTokenizer.tokenize("1) 5c, 2) 3d")
        val success = assertIs<ParseResult.Success>(result)
        assertEquals(2, success.rows.size)

        assertEquals(1, success.rows[0].startRow)
        assertEquals(1, success.rows[0].endRow)
        assertEquals(1, success.rows[0].tokens.size)
        assertEquals(Token(TokenType.CUBE, 5), success.rows[0].tokens[0])

        assertEquals(2, success.rows[1].startRow)
        assertEquals(2, success.rows[1].endRow)
        assertEquals(1, success.rows[1].tokens.size)
        assertEquals(Token(TokenType.RAMP_LEFT, 3), success.rows[1].tokens[0])
    }
}
