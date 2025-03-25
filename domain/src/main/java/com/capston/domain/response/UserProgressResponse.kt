package com.capston.domain.response

import com.google.gson.annotations.SerializedName

data class UserProgressResponse(
    @SerializedName("lectureProgress") val lectureProgress: List<LectureProgressResponse> = emptyList(),
    @SerializedName("totalCompletedLessons") val totalCompletedLessons: Int = 0,
    @SerializedName("totalLessons") val totalLessons: Int = 0
)