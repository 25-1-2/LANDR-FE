package com.capston.domain.response

data class TodayScheduleResponse(
    val date: String = "",
    val dayOfWeek: String = "",
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse>? = null
)
