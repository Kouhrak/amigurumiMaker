package com.example.amigurumimaker.presentation.wallmodeler.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MetricCard(
    label: String,
    value: String,
    sublabel: String = "",
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0x0A000000), RoundedCornerShape(8.dp))
            .border(0.5.dp, Color(0x1F000000), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        if (sublabel.isNotEmpty()) {
            Text(
                text = sublabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MetricCardPreview() {
    MetricCard(
        label = "Curvatura Total ∫K dA",
        value = "12.57",
        sublabel = "Esférica (χ ≈ 2.0)",
        valueColor = Color(0xFF10B981)
    )
}
