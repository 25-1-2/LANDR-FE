package com.capston.presentation.ui.onboarding

data class SubjectGrade(
    val subject: String = "",
    val schoolGrade: Int = 0, // 내신 등급 (1-9)
    val mockGrade: Int = 0, // 모의고사 등급 (1-9)
    val isNew: Boolean = false, // 새로 추가된 아이템인지 확인
    val id: String = System.currentTimeMillis().toString() // 고유 ID 추가
)
