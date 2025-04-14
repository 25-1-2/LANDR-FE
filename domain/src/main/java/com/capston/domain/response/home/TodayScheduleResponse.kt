package com.capston.domain.response.home

import com.capston.domain.response.enum_class.DayOfWeek

data class TodayScheduleResponse(
    val date: String = "",
    val dayOfWeek: DayOfWeek = DayOfWeek.MON,
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse>? = null
)
