package com.capston.presentation.ui

data class LectureItemDto (
    val id: Int,
    val title: String,
    val platform: String,
    val teacher: String,
    val imageResId: Int,
    val createdAt: String
)