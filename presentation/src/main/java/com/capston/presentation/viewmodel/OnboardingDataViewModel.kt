package com.capston.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.enum_class.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingData(
    val grade: String = "",
    val subjectGrades: List<SubjectGrade> = emptyList()
    // focus, goal, styles는 이제 각 SubjectGrade에 포함되므로 제거
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

    // 각 과목별 개별 스타일을 사용하여 추천 요청 생성
    fun createRecommendRequests(): List<RecommendDto> {
        val data = _onboardingData.value

        // 학년 정보를 grade 형태로 변환
        val gradeNumber = extractGradeNumber(data.grade)

        android.util.Log.d("OnboardingDataViewModel", "전체 과목 데이터 수: ${data.subjectGrades.size}")

        // 모든 유효한 과목에 대해 각각의 학습 설정으로 RecommendDto 생성
        val recommendRequests = data.subjectGrades.mapNotNull { subjectGrade ->
            val isValid = subjectGrade.subject.isNotEmpty() &&
                    subjectGrade.schoolGrade > 0 &&
                    subjectGrade.mockGrade > 0 &&
                    subjectGrade.focus.isNotEmpty() &&
                    subjectGrade.goal.isNotEmpty() &&
                    subjectGrade.styles.isNotEmpty()

            android.util.Log.d("OnboardingDataViewModel", "과목: ${subjectGrade.subject}, 유효성: $isValid")

            if (isValid) {
                // 과목명을 Subject enum으로 변환
                val subject = convertStringToSubject(subjectGrade.subject)

                val recommendDto = RecommendDto(
                    grade = gradeNumber,
                    schoolRank = subjectGrade.schoolGrade, // 내신 등급
                    mockRank = subjectGrade.mockGrade, // 모의고사 등급
                    focus = subjectGrade.focus, // 과목별 학습 방향
                    goal = subjectGrade.goal, // 과목별 학습 목표
                    styles = subjectGrade.styles, // 과목별 학습 스타일
                    subject = subject
                )

                android.util.Log.d("OnboardingDataViewModel", "추천 요청 생성됨: ${subject.label}")
                recommendDto
            } else {
                android.util.Log.d("OnboardingDataViewModel", "유효하지 않은 과목 데이터: ${subjectGrade.subject}")
                null
            }
        }

        android.util.Log.d("OnboardingDataViewModel", "생성된 추천 요청 수: ${recommendRequests.size}")
        return recommendRequests
    }

    // 단일 과목 추천 요청 (하위 호환성을 위해 유지 - 첫 번째 과목 반환)
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
            "농업이해", "농업기초기술", "공업일반", "기초제도", "상업경제", "회계원리", "해양의이해", "수산·해운산업기초", "인간발달", "생활서비스산업의이해" -> Subject.UNIV
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