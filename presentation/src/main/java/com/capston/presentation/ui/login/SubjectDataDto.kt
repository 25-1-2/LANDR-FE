package com.capston.presentation.ui.login

import androidx.compose.ui.graphics.Color
import com.capston.domain.response.enum_class.Subject

// 과목 데이터 클래스
data class SubjectDataDto(
    val subject: Subject,
    val hours: Int,
    val color: Color
)