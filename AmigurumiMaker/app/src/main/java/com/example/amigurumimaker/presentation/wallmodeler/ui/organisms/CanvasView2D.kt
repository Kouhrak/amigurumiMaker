package com.example.amigurumimaker.presentation.wallmodeler.ui.organisms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.amigurumimaker.domain.model.ColorMode
import com.example.amigurumimaker.domain.model.CylinderCell
import com.example.amigurumimaker.domain.model.MeshCell
import com.example.amigurumimaker.domain.model.ParsedRow
import com.example.amigurumimaker.domain.model.RoundAnalysis
import com.example.amigurumimaker.domain.model.StitchType
import com.example.amigurumimaker.domain.model.ViewMode
import com.example.amigurumimaker.domain.CurvatureComputer

@Composable
fun CanvasView(
    meshCells: List<MeshCell>,
    cylinderCells: List<CylinderCell>,
    parsedRows: List<ParsedRow>,
    viewMode: ViewMode,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    onZoom: (Float) -> Unit,
    roundAnalyses: List<RoundAnalysis> = emptyList(),
    colorMode: ColorMode = ColorMode.GAUSS_HEATMAP,
    wireframeEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        onZoom(zoom)
                        onDrag(pan.x, pan.y)
                    }
                }
        ) {
            if (meshCells.isEmpty() && cylinderCells.isEmpty()) {
                drawContext.canvas.nativeCanvas.drawText(
                    "Ingresa un patrón válido para renderizar la malla.",
                    size.width / 2f,
                    size.height / 2f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#64748B")
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
                return@Canvas
            }

            withTransform({
                translate(size.width / 2f + offsetX, size.height / 2f + offsetY)
                scale(scale, scale, Offset.Zero)
            }) {
                when (viewMode) {
                    ViewMode.MESH_2D -> {
                        draw2DRowLabels(parsedRows)
                        draw2DMesh(meshCells)
                    }
                    ViewMode.CYLINDER_3D -> draw3DCylinder(
                        cells = cylinderCells,
                        roundAnalyses = roundAnalyses,
                        colorMode = colorMode,
                        wireframeEnabled = wireframeEnabled
                    )
                    ViewMode.REVOLUTION_3D -> {}
                }
            }
        }

        LegendOverlay(
            colorMode = colorMode,
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
        )

        ViewScaleBadge(
            viewMode = viewMode,
            scale = scale,
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
        )
    }
}

@Composable
private fun LegendOverlay(
    colorMode: ColorMode,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xCC0F172A), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xCC334155), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = if (colorMode == ColorMode.GAUSS_HEATMAP) "LEYENDA DE CURVATURA" else "LEYENDA DE GEOMETRÍA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8)
        )

        when (colorMode) {
            ColorMode.GAUSS_HEATMAP -> {
                LegendItem(color = Color(0xFF10B981), label = "K > 0 — Esférica (Aumentos)")
                LegendItem(color = Color(0xFF06B6D4), label = "K = 0 — Plana/Cilíndrica")
                LegendItem(color = Color(0xFFEC4899), label = "K < 0 — Hiperbólica (Disminuciones)")
            }
            ColorMode.ROW_GRADIENT -> {
                LegendItem(color = Color(0xFF64748B), label = "Punto Base")
                LegendItem(color = Color(0xFF10B981), label = "Aumento")
                LegendItem(color = Color(0xFFF43F5E), label = "Disminución")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
                .border(1.dp, Color(0x80475569), RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
private fun ViewScaleBadge(
    viewMode: ViewMode,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xCC0F172A), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text(
            text = if (viewMode == ViewMode.MESH_2D) "2D - Trapecios" else "3D - Cilindro",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF94A3B8)
        )
        Text(
            text = "%.0f%%".format(scale * 100),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B)
        )
    }
}

private fun DrawScope.draw2DMesh(cells: List<MeshCell>) {
    cells.forEach { cell ->
        val path = Path().apply {
            moveTo(cell.bottomLeft.x, cell.bottomLeft.y)
            lineTo(cell.bottomRight.x, cell.bottomRight.y)
            lineTo(cell.topRight.x, cell.topRight.y)
            lineTo(cell.topLeft.x, cell.topLeft.y)
            close()
        }

        val (fillColor, strokeColor) = when (cell.type) {
            StitchType.INCREASE -> Color(0x4010B981) to Color(0xFF10B981)
            StitchType.DECREASE -> Color(0x40F43F5E) to Color(0xFFF43F5E)
            StitchType.CHAIN -> Color(0x406366F1) to Color(0xFF6366F1)
            StitchType.NORMAL -> Color(0x40475569) to Color(0xFF94A3B8)
        }

        drawPath(path = path, color = fillColor)
        drawPath(path = path, color = strokeColor, style = Stroke(width = 2f))

        drawContext.canvas.nativeCanvas.drawText(
            cell.type.symbol.uppercase(),
            cell.centerX,
            cell.centerY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#CBD5E1")
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

private fun DrawScope.draw2DRowLabels(rows: List<ParsedRow>) {
    val rowHeight = 36f
    val baseStitchWidth = 32f
    val totalRows = rows.size
    val totalHeight = totalRows * rowHeight
    val startY = totalHeight / 2f

    rows.forEachIndexed { rowIdx, row ->
        val yBottom = startY - (rowIdx * rowHeight)
        val yTop = yBottom - rowHeight

        val totalBlocks = row.tokens.sumOf { it.count }.coerceAtLeast(1)
        val totalRowWidth = totalBlocks * baseStitchWidth
        val labelX = -totalRowWidth / 2f - 15f
        val centerY = (yBottom + yTop) / 2f

        drawContext.canvas.nativeCanvas.drawText(
            "${row.rowIndex}) ${row.raw}",
            labelX,
            centerY + 4f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#818CF8")
                textSize = 22f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
        )
    }
}

private fun DrawScope.draw3DCylinder(
    cells: List<CylinderCell>,
    roundAnalyses: List<RoundAnalysis>,
    colorMode: ColorMode,
    wireframeEnabled: Boolean
) {
    val curvatureByRow = roundAnalyses.associate { it.rowIndex to it.localCurvature }

    cells.forEach { cell ->
        if (cell.depth > 0.3f) return@forEach

        val path = Path().apply {
            moveTo(cell.x1, cell.y1)
            lineTo(cell.x2, cell.y2)
            lineTo(cell.x3, cell.y3)
            lineTo(cell.x4, cell.y4)
            close()
        }

        val alpha = (1f - cell.depth).coerceIn(0f, 1f)

        val fillColor = when (colorMode) {
            ColorMode.GAUSS_HEATMAP -> {
                val k = curvatureByRow[cell.rowIndex] ?: 0.0
                curvatureFillColor(k, alpha)
            }
            ColorMode.ROW_GRADIENT -> {
                when (cell.type) {
                    StitchType.INCREASE -> Color(0.2f, 0.7f, 0.5f, alpha)
                    StitchType.DECREASE -> Color(0.95f, 0.25f, 0.37f, alpha)
                    StitchType.CHAIN -> Color(0.4f, 0.4f, 0.9f, alpha)
                    StitchType.NORMAL -> Color(0.3f, 0.4f, 0.6f, alpha * 0.8f)
                }
            }
        }

        drawPath(path = path, color = fillColor)

        if (wireframeEnabled) {
            val strokeColor = Color(0xFF334155)
            drawPath(path = path, color = strokeColor, style = Stroke(width = 1f))
        }
    }
}

private fun curvatureFillColor(k: Double, alpha: Float): Color {
    return when {
        k > 1e-8 -> Color(0xFF10B981).copy(alpha = alpha.coerceIn(0.2f, 0.9f))
        k < -1e-8 -> Color(0xFFEC4899).copy(alpha = alpha.coerceIn(0.2f, 0.9f))
        else -> Color(0xFF06B6D4).copy(alpha = alpha.coerceIn(0.2f, 0.9f))
    }
}

@Preview(showBackground = true)
@Composable
private fun CanvasViewEmptyPreview() {
    CanvasView(
        meshCells = emptyList(), cylinderCells = emptyList(), parsedRows = emptyList(),
        viewMode = ViewMode.MESH_2D, scale = 1f, offsetX = 0f, offsetY = 0f,
        onDrag = { _, _ -> }, onZoom = {}
    )
}
