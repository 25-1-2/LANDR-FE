package com.capston.domain.response

import com.google.gson.annotations.SerializedName

data class TodayScheduleResponse(
//    val date: String = "",
//    val dayOfWeek: String = "",
//    val totalLessons: Int = 0,
//    val totalDuration: Int = 0,
//    val lessonSchedules: List<LessonScheduleResponse> = emptyList()
    @SerializedName("someField") val someField: String? = null
)
