package com.capston.presentation.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LandrUtil {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun formatDateYMDE(dateString: String): String {
            val parsedDate = LocalDate.parse(dateString) // "2025-03-22"
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)")
            return parsedDate.format(formatter)
        }
    }
}