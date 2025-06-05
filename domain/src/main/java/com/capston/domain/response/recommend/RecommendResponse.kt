package com.capston.domain.response.recommend

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject

data class RecommendResponse(
    val id: Int = 0,
    val platform: Platform = Platform.DAESANG,
    val title: String = "",
    val teacher: String = "",
    val url: String = "",
    val description: String = "",
    val tag: String = "",
    val totalLessons: Int = 0,
    val recommendScore: Int = 0,
    val recommendReason: String = "",
    val difficulty: String = "",
    val isPersonalized: Boolean = false,
    val subject: Subject = Subject.ENG
)
