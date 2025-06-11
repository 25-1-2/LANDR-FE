package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.base.Result
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.usecase.error.GetExceptionApiUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ErrorViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getExceptionApiUseCase: GetExceptionApiUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: ErrorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = ErrorViewModel(
            getExceptionApiUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getExceptionApi should update error state with API response`() = runTest {
        // Given
        val expectedResult = Result(code = 5000, message = "API 예외가 발생했습니다")
        coEvery { getExceptionApiUseCase() } returns flowOf(expectedResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value).isEqualTo(expectedResult)
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(5000)
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo("API 예외가 발생했습니다")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getExceptionApiUseCase() }
    }

    @Test
    fun `getExceptionApi should handle network error`() = runTest {
        // Given
        val networkErrorResult = Result(code = 1000, message = "네트워크 오류")
        coEvery { getExceptionApiUseCase() } returns flowOf(networkErrorResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(1000)
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo("네트워크 오류")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `getExceptionApi should handle server error`() = runTest {
        // Given
        val serverErrorResult = Result(code = 5001, message = "서버 내부 오류")
        coEvery { getExceptionApiUseCase() } returns flowOf(serverErrorResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(5001)
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo("서버 내부 오류")
    }

    @Test
    fun `getExceptionApi should handle authentication error`() = runTest {
        // Given
        val authErrorResult = Result(code = 4001, message = "인증 토큰이 유효하지 않습니다")
        coEvery { getExceptionApiUseCase() } returns flowOf(authErrorResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(4001)
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo("인증 토큰이 유효하지 않습니다")
    }

    @Test
    fun `getExceptionApi should handle success response`() = runTest {
        // Given
        val successResult = Result(code = 200, message = "성공")
        coEvery { getExceptionApiUseCase() } returns flowOf(successResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(200)
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo("성공")
    }

    @Test
    fun `getExceptionApi should handle empty message`() = runTest {
        // Given
        val emptyMessageResult = Result(code = 5000, message = "")
        coEvery { getExceptionApiUseCase() } returns flowOf(emptyMessageResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(5000)
        assertThat(viewModel.getExceptionApi.value.message).isEmpty()
    }

    @Test
    fun `getExceptionApi should handle exception during API call`() = runTest {
        // Given
        coEvery { getExceptionApiUseCase() } throws RuntimeException("네트워크 연결 실패")

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        // 기본값이 유지되는지 확인
        assertThat(viewModel.getExceptionApi.value).isEqualTo(Result())

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getExceptionApiUseCase() }
    }

    @Test
    fun `getExceptionApi should maintain default state before API call`() = runTest {
        // Given - API 호출 전

        // When - API 호출하지 않음

        // Then
        assertThat(viewModel.getExceptionApi.value).isEqualTo(Result())
        assertThat(viewModel.getExceptionApi.value.code).isEqualTo(0)
        assertThat(viewModel.getExceptionApi.value.message).isEmpty()
    }

    @Test
    fun `getExceptionApi should handle multiple error codes`() = runTest {
        // Given
        val errorCodes = listOf(
            Result(code = 400, message = "잘못된 요청"),
            Result(code = 401, message = "인증 실패"),
            Result(code = 403, message = "접근 금지"),
            Result(code = 404, message = "리소스를 찾을 수 없음"),
            Result(code = 500, message = "서버 오류")
        )

        errorCodes.forEach { expectedResult ->
            // Given
            coEvery { getExceptionApiUseCase() } returns flowOf(expectedResult)

            // When
            viewModel.getExceptionApi()
            advanceUntilIdle()

            // Then
            assertThat(viewModel.getExceptionApi.value.code).isEqualTo(expectedResult.code)
            assertThat(viewModel.getExceptionApi.value.message).isEqualTo(expectedResult.message)
        }
    }

    @Test
    fun `getExceptionApi should handle long error messages`() = runTest {
        // Given
        val longMessage = "매우 긴 오류 메시지입니다. ".repeat(20)
        val longMessageResult = Result(code = 5000, message = longMessage)
        coEvery { getExceptionApiUseCase() } returns flowOf(longMessageResult)

        // When
        viewModel.getExceptionApi()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getExceptionApi.value.message).isEqualTo(longMessage)
        assertThat(viewModel.getExceptionApi.value.message.length).isGreaterThan(100)
    }
}