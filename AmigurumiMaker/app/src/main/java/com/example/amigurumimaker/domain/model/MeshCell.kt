package com.example.amigurumimaker.domain.model

data class Point2D(val x: Float, val y: Float)

data class MeshCell(
    val rowIndex: Int,
    val stitchIndex: Int,
    val type: StitchType,
    val bottomLeft: Point2D,
    val bottomRight: Point2D,
    val topRight: Point2D,
    val topLeft: Point2D
) {
    val centerX: Float get() = (bottomLeft.x + bottomRight.x + topLeft.x + topRight.x) / 4f
    val centerY: Float get() = (bottomLeft.y + bottomRight.y + topLeft.y + topRight.y) / 4f
}
