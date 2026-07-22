package com.example.amigurumimaker.presentation.wallmodeler.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color(0xFF1E1B4B) else Color.Transparent
    val textColor = if (isSelected) Color(0xFF818CF8) else Color(0xFF94A3B8)

    Text(
        text = label,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun TabButtonSelectedPreview() {
    TabButton(label = "Catálogo Puntos", isSelected = true, onClick = {})
}

@Preview(showBackground = true)
@Composable
private fun TabButtonUnselectedPreview() {
    TabButton(label = "Reglas Geométricas", isSelected = false, onClick = {})
}
