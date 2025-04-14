package com.capston.domain.response.home

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    var todaySchedule: TodayScheduleResponse = TodayScheduleResponse()
)
