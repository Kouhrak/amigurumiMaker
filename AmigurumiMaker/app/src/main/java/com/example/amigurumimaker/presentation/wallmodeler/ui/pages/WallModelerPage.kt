package com.example.amigurumimaker.presentation.wallmodeler.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.amigurumimaker.presentation.wallmodeler.WallModelerEffect
import com.example.amigurumimaker.presentation.wallmodeler.WallModelerIntent
import com.example.amigurumimaker.presentation.wallmodeler.WallModelerViewModel
import com.example.amigurumimaker.presentation.wallmodeler.ui.templates.WallModelerTemplate

@Composable
fun WallModelerPage(
    viewModel: WallModelerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var localText by remember { mutableStateOf("1) 8c (7p)\n2) 3p 1a 3p 1c (8p)\n3) 8p 1c (8p)\n4) 8p 1c (8p)") }

    LaunchedEffect(Unit) {
        viewModel.process(WallModelerIntent.ParseSyntax(localText))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WallModelerEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WallModelerEffect.CenterCamera -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WallModelerTemplate(
            syntaxText = localText,
            onTextChange = { localText = it },
            onParseClick = {
                viewModel.process(WallModelerIntent.ParseSyntax(localText))
            },
            onExampleClick = { index ->
                viewModel.process(WallModelerIntent.LoadExample(index))
            },
            parsedRows = state.parsedRows,
            meshCells = state.meshCells,
            cylinderCells = state.cylinderCells,
            buildEnabled = !state.isLoading,
            viewMode = state.viewMode,
            activeTab = state.activeTab,
            onViewModeChange = { viewModel.process(WallModelerIntent.SetViewMode(it)) },
            onTabChange = { viewModel.process(WallModelerIntent.SetActiveTab(it)) },
            scale = state.scale,
            offsetX = state.offsetX,
            offsetY = state.offsetY,
            onDrag = { dx, dy -> viewModel.process(WallModelerIntent.DragBy(dx, dy)) },
            onZoom = { factor -> viewModel.process(WallModelerIntent.ZoomBy(factor)) },
            onResetView = { viewModel.process(WallModelerIntent.ResetView) },
            logMessage = state.logMessage,
            totalStitches = state.totalStitches,
            totalIncreases = state.totalIncreases,
            totalDecreases = state.totalDecreases,
            modifier = Modifier.fillMaxSize()
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
