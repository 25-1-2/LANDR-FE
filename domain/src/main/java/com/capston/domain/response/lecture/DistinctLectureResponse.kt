package com.capston.domain.response.lecture

data class DistinctLectureResponse(
    val data: LectureResponseDto = LectureResponseDto(),
    val nextCursor: Int = 0,
    val nextCreateAt: String = "",
    val hasNext: Boolean = true
)
