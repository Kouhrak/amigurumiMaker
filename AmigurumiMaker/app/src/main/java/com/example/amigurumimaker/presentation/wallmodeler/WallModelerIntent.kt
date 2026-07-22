package com.example.amigurumimaker.presentation.wallmodeler

import com.example.amigurumimaker.domain.model.InfoTab
import com.example.amigurumimaker.domain.model.ViewMode

sealed interface WallModelerIntent {
    data class ParseSyntax(val text: String) : WallModelerIntent
    data object Reset : WallModelerIntent
    data class ZoomBy(val factor: Float) : WallModelerIntent
    data object ResetView : WallModelerIntent
    data class SetViewMode(val mode: ViewMode) : WallModelerIntent
    data class SetActiveTab(val tab: InfoTab) : WallModelerIntent
    data class LoadExample(val index: Int) : WallModelerIntent
    data class DragBy(val dx: Float, val dy: Float) : WallModelerIntent
}
