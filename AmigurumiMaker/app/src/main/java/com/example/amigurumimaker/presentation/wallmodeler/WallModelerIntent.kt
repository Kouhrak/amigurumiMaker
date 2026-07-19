package com.example.amigurumimaker.presentation.wallmodeler

sealed interface WallModelerIntent {
    data class ParseSyntax(val text: String) : WallModelerIntent
    data object Reset : WallModelerIntent
}
