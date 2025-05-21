package com.capston.domain.response.plan

data class GetPlanDetailResponse (
    val planId: Int = 0,
    val lectureTitle: String = "",
    val teacher: String = "",
    val platform: String = "",
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