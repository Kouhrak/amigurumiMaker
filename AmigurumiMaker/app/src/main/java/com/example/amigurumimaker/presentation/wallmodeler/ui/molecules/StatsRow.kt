package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.StatCard

@Composable
fun StatsRow(
    totalStitches: Int,
    totalIncreases: Int,
    totalDecreases: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "Total Puntos Malla",
            value = totalStitches.toString(),
            valueColor = Color(0xFF818CF8),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total Aumentos",
            value = totalIncreases.toString(),
            valueColor = Color(0xFF4ADE80),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Total Disminuciones",
            value = totalDecreases.toString(),
            valueColor = Color(0xFFF87171),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsRowPreview() {
    StatsRow(totalStitches = 42, totalIncreases = 12, totalDecreases = 6)
}
