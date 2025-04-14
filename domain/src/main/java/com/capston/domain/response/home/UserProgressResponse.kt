package com.capston.domain.response.home

data class UserProgressResponse(
    var lectureProgress: List<LectureProgressResponse> = emptyList(),
    val totalCompletedLessons: Int = 0,
    val totalLessons: Int = 0
)