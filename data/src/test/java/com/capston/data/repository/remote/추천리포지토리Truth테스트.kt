package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.RecommendRepositoryImpl
import com.capston.domain.datasource.RecommendDataSource
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.recommend.RecommendResponse
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 추천리포지토리Truth테스트 {

    private val 추천데이터소스: RecommendDataSource = mockk()
    private lateinit var 추천리포지토리: RecommendRepositoryImpl

    @Before
    fun 설정() {
        추천리포지토리 = RecommendRepositoryImpl(추천데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 강의 추천 테스트`() = runTest {
        // Given - 주어진 조건
        val 추천요청 = RecommendDto(
            grade = "3",
            schoolRank = 2,
            mockRank = 3,
            focus = "수능 중심",
            goal = "기출 분석",
            styles = listOf("체계적 학습", "문제풀이 중심"),
            subject = Subject.MATH
        )

        val 예상추천결과 = listOf(
            RecommendResponse(
                id = 1,
                platform = Platform.MEGA,
                title = "수학 기출문제 완전정복",
                teacher = "김수학",
                url = "https://example.com/math1",
                description = "체계적인 기출문제 분석으로 수능 수학 완벽 대비",
                tag = "기출분석, 실전대비",
                totalLessons = 50,
                recommendScore = 95,
                recommendReason = "학생의 등급과 목표에 최적화된 강의입니다",
                difficulty = "중상",
                isPersonalized = true,
                subject = Subject.MATH
            ),
            RecommendResponse(
                id = 2,
                platform = Platform.ETOOS,
                title = "수학 실전 모의고사",
                teacher = "이수학",
                url = "https://example.com/math2",
                description = "실전 감각을 기르는 모의고사 완벽 해설",
                tag = "모의고사, 실전연습",
                totalLessons = 30,
                recommendScore = 88,
                recommendReason = "문제풀이 중심 학습을 선호하는 학생에게 적합",
                difficulty = "상",
                isPersonalized = true,
                subject = Subject.MATH
            ),
            RecommendResponse(
                id = 3,
                platform = Platform.MEGA,
                title = "수학 개념 정리",
                teacher = "박수학",
                url = "https://example.com/math3",
                description = "수학 개념을 체계적으로 정리하는 강의",
                tag = "개념정리, 기초",
                totalLessons = 40,
                recommendScore = 75,
                recommendReason = "기초 개념 보강이 필요한 학생에게 추천",
                difficulty = "중",
                isPersonalized = false,
                subject = Subject.MATH
            )
        )

        coEvery { 추천데이터소스.postRecommendLectures(추천요청) } returns 예상추천결과

        // When - 실행
        val 결과 = 추천리포지토리.postRecommendLectures(추천요청)

        // Then - Truth 사용 검증
        assertThat(결과).hasSize(3)
        assertThat(결과).isNotEmpty()

        // 첫 번째 추천 강의 검증
        val 첫번째추천 = 결과.first()
        assertThat(첫번째추천.id).isEqualTo(1)
        assertThat(첫번째추천.title).isEqualTo("수학 기출문제 완전정복")
        assertThat(첫번째추천.title).contains("수학")
        assertThat(첫번째추천.title).contains("기출문제")
        assertThat(첫번째추천.teacher).isEqualTo("김수학")
        assertThat(첫번째추천.teacher).startsWith("김")
        assertThat(첫번째추천.platform).isEqualTo(Platform.MEGA)
        assertThat(첫번째추천.subject).isEqualTo(Subject.MATH)
        assertThat(첫번째추천.recommendScore).isEqualTo(95)
        assertThat(첫번째추천.recommendScore).isAtLeast(90)
        assertThat(첫번째추천.isPersonalized).isTrue()
        assertThat(첫번째추천.difficulty).isEqualTo("중상")
        assertThat(첫번째추천.totalLessons).isEqualTo(50)
        assertThat(첫번째추천.url).startsWith("https://")
        assertThat(첫번째추천.tag).contains("기출분석")

        // 두 번째 추천 강의 검증
        val 두번째추천 = 결과[1]
        assertThat(두번째추천.id).isEqualTo(2)
        assertThat(두번째추천.platform).isEqualTo(Platform.ETOOS)
        assertThat(두번째추천.title).contains("모의고사")
        assertThat(두번째추천.recommendScore).isEqualTo(88)
        assertThat(두번째추천.recommendScore).isLessThan(첫번째추천.recommendScore)
        assertThat(두번째추천.difficulty).isEqualTo("상")
        assertThat(두번째추천.totalLessons).isEqualTo(30)
        assertThat(두번째추천.totalLessons).isLessThan(첫번째추천.totalLessons)

        // 세 번째 추천 강의 검증
        val 세번째추천 = 결과[2]
        assertThat(세번째추천.id).isEqualTo(3)
        assertThat(세번째추천.title).contains("개념")
        assertThat(세번째추천.recommendScore).isEqualTo(75)
        assertThat(세번째추천.recommendScore).isLessThan(두번째추천.recommendScore)
        assertThat(세번째추천.isPersonalized).isFalse()
        assertThat(세번째추천.difficulty).isEqualTo("중")

        // 모든 추천이 같은 과목인지 확인
        val 과목목록 = 결과.map { it.subject }.distinct()
        assertThat(과목목록).hasSize(1)
        assertThat(과목목록.first()).isEqualTo(Subject.MATH)

        // 개인화 추천 개수 확인
        val 개인화추천개수 = 결과.count { it.isPersonalized }
        assertThat(개인화추천개수).isEqualTo(2)
        assertThat(개인화추천개수).isAtLeast(1)

        coVerify { 추천데이터소스.postRecommendLectures(추천요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 영어 과목 추천 테스트`() = runTest {
        // Given - 영어 과목 추천 요청
        val 영어추천요청 = RecommendDto(
            grade = "2",
            schoolRank = 3,
            mockRank = 4,
            focus = "내신 중심",
            goal = "개념 정리",
            styles = listOf("반복 학습", "이론 중심"),
            subject = Subject.ENG
        )

        val 영어추천결과 = listOf(
            RecommendResponse(
                id = 10,
                platform = Platform.ETOOS,
                title = "영어 문법 기초 완성",
                teacher = "최영어",
                url = "https://example.com/eng1",
                description = "내신 영어 문법을 체계적으로 정리",
                tag = "문법, 내신",
                totalLessons = 35,
                recommendScore = 92,
                recommendReason = "내신 중심 학습을 원하는 학생에게 최적",
                difficulty = "중하",
                isPersonalized = true,
                subject = Subject.ENG
            ),
            RecommendResponse(
                id = 11,
                platform = Platform.MEGA,
                title = "영어 독해 마스터",
                teacher = "정영어",
                url = "https://example.com/eng2",
                description = "영어 독해 능력 향상을 위한 체계적 접근",
                tag = "독해, 어휘",
                totalLessons = 42,
                recommendScore = 85,
                recommendReason = "이론 중심 학습을 선호하는 학생에게 적합",
                difficulty = "중",
                isPersonalized = true,
                subject = Subject.ENG
            )
        )

        coEvery { 추천데이터소스.postRecommendLectures(영어추천요청) } returns 영어추천결과

        // When - 실행
        val 결과 = 추천리포지토리.postRecommendLectures(영어추천요청)

        // Then - Truth 사용 검증
        assertThat(결과).hasSize(2)
        assertThat(결과).isNotEmpty()

        // 영어 과목 특성 검증
        결과.forEach { 추천 ->
            assertThat(추천.subject).isEqualTo(Subject.ENG)
            assertThat(추천.isPersonalized).isTrue()
            assertThat(추천.recommendScore).isAtLeast(80)
        }

        // 첫 번째 영어 추천 상세 검증
        val 첫번째영어추천 = 결과.first()
        assertThat(첫번째영어추천.title).contains("영어")
        assertThat(첫번째영어추천.title).contains("문법")
        assertThat(첫번째영어추천.teacher).endsWith("영어")
        assertThat(첫번째영어추천.platform).isEqualTo(Platform.ETOOS)
        assertThat(첫번째영어추천.difficulty).isEqualTo("중하")
        assertThat(첫번째영어추천.tag).contains("내신")

        // 두 번째 영어 추천 상세 검증
        val 두번째영어추천 = 결과[1]
        assertThat(두번째영어추천.title).contains("독해")
        assertThat(두번째영어추천.platform).isEqualTo(Platform.MEGA)
        assertThat(두번째영어추천.difficulty).isEqualTo("중")
        assertThat(두번째영어추천.totalLessons).isGreaterThan(첫번째영어추천.totalLessons)

        coVerify { 추천데이터소스.postRecommendLectures(영어추천요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 빈 추천 결과 테스트`() = runTest {
        // Given - 조건에 맞는 강의가 없는 경우
        val 까다로운요청 = RecommendDto(
            grade = "1",
            schoolRank = 9,
            mockRank = 9,
            focus = "수능 중심",
            goal = "빠른 요약 정리",
            styles = listOf("빠른 진도"),
            subject = Subject.HIST
        )

        val 빈결과 = emptyList<RecommendResponse>()

        coEvery { 추천데이터소스.postRecommendLectures(까다로운요청) } returns 빈결과

        // When - 실행
        val 결과 = 추천리포지토리.postRecommendLectures(까다로운요청)

        // Then - Truth로 빈 값 검증
        assertThat(결과).isEmpty()
        assertThat(결과).hasSize(0)
        assertThat(결과).isNotNull()

        coVerify { 추천데이터소스.postRecommendLectures(까다로운요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 추천 실패 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 잘못된요청 = RecommendDto(subject = Subject.MATH)
        val 예외 = RuntimeException("추천 서비스 오류")

        coEvery { 추천데이터소스.postRecommendLectures(잘못된요청) } throws 예외

        // When & Then - Truth 사용
        try {
            추천리포지토리.postRecommendLectures(잘못된요청)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("추천 서비스 오류")
            assertThat(e.message).contains("오류")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 추천데이터소스.postRecommendLectures(잘못된요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 다양한 플랫폼 추천 테스트`() = runTest {
        // Given - 여러 플랫폼의 추천 결과
        val 과탐요청 = RecommendDto(
            grade = "3",
            schoolRank = 2,
            mockRank = 2,
            focus = "수능 중심",
            goal = "실전 문제풀이",
            styles = listOf("실전 중심", "문제풀이 중심"),
            subject = Subject.SCI
        )

        val 다양한플랫폼추천 = listOf(
            RecommendResponse(
                id = 20,
                platform = Platform.MEGA,
                title = "물리 실전 문제풀이",
                teacher = "김물리",
                subject = Subject.SCI,
                recommendScore = 94
            ),
            RecommendResponse(
                id = 21,
                platform = Platform.ETOOS,
                title = "화학 기출 분석",
                teacher = "이화학",
                subject = Subject.SCI,
                recommendScore = 91
            ),
            RecommendResponse(
                id = 22,
                platform = Platform.DAESANG,
                title = "생물 실전 대비",
                teacher = "박생물",
                subject = Subject.SCI,
                recommendScore = 87
            ),
            RecommendResponse(
                id = 23,
                platform = Platform.EBSI,
                title = "지구과학 완성",
                teacher = "최지구",
                subject = Subject.SCI,
                recommendScore = 83
            )
        )

        coEvery { 추천데이터소스.postRecommendLectures(과탐요청) } returns 다양한플랫폼추천

        // When - 실행
        val 결과 = 추천리포지토리.postRecommendLectures(과탐요청)

        // Then - Truth 사용 검증
        assertThat(결과).hasSize(4)

        // 플랫폼 다양성 검증
        val 플랫폼목록 = 결과.map { it.platform }.distinct()
        assertThat(플랫폼목록).hasSize(4)
        assertThat(플랫폼목록).containsExactly(Platform.MEGA, Platform.ETOOS, Platform.DAESANG, Platform.EBSI)

        // 각 플랫폼별 검증
        val 메가추천 = 결과.find { it.platform == Platform.MEGA }!!
        assertThat(메가추천.title).contains("물리")
        assertThat(메가추천.recommendScore).isEqualTo(94)

        val 이투스추천 = 결과.find { it.platform == Platform.ETOOS }!!
        assertThat(이투스추천.title).contains("화학")
        assertThat(이투스추천.recommendScore).isEqualTo(91)

        val 대성추천 = 결과.find { it.platform == Platform.DAESANG }!!
        assertThat(대성추천.title).contains("생물")
        assertThat(대성추천.recommendScore).isEqualTo(87)

        val EBSI추천 = 결과.find { it.platform == Platform.EBSI }!!
        assertThat(EBSI추천.title).contains("지구과학")
        assertThat(EBSI추천.recommendScore).isEqualTo(83)

        // 모든 추천이 과탐 과목인지 확인
        결과.forEach { 추천 ->
            assertThat(추천.subject).isEqualTo(Subject.SCI)
        }

        coVerify { 추천데이터소스.postRecommendLectures(과탐요청) }
    }
}