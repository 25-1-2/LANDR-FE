package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    protected val testDispatcher = StandardTestDispatcher()

    @Before
    open fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Android Log 모킹
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        // every { android.util.Log.w(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.v(any(), any()) } returns 0
    }

    @After
    open fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }
}