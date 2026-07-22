package com.example.amigurumimaker.presentation.wallmodeler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WallModelerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: WallModelerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WallModelerViewModel()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parseSyntax valid input updates state with parsed rows and mesh cells`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("1) 8c (7p)"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.parsedRows.size)
        assertTrue(state.meshCells.isNotEmpty())
        assertEquals(0, state.totalStitches)
        assertNull(state.error)
    }

    @Test
    fun `parseSyntax with increases counts correctly`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("1) [1a] 6v (12p)"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.parsedRows.size)
        assertEquals(12, state.totalStitches)
        assertEquals(6, state.totalIncreases)
        assertNull(state.error)
    }

    @Test
    fun `parseSyntax with no tokens produces row but no mesh cells`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("invalid"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.parsedRows.size)
        assertTrue(state.parsedRows.first().tokens.isEmpty())
        assertTrue(state.meshCells.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `reset returns state to initial values`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("1) 8c (7p)"))
        advanceUntilIdle()

        viewModel.process(WallModelerIntent.Reset)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("", state.syntaxText)
        assertTrue(state.parsedRows.isEmpty())
        assertTrue(state.meshCells.isEmpty())
        assertNull(state.error)
        assertEquals(0, state.totalStitches)
    }

    @Test
    fun `zoomBy changes scale within bounds`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ZoomBy(2f))
        assertEquals(2f, viewModel.state.value.scale)

        viewModel.process(WallModelerIntent.ZoomBy(3f))
        assertEquals(4f, viewModel.state.value.scale)

        viewModel.process(WallModelerIntent.ZoomBy(0.1f))
        assertEquals(0.4f, viewModel.state.value.scale)
    }

    @Test
    fun `dragBy updates offset`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.DragBy(50f, 30f))
        assertEquals(50f, viewModel.state.value.offsetX)
        assertEquals(30f, viewModel.state.value.offsetY)
    }

    @Test
    fun `resetView restores scale and offset`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ZoomBy(2f))
        viewModel.process(WallModelerIntent.DragBy(50f, 30f))
        viewModel.process(WallModelerIntent.ResetView)

        assertEquals(1f, viewModel.state.value.scale)
        assertEquals(0f, viewModel.state.value.offsetX)
        assertEquals(0f, viewModel.state.value.offsetY)
    }
}
