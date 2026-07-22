package com.example.amigurumimaker.presentation.wallmodeler.ui.atoms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StitchSymbol(
    symbol: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$label  $symbol",
        style = MaterialTheme.typography.bodySmall,
        fontFamily = FontFamily.Monospace,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StitchSymbolPreview() {
    StitchSymbol(symbol = "+ / x", label = "P. Bajo (p.b.)")
}
