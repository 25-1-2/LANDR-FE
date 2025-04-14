package com.capston.domain.response

data class CheckResponse(
    val lessonScheduleId: Int = 0,
    var checked: Boolean = false
)