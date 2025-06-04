package com.capston.presentation.ui.onboarding

data class SubjectGrade(
    val subject: String = "",
    val schoolGrade: Int = 0, // 내신 등급 (1-9)
    val mockGrade: Int = 0, // 모의고사 등급 (1-9)
    val focus: String = "", // 학습 방향 (과목별)
    val goal: String = "", // 학습 목표 (과목별)
    val styles: List<String> = emptyList(), // 학습 스타일 (과목별)
    val isNew: Boolean = false, // 새로 추가된 아이템인지 확인
    val id: String = System.currentTimeMillis().toString() // 고유 ID 추가
)