package com.example.amigurumimaker.domain.model

data class MeshSettings(
    val stitchWidth: Float = 0.5f,
    val stitchHeight: Float = 0.5f,
    val wireframe: Boolean = true,
    val colorMode: ColorMode = ColorMode.GAUSS_HEATMAP
)
