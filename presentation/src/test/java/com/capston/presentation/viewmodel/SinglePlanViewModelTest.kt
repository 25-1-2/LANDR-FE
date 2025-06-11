package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailDailySchedule
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.PostPlanRescheduleUseCase
import com.capston.domain.usecase.study_group.PostNewStudyGroupUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
class SinglePlanViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getPlanDetailUseCase: GetPlanDetailUseCase = mockk()
    private val postPlanRescheduleUseCase: PostPlanRescheduleUseCase = mockk()
    private val deleteOnePlanUseCase: DeleteOnePlanUseCase = mockk()
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase = mockk()
    private val postNewStudyGroupUseCase: PostNewStudyGroupUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: SinglePlanViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = SinglePlanViewModel(
            getPlanDetailUseCase,
            postPlanRescheduleUseCase,
            deleteOnePlanUseCase,
            patchLessonSchedulesCheckToggleUseCase,
            postNewStudyGroupUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPlanDetail should return plan details`() = runTest {
        // Given
        val planId = 123
        val lessonSchedules = listOf(
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

        val dailySchedules = listOf(
            PlanDetailDailySchedule(
                date = "2024-03-15",
                dayOfWeek = "FRI",
                lessonSchedules = lessonSchedules
            )
        )

        val expectedResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "수학 기초 완성",
            teacher = "김수학",
            platform = "메가스터디",
            dailySchedules = dailySchedules
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.planDetailResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.planId).isEqualTo(planId)
        assertThat(result.lectureTitle).isEqualTo("수학 기초 완성")
        assertThat(result.teacher).isEqualTo("김수학")
        assertThat(result.platform).isEqualTo("메가스터디")
        assertThat(result.dailySchedules).hasSize(1)
        assertThat(result.dailySchedules.first().lessonSchedules).hasSize(2)

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `postPlanReschedule should reschedule plan successfully`() = runTest {
        // Given
        val planId = 456
        val expectedResponse = MessageResponse(message = "계획이 성공적으로 재스케줄링되었습니다.")

        coEvery { postPlanRescheduleUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.postPlanReschedule(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.postPlanRescheduleResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.message).isEqualTo("계획이 성공적으로 재스케줄링되었습니다.")

        coVerify(exactly = 1) { postPlanRescheduleUseCase(planId) }
    }

    @Test
    fun `deleteOnePlan should delete plan and trigger callback`() = runTest {
        // Given
        val planId = 789
        val expectedResponse = MessageResponse(message = "계획이 성공적으로 삭제되었습니다.")
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { deleteOnePlanUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.deleteOnePlan(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.deleteOnePlanResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.message).isEqualTo("계획이 성공적으로 삭제되었습니다.")
        assertThat(callbackInvoked).isTrue()

        coVerify(exactly = 1) { deleteOnePlanUseCase(planId) }
    }

    @Test
    fun `patchLessonSchedulesCheckToggle should toggle lesson check and refresh plan`() = runTest {
        // Given
        val lessonScheduleId = 111
        val planId = 222
        val checkResponse = CheckResponse(lessonScheduleId = lessonScheduleId, checked = true)
        val planDetailResponse = PlanDetailResponse(planId = planId)
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }

        // 먼저 planId를 설정하기 위해 getPlanDetail 호출
        coEvery { getPlanDetailUseCase(planId) } returns flowOf(planDetailResponse)
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        coEvery { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) } returns flowOf(checkResponse)

        // When
        viewModel.patchLessonSchedulesCheckToggle(lessonScheduleId)
        advanceUntilIdle()

        // Then
        val result = viewModel.patchLessonSchedulesCheckToggle.value
        assertThat(result).isEqualTo(checkResponse)
        assertThat(result.lessonScheduleId).isEqualTo(lessonScheduleId)
        assertThat(result.checked).isTrue()
        assertThat(callbackInvoked).isTrue()

        coVerify(exactly = 1) { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) }
        coVerify(exactly = 2) { getPlanDetailUseCase(planId) } // 초기 + 새로고침
    }

    @Test
    fun `postNewStudyGroup should create study group successfully`() = runTest {
        // Given
        val planId = 333
        val expectedResponse = NewStudyGroupResponse(
            studyGroupId = 555,
            inviteCode = "ABC123",
            name = "수학 스터디 그룹"
        )

        coEvery { postNewStudyGroupUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.postNewStudyGroup(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.postNewStudyGroupResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.studyGroupId).isEqualTo(555)
        assertThat(result.inviteCode).isEqualTo("ABC123")
        assertThat(result.name).isEqualTo("수학 스터디 그룹")

        coVerify(exactly = 1) { postNewStudyGroupUseCase(planId) }
    }

    @Test
    fun `getPlanDetail with error should handle gracefully`() = runTest {
        // Given
        val planId = 999
        coEvery { getPlanDetailUseCase(planId) } throws RuntimeException("Network error")

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.planDetailResponse.value
        assertThat(result).isEqualTo(PlanDetailResponse())

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `postPlanReschedule with error should handle gracefully`() = runTest {
        // Given
        val planId = 888
        coEvery { postPlanRescheduleUseCase(planId) } throws RuntimeException("Reschedule failed")

        // When
        viewModel.postPlanReschedule(planId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.postPlanRescheduleResponse.value
        assertThat(result).isEqualTo(MessageResponse())

        coVerify(exactly = 1) { postPlanRescheduleUseCase(planId) }
    }

    @Test
    fun `deleteOnePlan with error should handle gracefully`() = runTest {
        // Given
        val planId = 777
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { deleteOnePlanUseCase(planId) } throws RuntimeException("Delete failed")

        // When
        viewModel.deleteOnePlan(planId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.deleteOnePlanResponse.value
        assertThat(result).isEqualTo(MessageResponse())
        // 에러가 발생해도 콜백은 호출되지 않아야 함
        assertThat(callbackInvoked).isFalse()

        coVerify(exactly = 1) { deleteOnePlanUseCase(planId) }
    }

    @Test
    fun `patchLessonSchedulesCheckToggle with error should handle gracefully`() = runTest {
        // Given
        val lessonScheduleId = 666
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) } throws RuntimeException("Toggle failed")

        // When
        viewModel.patchLessonSchedulesCheckToggle(lessonScheduleId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.patchLessonSchedulesCheckToggle.value
        assertThat(result).isEqualTo(CheckResponse())
        // 에러가 발생해도 콜백은 호출되지 않아야 함
        assertThat(callbackInvoked).isFalse()

        coVerify(exactly = 1) { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) }
    }

    @Test
    fun `postNewStudyGroup with error should handle gracefully`() = runTest {
        // Given
        val planId = 444
        coEvery { postNewStudyGroupUseCase(planId) } throws RuntimeException("Study group creation failed")

        // When
        viewModel.postNewStudyGroup(planId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.postNewStudyGroupResponse.value
        assertThat(result).isEqualTo(NewStudyGroupResponse())

        coVerify(exactly = 1) { postNewStudyGroupUseCase(planId) }
    }

    @Test
    fun `getPlanDetail with empty daily schedules should handle correctly`() = runTest {
        // Given
        val planId = 111
        val expectedResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "빈 계획",
            teacher = "선생님",
            platform = "플랫폼",
            dailySchedules = emptyList()
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.planDetailResponse.value
        assertThat(result.dailySchedules).isEmpty()
        assertThat(result.planId).isEqualTo(planId)

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `patchLessonSchedulesCheckToggle without existing plan should not refresh`() = runTest {
        // Given
        val lessonScheduleId = 123
        val checkResponse = CheckResponse(lessonScheduleId = lessonScheduleId, checked = true)

        // planId가 0인 상태 (기본값)에서 테스트
        coEvery { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) } returns flowOf(checkResponse)

        // When
        viewModel.patchLessonSchedulesCheckToggle(lessonScheduleId)
        advanceUntilIdle()

        // Then
        val result = viewModel.patchLessonSchedulesCheckToggle.value
        assertThat(result).isEqualTo(checkResponse)

        coVerify(exactly = 1) { patchLessonSchedulesCheckToggleUseCase(lessonScheduleId) }
        // getPlanDetailUseCase는 호출되지 않아야 함 (planId가 0이므로)
        coVerify(exactly = 0) { getPlanDetailUseCase(any()) }
    }

    @Test
    fun `onDataChanged callback should be settable and callable`() = runTest {
        // Given
        var callbackCount = 0
        viewModel.onDataChanged = { callbackCount++ }

        // When
        viewModel.onDataChanged?.invoke()
        viewModel.onDataChanged?.invoke()

        // Then
        assertThat(callbackCount).isEqualTo(2)
    }

    @Test
    fun `getPlanDetail with multiple daily schedules should handle correctly`() = runTest {
        // Given
        val planId = 555
        val dailySchedules = (1..7).map { day ->
            PlanDetailDailySchedule(
                date = "2024-03-${day.toString().padStart(2, '0')}",
                dayOfWeek = when (day % 7) {
                    1 -> "MON"
                    2 -> "TUE"
                    3 -> "WED"
                    4 -> "THU"
                    5 -> "FRI"
                    6 -> "SAT"
                    0 -> "SUN"
                    else -> "MON"
                },
                lessonSchedules = listOf(
                    PlanDetailLessonSchedule(
                        id = day,
                        lessonTitle = "${day}강. 레슨 $day",
                        lectureName = "주간 계획",
                        adjustedDuration = 60,
                        displayOrder = day,
                        completed = day % 2 == 0
                    )
                )
            )
        }

        val expectedResponse = PlanDetailResponse(
            planId = planId,
            lectureTitle = "일주일 계획",
            teacher = "주간선생",
            platform = "주간플랫폼",
            dailySchedules = dailySchedules
        )

        coEvery { getPlanDetailUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.getPlanDetail(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.planDetailResponse.value
        assertThat(result.dailySchedules).hasSize(7)
        assertThat(result.dailySchedules.first().date).isEqualTo("2024-03-01")
        assertThat(result.dailySchedules.last().date).isEqualTo("2024-03-07")

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }
}