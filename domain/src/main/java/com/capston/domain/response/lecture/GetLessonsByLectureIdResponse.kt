package com.capston.domain.response.lecture

import com.capston.domain.model.NewPlanLesson

data class GetLessonsByLectureIdResponse (
    val lessons: List<NewPlanLesson> = emptyList()
)