package com.capston.domain.response.lecture

data class GetLessonsByLectureIdResponse(
    val lessons: List<LessonByLectureId> = emptyList()
)

data class LessonByLectureId (
    val id: Int = 0,
    val title: String = "",
)