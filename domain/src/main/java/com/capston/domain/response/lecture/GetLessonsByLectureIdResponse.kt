package com.capston.domain.response.lecture

import com.capston.domain.model.Lesson

data class GetLessonsByLectureIdResponse(
    val lessons: List<Lesson> = emptyList()
)