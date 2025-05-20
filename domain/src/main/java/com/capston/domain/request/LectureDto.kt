package com.capston.domain.request

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject

data class LectureDto (
    val search: String = "",
    val cursorLectureId: String = "",
    val cursorCreatedAt: String? = "",
    val offset: String = "10",
    val platform: Platform? = null,
    val subject: Subject? = null
)