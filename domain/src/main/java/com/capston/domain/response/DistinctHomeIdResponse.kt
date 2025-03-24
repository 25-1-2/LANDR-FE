package com.capston.domain.response

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    val todaySchedule: TodayScheduleResponse = TodayScheduleResponse()
)