package com.capston.domain.response.plan

import com.capston.domain.model.DailySchedule

data class GetPlanDetailResponse (
    val planId: Int = 0,
    val lectureTitle: String = "",
    val teacher: String = "",
    val platform: String = "",
    val dailySchedules: List<DailySchedule> = emptyList(),
)