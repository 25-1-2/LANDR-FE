package com.capston.domain.response.daily_schedule

import com.capston.domain.response.enum_class.Day
import com.capston.domain.response.home.LessonScheduleResponse

data class DailyScheduleResponse (
    val date: String = "",
    val dayOfWeek: Day = Day.MON,
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse>? = null
)