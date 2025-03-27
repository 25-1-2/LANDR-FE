package com.capston.domain.response

data class LectureProgressResponse(
    val lectureId: Int = 0,
    val lectureName: String = "",
    val completedLessons: Int = 0,
    val totalLessons: Int = 0
)