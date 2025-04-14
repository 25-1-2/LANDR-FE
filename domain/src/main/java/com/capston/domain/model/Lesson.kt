package com.capston.domain.model

data class Lesson(
    val id: Int = 0,
    val lecture: Lecture = Lecture(),
    val order: Int = 0,
    val title: String = "",
    val duration: Int = 0
)