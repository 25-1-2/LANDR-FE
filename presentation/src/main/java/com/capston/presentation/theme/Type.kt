package com.capston.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.capston.presentation.R

val pretendard = FontFamily(
    Font(R.font.pretendardbold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.pretendardmedium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.pretendardregular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.pretendardsemibold, FontWeight.SemiBold, FontStyle.Normal)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)