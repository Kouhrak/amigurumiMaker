package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StitchCatalog(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp)
    ) {
        SectionHeader("1. Puntos Básicos")
        StitchEntry("P. Cadena (p.c. / cad.)", "○")
        StitchEntry("P. Deslizado (p.d. / p.r.)", "●")
        StitchEntry("P. Bajo (p.b. / m.p.)", "+ / x")
        StitchEntry("P. Medio Alto (p.m.a.)", "T")
        StitchEntry("P. Alto (p.a. / v.)", "ǂ")
        StitchEntry("P. Alto Doble (p.a.d.)", "╪")

        Spacer(Modifier.height(8.dp))
        SectionHeader("2. Modificaciones & Texturas")
        StitchEntry("FLO (Hebra Delantera)", "⌒")
        StitchEntry("BLO (Hebra Trasera)", "◡")

        Spacer(Modifier.height(8.dp))
        SectionHeader("3. Puntos Compuestos")
        Text(
            "Aumento (a): 2p tejiendo sobre 1 p. base.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            "Disminución (d): 2p fusionados hacia 1 punta.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun StitchEntry(label: String, symbol: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = symbol,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StitchCatalogPreview() {
    StitchCatalog()
}
