package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*

/**
 * Regex-based tokenizer for the amigurumi wall pattern grammar.
 *
 * Grammar:
 * ```
 * input := segment (',' segment)*
 * segment := rowSpec token+
 * rowSpec := digit+ ')' | digit+ '-' digit+ ')'
 * token := digit+ ('c' | 'd' | 'p')
 * ```
 *
 * Token types:
 * - `c` → CUBE
 * - `d` → RAMP_LEFT (GeometryComputer later resolves to RAMP_LEFT vs RAMP_RIGHT)
 * - `p` → PLACEHOLDER
 */
object WallTokenizer {

    private val SEGMENT_REGEX = Regex("""(\d+(?:-\d+)?)\)\s*(?:\(?\d+[cdp]\)?\s*)+""")
    private val ROW_SPEC_REGEX = Regex("""(\d+)(?:-(\d+))?\)""")
    private val TOKEN_REGEX = Regex("""\(?(\d+)([cdp])\)?""")

    fun tokenize(input: String): ParseResult {
        if (input.isBlank()) {
            return ParseResult.Error("Empty input")
        }

        val segments = input.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (segments.isEmpty()) {
            return ParseResult.Error("Empty input")
        }

        val rowDefs = mutableListOf<RowDef>()

        for (segment in segments) {
            val segmentMatch = SEGMENT_REGEX.matchEntire(segment)
            if (segmentMatch == null) {
                // Try to determine the specific error
                val rowSpecMatch = ROW_SPEC_REGEX.find(segment)
                if (rowSpecMatch == null) {
                    return ParseResult.Error("Invalid row specifier in segment: \"$segment\"")
                }
                // Row spec is valid, but tokens might be wrong
                val tokensPart = segment.substring(rowSpecMatch.value.length).trim()
                if (tokensPart.isEmpty()) {
                    return ParseResult.Error("No valid tokens in segment: \"$segment\"")
                }
                val tokenMatches = TOKEN_REGEX.findAll(tokensPart).toList()
                if (tokenMatches.isEmpty()) {
                    return ParseResult.Error("No valid tokens in segment: \"$segment\"")
                }
                // There might be invalid characters
                return ParseResult.Error("Invalid token format in segment: \"$segment\"")
            }

            val segmentsText = segmentMatch.value
            val rowSpecMatch = ROW_SPEC_REGEX.find(segmentsText)!!

            val startRow = rowSpecMatch.groupValues[1].toInt()
            val endRow = if (rowSpecMatch.groupValues[2].isNotEmpty()) {
                rowSpecMatch.groupValues[2].toInt()
            } else {
                startRow
            }

            val tokensPart = segmentsText.substring(rowSpecMatch.value.length).trim()
            val tokenMatches = TOKEN_REGEX.findAll(tokensPart).toList()

            if (tokenMatches.isEmpty()) {
                return ParseResult.Error("No valid tokens in segment: \"$segment\"")
            }

            val tokens = tokenMatches.map { match ->
                val count = match.groupValues[1].toInt()
                val typeChar = match.groupValues[2]
                val type = when (typeChar) {
                    "c" -> TokenType.CUBE
                    "d" -> TokenType.RAMP_LEFT
                    "p" -> TokenType.PLACEHOLDER
                    else -> return ParseResult.Error("Unknown token type '$typeChar' in segment: \"$segment\"")
                }
                Token(type, count)
            }

            rowDefs.add(RowDef(startRow, endRow, tokens))
        }

        return ParseResult.Success(rowDefs)
    }
}
