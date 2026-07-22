package com.example.amigurumimaker.presentation.wallmodeler.ui.atoms

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SyntaxInputField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Código de Estructura",
    placeholder: String = "Ej: 1) 10c (10p), 2) 1d 8p 1d (8p)"
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(8.dp),
        minLines = 4,
        maxLines = Int.MAX_VALUE,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
    )
}

@Preview(showBackground = true)
@Composable
private fun SyntaxInputFieldPreview() {
    SyntaxInputField(
        text = "1) 8c (7p)\n2) 3p 1a 3p 1c (8p)",
        onTextChange = {}
    )
}
