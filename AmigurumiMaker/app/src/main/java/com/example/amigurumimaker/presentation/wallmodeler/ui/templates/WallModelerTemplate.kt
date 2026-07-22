package com.example.amigurumimaker.presentation.wallmodeler.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.model.CylinderCell
import com.example.amigurumimaker.domain.model.InfoTab
import com.example.amigurumimaker.domain.model.MeshCell
import com.example.amigurumimaker.domain.model.ParsedRow
import com.example.amigurumimaker.domain.model.ViewMode
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.HeaderBar
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.StatsRow
import com.example.amigurumimaker.presentation.wallmodeler.ui.organisms.AnalysisTable
import com.example.amigurumimaker.presentation.wallmodeler.ui.organisms.CanvasView
import com.example.amigurumimaker.presentation.wallmodeler.ui.organisms.PatternEditorPanel
import com.example.amigurumimaker.presentation.wallmodeler.ui.organisms.StitchInfoPanel

@Composable
fun WallModelerTemplate(
    syntaxText: String,
    onTextChange: (String) -> Unit,
    onParseClick: () -> Unit,
    onExampleClick: (Int) -> Unit,
    parsedRows: List<ParsedRow>,
    meshCells: List<MeshCell>,
    cylinderCells: List<CylinderCell>,
    buildEnabled: Boolean,
    viewMode: ViewMode,
    activeTab: InfoTab,
    onViewModeChange: (ViewMode) -> Unit,
    onTabChange: (InfoTab) -> Unit,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    onZoom: (Float) -> Unit,
    onResetView: () -> Unit,
    logMessage: String,
    totalStitches: Int,
    totalIncreases: Int,
    totalDecreases: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        HeaderBar(title = "AmiCube Visualizer")

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
            ) {
                PatternEditorPanel(
                    text = syntaxText,
                    onTextChange = onTextChange,
                    onParseClick = onParseClick,
                    onExampleClick = onExampleClick,
                    enabled = buildEnabled,
                    logMessage = logMessage
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                StitchInfoPanel(
                    activeTab = activeTab,
                    onTabChange = onTabChange
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxSize()
            ) {
                CanvasControlsBar(
                    viewMode = viewMode,
                    parsedRowCount = parsedRows.size,
                    onViewModeChange = onViewModeChange,
                    onZoomIn = { onZoom(1.2f) },
                    onZoomOut = { onZoom(1f / 1.2f) },
                    onResetView = onResetView,
                    modifier = Modifier.fillMaxWidth()
                )

                CanvasView(
                    meshCells = meshCells,
                    cylinderCells = cylinderCells,
                    parsedRows = parsedRows,
                    viewMode = viewMode,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    onDrag = onDrag,
                    onZoom = { factor -> if (factor > 0f) onZoom(factor) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                StatsRow(
                    totalStitches = totalStitches,
                    totalIncreases = totalIncreases,
                    totalDecreases = totalDecreases,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                AnalysisTable(
                    rows = parsedRows,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CanvasControlsBar(
    viewMode: ViewMode,
    parsedRowCount: Int,
    onViewModeChange: (ViewMode) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Vista de Deformación Célula/Cubo",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "$parsedRowCount filas procesadas",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF818CF8),
                modifier = Modifier
                    .background(Color(0xFF1E1B4B), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ViewModeButton(
                label = "Plano 2D (Trapecios)",
                isActive = viewMode == ViewMode.MESH_2D,
                onClick = { onViewModeChange(ViewMode.MESH_2D) }
            )
            ViewModeButton(
                label = "Cilindro 3D",
                isActive = viewMode == ViewMode.CYLINDER_3D,
                onClick = { onViewModeChange(ViewMode.CYLINDER_3D) }
            )

            Box(
                modifier = Modifier
                    .size(width = 1.dp, height = 20.dp)
                    .background(Color(0xFFCBD5E1))
            )

            ZoomIconButton(label = "+", onClick = onZoomIn)
            ZoomIconButton(label = "−", onClick = onZoomOut)
            ZoomIconButton(label = "↺", onClick = onResetView)
        }
    }
}

@Composable
private fun ViewModeButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isActive) Color(0xFF4F46E5) else Color.Transparent
    val textColor = if (isActive) Color.White else Color(0xFF94A3B8)
    val borderColor = if (isActive) Color.Transparent else Color(0xFFE2E8F0)

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        color = textColor,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun ZoomIconButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF1F5F9))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF475569)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WallModelerTemplatePreview() {
    WallModelerTemplate(
        syntaxText = "1) 8c (7p)\n2) 3p 1a 3p 1c (8p)",
        onTextChange = {},
        onParseClick = {},
        onExampleClick = {},
        parsedRows = emptyList(),
        meshCells = emptyList(),
        cylinderCells = emptyList(),
        buildEnabled = true,
        viewMode = com.example.amigurumimaker.domain.model.ViewMode.MESH_2D,
        activeTab = com.example.amigurumimaker.domain.model.InfoTab.CATALOG,
        onViewModeChange = {},
        onTabChange = {},
        scale = 1f,
        offsetX = 0f,
        offsetY = 0f,
        onDrag = { _, _ -> },
        onZoom = {},
        onResetView = {},
        logMessage = "Listo para procesar patrón.",
        totalStitches = 0,
        totalIncreases = 0,
        totalDecreases = 0
    )
}
