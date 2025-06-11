package com.capston.presentation.viewmodel

import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.domain.usecase.daily_schedule.GetDailyScheduleUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DailyScheduleViewModelTest {

    private val getDailyScheduleUseCase: GetDailyScheduleUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var dailyScheduleViewModel: DailyScheduleViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dailyScheduleViewModel = DailyScheduleViewModel(
            getDailyScheduleUseCase = getDailyScheduleUseCase,
            loadingStateManager = loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getDailySchedule 성공적으로 일일 스케줄 조회`() = runTest {
        // Given
        val date = "2024-03-15"
        val lessonSchedules = listOf(
            LessonScheduleResponse(
                id = 1,
                lessonTitle = "1강. 기초 개념",
                lectureName = "수학 기초",
                adjustedDuration = 45,
                displayOrder = 1,
                completed = false
            ),
            LessonScheduleResponse(
                id = 2,
                lessonTitle = "2강. 응용 문제",
                lectureName = "수학 기초",
                adjustedDuration = 50,
                displayOrder = 2,
                completed = true
            )
        )

        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 2,
            totalDuration = 95,
            lessonSchedules = lessonSchedules
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.date).isEqualTo(date)
        assertThat(result.dayOfWeek).isEqualTo(DayOfWeek.FRI)
        assertThat(result.totalLessons).isEqualTo(2)
        assertThat(result.totalDuration).isEqualTo(95)
        assertThat(result.lessonSchedules).hasSize(2)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify { getDailyScheduleUseCase(date) }
    }

    @Test
    fun `getDailySchedule 빈 스케줄 조회`() = runTest {
        // Given
        val date = "2024-03-16"
        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.SAT,
            totalLessons = 0,
            totalDuration = 0,
            lessonSchedules = emptyList()
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result.totalLessons).isEqualTo(0)
        assertThat(result.totalDuration).isEqualTo(0)
        assertThat(result.lessonSchedules).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `getDailySchedule 에러 발생 시 처리`() = runTest {
        // Given
        val date = "2024-03-15"
        coEvery { getDailyScheduleUseCase(date) } throws RuntimeException("네트워크 오류")

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // 에러가 발생해도 기본값이 유지되는지 확인
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result).isEqualTo(DailyScheduleResponse())

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify { getDailyScheduleUseCase(date) }
    }

    @Test
    fun `forceRefresh 현재 날짜로 다시 로드`() = runTest {
        // Given
        val currentDate = "2024-03-15"
        val expectedResponse = DailyScheduleResponse(
            date = currentDate,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 1,
            totalDuration = 60
        )

        coEvery { getDailyScheduleUseCase(currentDate) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.forceRefresh(currentDate)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result.date).isEqualTo(currentDate)
        assertThat(result.totalLessons).isEqualTo(1)
        coVerify { getDailyScheduleUseCase(currentDate) }
    }

    @Test
    fun `getDailySchedule 완료된 레슨만 있는 경우`() = runTest {
        // Given
        val date = "2024-03-15"
        val completedLessons = listOf(
            LessonScheduleResponse(
                id = 1,
                lessonTitle = "1강. 완료된 레슨",
                lectureName = "수학 기초",
                adjustedDuration = 45,
                displayOrder = 1,
                completed = true
            ),
            LessonScheduleResponse(
                id = 2,
                lessonTitle = "2강. 완료된 레슨",
                lectureName = "수학 기초",
                adjustedDuration = 50,
                displayOrder = 2,
                completed = true
            )
        )

        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 2,
            totalDuration = 95,
            lessonSchedules = completedLessons
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result.lessonSchedules?.all { it.completed }).isTrue()
    }

    @Test
    fun `getDailySchedule 미완료 레슨만 있는 경우`() = runTest {
        // Given
        val date = "2024-03-15"
        val incompleteLessons = listOf(
            LessonScheduleResponse(
                id = 1,
                lessonTitle = "1강. 미완료 레슨",
                lectureName = "수학 기초",
                adjustedDuration = 45,
                displayOrder = 1,
                completed = false
            ),
            LessonScheduleResponse(
                id = 2,
                lessonTitle = "2강. 미완료 레슨",
                lectureName = "수학 기초",
                adjustedDuration = 50,
                displayOrder = 2,
                completed = false
            )
        )

        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 2,
            totalDuration = 95,
            lessonSchedules = incompleteLessons
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result.lessonSchedules?.all { !it.completed }).isTrue()
    }

    @Test
    fun `getDailySchedule 다양한 요일 테스트`() = runTest {
        // Given
        val testCases = listOf(
            "2024-03-11" to DayOfWeek.MON,
            "2024-03-12" to DayOfWeek.TUE,
            "2024-03-13" to DayOfWeek.WED,
            "2024-03-14" to DayOfWeek.THU,
            "2024-03-15" to DayOfWeek.FRI,
            "2024-03-16" to DayOfWeek.SAT,
            "2024-03-17" to DayOfWeek.SUN
        )

        testCases.forEach { (date, expectedDayOfWeek) ->
            // Given
            val expectedResponse = DailyScheduleResponse(
                date = date,
                dayOfWeek = expectedDayOfWeek,
                totalLessons = 1,
                totalDuration = 60
            )

            coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

            // When
            dailyScheduleViewModel.getDailySchedule(date)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val result = dailyScheduleViewModel.getDailySchedule.value
            assertThat(result.dayOfWeek).isEqualTo(expectedDayOfWeek)
            assertThat(result.date).isEqualTo(date)
        }
    }

    @Test
    fun `getDailySchedule 긴 레슨 제목 처리`() = runTest {
        // Given
        val date = "2024-03-15"
        val longTitleLesson = LessonScheduleResponse(
            id = 1,
            lessonTitle = "1강. 매우 긴 레슨 제목입니다. 이 제목은 화면에 모두 표시되지 않을 수 있으며 말줄임표로 처리될 수 있습니다.",
            lectureName = "매우 긴 강의명입니다. 이것도 화면에 모두 표시되지 않을 수 있습니다.",
            adjustedDuration = 120,
            displayOrder = 1,
            completed = false
        )

        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 1,
            totalDuration = 120,
            lessonSchedules = listOf(longTitleLesson)
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        val lesson = result.lessonSchedules?.first()
        assertThat(lesson?.lessonTitle?.length).isGreaterThan(50)
        assertThat(lesson?.lectureName?.length).isGreaterThan(30)
    }

    @Test
    fun `onDataChanged 콜백 설정 및 호출 테스트`() = runTest {
        // Given
        var callbackInvoked = false
        dailyScheduleViewModel.onDataChanged = { callbackInvoked = true }

        // When - 실제로는 다른 ViewModel에서 이 콜백을 호출할 것임
        dailyScheduleViewModel.onDataChanged?.invoke()

        // Then
        assertThat(callbackInvoked).isTrue()
    }

    @Test
    fun `getDailySchedule 최대 레슨 수 처리`() = runTest {
        // Given
        val date = "2024-03-15"
        val manyLessons = (1..20).map { index ->
            LessonScheduleResponse(
                id = index,
                lessonTitle = "${index}강. 레슨 $index",
                lectureName = "강의 $index",
                adjustedDuration = 30,
                displayOrder = index,
                completed = index % 2 == 0
            )
        }

        val expectedResponse = DailyScheduleResponse(
            date = date,
            dayOfWeek = DayOfWeek.FRI,
            totalLessons = 20,
            totalDuration = 600, // 30분 * 20강
            lessonSchedules = manyLessons
        )

        coEvery { getDailyScheduleUseCase(date) } returns flowOf(expectedResponse)

        // When
        dailyScheduleViewModel.getDailySchedule(date)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = dailyScheduleViewModel.getDailySchedule.value
        assertThat(result.lessonSchedules).hasSize(20)
        assertThat(result.totalLessons).isEqualTo(20)
        assertThat(result.totalDuration).isEqualTo(600)

        // 완료된 레슨과 미완료 레슨이 섞여있는지 확인
        val completedCount = result.lessonSchedules?.count { it.completed } ?: 0
        val incompleteCount = result.lessonSchedules?.count { !it.completed } ?: 0
        assertThat(completedCount).isEqualTo(10)
        assertThat(incompleteCount).isEqualTo(10)
    }
}