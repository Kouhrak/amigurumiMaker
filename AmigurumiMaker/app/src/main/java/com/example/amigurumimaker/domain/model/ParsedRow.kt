package com.example.amigurumimaker.domain.model

data class ParsedRow(
    val raw: String,
    val rowIndex: Int,
    val tokens: List<ParsedToken>,
    val totalCalculated: Int,
    val totalExpected: Int?,
    val isValid: Boolean,
    val errorMsg: String,
    val increaseCount: Int,
    val decreaseCount: Int
)
