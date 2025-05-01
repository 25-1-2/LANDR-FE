package com.capston.domain.response.lecture

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject

data class LectureResponseDto(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val platform: Platform = Platform.DAESANG,
    val subject: Subject = Subject.UNIV,
    val createdAt: String = ""
)
