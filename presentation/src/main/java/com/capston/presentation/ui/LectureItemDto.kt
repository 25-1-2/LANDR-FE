package com.capston.presentation.ui

import com.capston.domain.response.enum_class.Platform

data class LectureItemDto(
    val id: Int,
    val title: String,
    val platform: Platform,
    val teacher: String,
    val imageResId: Int,
    val createdAt: String,
    val tag: String,
    val totalLessons: Int
)