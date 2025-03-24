package com.capston.domain.response

data class LectureProgressResponse(
    val lectureId: Int,
    val lectureName: String,
    val completedLessons: Int,
    val totalLessons: Int
)