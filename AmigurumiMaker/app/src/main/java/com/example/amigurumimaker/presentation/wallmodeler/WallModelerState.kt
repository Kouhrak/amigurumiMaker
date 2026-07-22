package com.example.amigurumimaker.presentation.wallmodeler

import com.example.amigurumimaker.domain.model.*

data class WallModelerState(
    val syntaxText: String = "",
    val parsedRows: List<ParsedRow> = emptyList(),
    val meshCells: List<MeshCell> = emptyList(),
    val cylinderCells: List<CylinderCell> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val viewMode: ViewMode = ViewMode.MESH_2D,
    val activeTab: InfoTab = InfoTab.CATALOG,
    val scale: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val logMessage: String = "Listo para procesar patrón.",
    val totalStitches: Int = 0,
    val totalIncreases: Int = 0,
    val totalDecreases: Int = 0
)
