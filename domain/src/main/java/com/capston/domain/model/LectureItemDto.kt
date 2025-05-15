package com.capston.domain.model

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject

data class LectureItemDto(
    val id: Int,
    val title: String,
    val platform: Platform,
    val teacher: String,
    val subject: Subject,
    val createdAt: String,
    val tag: String,
    val totalLessons: Int
)