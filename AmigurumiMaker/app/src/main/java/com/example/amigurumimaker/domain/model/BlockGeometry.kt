package com.example.amigurumimaker.domain.model

data class Float3(val x: Float, val y: Float, val z: Float)
data class IntPair(val first: Int, val second: Int)
data class IntTriple(val first: Int, val second: Int, val third: Int)
data class BlockGeometry(
    val vertices: List<Float3>,
    val edges: List<IntPair>,
    val faceIndices: List<IntTriple>
)
