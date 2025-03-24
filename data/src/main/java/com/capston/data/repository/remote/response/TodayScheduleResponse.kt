package com.capston.data.repository.remote.response

data class TodayScheduleResponse(
    val date: String = "",
    val dayOfWeek: String = "",
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val lessonSchedules: List<LessonScheduleResponse> = emptyList()
)
