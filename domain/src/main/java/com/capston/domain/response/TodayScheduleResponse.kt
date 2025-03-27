package com.capston.domain.response

import com.capston.domain.response.enum_class.Day

data class TodayScheduleResponse(
    val date: String = "",
    val dayOfWeek: Enum<Day> = Day.MON,
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse>? = null
)
