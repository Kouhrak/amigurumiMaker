package com.example.amigurumimaker.domain.crochet.model

sealed interface AstNode {
    data class PatternNode(val rows: List<RowNode>) : AstNode
    data class RowNode(
        val rowNumbers: IntRange,
        val stitches: List<StitchNode>,
        val declaredCount: Int? = null
    ) : AstNode

    data class StitchNode(
        val abbreviation: String,
        val count: Int = 1,
        val modifier: String? = null
    ) : AstNode

    data class RepeatGroupNode(
        val sequence: List<AstNode>,
        val repeats: Int
    ) : AstNode

    data class CompoundStitchNode(
        val type: CompoundType,
        val repeats: Int
    ) : AstNode
}

enum class CompoundType {
    V_STITCH, PUFF, POPCORN, FAN
}
