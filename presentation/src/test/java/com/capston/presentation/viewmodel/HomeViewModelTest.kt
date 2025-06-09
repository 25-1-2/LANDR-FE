package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.home.UserProgressResponse
import com.capston.domain.usecase.home.*
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
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getDistinctHomeUseCase: GetDistinctHomeUseCase = mockk()
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase = mockk()
    private val getDDayUseCase: GetDDayUseCase = mockk()
    private val postDDayUseCase: PostDDayUseCase = mockk()
    private val deleteDDayUseCase: DeleteDDayUseCase = mockk()
    private val patchDDayUseCase: PatchDDayUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = HomeViewModel(
            getDistinctHomeUseCase,
            patchLessonSchedulesCheckToggleUseCase,
            getDDayUseCase,
            postDDayUseCase,
            deleteDDayUseCase,
            patchDDayUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDistinctHome should update state with home data`() = runTest {
        // Given
        val expectedResponse = DistinctHomeIdResponse(
            userProgress = UserProgressResponse(totalCompletedLessons = 5, totalLessons = 10)
        )
        coEvery { getDistinctHomeUseCase() } returns flowOf(expectedResponse)

        // When
        viewModel.getDistinctHome(forceRefresh = true)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getDistinctHome.value).isEqualTo(expectedResponse)
        assertThat(viewModel.getDistinctHome.value.userProgress.totalCompletedLessons).isEqualTo(5)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getDistinctHomeUseCase() }
    }

    @Test
    fun `patchLessonSchedulesCheckToggle should update check state`() = runTest {
        // Given
        val lessonScheduleId = 123
        val expectedResponse = CheckResponse(lessonScheduleId = lessonScheduleId, checked = true)
        val homeResponse = DistinctHomeIdResponse()

        coEvery { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) } returns flowOf(expectedResponse)
        coEvery { getDistinctHomeUseCase() } returns flowOf(homeResponse)

        // When
        viewModel.patchLessonSchedulesCheckToggle(lessonScheduleId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.patchLessonSchedulesCheckToggle.value).isEqualTo(expectedResponse)
        assertThat(viewModel.patchLessonSchedulesCheckToggle.value.checked).isTrue()

        coVerify(exactly = 1) { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) }
        coVerify(exactly = 1) { getDistinctHomeUseCase() }
    }

    @Test
    fun `postDDay should create new D-Day`() = runTest {
        // Given
        val request = UpdateDDayRequest(title = "수능", goalDate = "2024-11-14")
        val expectedResponse = DDayResponse(title = "수능", goalDate = "2024-11-14", ddayId = 1)
        val homeResponse = DistinctHomeIdResponse()

        coEvery { postDDayUseCase(request) } returns flowOf(expectedResponse)
        coEvery { getDistinctHomeUseCase() } returns flowOf(homeResponse)

        // When
        viewModel.postDDay(request)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.dDay.value).isEqualTo(expectedResponse)
        assertThat(viewModel.dDay.value?.title).isEqualTo("수능")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { postDDayUseCase(request) }
    }

    @Test
    fun `deleteDDay should clear D-Day state`() = runTest {
        // Given
        val ddayId = 1
        val homeResponse = DistinctHomeIdResponse()

        coEvery { deleteDDayUseCase(ddayId) } returns Unit
        coEvery { getDistinctHomeUseCase() } returns flowOf(homeResponse)

        // When
        viewModel.deleteDDay(ddayId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.dDay.value).isNull()

        coVerify(exactly = 1) { deleteDDayUseCase(ddayId) }
        coVerify(exactly = 1) { getDistinctHomeUseCase() }
    }
}