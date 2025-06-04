package com.capston.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.enum_class.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingData(
    val grade: String = "",
    val subjectGrades: List<SubjectGrade> = emptyList(),
    val focus: String = "", // 학습 방향
    val goal: String = "", // 학습 목표
    val styles: List<String> = emptyList() // 학습 스타일
)

class OnboardingDataViewModel : ViewModel() {
    private val _onboardingData = MutableStateFlow(OnboardingData())
    val onboardingData: StateFlow<OnboardingData> = _onboardingData.asStateFlow()

    fun updateGrade(grade: String) {
        _onboardingData.value = _onboardingData.value.copy(grade = grade)
    }

    fun updateSubjectGrades(subjectGrades: List<SubjectGrade>) {
        _onboardingData.value = _onboardingData.value.copy(subjectGrades = subjectGrades)
    }

    fun updateLearningPreferences(focus: String, goal: String, styles: List<String>) {
        _onboardingData.value = _onboardingData.value.copy(
            focus = focus,
            goal = goal,
            styles = styles
        )
    }

    // 모든 과목에 대한 추천 요청 생성 (수정된 부분)
    fun createRecommendRequests(): List<RecommendDto> {
        val data = _onboardingData.value

        // 학년 정보를 grade 형태로 변환
        val gradeNumber = extractGradeNumber(data.grade)

        // 모든 유효한 과목에 대해 RecommendDto 생성
        return data.subjectGrades.mapNotNull { subjectGrade ->
            if (subjectGrade.subject.isNotEmpty() &&
                subjectGrade.schoolGrade > 0 &&
                subjectGrade.mockGrade > 0) {

                // 과목명을 Subject enum으로 변환
                val subject = convertStringToSubject(subjectGrade.subject)

                RecommendDto(
                    grade = gradeNumber,
                    schoolRank = subjectGrade.schoolGrade, // 내신 등급
                    mockRank = subjectGrade.mockGrade, // 모의고사 등급
                    focus = data.focus,
                    goal = data.goal,
                    styles = data.styles,
                    subject = subject
                )
            } else {
                null
            }
        }
    }

    // 단일 과목 추천 요청 (하위 호환성을 위해 유지)
    fun createRecommendRequest(): RecommendDto? {
        return createRecommendRequests().firstOrNull()
    }

    private fun convertStringToSubject(subjectName: String): Subject {
        return when (subjectName) {
            "국어", "언어와매체", "화법과작문" -> Subject.KOR
            "영어", "영어Ⅰ", "영어Ⅱ", "영어독해와작문" -> Subject.ENG
            "수학", "수학Ⅰ", "수학Ⅱ", "미적분", "확률과통계", "기하" -> Subject.MATH
            "한국사" -> Subject.HIST
            "물리학Ⅰ", "물리학Ⅱ", "화학Ⅰ", "화학Ⅱ", "생명과학Ⅰ", "생명과학Ⅱ", "지구과학Ⅰ", "지구과학Ⅱ" -> Subject.SCI
            "한국지리", "세계지리", "동아시아사", "세계사", "생활과윤리", "윤리와사상", "정치와법", "경제", "사회·문화" -> Subject.SOC
            else -> Subject.UNIV
        }
    }

    private fun extractGradeNumber(gradeString: String): String {
        return when (gradeString) {
            "고등학교 1학년" -> "1"
            "고등학교 2학년" -> "2"
            "고등학교 3학년" -> "3"
            "N수 / 그외" -> "N"
            else -> "1"
        }
    }
}