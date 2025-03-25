package com.capston.domain.response

import com.google.gson.annotations.SerializedName

data class TodayScheduleResponse(
    @SerializedName("date") val date: String = "",
    @SerializedName("dayOfWeek") val dayOfWeek: String = "",
    @SerializedName("totalLessons") val totalLessons: Int = 0,
    @SerializedName("totalDuration") val totalDuration: Int = 0,
    @SerializedName("lessonSchedules") val lessonSchedules: List<LessonScheduleResponse> = emptyList()
)
