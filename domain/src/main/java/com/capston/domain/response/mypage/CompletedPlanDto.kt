package com.capston.domain.response.mypage

import com.capston.domain.response.enum_class.Platform

data class CompletedPlanDto(
    val planId: Int = 0,
    val lectureTitle: String = "",
    val teacher: String = "",
    val platform: Platform = Platform.DAESANG,
)