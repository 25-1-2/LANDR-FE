package com.capston.domain.response.home

data class LectureProgressResponse(
    val planId: Int = 0,
    var lectureAlias: String ="",
    val lectureName: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 0
)