package com.capston.domain.request

data class PostNewPeriodPlanDto(
    val lectureId: Int = 0,
    val planType: String = "",
    val startLessonId: Int = 0,
    val endLessonId: Int = 0,
    val studyDayOfWeeks: List<String> = emptyList(),
    val dailyTime: Int = 0, // 시간으로 계획하기에서 사용
    val startDate: String = "",
    val endDate: String = "",
    val playbackSpeed: Double = 1.0
)