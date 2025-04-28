package com.capston.domain.response.lecture

data class DistinctLectureResponse(
    val data: List<LectureResponseDto>?,
    val nextCursor: Int = 0,
    val nextCreateAt: String = "",
    val hasNext: Boolean = true
)
