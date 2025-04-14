package com.capston.domain.request

data class PostPlanDto(
    val lectureId: Int = 0,
    val planType: String = "",
    val startLessonId: Int = 0,
    val endLessonId: Int = 0,
    val studyDayOfWeeks: List<String> = emptyList(),
    val dailyTime: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val playbackSpeed: Double = 1.0
)