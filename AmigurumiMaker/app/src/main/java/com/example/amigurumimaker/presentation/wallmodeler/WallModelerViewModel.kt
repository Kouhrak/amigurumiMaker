package com.example.amigurumimaker.presentation.wallmodeler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amigurumimaker.domain.CurvatureComputer
import com.example.amigurumimaker.domain.MeshComputer
import com.example.amigurumimaker.domain.PatternParser
import com.example.amigurumimaker.domain.PresetPatterns
import com.example.amigurumimaker.domain.RevolutionMeshComputer
import com.example.amigurumimaker.domain.model.InfoTab
import com.example.amigurumimaker.domain.model.ViewMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WallModelerViewModel : ViewModel() {
    private val _state = MutableStateFlow(WallModelerState())
    val state: StateFlow<WallModelerState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WallModelerEffect>()
    val effect: SharedFlow<WallModelerEffect> = _effect.asSharedFlow()

    fun process(intent: WallModelerIntent) {
        when (intent) {
            is WallModelerIntent.ParseSyntax -> parseSyntax(intent.text)
            is WallModelerIntent.Reset -> reset()
            is WallModelerIntent.ZoomBy -> zoom(intent.factor)
            is WallModelerIntent.ResetView -> resetView()
            is WallModelerIntent.SetViewMode -> setViewMode(intent.mode)
            is WallModelerIntent.SetActiveTab -> setActiveTab(intent.tab)
            is WallModelerIntent.LoadExample -> loadExample(intent.index)
            is WallModelerIntent.DragBy -> dragBy(intent.dx, intent.dy)
            is WallModelerIntent.SetColorMode -> setColorMode(intent.mode)
            is WallModelerIntent.SetWireframe -> setWireframe(intent.enabled)
            is WallModelerIntent.LoadPreset -> loadPreset(intent.index)
        }
    }

    private fun parseSyntax(text: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val parsedRows = PatternParser.parse(text)
            if (parsedRows.isEmpty()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No se encontraron filas válidas.",
                        logMessage = "Error: Sintaxis no válida."
                    )
                }
                _effect.emit(WallModelerEffect.ShowError("No se encontraron filas válidas."))
                return@launch
            }

            val meshCells = MeshComputer.compute2DCells(parsedRows)
            val cylinderCells = MeshComputer.compute3DProjection(parsedRows)
            val totalStitches = parsedRows.sumOf { it.totalCalculated }
            val totalIncreases = parsedRows.sumOf { it.increaseCount }
            val totalDecreases = parsedRows.sumOf { it.decreaseCount }

            val roundAnalyses = CurvatureComputer.computeRoundAnalyses(parsedRows)
            val surfaceMetrics = CurvatureComputer.computeSurfaceMetrics(roundAnalyses)
            val surfaceClassification = CurvatureComputer.classifySurface(roundAnalyses)
            val revolutionMesh = RevolutionMeshComputer.computeMesh(
                analyses = roundAnalyses,
                colorMode = _state.value.colorMode
            )

            _state.update {
                it.copy(
                    syntaxText = text,
                    parsedRows = parsedRows,
                    meshCells = meshCells,
                    cylinderCells = cylinderCells,
                    isLoading = false,
                    error = null,
                    totalStitches = totalStitches,
                    totalIncreases = totalIncreases,
                    totalDecreases = totalDecreases,
                    roundAnalyses = roundAnalyses,
                    surfaceMetrics = surfaceMetrics,
                    surfaceClassification = surfaceClassification,
                    revolutionMesh = revolutionMesh,
                    logMessage = "Procesadas exitosamente ${parsedRows.size} filas. Malla 3D generada."
                )
            }
        }
    }

    private fun zoom(factor: Float) {
        _state.update {
            val newScale = (it.scale * factor).coerceIn(0.3f, 4f)
            it.copy(scale = newScale)
        }
    }

    private fun resetView() {
        _state.update { it.copy(scale = 1f, offsetX = 0f, offsetY = 0f) }
    }

    private fun setViewMode(mode: ViewMode) {
        _state.update { it.copy(viewMode = mode) }
    }

    private fun setActiveTab(tab: InfoTab) {
        _state.update { it.copy(activeTab = tab) }
    }

    private fun dragBy(dx: Float, dy: Float) {
        _state.update {
            it.copy(offsetX = it.offsetX + dx, offsetY = it.offsetY + dy)
        }
    }

    private fun loadExample(index: Int) {
        val text = when (index) {
            1 -> "1) 8c (7p)\n2) 3p 1a 3p 1c (8p)\n3) 8p 1c (8p)\n4) 8p 1c (8p)"
            2 -> "1) [1a] 6v (12p)\n2) [1p 1a] 6v (18p)\n3) [2p 1a] 6v (24p)\n4) [2p 1d] 6v (18p)\n5) [1p 1d] 6v (12p)"
            else -> ""
        }
        if (text.isNotEmpty()) {
            parseSyntax(text)
        }
    }

    private fun setColorMode(mode: com.example.amigurumimaker.domain.model.ColorMode) {
        _state.update {
            val mesh = it.roundAnalyses.let { analyses ->
                RevolutionMeshComputer.computeMesh(analyses, mode)
            }
            it.copy(
                colorMode = mode,
                revolutionMesh = mesh
            )
        }
    }

    private fun setWireframe(enabled: Boolean) {
        _state.update { it.copy(wireframeEnabled = enabled) }
    }

    private fun loadPreset(index: Int) {
        val presets = PresetPatterns.presets
        if (index in presets.indices) {
            parseSyntax(presets[index].pattern)
        }
    }

    private fun reset() {
        _state.value = WallModelerState()
    }
}
