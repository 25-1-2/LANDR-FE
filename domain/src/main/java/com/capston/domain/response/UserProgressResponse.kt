package com.capston.domain.response

data class UserProgressResponse(
    val lectureProgress: List<LectureProgressResponse> = emptyList(),
    val totalCompletedLessons: Int = 0,
    val totalLessons: Int = 0
)