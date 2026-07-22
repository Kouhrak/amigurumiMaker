package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.AppButton
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.SyntaxInputField

@Composable
fun PatternEditorPanel(
    text: String,
    onTextChange: (String) -> Unit,
    onParseClick: () -> Unit,
    onExampleClick: (Int) -> Unit,
    enabled: Boolean = true,
    logMessage: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Editor de Patrón (Sintaxis Estándar)",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Formatos: 1) 8c (7p)  |  2) [1p 1a] 6v (18p)  |  3) 3p 1a 3p 1c (8p)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        SyntaxInputField(
            text = text,
            onTextChange = onTextChange
        )

        Spacer(Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { onExampleClick(1) },
                modifier = Modifier.weight(1f)
            ) { Text("Ejemplo 1", style = MaterialTheme.typography.bodySmall) }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                onClick = { onExampleClick(2) },
                modifier = Modifier.weight(1f)
            ) { Text("Ejemplo 2", style = MaterialTheme.typography.bodySmall) }
        }

        Spacer(Modifier.height(8.dp))

        AppButton(
            label = "Procesar y Reconstruir Malla",
            onClick = onParseClick,
            enabled = enabled
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = logMessage,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PatternEditorPanelPreview() {
    PatternEditorPanel(
        text = "1) 8c (7p)\n2) 3p 1a 3p 1c (8p)",
        onTextChange = {},
        onParseClick = {},
        onExampleClick = {},
        logMessage = "Listo para procesar patrón."
    )
}
