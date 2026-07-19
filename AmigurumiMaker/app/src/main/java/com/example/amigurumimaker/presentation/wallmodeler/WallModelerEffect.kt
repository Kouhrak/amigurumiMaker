package com.example.amigurumimaker.presentation.wallmodeler

sealed interface WallModelerEffect {
    data class ShowError(val message: String) : WallModelerEffect
    data object CenterCamera : WallModelerEffect
}
