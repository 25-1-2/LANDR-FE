package com.capston.data.repository.remote.response

data class LectureProgressResponse(
    val lectureId: Int,
    val lectureName: String,
    val completedLessons: Int,
    val totalLessons: Int
)