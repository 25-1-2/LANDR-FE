package com.capston.domain.response.plan

import com.capston.domain.response.enum_class.Platform

data class GetPlanLectureRoomResponse(
    val planId: Int = 0,
    val lectureTitle: String = "",
    val platform: Platform = Platform.DAESANG, // 예: "MEGA", "DAESUNG" 등
    val teacher: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,
)