package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.model.ParsedRow
import com.example.amigurumimaker.domain.model.ParsedToken
import com.example.amigurumimaker.domain.model.RoundAnalysis
import com.example.amigurumimaker.domain.model.StitchType
import com.example.amigurumimaker.presentation.wallmodeler.ui.atoms.StatusBadge

@Composable
fun AnalysisTable(
    rows: List<ParsedRow>,
    roundAnalyses: List<RoundAnalysis>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "Desglose de Filas y Análisis Geométrico",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (rows.isEmpty()) {
            Text(
                text = "No hay datos procesados. Ingresa un patrón arriba.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
            return
        }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .horizontalScroll(scrollState)
        ) {
            TableHeader()

            rows.forEachIndexed { index, row ->
                if (index > 0) HorizontalDivider(color = Color(0x1A000000))
                TableRow(
                    row = row,
                    analysis = roundAnalyses.getOrNull(index)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalysisTableWithDataPreview() {
    val mockRows = listOf(
        ParsedRow(
            raw = "1) 8c (7p)", rowIndex = 1,
            tokens = listOf(ParsedToken(StitchType.CHAIN, 8, 0)),
            totalCalculated = 0, totalExpected = 7,
            isValid = false, errorMsg = "Esperados 7p, calculados 0p.",
            increaseCount = 0, decreaseCount = 0
        ),
        ParsedRow(
            raw = "2) 3p 1a 3p 1c (8p)", rowIndex = 2,
            tokens = listOf(
                ParsedToken(StitchType.NORMAL, 3, 3),
                ParsedToken(StitchType.INCREASE, 1, 2),
                ParsedToken(StitchType.NORMAL, 3, 3),
                ParsedToken(StitchType.CHAIN, 1, 0)
            ),
            totalCalculated = 8, totalExpected = 8,
            isValid = true, errorMsg = "",
            increaseCount = 1, decreaseCount = 0
        )
    )
    val mockAnalyses = listOf(
        RoundAnalysis(1, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
        RoundAnalysis(2, 8, 8, 1.27, 1.0, 0.0, 7.26, 0.5, 0.126, 7.98)
    )
    AnalysisTable(rows = mockRows, roundAnalyses = mockAnalyses)
}

@Preview(showBackground = true)
@Composable
private fun AnalysisTableEmptyPreview() {
    AnalysisTable(rows = emptyList(), roundAnalyses = emptyList())
}

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .background(Color(0x0A000000), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        HeaderCell("Fila #", 48.dp)
        HeaderCell("Instrucción", 200.dp)
        HeaderCell("Aum.", 40.dp, TextAlign.Center)
        HeaderCell("Dis.", 40.dp, TextAlign.Center)
        HeaderCell("Nᵢ", 40.dp, TextAlign.End)
        HeaderCell("ΔN", 40.dp, TextAlign.End)
        HeaderCell("rᵢ", 48.dp, TextAlign.End)
        HeaderCell("θᵢ", 48.dp, TextAlign.End)
        HeaderCell("Kᵢ", 56.dp, TextAlign.End)
        HeaderCell("Aᵢ", 56.dp, TextAlign.End)
        HeaderCell("Estado", 56.dp, TextAlign.Center)
    }
}

@Composable
private fun HeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    align: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = align,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableRow(
    row: ParsedRow,
    analysis: RoundAnalysis?
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = row.rowIndex.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(48.dp)
        )
        Text(
            text = row.raw,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(200.dp)
        )
        Text(
            text = if (row.increaseCount > 0) row.increaseCount.toString() else "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF4ADE80),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = if (row.decreaseCount > 0) row.decreaseCount.toString() else "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFFF87171),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = "${row.totalCalculated}p",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.width(40.dp)
        )

        val deltaNText = if (analysis != null && analysis.deltaN != 0) {
            if (analysis.deltaN > 0) "+${analysis.deltaN}" else analysis.deltaN.toString()
        } else "-"
        Text(
            text = deltaNText,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = when {
                analysis == null || analysis.deltaN == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                analysis.deltaN > 0 -> Color(0xFF4ADE80)
                else -> Color(0xFFF87171)
            },
            textAlign = TextAlign.End,
            modifier = Modifier.width(40.dp)
        )

        Text(
            text = analysis?.let { "%.2f".format(it.theoreticalRadius) } ?: "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(48.dp)
        )

        Text(
            text = analysis?.let { "%.1f°".format(it.inclinationAngleDeg) } ?: "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(48.dp)
        )

        val kValue = analysis?.localCurvature ?: 0.0
        val kColor = if (kValue > 1e-8) Color(0xFF10B981)
        else if (kValue < -1e-8) Color(0xFFEC4899)
        else MaterialTheme.colorScheme.onSurfaceVariant
        Text(
            text = analysis?.let { "%.4f".format(it.localCurvature) } ?: "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = kColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(56.dp)
        )

        Text(
            text = analysis?.let { "%.2f".format(it.roundArea) } ?: "-",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(56.dp)
        )

        StatusBadge(
            isValid = row.isValid,
            errorMsg = row.errorMsg,
            modifier = Modifier.width(56.dp)
        )
    }
}
