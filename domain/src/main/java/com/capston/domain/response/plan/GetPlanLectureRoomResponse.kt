package com.capston.domain.response.plan

data class GetPlanLectureRoomResponse (
    val planId: Int = 0,
    var lectureTitle: String = "",
    var teacher: String = "",
    var platform: String = "",
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
)