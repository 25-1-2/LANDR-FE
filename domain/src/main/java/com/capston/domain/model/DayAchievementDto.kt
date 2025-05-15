package com.capston.domain.model

import com.capston.domain.response.enum_class.DayOfWeek

// 일별 성취 데이터 클래스
data class DayAchievementDto(
    val day: DayOfWeek,
    val isAchieved: Boolean
)