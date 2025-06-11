package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.HomeRepositoryImpl
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.home.LectureProgressResponse
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.domain.response.home.TodayScheduleResponse
import com.capston.domain.response.home.UserProgressResponse
import com.capston.domain.response.home.WeeklyAchievement
import com.capston.domain.response.enum_class.DayOfWeek
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 홈리포지토리Truth테스트 {

    private val 홈데이터소스: HomeDataSource = mockk()
    private lateinit var 홈리포지토리: HomeRepositoryImpl

    @Before
    fun 설정() {
        홈리포지토리 = HomeRepositoryImpl(홈데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 홈 단건 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의진행률목록 = listOf(
            LectureProgressResponse(
                planId = 1,
                lectureAlias = "기본 수학",
                lectureName = "수학의 기초",
                completedLessons = 5,
                totalLessons = 20
            ),
            LectureProgressResponse(
                planId = 2,
                lectureAlias = "심화 영어",
                lectureName = "영어 독해의 모든 것",
                completedLessons = 12,
                totalLessons = 30
            )
        )

        val 오늘일정목록 = listOf(
            LessonScheduleResponse(
                id = 1,
                lessonTitle = "1강. 기초 개념 정리",
                lectureName = "기본 수학",
                adjustedDuration = 45,
                displayOrder = 1,
                completed = false
            ),
            LessonScheduleResponse(
                id = 2,
                lessonTitle = "2강. 문제 풀이",
                lectureName = "기본 수학",
                adjustedDuration = 50,
                displayOrder = 2,
                completed = true
            )
        )

        val 주간성취도 = WeeklyAchievement(
            mondayAchieved = true,
            tuesdayAchieved = true,
            wednesdayAchieved = false,
            thursdayAchieved = true,
            fridayAchieved = false,
            saturdayAchieved = true,
            sundayAchieved = false
        )

        val 예상응답 = DistinctHomeIdResponse(
            userProgress = UserProgressResponse(
                lectureProgress = 강의진행률목록,
                totalCompletedLessons = 17,
                totalLessons = 50
            ),
            todaySchedule = TodayScheduleResponse(
                date = "2024-01-15",
                dayOfWeek = DayOfWeek.MON,
                totalLessons = 2,
                totalDuration = 95,
                lessonSchedules = 오늘일정목록
            ),
            weeklyAchievement = 주간성취도,
            dday = DDayResponse(
                title = "수능",
                goalDate = "2024-11-14",
                ddayId = 1
            )
        )

        coEvery { 홈데이터소스.getDistinctHome() } returns flowOf(예상응답)

        // When - 실행
        홈리포지토리.getDistinctHome().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()

            // 사용자 진행률 검증
            assertThat(결과.userProgress.lectureProgress).hasSize(2)
            assertThat(결과.userProgress.totalCompletedLessons).isEqualTo(17)
            assertThat(결과.userProgress.totalLessons).isEqualTo(50)

            val 첫번째강의 = 결과.userProgress.lectureProgress.first()
            assertThat(첫번째강의.planId).isEqualTo(1)
            assertThat(첫번째강의.lectureAlias).isEqualTo("기본 수학")
            assertThat(첫번째강의.completedLessons).isEqualTo(5)
            assertThat(첫번째강의.totalLessons).isEqualTo(20)
            assertThat(첫번째강의.completedLessons).isLessThan(첫번째강의.totalLessons)

            // 오늘 일정 검증
            assertThat(결과.todaySchedule.date).isEqualTo("2024-01-15")
            assertThat(결과.todaySchedule.dayOfWeek).isEqualTo(DayOfWeek.MON)
            assertThat(결과.todaySchedule.totalLessons).isEqualTo(2)
            assertThat(결과.todaySchedule.totalDuration).isEqualTo(95)
            assertThat(결과.todaySchedule.lessonSchedules).hasSize(2)

            val 첫번째레슨 = 결과.todaySchedule.lessonSchedules!!.first()
            assertThat(첫번째레슨.id).isEqualTo(1)
            assertThat(첫번째레슨.lessonTitle).contains("기초 개념")
            assertThat(첫번째레슨.completed).isFalse()

            val 두번째레슨 = 결과.todaySchedule.lessonSchedules!![1]
            assertThat(두번째레슨.completed).isTrue()
            assertThat(두번째레슨.adjustedDuration).isGreaterThan(첫번째레슨.adjustedDuration)

            // 주간 성취도 검증
            assertThat(결과.weeklyAchievement.mondayAchieved).isTrue()
            assertThat(결과.weeklyAchievement.tuesdayAchieved).isTrue()
            assertThat(결과.weeklyAchievement.wednesdayAchieved).isFalse()
            assertThat(결과.weeklyAchievement.thursdayAchieved).isTrue()

            // D-Day 검증
            assertThat(결과.dday.title).isEqualTo("수능")
            assertThat(결과.dday.goalDate).isEqualTo("2024-11-14")
            assertThat(결과.dday.ddayId).isEqualTo(1)
        }

        coVerify { 홈데이터소스.getDistinctHome() }
    }

    @Test
    fun `Truth 라이브러리 사용 강의 체크 토글 테스트`() = runTest {
        // Given - 주어진 조건
        val 레슨스케줄아이디 = 123
        val 예상응답 = CheckResponse(
            lessonScheduleId = 레슨스케줄아이디,
            checked = true
        )

        coEvery { 홈데이터소스.patchLessonSchedulesCheckToggle(레슨스케줄아이디) } returns flowOf(예상응답)

        // When - 실행
        홈리포지토리.patchLessonSchedulesCheckToggle(레슨스케줄아이디).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.lessonScheduleId).isEqualTo(레슨스케줄아이디)
            assertThat(결과.checked).isTrue()
            assertThat(결과.lessonScheduleId).isAtLeast(1)
        }

        coVerify { 홈데이터소스.patchLessonSchedulesCheckToggle(레슨스케줄아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 디데이 생성 테스트`() = runTest {
        // Given - 주어진 조건
        val 디데이요청 = UpdateDDayRequest(
            title = "중간고사",
            goalDate = "2024-05-15"
        )
        val 예상응답 = DDayResponse(
            title = "중간고사",
            goalDate = "2024-05-15",
            ddayId = 5
        )

        coEvery { 홈데이터소스.postDDay(디데이요청) } returns flowOf(예상응답)

        // When - 실행
        홈리포지토리.postDDay(디데이요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.title).isEqualTo("중간고사")
            assertThat(결과.goalDate).isEqualTo("2024-05-15")
            assertThat(결과.ddayId).isEqualTo(5)
            assertThat(결과.title).contains("고사")
            assertThat(결과.goalDate).startsWith("2024")
            assertThat(결과.ddayId).isAtLeast(1)
        }

        coVerify { 홈데이터소스.postDDay(디데이요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 디데이 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 디데이아이디 = 10
        val 예상응답 = DDayResponse(
            title = "기말고사",
            goalDate = "2024-12-20",
            ddayId = 디데이아이디
        )

        coEvery { 홈데이터소스.getDDay(디데이아이디) } returns flowOf(예상응답)

        // When - 실행
        홈리포지토리.getDDay(디데이아이디).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.title).isEqualTo("기말고사")
            assertThat(결과.goalDate).isEqualTo("2024-12-20")
            assertThat(결과.ddayId).isEqualTo(디데이아이디)
            assertThat(결과.title).isNotEmpty()
            assertThat(결과.goalDate).endsWith("20")
        }

        coVerify { 홈데이터소스.getDDay(디데이아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 디데이 삭제 테스트`() = runTest {
        // Given - 주어진 조건
        val 디데이아이디 = 7

        coEvery { 홈데이터소스.deleteDDay(디데이아이디) } returns Unit

        // When - 실행
        val 결과 = 홈리포지토리.deleteDDay(디데이아이디)

        // Then - Truth 사용 검증 (Unit 반환이므로 호출 확인만)
        assertThat(결과).isEqualTo(Unit)

        coVerify { 홈데이터소스.deleteDDay(디데이아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 디데이 수정 테스트`() = runTest {
        // Given - 주어진 조건
        val 디데이아이디 = 3
        val 수정요청 = UpdateDDayRequest(
            title = "수정된 수능",
            goalDate = "2024-11-15"
        )
        val 예상응답 = DDayResponse(
            title = "수정된 수능",
            goalDate = "2024-11-15",
            ddayId = 디데이아이디
        )

        coEvery { 홈데이터소스.patchDDay(디데이아이디, 수정요청) } returns flowOf(예상응답)

        // When - 실행
        홈리포지토리.patchDDay(디데이아이디, 수정요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.title).isEqualTo("수정된 수능")
            assertThat(결과.goalDate).isEqualTo("2024-11-15")
            assertThat(결과.ddayId).isEqualTo(디데이아이디)
            assertThat(결과.title).contains("수정된")
            assertThat(결과.title).contains("수능")
            assertThat(결과.goalDate).isNotEqualTo("2024-11-14")
        }

        coVerify { 홈데이터소스.patchDDay(디데이아이디, 수정요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 체크 토글 실패 테스트`() = runTest {
        // Given - 주어진 조건
        val 잘못된레슨아이디 = -1
        val 예외 = RuntimeException("잘못된 레슨 ID")

        coEvery { 홈데이터소스.patchLessonSchedulesCheckToggle(잘못된레슨아이디) } throws 예외

        // When & Then - Truth 사용
        try {
            홈리포지토리.patchLessonSchedulesCheckToggle(잘못된레슨아이디).collect { }
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("잘못된 레슨 ID")
            assertThat(e.message).contains("잘못된")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 홈데이터소스.patchLessonSchedulesCheckToggle(잘못된레슨아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 빈 강의 진행률 테스트`() = runTest {
        // Given - 주어진 조건
        val 빈홈응답 = DistinctHomeIdResponse(
            userProgress = UserProgressResponse(
                lectureProgress = emptyList(),
                totalCompletedLessons = 0,
                totalLessons = 0
            ),
            todaySchedule = TodayScheduleResponse(
                date = "2024-01-15",
                dayOfWeek = DayOfWeek.MON,
                totalLessons = 0,
                totalDuration = 0,
                lessonSchedules = null
            )
        )

        coEvery { 홈데이터소스.getDistinctHome() } returns flowOf(빈홈응답)

        // When - 실행
        홈리포지토리.getDistinctHome().collect { 결과 ->
            // Then - Truth로 빈 값 검증
            assertThat(결과.userProgress.lectureProgress).isEmpty()
            assertThat(결과.userProgress.lectureProgress).hasSize(0)
            assertThat(결과.userProgress.totalCompletedLessons).isEqualTo(0)
            assertThat(결과.userProgress.totalLessons).isEqualTo(0)
            assertThat(결과.todaySchedule.lessonSchedules).isNull()
            assertThat(결과.todaySchedule.totalLessons).isEqualTo(0)
        }

        coVerify { 홈데이터소스.getDistinctHome() }
    }
}