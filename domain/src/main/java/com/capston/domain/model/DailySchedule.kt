package com.capston.domain.model

data class DailySchedule (
    val date: String = "",
    val dayOfWeek: String = "",
    val lessonSchedules: List<LessonSchedule> = emptyList()
)