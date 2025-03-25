package com.capston.domain.response

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    var todaySchedule: TodayScheduleResponse = TodayScheduleResponse()
)
