package com.capston.domain.response

import com.google.gson.annotations.SerializedName

//data class DistinctHomeIdResponse(
//    val userProgress: UserProgressResponse = UserProgressResponse(),
//    val todaySchedule: TodayScheduleResponse = TodayScheduleResponse()
//)

data class DistinctHomeIdResponse(
    @SerializedName("userProgress") val userProgress: UserProgressResponse?,
    @SerializedName("todaySchedule") val todaySchedule: TodayScheduleResponse?
)
