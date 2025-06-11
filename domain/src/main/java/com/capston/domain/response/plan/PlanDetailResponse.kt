package com.capston.domain.response.plan

data class PlanDetailResponse (
    val planId: Int = 0,
    val lectureId: Int = 0,
    val lectureTitle: String = "",
    val teacher: String = "",
    val platform: String = "",
    val planType: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val dailyTime: Int = 0,
    val playbackSpeed: Int = 0,
    val dailySchedules: List<PlanDetailDailySchedule> = emptyList(),
)

data class PlanDetailDailySchedule (
    val date: String = "",
    val dayOfWeek: String = "",
    val lessonSchedules: List<PlanDetailLessonSchedule> = emptyList()
)

data class PlanDetailLessonSchedule (
    val id: Int = 0,
    val lessonTitle: String = "",
    val lectureName: String = "",
    val adjustedDuration: Int = 0,
    val displayOrder: Int = 0,
    val completed: Boolean = false
)