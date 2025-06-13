package com.capston.domain.response.lecture

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject

data class LectureItemDto(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val platform: Platform = Platform.DAESANG,
    val subject: Subject = Subject.UNIV,
    val createdAt: String = "",
    val tag: String = "",
    val totalLessons: Int = 0
)
