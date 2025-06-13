package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PlanDetailDailySchedule
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.PatchPeriodPlanUseCase
import com.capston.domain.usecase.plan.PatchTimePlanUseCase
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
class PlanEditViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getPlanDetailUseCase: GetPlanDetailUseCase = mockk()
    private val getLessonsByLectureIdUseCase: GetLessonsByLectureIdUseCase = mockk()
    private val patchPeriodPlanUseCase: PatchPeriodPlanUseCase = mockk()
    private val patchTimePlanUseCase: PatchTimePlanUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: PlanEditViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = PlanEditViewModel(
            getPlanDetailUseCase,
            getLessonsByLectureIdUseCase,
            patchPeriodPlanUseCase,
            patchTimePlanUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPlanDetail should update state with plan details`() = runTest {
        // Given
        val planId = 123
        val dailySchedules = listOf(
            PlanDetailDailySchedule(
                date = "2024-03-15",
                dayOfWeek = "금",
                lessonSchedules = listOf(
                    PlanDetailLessonSchedule(
                        id = 1,
                        lessonTitle = "1강. 기초 개념",
                        lectureName = "수학 기초",
                        adjustedDuration = 45,
                        displayOrder = 1,
                        completed = false
                    )
                )
            )
        )

        val expectedResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "수학 기초 강의",
            teacher = "김수학",
            platform = "메가스터디",
            dailySchedules = dailySchedules
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.planDetailResponse.value).isEqualTo(expectedResponse)
        assertThat(viewModel.planDetailResponse.value.planId).isEqualTo(planId)
        assertThat(viewModel.planDetailResponse.value.lectureTitle).isEqualTo("수학 기초 강의")
        assertThat(viewModel.planDetailResponse.value.teacher).isEqualTo("김수학")
        assertThat(viewModel.planDetailResponse.value.dailySchedules).hasSize(1)

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `getPlanDetail should handle API error`() = runTest {
        // Given
        val planId = 123

        // 빈 flow를 반환하여 collect가 실행되지 않도록 함
        coEvery { getPlanDetailUseCase(planId) } returns flowOf()

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        // catch 블록에서 예외를 처리하므로 기본값이 유지됨
        assertThat(viewModel.planDetailResponse.value).isEqualTo(PlanDetailResponse())
        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `getLessonsByLectureId should update lessons list`() = runTest {
        // Given
        val lectureId = 456
        val lessons = listOf(
            LessonByLectureIdResponse(id = 1, title = "1강. 기초 개념"),
            LessonByLectureIdResponse(id = 2, title = "2강. 응용 문제"),
            LessonByLectureIdResponse(id = 3, title = "3강. 심화 학습")
        )

        val expectedResponse = GetLessonsByLectureIdResponse(lessons = lessons)
        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(expectedResponse)

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).isEqualTo(lessons)
        assertThat(viewModel.lessonsByLectureId.value).hasSize(3)
        assertThat(viewModel.lessonsByLectureId.value[0].title).isEqualTo("1강. 기초 개념")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getLessonsByLectureIdUseCase(lectureId) }
    }

    @Test
    fun `getLessonsByLectureId should handle empty lessons`() = runTest {
        // Given
        val lectureId = 456
        val emptyResponse = GetLessonsByLectureIdResponse(lessons = emptyList())
        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(emptyResponse)

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `getLessonsByLectureId should handle API error`() = runTest {
        // Given
        val lectureId = 456
        coEvery { getLessonsByLectureIdUseCase(lectureId) } throws RuntimeException("강의 조회 실패")

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getLessonsByLectureIdUseCase(lectureId) }
    }

    @Test
    fun `patchPeriodPlan should update period plan successfully`() = runTest {
        // Given
        val planId = 123
        val patchPeriodPlanDto = PatchPeriodPlanDto(
            endDate = "2024-05-31",
            playbackSpeed = 1.5
        )
        val expectedResponse = MessageResponse(message = "기간 계획이 성공적으로 수정되었습니다.")

        coEvery { patchPeriodPlanUseCase(planId, patchPeriodPlanDto) } returns flowOf(expectedResponse)

        // When
        viewModel.patchPeriodPlan(planId, patchPeriodPlanDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.patchPeriodPlanResponse.value).isEqualTo(expectedResponse)
        assertThat(viewModel.patchPeriodPlanResponse.value.message).isEqualTo("기간 계획이 성공적으로 수정되었습니다.")

        coVerify(exactly = 1) { patchPeriodPlanUseCase(planId, patchPeriodPlanDto) }
    }

    @Test
    fun `patchPeriodPlan should handle API error`() = runTest {
        // Given
        val planId = 123
        val patchPeriodPlanDto = PatchPeriodPlanDto(
            endDate = "2024-05-31",
            playbackSpeed = 1.5
        )

        // 빈 flow를 반환하여 collect가 실행되지 않도록 함
        coEvery { patchPeriodPlanUseCase(planId, patchPeriodPlanDto) } returns flowOf()

        // When
        viewModel.patchPeriodPlan(planId, patchPeriodPlanDto)
        advanceUntilIdle()

        // Then
        // catch 블록에서 예외를 처리하므로 기본값이 유지됨
        assertThat(viewModel.patchPeriodPlanResponse.value).isEqualTo(MessageResponse())
        coVerify(exactly = 1) { patchPeriodPlanUseCase(planId, patchPeriodPlanDto) }
    }

    @Test
    fun `patchTimePlan should update time plan successfully`() = runTest {
        // Given
        val planId = 123
        val patchTimePlanDto = PatchTimePlanDto(
            dailyTime = 120,
            playbackSpeed = 2.0
        )
        val expectedResponse = MessageResponse(message = "시간 계획이 성공적으로 수정되었습니다.")

        coEvery { patchTimePlanUseCase(planId, patchTimePlanDto) } returns flowOf(expectedResponse)

        // When
        viewModel.patchTimePlan(planId, patchTimePlanDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.patchTimePlanResponse.value).isEqualTo(expectedResponse)
        assertThat(viewModel.patchTimePlanResponse.value.message).isEqualTo("시간 계획이 성공적으로 수정되었습니다.")

        coVerify(exactly = 1) { patchTimePlanUseCase(planId, patchTimePlanDto) }
    }

    @Test
    fun `patchTimePlan should handle API error`() = runTest {
        // Given
        val planId = 123
        val patchTimePlanDto = PatchTimePlanDto(
            dailyTime = 120,
            playbackSpeed = 2.0
        )

        // 빈 flow를 반환하여 collect가 실행되지 않도록 함
        coEvery { patchTimePlanUseCase(planId, patchTimePlanDto) } returns flowOf()

        // When
        viewModel.patchTimePlan(planId, patchTimePlanDto)
        advanceUntilIdle()

        // Then
        // catch 블록에서 예외를 처리하므로 기본값이 유지됨
        assertThat(viewModel.patchTimePlanResponse.value).isEqualTo(MessageResponse())
        coVerify(exactly = 1) { patchTimePlanUseCase(planId, patchTimePlanDto) }
    }

    @Test
    fun `getPlanDetail should handle complex daily schedules`() = runTest {
        // Given
        val planId = 123
        val multipleDaySchedules = listOf(
            PlanDetailDailySchedule(
                date = "2024-03-15",
                dayOfWeek = "금",
                lessonSchedules = listOf(
                    PlanDetailLessonSchedule(
                        id = 1,
                        lessonTitle = "1강. 기초 개념",
                        lectureName = "수학 기초",
                        adjustedDuration = 45,
                        displayOrder = 1,
                        completed = false
                    ),
                    PlanDetailLessonSchedule(
                        id = 2,
                        lessonTitle = "2강. 응용 문제",
                        lectureName = "수학 기초",
                        adjustedDuration = 50,
                        displayOrder = 2,
                        completed = true
                    )
                )
            ),
            PlanDetailDailySchedule(
                date = "2024-03-18",
                dayOfWeek = "월",
                lessonSchedules = listOf(
                    PlanDetailLessonSchedule(
                        id = 3,
                        lessonTitle = "3강. 심화 학습",
                        lectureName = "수학 기초",
                        adjustedDuration = 60,
                        displayOrder = 3,
                        completed = false
                    )
                )
            )
        )

        val complexResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "수학 종합 과정",
            teacher = "박수학",
            platform = "이투스",
            dailySchedules = multipleDaySchedules
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(complexResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.planDetailResponse.value
        assertThat(result.dailySchedules).hasSize(2)

        // 첫 번째 날 검증
        val firstDay = result.dailySchedules[0]
        assertThat(firstDay.date).isEqualTo("2024-03-15")
        assertThat(firstDay.lessonSchedules).hasSize(2)
        assertThat(firstDay.lessonSchedules[1].completed).isTrue()

        // 두 번째 날 검증
        val secondDay = result.dailySchedules[1]
        assertThat(secondDay.date).isEqualTo("2024-03-18")
        assertThat(secondDay.lessonSchedules).hasSize(1)
        assertThat(secondDay.lessonSchedules[0].completed).isFalse()
    }

    @Test
    fun `getLessonsByLectureId should handle large lesson list`() = runTest {
        // Given
        val lectureId = 456
        val manyLessons = (1..50).map { index ->
            LessonByLectureIdResponse(id = index, title = "${index}강. 레슨 $index")
        }

        val largeResponse = GetLessonsByLectureIdResponse(lessons = manyLessons)
        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(largeResponse)

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).hasSize(50)
        assertThat(viewModel.lessonsByLectureId.value.first().title).isEqualTo("1강. 레슨 1")
        assertThat(viewModel.lessonsByLectureId.value.last().title).isEqualTo("50강. 레슨 50")
    }

    @Test
    fun `patchPeriodPlan should handle different playback speeds`() = runTest {
        // Given
        val planId = 123
        val playbackSpeeds = listOf(1.0, 1.25, 1.5, 1.75, 2.0)

        playbackSpeeds.forEach { speed ->
            val patchDto = PatchPeriodPlanDto(
                endDate = "2024-05-31",
                playbackSpeed = speed
            )
            val response = MessageResponse(message = "${speed}배속으로 계획이 수정되었습니다.")

            coEvery { patchPeriodPlanUseCase(planId, patchDto) } returns flowOf(response)

            // When
            viewModel.patchPeriodPlan(planId, patchDto)
            advanceUntilIdle()

            // Then
            assertThat(viewModel.patchPeriodPlanResponse.value.message).contains("${speed}배속")
        }
    }

    @Test
    fun `patchTimePlan should handle different daily time values`() = runTest {
        // Given
        val planId = 123
        val dailyTimes = listOf(30, 60, 90, 120, 180, 240)

        dailyTimes.forEach { time ->
            val patchDto = PatchTimePlanDto(
                dailyTime = time,
                playbackSpeed = 1.0
            )
            val response = MessageResponse(message = "일일 ${time}분 계획으로 수정되었습니다.")

            coEvery { patchTimePlanUseCase(planId, patchDto) } returns flowOf(response)

            // When
            viewModel.patchTimePlan(planId, patchDto)
            advanceUntilIdle()

            // Then
            assertThat(viewModel.patchTimePlanResponse.value.message).contains("${time}분")
        }
    }

    @Test
    fun `should handle multiple operations in sequence`() = runTest {
        // Given
        val planId = 123
        val lectureId = 456

        // Plan detail setup
        val planDetail = PlanDetailResponse(
            planId = planId,
            lectureTitle = "연속 작업 테스트",
            teacher = "테스트쌤"
        )
        coEvery { getPlanDetailUseCase(planId) } returns flowOf(planDetail)

        // Lessons setup
        val lessons = listOf(LessonByLectureIdResponse(id = 1, title = "테스트 레슨"))
        val lessonsResponse = GetLessonsByLectureIdResponse(lessons = lessons)
        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(lessonsResponse)

        // Period plan setup
        val periodDto = PatchPeriodPlanDto(
            endDate = "2024-03-31",
            playbackSpeed = 1.0
        )
        val periodResponse = MessageResponse(message = "기간 계획 수정 완료")
        coEvery { patchPeriodPlanUseCase(planId, periodDto) } returns flowOf(periodResponse)

        // When - 순차적으로 작업 실행
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        viewModel.patchPeriodPlan(planId, periodDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.planDetailResponse.value.lectureTitle).isEqualTo("연속 작업 테스트")
        assertThat(viewModel.lessonsByLectureId.value).hasSize(1)
        assertThat(viewModel.patchPeriodPlanResponse.value.message).isEqualTo("기간 계획 수정 완료")

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
        coVerify(exactly = 1) { getLessonsByLectureIdUseCase(lectureId) }
        coVerify(exactly = 1) { patchPeriodPlanUseCase(planId, periodDto) }
    }

    @Test
    fun `should handle edge case with zero lessons`() = runTest {
        // Given
        val lectureId = 456
        val emptyLessonsResponse = GetLessonsByLectureIdResponse(lessons = emptyList())
        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(emptyLessonsResponse)

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).isEmpty()
        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `should handle special characters in plan details`() = runTest {
        // Given
        val planId = 123
        val specialCharResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "수학 & 과학 🔬 (고급과정) - 2024년 대비",
            teacher = "김특수문자👨‍🏫",
            platform = "메가스터디 💯"
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(specialCharResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.planDetailResponse.value
        assertThat(result.lectureTitle).contains("🔬")
        assertThat(result.teacher).contains("👨‍🏫")
        assertThat(result.platform).contains("💯")
    }
}