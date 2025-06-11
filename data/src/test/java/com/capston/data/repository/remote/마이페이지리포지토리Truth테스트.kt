package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.MyPageRepositoryImpl
import com.capston.domain.datasource.MyPageDataSource
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.mypage.CompletedPlanDto
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.response.mypage.SubjectAchievementDto
import com.capston.domain.response.mypage.SubjectTimeDto
import com.capston.domain.response.mypage.WeeklyTimeDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 마이페이지리포지토리Truth테스트 {

    private val 마이페이지데이터소스: MyPageDataSource = mockk()
    private lateinit var 마이페이지리포지토리: MyPageRepositoryImpl

    @Before
    fun 설정() {
        마이페이지리포지토리 = MyPageRepositoryImpl(마이페이지데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 마이페이지 기본 정보 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 완료된계획목록 = listOf(
            CompletedPlanDto(
                planId = 1,
                lectureTitle = "수학 기초 완성",
                teacher = "김수학",
                platform = Platform.MEGA
            ),
            CompletedPlanDto(
                planId = 2,
                lectureTitle = "영어 독해 마스터",
                teacher = "이영어",
                platform = Platform.ETOOS
            )
        )

        val 과목성취도목록 = listOf(
            SubjectAchievementDto(
                subject = Subject.MATH,
                startDate = "2024-01-01",
                endDate = "2024-02-28",
                totalLessons = 30,
                completedLessons = 25
            ),
            SubjectAchievementDto(
                subject = Subject.ENG,
                startDate = "2024-01-15",
                endDate = "2024-03-15",
                totalLessons = 40,
                completedLessons = 20
            )
        )

        val 예상응답 = GetDistinctMyPageResponse(
            userName = "홍길동",
            todayTotalLessonCount = 5,
            todayCompletedLessonCount = 3,
            completedLectureCount = 2,
            studyStreak = 7,
            inProgressLectureCount = 3,
            completedPlanList = 완료된계획목록,
            subjectAchievementList = 과목성취도목록
        )

        coEvery { 마이페이지데이터소스.getDistinctMyPage() } returns 예상응답

        // When - 실행
        val 결과 = 마이페이지리포지토리.getDistinctMyPage()

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.userName).isEqualTo("홍길동")
        assertThat(결과.todayTotalLessonCount).isEqualTo(5)
        assertThat(결과.todayCompletedLessonCount).isEqualTo(3)
        assertThat(결과.completedLectureCount).isEqualTo(2)
        assertThat(결과.studyStreak).isEqualTo(7)
        assertThat(결과.inProgressLectureCount).isEqualTo(3)

        // 완료된 계획 검증
        assertThat(결과.completedPlanList).hasSize(2)

        val 첫번째완료계획 = 결과.completedPlanList[0]
        assertThat(첫번째완료계획.planId).isEqualTo(1)
        assertThat(첫번째완료계획.lectureTitle).isEqualTo("수학 기초 완성")
        assertThat(첫번째완료계획.teacher).isEqualTo("김수학")
        assertThat(첫번째완료계획.platform).isEqualTo(Platform.MEGA)

        val 두번째완료계획 = 결과.completedPlanList[1]
        assertThat(두번째완료계획.planId).isEqualTo(2)
        assertThat(두번째완료계획.lectureTitle).isEqualTo("영어 독해 마스터")
        assertThat(두번째완료계획.teacher).isEqualTo("이영어")
        assertThat(두번째완료계획.platform).isEqualTo(Platform.ETOOS)

        // 과목 성취도 검증
        assertThat(결과.subjectAchievementList).hasSize(2)

        val 수학성취도 = 결과.subjectAchievementList[0]
        assertThat(수학성취도.subject).isEqualTo(Subject.MATH)
        assertThat(수학성취도.startDate).isEqualTo("2024-01-01")
        assertThat(수학성취도.endDate).isEqualTo("2024-02-28")
        assertThat(수학성취도.totalLessons).isEqualTo(30)
        assertThat(수학성취도.completedLessons).isEqualTo(25)

        val 영어성취도 = 결과.subjectAchievementList[1]
        assertThat(영어성취도.subject).isEqualTo(Subject.ENG)
        assertThat(영어성취도.startDate).isEqualTo("2024-01-15")
        assertThat(영어성취도.endDate).isEqualTo("2024-03-15")
        assertThat(영어성취도.totalLessons).isEqualTo(40)
        assertThat(영어성취도.completedLessons).isEqualTo(20)

        // 백분율 계산 검증
        val 수학완료율 = (수학성취도.completedLessons.toDouble() / 수학성취도.totalLessons.toDouble()) * 100
        assertThat(수학완료율).isWithin(0.1).of(83.33) // 25/30 = 약 83.33%

        val 영어완료율 = (영어성취도.completedLessons.toDouble() / 영어성취도.totalLessons.toDouble()) * 100
        assertThat(영어완료율).isWithin(0.1).of(50.0) // 20/40 = 50%

        coVerify { 마이페이지데이터소스.getDistinctMyPage() }
    }

    @Test
    fun `Truth 라이브러리 사용 월별 공부 기록 통계 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 날짜 = "2024-01"
        val 과목별학습시간 = listOf(
            SubjectTimeDto(
                subject = Subject.MATH,
                totalMinutes = 360, // 6시간
                percentage = 40.0
            ),
            SubjectTimeDto(
                subject = Subject.ENG,
                totalMinutes = 300, // 5시간
                percentage = 33.33
            ),
            SubjectTimeDto(
                subject = Subject.KOR,
                totalMinutes = 240, // 4시간
                percentage = 26.67
            )
        )

        val 주차별학습시간 = listOf(
            WeeklyTimeDto(
                weekNumber = 1,
                totalMinutes = 300 // 5시간
            ),
            WeeklyTimeDto(
                weekNumber = 2,
                totalMinutes = 240 // 4시간
            ),
            WeeklyTimeDto(
                weekNumber = 3,
                totalMinutes = 180 // 3시간
            ),
            WeeklyTimeDto(
                weekNumber = 4,
                totalMinutes = 180 // 3시간
            )
        )

        val 예상응답 = GetMyPageStatisticsResponse(
            date = 날짜,
            totalStudyMinutes = 900, // 15시간
            subjectTimes = 과목별학습시간,
            weeklyTimes = 주차별학습시간
        )

        coEvery { 마이페이지데이터소스.getMonthlyStatistics(날짜) } returns 예상응답

        // When - 실행
        val 결과 = 마이페이지리포지토리.getMonthlyStatistics(날짜)

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.date).isEqualTo("2024-01")
        assertThat(결과.totalStudyMinutes).isEqualTo(900)

        // 과목별 학습 시간 검증
        assertThat(결과.subjectTimes).hasSize(3)

        val 수학학습시간 = 결과.subjectTimes[0]
        assertThat(수학학습시간.subject).isEqualTo(Subject.MATH)
        assertThat(수학학습시간.totalMinutes).isEqualTo(360)
        assertThat(수학학습시간.percentage).isWithin(0.1).of(40.0)

        val 영어학습시간 = 결과.subjectTimes[1]
        assertThat(영어학습시간.subject).isEqualTo(Subject.ENG)
        assertThat(영어학습시간.totalMinutes).isEqualTo(300)
        assertThat(영어학습시간.percentage).isWithin(0.1).of(33.33)

        val 국어학습시간 = 결과.subjectTimes[2]
        assertThat(국어학습시간.subject).isEqualTo(Subject.KOR)
        assertThat(국어학습시간.totalMinutes).isEqualTo(240)
        assertThat(국어학습시간.percentage).isWithin(0.1).of(26.67)

        // 백분율 합계가 100%인지 확인
        val 백분율합계 = 결과.subjectTimes.sumOf { it.percentage }
        assertThat(백분율합계).isWithin(0.1).of(100.0)

        // 주차별 학습 시간 검증
        assertThat(결과.weeklyTimes).hasSize(4)
        // assertThat(결과.weeklyTimes).isInOrder { a, b -> a.weekNumber - b.weekNumber }

        val 첫주학습시간 = 결과.weeklyTimes[0]
        assertThat(첫주학습시간.weekNumber).isEqualTo(1)
        assertThat(첫주학습시간.totalMinutes).isEqualTo(300)

        val 둘째주학습시간 = 결과.weeklyTimes[1]
        assertThat(둘째주학습시간.weekNumber).isEqualTo(2)
        assertThat(둘째주학습시간.totalMinutes).isEqualTo(240)

        // 주차별 학습 시간 합계가 총 학습 시간과 일치하는지 확인
        val 주차별학습시간합계 = 결과.weeklyTimes.sumOf { it.totalMinutes }
        assertThat(주차별학습시간합계).isEqualTo(결과.totalStudyMinutes)

        coVerify { 마이페이지데이터소스.getMonthlyStatistics(날짜) }
    }

    @Test
    fun `Truth 라이브러리 사용 학습 기록이 없는 사용자 마이페이지 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = GetDistinctMyPageResponse(
            userName = "신규사용자",
            todayTotalLessonCount = 0,
            todayCompletedLessonCount = 0,
            completedLectureCount = 0,
            studyStreak = 0,
            inProgressLectureCount = 0,
            completedPlanList = emptyList(),
            subjectAchievementList = emptyList()
        )

        coEvery { 마이페이지데이터소스.getDistinctMyPage() } returns 예상응답

        // When - 실행
        val 결과 = 마이페이지리포지토리.getDistinctMyPage()

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.userName).isEqualTo("신규사용자")
        assertThat(결과.todayTotalLessonCount).isEqualTo(0)
        assertThat(결과.todayCompletedLessonCount).isEqualTo(0)
        assertThat(결과.completedLectureCount).isEqualTo(0)
        assertThat(결과.studyStreak).isEqualTo(0)
        assertThat(결과.inProgressLectureCount).isEqualTo(0)
        assertThat(결과.completedPlanList).isEmpty()
        assertThat(결과.subjectAchievementList).isEmpty()

        coVerify { 마이페이지데이터소스.getDistinctMyPage() }
    }

    @Test
    fun `Truth 라이브러리 사용 공부 기록이 없는 월 통계 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 날짜 = "2024-02"
        val 예상응답 = GetMyPageStatisticsResponse(
            date = 날짜,
            totalStudyMinutes = 0,
            subjectTimes = emptyList(),
            weeklyTimes = emptyList()
        )

        coEvery { 마이페이지데이터소스.getMonthlyStatistics(날짜) } returns 예상응답

        // When - 실행
        val 결과 = 마이페이지리포지토리.getMonthlyStatistics(날짜)

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.date).isEqualTo("2024-02")
        assertThat(결과.totalStudyMinutes).isEqualTo(0)
        assertThat(결과.subjectTimes).isEmpty()
        assertThat(결과.weeklyTimes).isEmpty()

        coVerify { 마이페이지데이터소스.getMonthlyStatistics(날짜) }
    }

    @Test
    fun `Truth 라이브러리 사용 다양한 과목 학습 기록 테스트`() = runTest {
        // Given - 주어진 조건
        val 날짜 = "2024-03"
        val 다양한과목학습시간 = listOf(
            SubjectTimeDto(subject = Subject.MATH, totalMinutes = 300, percentage = 30.0),
            SubjectTimeDto(subject = Subject.ENG, totalMinutes = 200, percentage = 20.0),
            SubjectTimeDto(subject = Subject.KOR, totalMinutes = 150, percentage = 15.0),
            SubjectTimeDto(subject = Subject.SOC, totalMinutes = 100, percentage = 10.0),
            SubjectTimeDto(subject = Subject.SCI, totalMinutes = 150, percentage = 15.0),
            SubjectTimeDto(subject = Subject.HIST, totalMinutes = 100, percentage = 10.0)
        )

        val 예상응답 = GetMyPageStatisticsResponse(
            date = 날짜,
            totalStudyMinutes = 1000,
            subjectTimes = 다양한과목학습시간,
            weeklyTimes = listOf(
                WeeklyTimeDto(weekNumber = 1, totalMinutes = 250),
                WeeklyTimeDto(weekNumber = 2, totalMinutes = 250),
                WeeklyTimeDto(weekNumber = 3, totalMinutes = 250),
                WeeklyTimeDto(weekNumber = 4, totalMinutes = 250)
            )
        )

        coEvery { 마이페이지데이터소스.getMonthlyStatistics(날짜) } returns 예상응답

        // When - 실행
        val 결과 = 마이페이지리포지토리.getMonthlyStatistics(날짜)

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.subjectTimes).hasSize(6)

        // 모든 과목이 포함되어 있는지 확인
        val 과목목록 = 결과.subjectTimes.map { it.subject }
        assertThat(과목목록).containsAtLeast(
            Subject.MATH, Subject.ENG, Subject.KOR,
            Subject.SOC, Subject.SCI, Subject.HIST
        )

        // 과목별 학습 시간 비율 검증
        val 수학비율 = 결과.subjectTimes.find { it.subject == Subject.MATH }!!.percentage
        val 영어비율 = 결과.subjectTimes.find { it.subject == Subject.ENG }!!.percentage

        assertThat(수학비율).isGreaterThan(영어비율)

        // 백분율 합계가 100%인지 확인
        val 백분율합계 = 결과.subjectTimes.sumOf { it.percentage }
        assertThat(백분율합계).isWithin(0.1).of(100.0)

        coVerify { 마이페이지데이터소스.getMonthlyStatistics(날짜) }
    }

    @Test
    fun `Truth 라이브러리 사용 마이페이지 조회 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 예외 = RuntimeException("마이페이지 정보를 불러오는 중 오류가 발생했습니다.")

        coEvery { 마이페이지데이터소스.getDistinctMyPage() } throws 예외

        // When & Then - Truth 사용
        try {
            마이페이지리포지토리.getDistinctMyPage()
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("마이페이지 정보를 불러오는 중 오류가 발생했습니다.")
            assertThat(e.message).contains("마이페이지")
            assertThat(e.message).contains("오류")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 마이페이지데이터소스.getDistinctMyPage() }
    }

    @Test
    fun `Truth 라이브러리 사용 월별 통계 조회 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 날짜 = "2024-04"
        val 예외 = RuntimeException("월별 통계를 불러오는 중 오류가 발생했습니다.")

        coEvery { 마이페이지데이터소스.getMonthlyStatistics(날짜) } throws 예외

        // When & Then - Truth 사용
        try {
            마이페이지리포지토리.getMonthlyStatistics(날짜)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("월별 통계를 불러오는 중 오류가 발생했습니다.")
            assertThat(e.message).contains("월별 통계")
            assertThat(e.message).contains("오류")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 마이페이지데이터소스.getMonthlyStatistics(날짜) }
    }
}