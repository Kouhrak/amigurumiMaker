package com.example.amigurumimaker.domain.model

data class CylinderCell(
    val x1: Float, val y1: Float,
    val x2: Float, val y2: Float,
    val x3: Float, val y3: Float,
    val x4: Float, val y4: Float,
    val type: StitchType,
    val depth: Float,
    val rowIndex: Int = 0
)
