package com.example.amigurumimaker.domain.model

data class ParsedToken(
    val type: StitchType,
    val count: Int,
    val yieldCount: Int
)
