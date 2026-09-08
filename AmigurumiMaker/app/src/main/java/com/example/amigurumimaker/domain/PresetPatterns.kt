package com.example.amigurumimaker.domain

data class PresetPattern(
    val name: String,
    val description: String,
    val pattern: String
)

object PresetPatterns {
    val presets = listOf(
        PresetPattern(
            name = "Esfera",
            description = "K > 0 — Curvatura esférica positiva",
            pattern = "1) 6c (6p)\n2) [1a] 6v (12p)\n3) [1p 1a] 6v (18p)\n4) [2p 1a] 6v (24p)\n5) [3p 1a] 6v (30p)\n6) [9p 1a] 3v (33p)"
        ),
        PresetPattern(
            name = "Hiperbólica",
            description = "K < 0 — Curvatura hiperbólica negativa",
            pattern = "1) 6c (6p)\n2) [1p 1a] 6v (12p)\n3) [1p 1a] 12v (24p)\n4) [1p 1a] 24v (48p)\n5) [1p 1a] 48v (96p)"
        ),
        PresetPattern(
            name = "Cono",
            description = "δ > 0 — Expansión radial angular",
            pattern = "1) 6c (6p)\n2) [1a] 6v (12p)\n3) [1a] 12v (24p)\n4) [1a] 24v (48p)"
        ),
        PresetPattern(
            name = "Cilindro",
            description = "K ≈ 0 — Sin curvatura neta",
            pattern = "1) [1p] 10v (10p)\n2) [1p] 10v (10p)\n3) [1p] 10v (10p)\n4) [1p] 10v (10p)\n5) [1p] 10v (10p)"
        ),
        PresetPattern(
            name = "Disco Plano",
            description = "K → 0 — Superficie plana",
            pattern = "1) 6c (6p)\n2) [1a] 6v (12p)\n3) [1a] 12v (24p)\n4) [1a] 24v (48p)\n5) [1a] 48v (96p)\n6) [1a] 96v (192p)"
        )
    )
}
