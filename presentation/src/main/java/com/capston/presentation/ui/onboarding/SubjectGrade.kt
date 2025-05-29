package com.capston.presentation.ui.onboarding

data class SubjectGrade(
    val subject: String = "",
    val grade: Int = 0,
    val gradeType: String = "내신", // "내신" 또는 "모의고사"
    val isNew: Boolean = false, // 새로 추가된 아이템인지 확인
    val id: String = System.currentTimeMillis().toString() // 고유 ID 추가
)
