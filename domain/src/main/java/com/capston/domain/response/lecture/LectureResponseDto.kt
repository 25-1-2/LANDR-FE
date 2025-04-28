package com.capston.domain.response.lecture

import com.capston.domain.response.enum_class.Platform

data class LectureResponseDto(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val platform: Platform = Platform.DAESANG,
    val subject: String = "",
    val createdAt: String = ""
)
