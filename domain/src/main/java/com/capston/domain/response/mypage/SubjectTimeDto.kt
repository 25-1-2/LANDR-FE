package com.capston.domain.response.mypage

import com.capston.domain.response.enum_class.Subject

data class SubjectTimeDto(
    val subject: Subject = Subject.UNIV,
    val totalMinutes: Int = 0,
    val percentage: Double = 0.0,
)
