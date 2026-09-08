package com.example.amigurumimaker.presentation.wallmodeler.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.PresetPatterns

@Composable
fun PresetsBar(
    activePresetIndex: Int = -1,
    onPresetClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = "Geometrías de Ejemplo",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetPatterns.presets.forEachIndexed { index, preset ->
                PresetChip(
                    name = preset.name,
                    description = preset.description,
                    isActive = index == activePresetIndex,
                    onClick = { onPresetClick(index) }
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    name: String,
    description: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isActive) Color(0xFF4F46E5) else Color(0xFFF1F5F9)
    val borderColor = if (isActive) Color.Transparent else Color(0xFFE2E8F0)
    val textColor = if (isActive) Color.White else Color(0xFF475569)
    val descColor = if (isActive) Color(0xFFC7D2FE) else Color(0xFF94A3B8)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = descColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PresetsBarPreview() {
    PresetsBar(activePresetIndex = 0, onPresetClick = {})
}
