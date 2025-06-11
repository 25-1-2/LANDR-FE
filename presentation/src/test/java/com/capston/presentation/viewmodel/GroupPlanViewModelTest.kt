package com.capston.presentation.viewmodel

import com.capston.domain.response.plan.PlanDetailResponse
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PlanDetailDailySchedule
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.domain.response.study_group.StudyGroupMember
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.PostPlanRescheduleUseCase
import com.capston.domain.usecase.study_group.DeleteOneStudyGroupUseCase
import com.capston.domain.usecase.study_group.GetOneStudyGroupUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class GroupPlanViewModelTest: BaseViewModelTest() {
    private val getPlanDetailUseCase: GetPlanDetailUseCase = mockk()
    private val postPlanRescheduleUseCase: PostPlanRescheduleUseCase = mockk()
    private val deleteOnePlanUseCase: DeleteOnePlanUseCase = mockk()
    private val deleteOneStudyGroupUseCase: DeleteOneStudyGroupUseCase = mockk()
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase = mockk()
    private val getOneStudyGroupUseCase: GetOneStudyGroupUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: GroupPlanViewModel

    @Before
    override fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(android.util.Log::class)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = GroupPlanViewModel(
            getPlanDetailUseCase,
            postPlanRescheduleUseCase,
            deleteOnePlanUseCase,
            deleteOneStudyGroupUseCase,
            patchLessonSchedulesCheckToggleUseCase,
            getOneStudyGroupUseCase,
            loadingStateManager
        )
    }

    @After
    override fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `getPlanDetail should return group plan details`() = runTest {
        // Given
        val planId = 123
        val lessonSchedules = listOf(
            PlanDetailLessonSchedule(
                id = 1,
                lessonTitle = "1강. 그룹 스터디 기초",
                lectureName = "그룹 수학",
                adjustedDuration = 45,
                displayOrder = 1,
                completed = false
            ),
            PlanDetailLessonSchedule(
                id = 2,
                lessonTitle = "2강. 그룹 스터디 심화",
                lectureName = "그룹 수학",
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
            lectureTitle = "그룹 수학 기초 완성",
            teacher = "김그룹수학",
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
        assertThat(result.lectureTitle).isEqualTo("그룹 수학 기초 완성")
        assertThat(result.teacher).isEqualTo("김그룹수학")
        assertThat(result.platform).isEqualTo("메가스터디")
        assertThat(result.dailySchedules).hasSize(1)
        assertThat(result.dailySchedules.first().lessonSchedules).hasSize(2)

        coVerify(exactly = 1) { getPlanDetailUseCase(planId) }
    }

    @Test
    fun `getOneStudyGroup should return study group information`() = runTest {
        // Given
        val studyGroupId = 555
        val members = listOf(
            StudyGroupMember(
                userId = 1,
                userName = "김그룹원1",
                planId = 101
            ),
            StudyGroupMember(
                userId = 2,
                userName = "이그룹원2",
                planId = 102
            ),
            StudyGroupMember(
                userId = 3,
                userName = "박그룹원3",
                planId = 103
            )
        )

        val expectedResponse = OneStudyGroupResponse(
            studyGroupId = studyGroupId,
            name = "수학 마스터 그룹",
            inviteCode = "MATH123",
            leaderId = 1,
            leaderName = "김그룹원1",
            lectureName = "수학 기초 완성",
            members = members
        )

        coEvery { getOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.studyGroupId).isEqualTo(studyGroupId)
        assertThat(result.name).isEqualTo("수학 마스터 그룹")
        assertThat(result.inviteCode).isEqualTo("MATH123")
        assertThat(result.leaderId).isEqualTo(1)
        assertThat(result.leaderName).isEqualTo("김그룹원1")
        assertThat(result.lectureName).isEqualTo("수학 기초 완성")
        assertThat(result.members).hasSize(3)

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
    }

    @Test
    fun `postPlanReschedule should reschedule group plan successfully`() = runTest {
        // Given
        val planId = 456
        val expectedResponse = MessageResponse(message = "그룹 계획이 성공적으로 재스케줄링되었습니다.")

        coEvery { postPlanRescheduleUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.postPlanReschedule(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.postPlanRescheduleResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.message).isEqualTo("그룹 계획이 성공적으로 재스케줄링되었습니다.")

        coVerify(exactly = 1) { postPlanRescheduleUseCase(planId) }
    }

    @Test
    fun `deleteOnePlan should delete group plan and trigger callback`() = runTest {
        // Given
        val planId = 789
        val expectedResponse = MessageResponse(message = "그룹 계획이 성공적으로 삭제되었습니다.")
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { deleteOnePlanUseCase(planId) } returns flowOf(expectedResponse)

        // When
        viewModel.deleteOnePlan(planId)
        advanceUntilIdle()

        // Then
        val result = viewModel.deleteOnePlanResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.message).isEqualTo("그룹 계획이 성공적으로 삭제되었습니다.")
        assertThat(callbackInvoked).isTrue()

        coVerify(exactly = 1) { deleteOnePlanUseCase(planId) }
    }

    @Test
    fun `deleteOneStudyGroup should delete study group and trigger callback`() = runTest {
        // Given
        val studyGroupId = 999
        val expectedResponse = MessageResponse(message = "스터디 그룹이 성공적으로 삭제되었습니다.")
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { deleteOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.deleteOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.deleteOneStudyGroupResponse.value
        assertThat(result).isEqualTo(expectedResponse)
        assertThat(result.message).isEqualTo("스터디 그룹이 성공적으로 삭제되었습니다.")
        assertThat(callbackInvoked).isTrue()

        coVerify(exactly = 1) { deleteOneStudyGroupUseCase(studyGroupId) }
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
    fun `getOneStudyGroup with single member should work correctly`() = runTest {
        // Given
        val studyGroupId = 333
        val singleMember = listOf(
            StudyGroupMember(
                userId = 1,
                userName = "혼자스터디",
                planId = 201
            )
        )

        val expectedResponse = OneStudyGroupResponse(
            studyGroupId = studyGroupId,
            name = "1인 스터디 그룹",
            inviteCode = "SOLO123",
            leaderId = 1,
            leaderName = "혼자스터디",
            lectureName = "혼자 공부하는 수학",
            members = singleMember
        )

        coEvery { getOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result.members).hasSize(1)
        assertThat(result.members.first().userName).isEqualTo("혼자스터디")
        assertThat(result.leaderId).isEqualTo(result.members.first().userId)

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
    }

    @Test
    fun `getOneStudyGroup with large group should handle correctly`() = runTest {
        // Given
        val studyGroupId = 777
        val largeGroup = (1..20).map { index ->
            StudyGroupMember(
                userId = index,
                userName = "그룹원$index",
                planId = 300 + index
            )
        }

        val expectedResponse = OneStudyGroupResponse(
            studyGroupId = studyGroupId,
            name = "대형 스터디 그룹",
            inviteCode = "LARGE20",
            leaderId = 1,
            leaderName = "그룹원1",
            lectureName = "대형 그룹 수학",
            members = largeGroup
        )

        coEvery { getOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result.members).hasSize(20)
        assertThat(result.name).isEqualTo("대형 스터디 그룹")

        // 모든 멤버가 고유한 userId를 가지는지 확인
        val userIds = result.members.map { it.userId }
        assertThat(userIds.distinct()).hasSize(20)

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
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
    fun `getOneStudyGroup with error should handle gracefully`() = runTest {
        // Given
        val studyGroupId = 888
        coEvery { getOneStudyGroupUseCase(studyGroupId) } throws RuntimeException("Study group not found")

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result).isEqualTo(OneStudyGroupResponse())

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
    }

    @Test
    fun `deleteOneStudyGroup with error should handle gracefully`() = runTest {
        // Given
        val studyGroupId = 666
        var callbackInvoked = false

        viewModel.onDataChanged = { callbackInvoked = true }
        coEvery { deleteOneStudyGroupUseCase(studyGroupId) } throws RuntimeException("Delete failed")

        // When
        viewModel.deleteOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되어야 함
        val result = viewModel.deleteOneStudyGroupResponse.value
        assertThat(result).isEqualTo(MessageResponse())
        // 에러가 발생해도 콜백은 호출되지 않아야 함
        assertThat(callbackInvoked).isFalse()

        coVerify(exactly = 1) { deleteOneStudyGroupUseCase(studyGroupId) }
    }

    @Test
    fun `patchLessonSchedulesCheckToggle with error should handle gracefully`() = runTest {
        // Given
        val lessonScheduleId = 555
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
    fun `getOneStudyGroup with empty group should handle correctly`() = runTest {
        // Given
        val studyGroupId = 444
        val expectedResponse = OneStudyGroupResponse(
            studyGroupId = studyGroupId,
            name = "빈 스터디 그룹",
            inviteCode = "EMPTY00",
            leaderId = 0,
            leaderName = "",
            lectureName = "빈 강의",
            members = emptyList()
        )

        coEvery { getOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result.members).isEmpty()
        assertThat(result.leaderId).isEqualTo(0)
        assertThat(result.leaderName).isEmpty()

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
    }

    @Test
    fun `onDataChanged callback should be settable and callable`() = runTest {
        // Given
        var callbackCount = 0
        viewModel.onDataChanged = { callbackCount++ }

        // When
        viewModel.onDataChanged?.invoke()
        viewModel.onDataChanged?.invoke()
        viewModel.onDataChanged?.invoke()

        // Then
        assertThat(callbackCount).isEqualTo(3)
    }

    @Test
    fun `postPlanReschedule with error should handle gracefully`() = runTest {
        // Given
        val planId = 123
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
    fun `getOneStudyGroup with special characters in names should work correctly`() = runTest {
        // Given
        val studyGroupId = 111
        val specialMembers = listOf(
            StudyGroupMember(
                userId = 1,
                userName = "김특수문자!@#",
                planId = 401
            ),
            StudyGroupMember(
                userId = 2,
                userName = "이한글♥★",
                planId = 402
            )
        )

        val expectedResponse = OneStudyGroupResponse(
            studyGroupId = studyGroupId,
            name = "특수문자 그룹 ♠♣♥♦",
            inviteCode = "SPEC!@#",
            leaderId = 1,
            leaderName = "김특수문자!@#",
            lectureName = "특수문자가 포함된 강의명 ★☆",
            members = specialMembers
        )

        coEvery { getOneStudyGroupUseCase(studyGroupId) } returns flowOf(expectedResponse)

        // When
        viewModel.getOneStudyGroup(studyGroupId)
        advanceUntilIdle()

        // Then
        val result = viewModel.getOneStudyGroupResponse.value
        assertThat(result.name).contains("♠♣♥♦")
        assertThat(result.leaderName).contains("!@#")
        assertThat(result.lectureName).contains("★☆")
        assertThat(result.members.first().userName).contains("!@#")

        coVerify(exactly = 1) { getOneStudyGroupUseCase(studyGroupId) }
    }
}