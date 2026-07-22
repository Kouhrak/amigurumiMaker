package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GeometricRules(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            "Deformación del Cubo/Célula:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        RuleCard("Punto Normal (p):", "Bloque rectangular regular [1 x 1].")
        RuleCard("Aumento (a):", "Trapecio divergente (Base Superior > Base Inferior). Se ensancha creando un abanico.")
        RuleCard("Disminución (d):", "Trapecio convergente (Base Superior < Base Inferior). Se estrecha el entramado.")
        RuleCard("Punto Cadena (c):", "Ancla inicial o desfase de borde.")

        Text(
            "Estructura Sintáctica Por Fila:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )

        Text(
            "X) Identificador de fila cronológica.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "[Cant][Tipo] p.ej. 3p 1a 3p.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "([Total]p) Verificador matemático de cierre de fila.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GeometricRulesPreview() {
    GeometricRules()
}

@Composable
private fun RuleCard(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(Color(0x0A000000), RoundedCornerShape(6.dp))
            .border(0.5.dp, Color(0x1A000000), RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
