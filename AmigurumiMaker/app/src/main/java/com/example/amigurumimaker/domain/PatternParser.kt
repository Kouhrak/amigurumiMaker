package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.*

object PatternParser {

    private val ROW_HEADER_REGEX = Regex("""^(\d+)\)\s*""")
    private val TOTAL_REGEX = Regex("""\((\d+)p?\)\s*$""")
    private val TOKEN_REGEX = Regex("""(\d+)([padc])""")
    private val REPEAT_GROUP_REGEX = Regex("""\[(.*?)\]\s*(\d+)v""")

    fun parse(text: String): List<ParsedRow> {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return emptyList()

        return lines.mapIndexed { index, line ->
            parseLine(line, index + 1)
        }
    }

    private fun parseLine(line: String, defaultIndex: Int): ParsedRow {
        var remaining = line

        val rowHeaderMatch = ROW_HEADER_REGEX.find(remaining)
        val rowIndex = if (rowHeaderMatch != null) {
            remaining = remaining.replace(ROW_HEADER_REGEX, "")
            rowHeaderMatch.groupValues[1].toInt()
        } else {
            defaultIndex
        }

        val totalMatch = TOTAL_REGEX.find(remaining)
        val totalExpected = if (totalMatch != null) {
            remaining = remaining.replace(TOTAL_REGEX, "").trim()
            totalMatch.groupValues[1].toIntOrNull()
        } else {
            null
        }

        val expandedText = expandRepeatGroups(remaining)
        val tokens = parseTokens(expandedText)

        var calculatedStitches = 0
        var increaseCount = 0
        var decreaseCount = 0

        val parsedTokens = tokens.map { (count, typeChar) ->
            val stitchType = charToStitchType(typeChar)
            val yieldCount = stitchType.yieldPerUnit * count
            calculatedStitches += yieldCount
            if (stitchType == StitchType.INCREASE) increaseCount += count
            if (stitchType == StitchType.DECREASE) decreaseCount += count
            ParsedToken(type = stitchType, count = count, yieldCount = yieldCount)
        }

        val isValid = totalExpected == null || totalExpected == calculatedStitches
        val errorMsg = if (!isValid) {
            "Esperados ${totalExpected}p, pero calculados ${calculatedStitches}p."
        } else ""

        return ParsedRow(
            raw = line,
            rowIndex = rowIndex,
            tokens = parsedTokens,
            totalCalculated = calculatedStitches,
            totalExpected = totalExpected,
            isValid = isValid,
            errorMsg = errorMsg,
            increaseCount = increaseCount,
            decreaseCount = decreaseCount
        )
    }

    private fun expandRepeatGroups(input: String): String {
        var result = input
        var match = REPEAT_GROUP_REGEX.find(result)
        while (match != null) {
            val sequence = match.groupValues[1]
            val multiplier = match.groupValues[2].toInt()
            val expanded = (1..multiplier).joinToString(" ") { sequence }
            result = result.replaceRange(match.range, expanded)
            match = REPEAT_GROUP_REGEX.find(result)
        }
        return result
    }

    private fun parseTokens(text: String): List<Pair<Int, Char>> {
        return TOKEN_REGEX.findAll(text).map { match ->
            val count = match.groupValues[1].toInt()
            val typeChar = match.groupValues[2].first()
            count to typeChar
        }.toList()
    }

    private fun charToStitchType(char: Char): StitchType = when (char) {
        'p' -> StitchType.NORMAL
        'a' -> StitchType.INCREASE
        'd' -> StitchType.DECREASE
        'c' -> StitchType.CHAIN
        else -> StitchType.NORMAL
    }
}
