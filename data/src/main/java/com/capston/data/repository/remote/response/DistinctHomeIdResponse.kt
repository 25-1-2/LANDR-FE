package com.capston.data.repository.remote.response

data class DistinctHomeIdResponse(
    val userProgress: UserProgressResponse = UserProgressResponse(),
    val todaySchedule: TodayScheduleResponse = TodayScheduleResponse()
)