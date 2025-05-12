package com.capston.domain.response.mypage

import com.capston.domain.response.enum_class.Subject

data class SubjectAchievementDto(
    val subject: Subject = Subject.UNIV,
    val startDate: String = "",
    val endDate: String = "",
    val totalLessons: Int = 0,
    val completedLessons: Int = 0
)
