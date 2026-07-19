package com.example.amigurumimaker.domain.model

sealed class ParseResult {
    data class Success(val rows: List<RowDef>) : ParseResult()
    data class Error(val message: String) : ParseResult()
}
