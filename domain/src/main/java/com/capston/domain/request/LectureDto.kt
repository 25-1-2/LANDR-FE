package com.capston.domain.request

data class LectureDto (
    val search: String = "",
    val cursorLectureId: String = "",
    val cursorCreatedAt: String = "",
    val offset: String = "",
)