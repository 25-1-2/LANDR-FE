package com.capston.domain.model

data class Lecture(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val platform: String = "",
    val subject: String = "",
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val tag: String = ""
)