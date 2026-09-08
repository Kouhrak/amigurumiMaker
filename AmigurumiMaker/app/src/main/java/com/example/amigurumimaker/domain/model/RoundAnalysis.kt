package com.example.amigurumimaker.domain.model

data class RoundAnalysis(
    val rowIndex: Int,
    val stitchCount: Int,
    val deltaN: Int,
    val theoreticalRadius: Double,
    val cosTheta: Double,
    val sinTheta: Double,
    val inclinationAngleDeg: Double,
    val liftZ: Double,
    val localCurvature: Double,
    val roundArea: Double
)
