package com.example.amigurumimaker.domain.model

enum class StitchType(val symbol: String, val yieldPerUnit: Int) {
    NORMAL("p", 1),
    INCREASE("a", 2),
    DECREASE("d", 1),
    CHAIN("c", 0)
}
