package com.example.amigurumimaker.domain.model

enum class SurfaceClassification(val label: String, val description: String) {
    SPHERE("Esférica", "K > 0 — Aumentos predominan"),
    HYPERBOLIC("Hiperbólica", "K < 0 — Disminuciones predominan"),
    CYLINDRICAL("Cilíndrica", "K ≈ 0 — Estable, sin curvatura neta"),
    CONICAL("Cónica", "δ > 0 — Expansión radial constante"),
    FLAT("Plana", "K = 0 — Sin curvatura");

    companion object {
        fun classify(avgCurvature: Double, hasConicalGrowth: Boolean): SurfaceClassification {
            if (hasConicalGrowth && avgCurvature > 1e-6) return CONICAL
            return when {
                avgCurvature > 1e-6 -> SPHERE
                avgCurvature < -1e-6 -> HYPERBOLIC
                else -> CYLINDRICAL
            }
        }
    }
}
