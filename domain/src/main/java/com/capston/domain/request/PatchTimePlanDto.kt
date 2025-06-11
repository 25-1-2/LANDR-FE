package com.capston.domain.request

data class PatchTimePlanDto (
    val dailyTime: Int = 0,
    val playbackSpeed: Double = 0.0
)