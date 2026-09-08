package com.example.amigurumimaker.domain.model

data class RingSegment(
    val vertices: List<Float3>,
    val indices: List<Int>,
    val color: Float3
)

data class RevolutionMesh(
    val segments: List<RingSegment>,
    val totalHeight: Float
)
