package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.PlanRepositoryImpl
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPeriodPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PlanDetailDailySchedule
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.domain.response.enum_class.Platform
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 계획리포지토리Truth테스트 {

    private val 계획데이터소스: PlanDataSource = mockk()
    private lateinit var 계획리포지토리: PlanRepositoryImpl

    @Before
    fun 설정() {
        계획리포지토리 = PlanRepositoryImpl(계획데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 새 계획 생성 테스트`() = runTest {
        // Given - 주어진 조건
        val 새계획요청 = PostNewPeriodPlanDto(
            lectureId = 1,
            planType = "PERIOD",
            startLessonId = 1,
            endLessonId = 20,
            studyDayOfWeeks = listOf("MON", "WED", "FRI"),
            dailyTime = 90,
            startDate = "2024-01-15",
            endDate = "2024-02-15",
            playbackSpeed = 1.5
        )
        val 예상응답 = MessageResponse(message = "계획이 성공적으로 생성되었습니다.")

        coEvery { 계획데이터소스.postNewPeriodPlan(새계획요청) } returns 예상응답

        // When - 실행
        val 결과 = 계획리포지토리.postNewPeriodPlan(새계획요청)

        // Then - Truth 사용 검증
        assertThat(결과.message).isEqualTo("계획이 성공적으로 생성되었습니다.")
        assertThat(결과.message).contains("성공")
        assertThat(결과.message).contains("생성")
        assertThat(결과.message).isNotEmpty()
        assertThat(결과.message).endsWith("습니다.")

        coVerify { 계획데이터소스.postNewPeriodPlan(새계획요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 계획 이름 수정 테스트`() = runTest {
        // Given - 주어진 조건
        val 계획아이디 = 1
        val 수정요청 = PatchPlanDto(lectureAlias = "수정된 강의명")
        val 예상응답 = LectureAliasResponse(
            planId = 계획아이디,
            lectureAlias = "수정된 강의명"
        )

        coEvery { 계획데이터소스.patchPlanName(계획아이디, 수정요청) } returns 예상응답

        // When - 실행
        val 결과 = 계획리포지토리.patchPlanName(계획아이디, 수정요청)

        // Then - Truth 사용 검증
        assertThat(결과.planId).isEqualTo(계획아이디)
        assertThat(결과.planId).isAtLeast(1)
        assertThat(결과.lectureAlias).isEqualTo("수정된 강의명")
        assertThat(결과.lectureAlias).contains("수정된")
        assertThat(결과.lectureAlias).contains("강의명")
        assertThat(결과.lectureAlias).isNotEmpty()

        coVerify { 계획데이터소스.patchPlanName(계획아이디, 수정요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 강의실 목록 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의실목록 = listOf(
            GetPlanLectureRoomResponse(
                planId = 1,
                lectureTitle = "기본 수학",
                platform = Platform.MEGA,
                teacher = "김수학",
                completedLessons = 5,
                totalLessons = 20,
                studyGroupId = null,
                studyGroup = false
            ),
            GetPlanLectureRoomResponse(
                planId = 2,
                lectureTitle = "심화 영어",
                platform = Platform.ETOOS,
                teacher = "이영어",
                completedLessons = 10,
                totalLessons = 30,
                studyGroupId = 100,
                studyGroup = true
            )
        )

        coEvery { 계획데이터소스.getPlanLectureRoom() } returns 강의실목록

        // When - 실행
        val 결과 = 계획리포지토리.getPlanLectureRoom()

        // Then - Truth 사용 검증
        assertThat(결과).hasSize(2)
        assertThat(결과).isNotEmpty()

        // 첫 번째 계획 검증
        val 첫번째계획 = 결과.first()
        assertThat(첫번째계획.planId).isEqualTo(1)
        assertThat(첫번째계획.lectureTitle).isEqualTo("기본 수학")
        assertThat(첫번째계획.lectureTitle).contains("수학")
        assertThat(첫번째계획.platform).isEqualTo(Platform.MEGA)
        assertThat(첫번째계획.teacher).isEqualTo("김수학")
        assertThat(첫번째계획.teacher).startsWith("김")
        assertThat(첫번째계획.completedLessons).isEqualTo(5)
        assertThat(첫번째계획.totalLessons).isEqualTo(20)
        assertThat(첫번째계획.completedLessons).isLessThan(첫번째계획.totalLessons)
        assertThat(첫번째계획.studyGroup).isFalse()
        assertThat(첫번째계획.studyGroupId).isNull()

        // 두 번째 계획 검증 (스터디 그룹)
        val 두번째계획 = 결과[1]
        assertThat(두번째계획.planId).isEqualTo(2)
        assertThat(두번째계획.lectureTitle).contains("영어")
        assertThat(두번째계획.platform).isEqualTo(Platform.ETOOS)
        assertThat(두번째계획.studyGroup).isTrue()
        assertThat(두번째계획.studyGroupId).isEqualTo(100)
        assertThat(두번째계획.studyGroupId).isAtLeast(1)
        assertThat(두번째계획.completedLessons).isGreaterThan(첫번째계획.completedLessons)

        coVerify { 계획데이터소스.getPlanLectureRoom() }
    }

    @Test
    fun `Truth 라이브러리 사용 계획 상세 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 계획아이디 = 1
        val 일일일정목록 = listOf(
            PlanDetailDailySchedule(
                date = "2024-01-15",
                dayOfWeek = "월",
                lessonSchedules = listOf(
                    PlanDetailLessonSchedule(
                        id = 1,
                        lessonTitle = "1강. 기초 개념",
                        lectureName = "기본 수학",
                        adjustedDuration = 30,
                        displayOrder = 1,
                        completed = true
                    ),
                    PlanDetailLessonSchedule(
                        id = 2,
                        lessonTitle = "2강. 응용 문제",
                        lectureName = "기본 수학",
                        adjustedDuration = 45,
                        displayOrder = 2,
                        completed = false
                    )
                )
            )
        )
        val 예상응답 = GetPlanDetailResponse(
            planId = 계획아이디,
            lectureTitle = "기본 수학 완전정복",
            teacher = "김수학",
            platform = "메가스터디",
            dailySchedules = 일일일정목록
        )

        coEvery { 계획데이터소스.getPlanDetail(계획아이디) } returns 예상응답

        // When - 실행
        val 결과 = 계획리포지토리.getPlanDetail(계획아이디)

        // Then - Truth 사용 검증
        assertThat(결과.planId).isEqualTo(계획아이디)
        assertThat(결과.lectureTitle).isEqualTo("기본 수학 완전정복")
        assertThat(결과.lectureTitle).contains("수학")
        assertThat(결과.lectureTitle).contains("완전정복")
        assertThat(결과.teacher).isEqualTo("김수학")
        assertThat(결과.platform).isEqualTo("메가스터디")
        assertThat(결과.platform).contains("메가")

        // 일일 일정 검증
        assertThat(결과.dailySchedules).hasSize(1)
        assertThat(결과.dailySchedules).isNotEmpty()

        val 일일일정 = 결과.dailySchedules.first()
        assertThat(일일일정.date).isEqualTo("2024-01-15")
        assertThat(일일일정.date).startsWith("2024")
        assertThat(일일일정.dayOfWeek).isEqualTo("월")
        assertThat(일일일정.lessonSchedules).hasSize(2)

        // 첫 번째 레슨 검증 (완료됨)
        val 첫번째레슨 = 일일일정.lessonSchedules.first()
        assertThat(첫번째레슨.id).isEqualTo(1)
        assertThat(첫번째레슨.lessonTitle).isEqualTo("1강. 기초 개념")
        assertThat(첫번째레슨.lessonTitle).contains("기초")
        assertThat(첫번째레슨.adjustedDuration).isEqualTo(30)
        assertThat(첫번째레슨.displayOrder).isEqualTo(1)
        assertThat(첫번째레슨.completed).isTrue()

        // 두 번째 레슨 검증 (미완료)
        val 두번째레슨 = 일일일정.lessonSchedules[1]
        assertThat(두번째레슨.id).isEqualTo(2)
        assertThat(두번째레슨.lessonTitle).contains("응용")
        assertThat(두번째레슨.adjustedDuration).isEqualTo(45)
        assertThat(두번째레슨.adjustedDuration).isGreaterThan(첫번째레슨.adjustedDuration)
        assertThat(두번째레슨.displayOrder).isGreaterThan(첫번째레슨.displayOrder)
        assertThat(두번째레슨.completed).isFalse()

        coVerify { 계획데이터소스.getPlanDetail(계획아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 계획 재스케줄링 테스트`() = runTest {
        // Given - 주어진 조건
        val 계획아이디 = 1
        val 예상응답 = MessageResponse(message = "재스케줄링이 완료되었습니다.")

        coEvery { 계획데이터소스.postPlanReschedule(계획아이디) } returns 예상응답

        // When - 실행
        val 결과 = 계획리포지토리.postPlanReschedule(계획아이디)

        // Then - Truth 사용 검증
        assertThat(결과.message).isEqualTo("재스케줄링이 완료되었습니다.")
        assertThat(결과.message).contains("재스케줄링")
        assertThat(결과.message).contains("완료")
        assertThat(결과.message).isNotEmpty()

        coVerify { 계획데이터소스.postPlanReschedule(계획아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 계획 삭제 테스트`() = runTest {
        // Given - 주어진 조건
        val 계획아이디 = 1
        val 예상응답 = MessageResponse(message = "계획이 삭제되었습니다.")

        coEvery { 계획데이터소스.deleteOnePlan(계획아이디) } returns 예상응답

        // When - 실행
        val 결과 = 계획리포지토리.deleteOnePlan(계획아이디)

        // Then - Truth 사용 검증
        assertThat(결과.message).isEqualTo("계획이 삭제되었습니다.")
        assertThat(결과.message).contains("삭제")
        assertThat(결과.message).isNotEmpty()

        coVerify { 계획데이터소스.deleteOnePlan(계획아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 빈 강의실 목록 테스트`() = runTest {
        // Given - 주어진 조건
        val 빈목록 = emptyList<GetPlanLectureRoomResponse>()

        coEvery { 계획데이터소스.getPlanLectureRoom() } returns 빈목록

        // When - 실행
        val 결과 = 계획리포지토리.getPlanLectureRoom()

        // Then - Truth로 빈 값 검증
        assertThat(결과).isEmpty()
        assertThat(결과).hasSize(0)
        assertThat(결과).isNotNull()

        coVerify { 계획데이터소스.getPlanLectureRoom() }
    }

    @Test
    fun `Truth 라이브러리 사용 계획 생성 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 새계획요청 = PostNewPeriodPlanDto(lectureId = 1)
        val 예외 = RuntimeException("계획 생성 실패")

        coEvery { 계획데이터소스.postNewPeriodPlan(새계획요청) } throws 예외

        // When & Then - Truth 사용
        try {
            계획리포지토리.postNewPeriodPlan(새계획요청)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("계획 생성 실패")
            assertThat(e.message).contains("실패")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 계획데이터소스.postNewPeriodPlan(새계획요청) }
    }
}