package com.example.amigurumimaker.presentation.wallmodeler.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(
    isValid: Boolean,
    errorMsg: String,
    modifier: Modifier = Modifier
) {
    val (bg, border, text) = if (isValid) {
        Triple(Color(0xFF052E16), Color(0xFF166534), Color(0xFF4ADE80))
    } else {
        Triple(Color(0xFF2D0A0E), Color(0xFF7F1D1D), Color(0xFFF87171))
    }

    Text(
        text = if (isValid) "OK" else "ERROR",
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = text,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgeOkPreview() {
    StatusBadge(isValid = true, errorMsg = "")
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgeErrorPreview() {
    StatusBadge(isValid = false, errorMsg = "Validation failed")
}
