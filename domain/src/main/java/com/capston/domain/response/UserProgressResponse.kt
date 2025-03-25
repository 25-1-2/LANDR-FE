package com.capston.domain.response

import com.google.gson.annotations.SerializedName

data class UserProgressResponse(
//    val lectureProgress: List<LectureProgressResponse> = emptyList(),
    @SerializedName("lectureProgress") val lectureProgress: List<String> = emptyList(),
    @SerializedName("totalCompletedLessons") val totalCompletedLessons: Int = 0,
    @SerializedName("totalLessons") val totalLessons: Int = 0
)