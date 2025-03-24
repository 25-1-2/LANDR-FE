package com.capston.data.repository.remote.response

data class UserProgressResponse(
    val lectureProgress: List<LectureProgressResponse> = emptyList(),
    val totalCompletedLessons: Int = 0,
    val totalLessons: Int = 0
)