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
    fun `parseSyntax valid input updates state with rows and geometries`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("1) 10c"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, state.rows.size)
        assertTrue(state.geometries.isNotEmpty())
        assertEquals(10, state.parsedBlockCount)
        assertNull(state.error)
    }

    @Test
    fun `parseSyntax invalid input sets error state`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("invalid"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNotNull(state.error)
        assertTrue(state.error.isNotEmpty())
        assertTrue(state.geometries.isEmpty())
    }

    @Test
    fun `reset returns state to initial values`() = runTest(testDispatcher) {
        viewModel.process(WallModelerIntent.ParseSyntax("1) 10c"))
        advanceUntilIdle()

        viewModel.process(WallModelerIntent.Reset)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("", state.syntaxText)
        assertTrue(state.rows.isEmpty())
        assertTrue(state.geometries.isEmpty())
        assertNull(state.error)
        assertEquals(0, state.parsedBlockCount)
    }
}
