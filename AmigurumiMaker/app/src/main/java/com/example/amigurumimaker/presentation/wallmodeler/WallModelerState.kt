package com.example.amigurumimaker.presentation.wallmodeler

import com.example.amigurumimaker.domain.model.BlockGeometry
import com.example.amigurumimaker.domain.model.RowDef

data class WallModelerState(
    val syntaxText: String = "",
    val rows: List<RowDef> = emptyList(),
    val geometries: List<BlockGeometry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val parsedBlockCount: Int = 0
)
