package com.capston.domain.response.lecture

data class DistinctLectureResponse(
    val data: List<LectureItemDto>?,
    val nextCursor: Int = 0,
    val nextCreatedAt: String = "",
    val hasNext: Boolean = true
)
