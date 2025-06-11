package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.study_group.PostJoinStudyGroupUseCase
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
class LectureRoomViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getPlanLectureRoomUseCase: GetPlanLectureRoomUseCase = mockk()
    private val postJoinStudyGroupUseCase: PostJoinStudyGroupUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: LectureRoomViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = LectureRoomViewModel(
            getPlanLectureRoomUseCase,
            postJoinStudyGroupUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPlanLectureRoom should update state with lecture room data`() = runTest {
        // Given
        val expectedPlans = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "수학 기초",
                platform = Platform.MEGA,
                teacher = "김수학",
                completedLessons = 5,
                totalLessons = 20,
                studyGroupId = null,
                studyGroup = false
            ),
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "영어 완성",
                platform = Platform.ETOOS,
                teacher = "이영어",
                completedLessons = 15,
                totalLessons = 30,
                studyGroupId = 100,
                studyGroup = true
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(expectedPlans)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getPlanLectureRoomResponse.value).isEqualTo(expectedPlans)
        assertThat(viewModel.getPlanLectureRoomResponse.value).hasSize(2)

        val firstPlan = viewModel.getPlanLectureRoomResponse.value[0]
        assertThat(firstPlan.planId).isEqualTo(1)
        assertThat(firstPlan.lectureTitle).isEqualTo("수학 기초")
        assertThat(firstPlan.platform).isEqualTo(Platform.MEGA)
        assertThat(firstPlan.studyGroup).isFalse()

        val secondPlan = viewModel.getPlanLectureRoomResponse.value[1]
        assertThat(secondPlan.studyGroup).isTrue()
        assertThat(secondPlan.studyGroupId).isEqualTo(100)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getPlanLectureRoomUseCase() }
    }

    @Test
    fun `getPlanLectureRoom should handle empty lecture room`() = runTest {
        // Given
        val emptyPlans = emptyList<GetPlanLectureRoomResponse>()
        coEvery { getPlanLectureRoomUseCase() } returns flowOf(emptyPlans)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getPlanLectureRoomResponse.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getPlanLectureRoomUseCase() }
    }

    @Test
    fun `getPlanLectureRoom should handle different platforms`() = runTest {
        // Given
        val plansWithDifferentPlatforms = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "메가스터디 수학",
                platform = Platform.MEGA,
                teacher = "메가쌤",
                completedLessons = 10,
                totalLessons = 50
            ),
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "이투스 영어",
                platform = Platform.ETOOS,
                teacher = "이투쌤",
                completedLessons = 20,
                totalLessons = 40
            ),
            GetPlanLectureRoomResponse(
                planId = 3,
                lectureTitle = "대성마이맥 국어",
                platform = Platform.DAESANG,
                teacher = "대성쌤",
                completedLessons = 5,
                totalLessons = 25
            ),
            GetPlanLectureRoomResponse(
                planId = 4,
                lectureTitle = "EBSI 과학",
                platform = Platform.EBSI,
                teacher = "EBSI쌤",
                completedLessons = 15,
                totalLessons = 35
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(plansWithDifferentPlatforms)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getPlanLectureRoomResponse.value).hasSize(4)

        val platforms = viewModel.getPlanLectureRoomResponse.value.map { it.platform }
        assertThat(platforms).containsExactly(
            Platform.MEGA, Platform.ETOOS, Platform.DAESANG, Platform.EBSI
        )
    }

    @Test
    fun `getPlanLectureRoom should handle API error`() = runTest {
        // Given
        coEvery { getPlanLectureRoomUseCase() } throws RuntimeException("네트워크 오류")

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        // 기본값이 유지되는지 확인 (빈 리스트)
        assertThat(viewModel.getPlanLectureRoomResponse.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getPlanLectureRoomUseCase() }
    }

    @Test
    fun `postJoinStudyGroup should successfully join study group`() = runTest {
        // Given
        val inviteCode = "INVITE123"
        val joinStudyGroupDto = JoinStudyGroupDto(inviteCode = inviteCode)
        val expectedResponse = MessageResponse(message = "스터디그룹 가입이 완료되었습니다.")

        coEvery { postJoinStudyGroupUseCase(joinStudyGroupDto) } returns flowOf(expectedResponse)

        // When
        viewModel.postJoinStudyGroup(joinStudyGroupDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postJoinStudyGroupResponse.value).isEqualTo(expectedResponse)
        assertThat(viewModel.postJoinStudyGroupResponse.value.message).isEqualTo("스터디그룹 가입이 완료되었습니다.")

        coVerify(exactly = 1) { postJoinStudyGroupUseCase(joinStudyGroupDto) }
    }

    @Test
    fun `postJoinStudyGroup should handle invalid invite code`() = runTest {
        // Given
        val invalidInviteCode = "INVALID"
        val joinStudyGroupDto = JoinStudyGroupDto(inviteCode = invalidInviteCode)
        val errorResponse = MessageResponse(message = "유효하지 않은 초대 코드입니다.")

        coEvery { postJoinStudyGroupUseCase(joinStudyGroupDto) } returns flowOf(errorResponse)

        // When
        viewModel.postJoinStudyGroup(joinStudyGroupDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postJoinStudyGroupResponse.value.message).isEqualTo("유효하지 않은 초대 코드입니다.")
    }

    @Test
    fun `postJoinStudyGroup should handle empty invite code`() = runTest {
        // Given
        val emptyInviteCode = ""
        val joinStudyGroupDto = JoinStudyGroupDto(inviteCode = emptyInviteCode)
        val errorResponse = MessageResponse(message = "초대 코드를 입력해주세요.")

        coEvery { postJoinStudyGroupUseCase(joinStudyGroupDto) } returns flowOf(errorResponse)

        // When
        viewModel.postJoinStudyGroup(joinStudyGroupDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postJoinStudyGroupResponse.value.message).isEqualTo("초대 코드를 입력해주세요.")
    }

    @Test
    fun `postJoinStudyGroup should handle network error`() = runTest {
        // Given
        val joinStudyGroupDto = JoinStudyGroupDto(inviteCode = "INVITE123")
        coEvery { postJoinStudyGroupUseCase(joinStudyGroupDto) } throws RuntimeException("네트워크 연결 실패")

        // When
        viewModel.postJoinStudyGroup(joinStudyGroupDto)
        advanceUntilIdle()

        // Then
        // 기본값이 유지되는지 확인
        assertThat(viewModel.postJoinStudyGroupResponse.value).isEqualTo(MessageResponse())
        coVerify(exactly = 1) { postJoinStudyGroupUseCase(joinStudyGroupDto) }
    }

    @Test
    fun `onDataChanged callback should be invokable`() = runTest {
        // Given
        var callbackInvoked = false
        viewModel.onDataChanged = { callbackInvoked = true }

        // When
        viewModel.onDataChanged?.invoke()

        // Then
        assertThat(callbackInvoked).isTrue()
    }

    @Test
    fun `onDataChanged callback should be nullable`() = runTest {
        // Given
        viewModel.onDataChanged = null

        // When & Then
        // 예외가 발생하지 않아야 함
        viewModel.onDataChanged?.invoke()
        assertThat(viewModel.onDataChanged).isNull()
    }

    @Test
    fun `getPlanLectureRoom should handle completed progress scenarios`() = runTest {
        // Given
        val plansWithVariousProgress = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "완료된 강의",
                completedLessons = 30,
                totalLessons = 30
            ),
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "진행 중인 강의",
                completedLessons = 15,
                totalLessons = 30
            ),
            GetPlanLectureRoomResponse(
                planId = 3,
                lectureTitle = "시작 안한 강의",
                completedLessons = 0,
                totalLessons = 20
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(plansWithVariousProgress)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        val plans = viewModel.getPlanLectureRoomResponse.value

        // 완료된 강의
        val completedPlan = plans[0]
        assertThat(completedPlan.completedLessons).isEqualTo(completedPlan.totalLessons)

        // 진행 중인 강의
        val inProgressPlan = plans[1]
        assertThat(inProgressPlan.completedLessons).isLessThan(inProgressPlan.totalLessons)
        assertThat(inProgressPlan.completedLessons).isGreaterThan(0)

        // 시작 안한 강의
        val notStartedPlan = plans[2]
        assertThat(notStartedPlan.completedLessons).isEqualTo(0)
    }

    @Test
    fun `getPlanLectureRoom should handle long lecture titles`() = runTest {
        // Given
        val longTitle = "매우 긴 강의 제목입니다. ".repeat(10)
        val planWithLongTitle = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = longTitle,
                teacher = "긴제목쌤",
                platform = Platform.MEGA,
                completedLessons = 5,
                totalLessons = 20
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(planWithLongTitle)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        val plan = viewModel.getPlanLectureRoomResponse.value[0]
        assertThat(plan.lectureTitle).isEqualTo(longTitle)
        assertThat(plan.lectureTitle.length).isGreaterThan(100)
    }

    @Test
    fun `getPlanLectureRoom should handle mixed study group statuses`() = runTest {
        // Given
        val mixedPlans = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "개인 학습",
                studyGroup = false,
                studyGroupId = null
            ),
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "그룹 학습",
                studyGroup = true,
                studyGroupId = 100
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(mixedPlans)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        val plans = viewModel.getPlanLectureRoomResponse.value

        val individualPlan = plans[0]
        assertThat(individualPlan.studyGroup).isFalse()
        assertThat(individualPlan.studyGroupId).isNull()

        val groupPlan = plans[1]
        assertThat(groupPlan.studyGroup).isTrue()
        assertThat(groupPlan.studyGroupId).isNotNull()
        assertThat(groupPlan.studyGroupId).isEqualTo(100)
    }

    @Test
    fun `getPlanLectureRoom should handle multiple API calls correctly`() = runTest {
        // Given
        val firstCallPlans = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "첫번째 호출 강의"
            )
        )
        val secondCallPlans = listOf(
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "두번째 호출 강의"
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returnsMany listOf(
            flowOf(firstCallPlans),
            flowOf(secondCallPlans)
        )

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getPlanLectureRoomResponse.value[0].lectureTitle).isEqualTo("첫번째 호출 강의")

        // When - 두번째 호출
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getPlanLectureRoomResponse.value[0].lectureTitle).isEqualTo("두번째 호출 강의")

        coVerify(exactly = 2) { getPlanLectureRoomUseCase() }
    }

    @Test
    fun `postJoinStudyGroup should handle various response messages`() = runTest {
        // Given
        val testCases = listOf(
            "스터디그룹 가입이 완료되었습니다.",
            "이미 가입된 스터디그룹입니다.",
            "만료된 초대 코드입니다.",
            "스터디그룹이 꽉 찼습니다."
        )

        testCases.forEach { message ->
            val joinStudyGroupDto = JoinStudyGroupDto(inviteCode = "TEST_CODE")
            val response = MessageResponse(message = message)

            coEvery { postJoinStudyGroupUseCase(joinStudyGroupDto) } returns flowOf(response)

            // When
            viewModel.postJoinStudyGroup(joinStudyGroupDto)
            advanceUntilIdle()

            // Then
            assertThat(viewModel.postJoinStudyGroupResponse.value.message).isEqualTo(message)
        }
    }

    @Test
    fun `getPlanLectureRoom should handle zero lesson plans`() = runTest {
        // Given
        val zeroLessonPlans = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "빈 강의",
                completedLessons = 0,
                totalLessons = 0
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(zeroLessonPlans)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        val plan = viewModel.getPlanLectureRoomResponse.value[0]
        assertThat(plan.totalLessons).isEqualTo(0)
        assertThat(plan.completedLessons).isEqualTo(0)
    }

    @Test
    fun `getPlanLectureRoom should handle special characters in titles`() = runTest {
        // Given
        val specialCharacterTitle = "수학 & 과학 🔬 (고급과정) - 2024년 대비"
        val planWithSpecialChars = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = specialCharacterTitle,
                teacher = "특수문자쌤 👨‍🏫"
            )
        )

        coEvery { getPlanLectureRoomUseCase() } returns flowOf(planWithSpecialChars)

        // When
        viewModel.getPlanLectureRoom()
        advanceUntilIdle()

        // Then
        val plan = viewModel.getPlanLectureRoomResponse.value[0]
        assertThat(plan.lectureTitle).isEqualTo(specialCharacterTitle)
        assertThat(plan.teacher).isEqualTo("특수문자쌤 👨‍🏫")
    }
}