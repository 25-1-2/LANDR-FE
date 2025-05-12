package com.capston.domain.response.home

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    val todaySchedule: TodayScheduleResponse = TodayScheduleResponse(),
    val dday: DDayResponse = DDayResponse()
)
