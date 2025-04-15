package com.capston.domain.model

data class MyLecture(
    val planId: Int = 0,
    val lectureTitle: String = "",
    val platform: String = "", // 예: "MEGA", "DAESUNG" 등
    val teacher: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 0,

)