package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.model.SurfaceClassification
import com.example.amigurumimaker.domain.model.SurfaceMetrics
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.MetricCard

@Composable
fun MetricsGrid(
    surfaceMetrics: SurfaceMetrics?,
    classification: SurfaceClassification,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Métricas Topológicas",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (surfaceMetrics == null) {
            Text(
                text = "Procesa un patrón para calcular métricas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            return
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                label = "Área Total",
                value = "%.2f u²".format(surfaceMetrics.totalArea),
                valueColor = Color(0xFF818CF8),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Curvatura ∫K dA",
                value = "%.2f".format(surfaceMetrics.totalCurvature),
                valueColor = when (classification) {
                    SurfaceClassification.SPHERE, SurfaceClassification.CONICAL -> Color(0xFF10B981)
                    SurfaceClassification.HYPERBOLIC -> Color(0xFFEC4899)
                    else -> Color(0xFF06B6D4)
                },
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Car. Euler χ",
                value = "%.2f".format(surfaceMetrics.eulerCharacteristic),
                sublabel = getClassificationLabel(classification),
                valueColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun getClassificationLabel(c: SurfaceClassification): String = c.label

@Preview(showBackground = true)
@Composable
private fun MetricsGridPreview() {
    MetricsGrid(
        surfaceMetrics = SurfaceMetrics(totalArea = 62.83, totalCurvature = 12.57, eulerCharacteristic = 2.0),
        classification = SurfaceClassification.SPHERE
    )
}

@Preview(showBackground = true)
@Composable
private fun MetricsGridEmptyPreview() {
    MetricsGrid(surfaceMetrics = null, classification = SurfaceClassification.FLAT)
}
