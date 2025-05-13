package com.capston.domain.response.home

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    val todaySchedule: TodayScheduleResponse = TodayScheduleResponse(),
    val weeklyAchievement: WeeklyAchievement = WeeklyAchievement(),
    val dday: DDayResponse = DDayResponse()
)
