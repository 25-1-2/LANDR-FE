package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.enum_class.Subject
import com.capston.presentation.ui.onboarding.OnboardingDataViewModel
import com.capston.presentation.ui.onboarding.SubjectGrade
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class OnboardingDataViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: OnboardingDataViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OnboardingDataViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateGrade should update grade in onboarding data`() = runTest {
        // Given
        val grade = "고등학교 3학년"

        // When
        viewModel.updateGrade(grade)
        advanceUntilIdle()

        // Then
        val result = viewModel.onboardingData.first()
        assertThat(result.grade).isEqualTo(grade)
    }

    @Test
    fun `updateSubjectGrades should update subject grades in onboarding data`() = runTest {
        // Given
        val subjectGrades = listOf(
            SubjectGrade(
                subject = "수학",
                schoolGrade = 2,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = listOf("체계적 학습", "문제풀이 중심"),
                id = "1"
            ),
            SubjectGrade(
                subject = "영어",
                schoolGrade = 3,
                mockGrade = 2,
                focus = "내신 중심",
                goal = "개념 정리",
                styles = listOf("반복 학습"),
                id = "2"
            )
        )

        // When
        viewModel.updateSubjectGrades(subjectGrades)
        advanceUntilIdle()

        // Then
        val result = viewModel.onboardingData.first()
        assertThat(result.subjectGrades).hasSize(2)
        assertThat(result.subjectGrades).isEqualTo(subjectGrades)
        assertThat(result.subjectGrades.first().subject).isEqualTo("수학")
        assertThat(result.subjectGrades.last().subject).isEqualTo("영어")
    }

    @Test
    fun `createRecommendRequests should create multiple recommend requests for valid subjects`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 3학년")

        val subjectGrades = listOf(
            SubjectGrade(
                subject = "수학",
                schoolGrade = 2,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = listOf("체계적 학습", "문제풀이 중심"),
                id = "1"
            ),
            SubjectGrade(
                subject = "영어",
                schoolGrade = 3,
                mockGrade = 2,
                focus = "내신 중심",
                goal = "개념 정리",
                styles = listOf("반복 학습"),
                id = "2"
            ),
            SubjectGrade(
                subject = "국어",
                schoolGrade = 1,
                mockGrade = 1,
                focus = "균형 잡힌 학습",
                goal = "실전 문제풀이",
                styles = listOf("이론 중심", "실전 중심"),
                id = "3"
            )
        )

        viewModel.updateSubjectGrades(subjectGrades)
        advanceUntilIdle()

        // When
        val recommendRequests = viewModel.createRecommendRequests()

        // Then
        assertThat(recommendRequests).hasSize(3)

        // 수학 과목 검증
        val mathRequest = recommendRequests.find { it.subject == Subject.MATH }
        assertThat(mathRequest).isNotNull()
        mathRequest?.let {
            assertThat(it.grade).isEqualTo("3")
            assertThat(it.schoolRank).isEqualTo(2)
            assertThat(it.mockRank).isEqualTo(3)
            assertThat(it.focus).isEqualTo("수능 중심")
            assertThat(it.goal).isEqualTo("기출 분석")
            assertThat(it.styles).containsExactly("체계적 학습", "문제풀이 중심")
        }

        // 영어 과목 검증
        val engRequest = recommendRequests.find { it.subject == Subject.ENG }
        assertThat(engRequest).isNotNull()
        engRequest?.let {
            assertThat(it.grade).isEqualTo("3")
            assertThat(it.schoolRank).isEqualTo(3)
            assertThat(it.mockRank).isEqualTo(2)
            assertThat(it.focus).isEqualTo("내신 중심")
            assertThat(it.goal).isEqualTo("개념 정리")
            assertThat(it.styles).containsExactly("반복 학습")
        }

        // 국어 과목 검증
        val korRequest = recommendRequests.find { it.subject == Subject.KOR }
        assertThat(korRequest).isNotNull()
        korRequest?.let {
            assertThat(it.grade).isEqualTo("3")
            assertThat(it.schoolRank).isEqualTo(1)
            assertThat(it.mockRank).isEqualTo(1)
            assertThat(it.focus).isEqualTo("균형 잡힌 학습")
            assertThat(it.goal).isEqualTo("실전 문제풀이")
            assertThat(it.styles).containsExactly("이론 중심", "실전 중심")
        }
    }

    @Test
    fun `createRecommendRequests should filter out invalid subjects`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 2학년")

        val subjectGrades = listOf(
            // 유효한 과목
            SubjectGrade(
                subject = "수학",
                schoolGrade = 2,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = listOf("체계적 학습"),
                id = "1"
            ),
            // 무효한 과목 (subject가 비어있음)
            SubjectGrade(
                subject = "",
                schoolGrade = 2,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = listOf("체계적 학습"),
                id = "2"
            ),
            // 무효한 과목 (schoolGrade가 0)
            SubjectGrade(
                subject = "영어",
                schoolGrade = 0,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = listOf("체계적 학습"),
                id = "3"
            ),
            // 무효한 과목 (styles가 비어있음)
            SubjectGrade(
                subject = "국어",
                schoolGrade = 2,
                mockGrade = 3,
                focus = "수능 중심",
                goal = "기출 분석",
                styles = emptyList(),
                id = "4"
            )
        )

        viewModel.updateSubjectGrades(subjectGrades)
        advanceUntilIdle()

        // When
        val recommendRequests = viewModel.createRecommendRequests()

        // Then
        assertThat(recommendRequests).hasSize(1) // 유효한 과목 1개만
        assertThat(recommendRequests.first().subject).isEqualTo(Subject.MATH)
    }

    @Test
    fun `createRecommendRequest should return first valid request for backward compatibility`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 1학년")

        val subjectGrades = listOf(
            SubjectGrade(
                subject = "물리학Ⅰ",
                schoolGrade = 3,
                mockGrade = 4,
                focus = "수능 중심",
                goal = "개념 정리",
                styles = listOf("이론 중심"),
                id = "1"
            )
        )

        viewModel.updateSubjectGrades(subjectGrades)
        advanceUntilIdle()

        // When
        val recommendRequest = viewModel.createRecommendRequest()

        // Then
        assertThat(recommendRequest).isNotNull()
        recommendRequest?.let {
            assertThat(it.grade).isEqualTo("1")
            assertThat(it.subject).isEqualTo(Subject.SCI)
            assertThat(it.schoolRank).isEqualTo(3)
            assertThat(it.mockRank).isEqualTo(4)
            assertThat(it.focus).isEqualTo("수능 중심")
            assertThat(it.goal).isEqualTo("개념 정리")
            assertThat(it.styles).containsExactly("이론 중심")
        }
    }

    @Test
    fun `convertStringToSubject should map subject names correctly`() = runTest {
        // Given & When & Then
        viewModel.updateGrade("고등학교 3학년")

        val testCases = mapOf(
            // 국어 영역
            "국어" to Subject.KOR,
            "언어와매체" to Subject.KOR,
            "화법과작문" to Subject.KOR,

            // 영어 영역
            "영어" to Subject.ENG,
            "영어Ⅰ" to Subject.ENG,
            "영어Ⅱ" to Subject.ENG,
            "영어독해와작문" to Subject.ENG,

            // 수학 영역
            "수학" to Subject.MATH,
            "수학Ⅰ" to Subject.MATH,
            "수학Ⅱ" to Subject.MATH,
            "미적분" to Subject.MATH,
            "확률과통계" to Subject.MATH,
            "기하" to Subject.MATH,

            // 한국사
            "한국사" to Subject.HIST,

            // 과학탐구
            "물리학Ⅰ" to Subject.SCI,
            "물리학Ⅱ" to Subject.SCI,
            "화학Ⅰ" to Subject.SCI,
            "화학Ⅱ" to Subject.SCI,
            "생명과학Ⅰ" to Subject.SCI,
            "생명과학Ⅱ" to Subject.SCI,
            "지구과학Ⅰ" to Subject.SCI,
            "지구과학Ⅱ" to Subject.SCI,

            // 사회탐구
            "한국지리" to Subject.SOC,
            "세계지리" to Subject.SOC,
            "동아시아사" to Subject.SOC,
            "세계사" to Subject.SOC,
            "생활과윤리" to Subject.SOC,
            "윤리와사상" to Subject.SOC,
            "정치와법" to Subject.SOC,
            "경제" to Subject.SOC,
            "사회·문화" to Subject.SOC,

            // 직업탐구 -> UNIV
            "농업이해" to Subject.UNIV,
            "공업일반" to Subject.UNIV,
            "상업경제" to Subject.UNIV,

            // 기타/알 수 없음 -> UNIV
            "알수없는과목" to Subject.UNIV
        )

        testCases.forEach { (subjectName, expectedSubject) ->
            val subjectGrade = SubjectGrade(
                subject = subjectName,
                schoolGrade = 2,
                mockGrade = 2,
                focus = "수능 중심",
                goal = "개념 정리",
                styles = listOf("체계적 학습"),
                id = "test"
            )

            viewModel.updateSubjectGrades(listOf(subjectGrade))
            advanceUntilIdle()

            val requests = viewModel.createRecommendRequests()
            assertThat(requests).hasSize(1)
            assertThat(requests.first().subject).isEqualTo(expectedSubject)
        }
    }

    @Test
    fun `extractGradeNumber should convert grade strings correctly`() = runTest {
        // Given & When & Then
        val testCases = mapOf(
            "고등학교 1학년" to "1",
            "고등학교 2학년" to "2",
            "고등학교 3학년" to "3",
            "N수 / 그외" to "N",
            "알수없는학년" to "1" // 기본값
        )

        testCases.forEach { (gradeString, expectedGrade) ->
            viewModel.updateGrade(gradeString)

            val subjectGrade = SubjectGrade(
                subject = "수학",
                schoolGrade = 2,
                mockGrade = 2,
                focus = "수능 중심",
                goal = "개념 정리",
                styles = listOf("체계적 학습"),
                id = "test"
            )

            viewModel.updateSubjectGrades(listOf(subjectGrade))
            advanceUntilIdle()

            val requests = viewModel.createRecommendRequests()
            assertThat(requests).hasSize(1)
            assertThat(requests.first().grade).isEqualTo(expectedGrade)
        }
    }

    @Test
    fun `createRecommendRequests should handle empty subject grades`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 3학년")
        viewModel.updateSubjectGrades(emptyList())
        advanceUntilIdle()

        // When
        val recommendRequests = viewModel.createRecommendRequests()

        // Then
        assertThat(recommendRequests).isEmpty()
    }

    @Test
    fun `createRecommendRequest should return null when no valid subjects`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 3학년")
        viewModel.updateSubjectGrades(emptyList())
        advanceUntilIdle()

        // When
        val recommendRequest = viewModel.createRecommendRequest()

        // Then
        assertThat(recommendRequest).isNull()
    }

    @Test
    fun `updateGrade with different grades should update correctly`() = runTest {
        // Given
        val grades = listOf(
            "고등학교 1학년",
            "고등학교 2학년",
            "고등학교 3학년",
            "N수 / 그외"
        )

        grades.forEach { grade ->
            // When
            viewModel.updateGrade(grade)
            advanceUntilIdle()

            // Then
            val result = viewModel.onboardingData.first()
            assertThat(result.grade).isEqualTo(grade)
        }
    }

    @Test
    fun `createRecommendRequests should handle all grade combinations`() = runTest {
        // Given
        val subjectGrade = SubjectGrade(
            subject = "수학",
            schoolGrade = 5,
            mockGrade = 9,
            focus = "균형 잡힌 학습",
            goal = "빠른 요약 정리",
            styles = listOf("빠른 진도", "반복 학습"),
            id = "1"
        )

        val gradeTestCases = listOf(
            "고등학교 1학년" to "1",
            "고등학교 2학년" to "2",
            "고등학교 3학년" to "3",
            "N수 / 그외" to "N"
        )

        gradeTestCases.forEach { (gradeString, expectedGrade) ->
            // When
            viewModel.updateGrade(gradeString)
            viewModel.updateSubjectGrades(listOf(subjectGrade))
            advanceUntilIdle()

            val requests = viewModel.createRecommendRequests()

            // Then
            assertThat(requests).hasSize(1)
            assertThat(requests.first().grade).isEqualTo(expectedGrade)
            assertThat(requests.first().schoolRank).isEqualTo(5)
            assertThat(requests.first().mockRank).isEqualTo(9)
        }
    }

    @Test
    fun `createRecommendRequests should handle extreme grade values`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 3학년")

        val extremeSubjectGrade = SubjectGrade(
            subject = "수학",
            schoolGrade = 9, // 최저 등급
            mockGrade = 1,   // 최고 등급
            focus = "수능 중심",
            goal = "개념 정리",
            styles = listOf("체계적 학습"),
            id = "1"
        )

        viewModel.updateSubjectGrades(listOf(extremeSubjectGrade))
        advanceUntilIdle()

        // When
        val requests = viewModel.createRecommendRequests()

        // Then
        assertThat(requests).hasSize(1)
        assertThat(requests.first().schoolRank).isEqualTo(9)
        assertThat(requests.first().mockRank).isEqualTo(1)
    }

    @Test
    fun `createRecommendRequests should handle multiple styles correctly`() = runTest {
        // Given
        viewModel.updateGrade("고등학교 2학년")

        val multiStyleSubject = SubjectGrade(
            subject = "영어",
            schoolGrade = 3,
            mockGrade = 3,
            focus = "내신 중심",
            goal = "실전 문제풀이",
            styles = listOf("체계적 학습", "빠른 진도", "반복 학습", "실전 중심", "이론 중심", "문제풀이 중심"),
            id = "1"
        )

        viewModel.updateSubjectGrades(listOf(multiStyleSubject))
        advanceUntilIdle()

        // When
        val requests = viewModel.createRecommendRequests()

        // Then
        assertThat(requests).hasSize(1)
        assertThat(requests.first().styles).hasSize(6)
        assertThat(requests.first().styles).containsExactly(
            "체계적 학습", "빠른 진도", "반복 학습", "실전 중심", "이론 중심", "문제풀이 중심"
        )
    }

    @Test
    fun `onboardingData should have initial empty state`() = runTest {
        // When
        val initialData = viewModel.onboardingData.first()

        // Then
        assertThat(initialData.grade).isEmpty()
        assertThat(initialData.subjectGrades).isEmpty()
    }
}