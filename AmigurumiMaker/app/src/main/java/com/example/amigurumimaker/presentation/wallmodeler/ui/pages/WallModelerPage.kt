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
    var localText by remember { mutableStateOf("1) 1c (1p)") }

    LaunchedEffect(Unit) {
        viewModel.process(WallModelerIntent.ParseSyntax(localText))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WallModelerEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WallModelerEffect.CenterCamera -> {
                    // Camera auto-center handled by GeometryViewport based on geometries
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        WallModelerTemplate(
            syntaxText = localText,
            onTextChange = { localText = it },
            onBuildClick = {
                viewModel.process(WallModelerIntent.ParseSyntax(localText))
            },
            geometries = state.geometries,
            buildEnabled = !state.isLoading,
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
