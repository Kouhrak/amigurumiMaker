package com.example.amigurumimaker.presentation.wallmodeler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.amigurumimaker.domain.GeometryComputer
import com.example.amigurumimaker.domain.RowExpander
import com.example.amigurumimaker.domain.WallTokenizer
import com.example.amigurumimaker.domain.model.ParseResult
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
        }
    }

    private fun parseSyntax(text: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = WallTokenizer.tokenize(text)) {
                is ParseResult.Success -> {
                    val expanded = result.rows.flatMap { RowExpander.expand(it) }
                    val geometries = GeometryComputer.compute(expanded)
                    val blockCount = geometries.count { it.vertices.isNotEmpty() }
                    _state.update {
                        it.copy(
                            syntaxText = text,
                            rows = expanded,
                            geometries = geometries,
                            isLoading = false,
                            error = null,
                            parsedBlockCount = blockCount
                        )
                    }
                    _effect.emit(WallModelerEffect.CenterCamera)
                }
                is ParseResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                    _effect.emit(WallModelerEffect.ShowError(result.message))
                }
            }
        }
    }

    private fun reset() {
        _state.value = WallModelerState()
    }
}
