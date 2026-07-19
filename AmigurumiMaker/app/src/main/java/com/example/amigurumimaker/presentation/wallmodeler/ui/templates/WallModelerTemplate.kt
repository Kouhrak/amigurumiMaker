package com.example.amigurumimaker.presentation.wallmodeler.ui.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.amigurumimaker.domain.model.BlockGeometry
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.HeaderBar
import com.example.amigurumimaker.presentation.wallmodeler.ui.molecules.SyntaxInputPanel
import com.example.amigurumimaker.presentation.wallmodeler.ui.organisms.GeometryViewport

@Composable
fun WallModelerTemplate(
    syntaxText: String,
    onTextChange: (String) -> Unit,
    onBuildClick: () -> Unit,
    geometries: List<BlockGeometry>,
    buildEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        HeaderBar(title = "Geometr\u00eda de Paredes")
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            SyntaxInputPanel(
                text = syntaxText,
                onTextChange = onTextChange,
                onBuildClick = onBuildClick,
                enabled = buildEnabled,
                modifier = Modifier.weight(1f)
            )
            GeometryViewport(
                geometries = geometries,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
