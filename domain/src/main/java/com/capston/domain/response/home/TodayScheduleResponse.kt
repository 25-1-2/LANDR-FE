package com.capston.domain.response.home

import com.capston.domain.response.enum_class.Day

data class TodayScheduleResponse(
    val date: String = "",
    val dayOfWeek: Day = Day.MON,
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse>? = null
)
