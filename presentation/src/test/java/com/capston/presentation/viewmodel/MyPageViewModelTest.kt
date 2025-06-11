package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.mypage.CompletedPlanDto
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.response.mypage.SubjectAchievementDto
import com.capston.domain.response.mypage.SubjectTimeDto
import com.capston.domain.response.mypage.WeeklyTimeDto
import com.capston.domain.usecase.mypage.GetDistinctMyPageUseCase
import com.capston.domain.usecase.mypage.GetMonthlyStatisticsUseCase
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
class MyPageViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getDistinctMyPageUseCase: GetDistinctMyPageUseCase = mockk()
    private val getMonthlyStatisticsUseCase: GetMonthlyStatisticsUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: MyPageViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = MyPageViewModel(
            getDistinctMyPageUseCase,
            getMonthlyStatisticsUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDistinctMyPage should update state with user data`() = runTest {
        // Given
        val completedPlans = listOf(
            CompletedPlanDto(
                planId = 1,
                lectureTitle = "수학 기초",
                teacher = "김수학",
                platform = Platform.MEGA
            ),
            CompletedPlanDto(
                planId = 2,
                lectureTitle = "영어 완성",
                teacher = "이영어",
                platform = Platform.ETOOS
            )
        )

        val subjectAchievements = listOf(
            SubjectAchievementDto(
                subject = Subject.MATH,
                startDate = "2024-01-01",
                endDate = "2024-03-31",
                totalLessons = 50,
                completedLessons = 45
            ),
            SubjectAchievementDto(
                subject = Subject.ENG,
                startDate = "2024-02-01",
                endDate = "2024-04-30",
                totalLessons = 40,
                completedLessons = 30
            )
        )

        val expectedResponse = GetDistinctMyPageResponse(
            userName = "테스트 사용자",
            todayTotalLessonCount = 5,
            todayCompletedLessonCount = 3,
            completedLectureCount = 2,
            studyStreak = 7,
            inProgressLectureCount = 3,
            completedPlanList = completedPlans,
            subjectAchievementList = subjectAchievements
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(expectedResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getDistinctMyPage.value).isEqualTo(expectedResponse)
        assertThat(viewModel.getDistinctMyPage.value.userName).isEqualTo("테스트 사용자")
        assertThat(viewModel.getDistinctMyPage.value.todayTotalLessonCount).isEqualTo(5)
        assertThat(viewModel.getDistinctMyPage.value.todayCompletedLessonCount).isEqualTo(3)
        assertThat(viewModel.getDistinctMyPage.value.completedLectureCount).isEqualTo(2)
        assertThat(viewModel.getDistinctMyPage.value.studyStreak).isEqualTo(7)
        assertThat(viewModel.getDistinctMyPage.value.completedPlanList).hasSize(2)
        assertThat(viewModel.getDistinctMyPage.value.subjectAchievementList).hasSize(2)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getDistinctMyPageUseCase() }
    }

    @Test
    fun `getDistinctMyPage should handle new user with no data`() = runTest {
        // Given
        val newUserResponse = GetDistinctMyPageResponse(
            userName = "신규 사용자",
            todayTotalLessonCount = 0,
            todayCompletedLessonCount = 0,
            completedLectureCount = 0,
            studyStreak = 0,
            inProgressLectureCount = 0,
            completedPlanList = emptyList(),
            subjectAchievementList = emptyList()
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(newUserResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getDistinctMyPage.value.userName).isEqualTo("신규 사용자")
        assertThat(viewModel.getDistinctMyPage.value.todayTotalLessonCount).isEqualTo(0)
        assertThat(viewModel.getDistinctMyPage.value.completedPlanList).isEmpty()
        assertThat(viewModel.getDistinctMyPage.value.subjectAchievementList).isEmpty()
    }

    @Test
    fun `getDistinctMyPage should handle API error`() = runTest {
        // Given
        // 빈 flow를 반환하여 collect가 실행되지 않도록 함
        coEvery { getDistinctMyPageUseCase() } returns flowOf()

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        // collect가 실행되지 않아 기본값이 유지됨
        assertThat(viewModel.getDistinctMyPage.value).isEqualTo(GetDistinctMyPageResponse())

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getDistinctMyPageUseCase() }
    }

    @Test
    fun `getMonthlyStatistics should update statistics with monthly data`() = runTest {
        // Given
        val date = "2024-03"
        val subjectTimes = listOf(
            SubjectTimeDto(
                subject = Subject.MATH,
                totalMinutes = 1200,
                percentage = 40.0
            ),
            SubjectTimeDto(
                subject = Subject.ENG,
                totalMinutes = 900,
                percentage = 30.0
            ),
            SubjectTimeDto(
                subject = Subject.KOR,
                totalMinutes = 600,
                percentage = 20.0
            ),
            SubjectTimeDto(
                subject = Subject.SCI,
                totalMinutes = 300,
                percentage = 10.0
            )
        )

        val weeklyTimes = listOf(
            WeeklyTimeDto(weekNumber = 1, totalMinutes = 720),
            WeeklyTimeDto(weekNumber = 2, totalMinutes = 840),
            WeeklyTimeDto(weekNumber = 3, totalMinutes = 960),
            WeeklyTimeDto(weekNumber = 4, totalMinutes = 480)
        )

        val expectedResponse = GetMyPageStatisticsResponse(
            date = date,
            totalStudyMinutes = 3000,
            subjectTimes = subjectTimes,
            weeklyTimes = weeklyTimes
        )

        coEvery { getMonthlyStatisticsUseCase(date) } returns flowOf(expectedResponse)

        // When
        viewModel.getMonthlyStatistics(date)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getMyPageStatistics.value).isEqualTo(expectedResponse)
        assertThat(viewModel.getMyPageStatistics.value.date).isEqualTo(date)
        assertThat(viewModel.getMyPageStatistics.value.totalStudyMinutes).isEqualTo(3000)
        assertThat(viewModel.getMyPageStatistics.value.subjectTimes).hasSize(4)
        assertThat(viewModel.getMyPageStatistics.value.weeklyTimes).hasSize(4)

        // 과목별 시간 확인
        val mathTime = viewModel.getMyPageStatistics.value.subjectTimes[0]
        assertThat(mathTime.subject).isEqualTo(Subject.MATH)
        assertThat(mathTime.totalMinutes).isEqualTo(1200)
        assertThat(mathTime.percentage).isEqualTo(40.0)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getMonthlyStatisticsUseCase(date) }
    }

    @Test
    fun `getMonthlyStatistics should handle month with no study data`() = runTest {
        // Given
        val date = "2024-01"
        val emptyResponse = GetMyPageStatisticsResponse(
            date = date,
            totalStudyMinutes = 0,
            subjectTimes = emptyList(),
            weeklyTimes = emptyList()
        )

        coEvery { getMonthlyStatisticsUseCase(date) } returns flowOf(emptyResponse)

        // When
        viewModel.getMonthlyStatistics(date)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getMyPageStatistics.value.totalStudyMinutes).isEqualTo(0)
        assertThat(viewModel.getMyPageStatistics.value.subjectTimes).isEmpty()
        assertThat(viewModel.getMyPageStatistics.value.weeklyTimes).isEmpty()
    }

    @Test
    fun `getMonthlyStatistics should handle statistics API error`() = runTest {
        // Given
        val date = "2024-03"
        // 빈 flow를 반환하여 collect가 실행되지 않도록 함
        coEvery { getMonthlyStatisticsUseCase(date) } returns flowOf()

        // When
        viewModel.getMonthlyStatistics(date)
        advanceUntilIdle()

        // Then
        // collect가 실행되지 않아 기본값이 유지됨
        assertThat(viewModel.getMyPageStatistics.value).isEqualTo(GetMyPageStatisticsResponse())

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getMonthlyStatisticsUseCase(date) }
    }

    @Test
    fun `getDistinctMyPage should handle high study streak`() = runTest {
        // Given
        val highStreakResponse = GetDistinctMyPageResponse(
            userName = "열심쌤",
            studyStreak = 365,
            completedLectureCount = 50,
            todayTotalLessonCount = 10,
            todayCompletedLessonCount = 10
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(highStreakResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getDistinctMyPage.value.studyStreak).isEqualTo(365)
        assertThat(viewModel.getDistinctMyPage.value.completedLectureCount).isEqualTo(50)
    }

    @Test
    fun `getDistinctMyPage should handle multiple subject achievements`() = runTest {
        // Given
        val multipleSubjectAchievements = listOf(
            SubjectAchievementDto(
                subject = Subject.MATH,
                startDate = "2024-01-01",
                endDate = "2024-02-29",
                totalLessons = 30,
                completedLessons = 30
            ),
            SubjectAchievementDto(
                subject = Subject.ENG,
                startDate = "2024-02-01",
                endDate = "2024-03-31",
                totalLessons = 25,
                completedLessons = 20
            ),
            SubjectAchievementDto(
                subject = Subject.KOR,
                startDate = "2024-03-01",
                endDate = "2024-04-30",
                totalLessons = 35,
                completedLessons = 10
            )
        )

        val multipleSubjectResponse = GetDistinctMyPageResponse(
            userName = "다과목학습자",
            subjectAchievementList = multipleSubjectAchievements
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(multipleSubjectResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        val achievements = viewModel.getDistinctMyPage.value.subjectAchievementList
        assertThat(achievements).hasSize(3)

        // 완료된 과목
        val completedSubject = achievements.find { it.subject == Subject.MATH }
        assertThat(completedSubject?.completedLessons).isEqualTo(completedSubject?.totalLessons)

        // 진행 중인 과목
        val inProgressSubject = achievements.find { it.subject == Subject.ENG }
        assertThat(inProgressSubject?.completedLessons).isLessThan(inProgressSubject?.totalLessons ?: 0)

        // 시작한 과목
        val startedSubject = achievements.find { it.subject == Subject.KOR }
        assertThat(startedSubject?.completedLessons).isGreaterThan(0)
    }

    @Test
    fun `getMonthlyStatistics should handle different date formats`() = runTest {
        // Given
        val testDates = listOf("2024-01", "2024-12", "2023-06")

        testDates.forEach { date ->
            val response = GetMyPageStatisticsResponse(
                date = date,
                totalStudyMinutes = 1500,
                subjectTimes = listOf(
                    SubjectTimeDto(subject = Subject.MATH, totalMinutes = 900, percentage = 60.0),
                    SubjectTimeDto(subject = Subject.ENG, totalMinutes = 600, percentage = 40.0)
                ),
                weeklyTimes = listOf(
                    WeeklyTimeDto(weekNumber = 1, totalMinutes = 400),
                    WeeklyTimeDto(weekNumber = 2, totalMinutes = 300),
                    WeeklyTimeDto(weekNumber = 3, totalMinutes = 400),
                    WeeklyTimeDto(weekNumber = 4, totalMinutes = 400)
                )
            )

            coEvery { getMonthlyStatisticsUseCase(date) } returns flowOf(response)

            // When
            viewModel.getMonthlyStatistics(date)
            advanceUntilIdle()

            // Then
            assertThat(viewModel.getMyPageStatistics.value.date).isEqualTo(date)
            assertThat(viewModel.getMyPageStatistics.value.totalStudyMinutes).isEqualTo(1500)
        }
    }

    @Test
    fun `getDistinctMyPage should handle various platform combinations`() = runTest {
        // Given
        val multiPlatformPlans = listOf(
            CompletedPlanDto(
                planId = 1,
                lectureTitle = "메가스터디 수학",
                teacher = "메가쌤",
                platform = Platform.MEGA
            ),
            CompletedPlanDto(
                planId = 2,
                lectureTitle = "이투스 영어",
                teacher = "이투쌤",
                platform = Platform.ETOOS
            ),
            CompletedPlanDto(
                planId = 3,
                lectureTitle = "대성 국어",
                teacher = "대성쌤",
                platform = Platform.DAESANG
            ),
            CompletedPlanDto(
                planId = 4,
                lectureTitle = "EBSI 과학",
                teacher = "EBSI쌤",
                platform = Platform.EBSI
            )
        )

        val multiPlatformResponse = GetDistinctMyPageResponse(
            userName = "다플랫폼학습자",
            completedPlanList = multiPlatformPlans
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(multiPlatformResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        val plans = viewModel.getDistinctMyPage.value.completedPlanList
        assertThat(plans).hasSize(4)

        val platforms = plans.map { it.platform }
        assertThat(platforms).containsExactly(
            Platform.MEGA, Platform.ETOOS, Platform.DAESANG, Platform.EBSI
        )
    }

    @Test
    fun `getMonthlyStatistics should validate percentage calculations`() = runTest {
        // Given
        val date = "2024-03"
        val subjectTimes = listOf(
            SubjectTimeDto(subject = Subject.MATH, totalMinutes = 600, percentage = 50.0),
            SubjectTimeDto(subject = Subject.ENG, totalMinutes = 300, percentage = 25.0),
            SubjectTimeDto(subject = Subject.KOR, totalMinutes = 180, percentage = 15.0),
            SubjectTimeDto(subject = Subject.SCI, totalMinutes = 120, percentage = 10.0)
        )

        val response = GetMyPageStatisticsResponse(
            date = date,
            totalStudyMinutes = 1200,
            subjectTimes = subjectTimes,
            weeklyTimes = emptyList()
        )

        coEvery { getMonthlyStatisticsUseCase(date) } returns flowOf(response)

        // When
        viewModel.getMonthlyStatistics(date)
        advanceUntilIdle()

        // Then
        val statistics = viewModel.getMyPageStatistics.value
        val totalPercentage = statistics.subjectTimes.sumOf { it.percentage }

        assertThat(totalPercentage).isEqualTo(100.0)

        // 각 과목별 비율 검증
        val mathPercentage = statistics.subjectTimes.find { it.subject == Subject.MATH }?.percentage
        assertThat(mathPercentage).isEqualTo(50.0)
    }

    @Test
    fun `getDistinctMyPage should handle long user names`() = runTest {
        // Given
        val longUserName = "매우 긴 사용자 이름입니다".repeat(5)
        val longNameResponse = GetDistinctMyPageResponse(
            userName = longUserName,
            todayTotalLessonCount = 1,
            todayCompletedLessonCount = 1
        )

        coEvery { getDistinctMyPageUseCase() } returns flowOf(longNameResponse)

        // When
        viewModel.getDistinctMyPage()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getDistinctMyPage.value.userName).isEqualTo(longUserName)
        assertThat(viewModel.getDistinctMyPage.value.userName.length).isGreaterThan(50)
    }
}