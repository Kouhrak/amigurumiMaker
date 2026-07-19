package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.AppButton
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.SyntaxInputField

@Composable
fun SyntaxInputPanel(
    text: String,
    onTextChange: (String) -> Unit,
    onBuildClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(
            text = "Código de Estructura",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        SyntaxInputField(
            text = text,
            onTextChange = onTextChange
        )
        Spacer(Modifier.height(12.dp))
        AppButton(
            label = "Dibujar",
            onClick = onBuildClick,
            enabled = enabled
        )
    }
}
