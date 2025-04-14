package com.capston.domain.response.home

data class LessonScheduleResponse(
    val id: Int = 0,
    val lessonTitle: String = "",
    val lectureName: String = "",
    val adjustedDuration: Int = 0,
    val displayOrder: Int = 0,
    val completed: Boolean = false
)
