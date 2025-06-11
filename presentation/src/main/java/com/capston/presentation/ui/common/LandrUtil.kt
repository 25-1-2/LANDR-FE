package com.capston.presentation.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.capston.domain.response.enum_class.Subject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

val Subject.bgColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFFD8D8)   // 연한 핑크
        Subject.ENG -> Color(0xFFC5D9FF)   // 연한 하늘색
        Subject.MATH -> Color(0xFFD3F7D3)  // 연한 민트
        Subject.SOC -> Color(0xFFFFF4C6)   // 연한 노랑
        Subject.SCI -> Color(0xFFC1E8F7)   // 연한 파랑
        Subject.HIST -> Color(0xFFE1B5E8)  // 연한 보라
        Subject.UNIV -> Color(0xFFC7F6F9)  // 연한 청록
        Subject.LANG2 -> Color(0xFFFFD8E6) // 연한 분홍
        Subject.VOC -> Color(0xFFF0F0F0)   // 연한 회색
    }

val Subject.borderColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFF6B6B)   // 부드러운 빨강
        Subject.ENG -> Color(0xFF5D9CFF)    // 부드러운 파랑
        Subject.MATH -> Color(0xFF5BBF63)   // 부드러운 초록
        Subject.SOC -> Color(0xFFFFC046)    // 부드러운 노랑
        Subject.SCI -> Color(0xFF1EB0D2)    // 부드러운 하늘색
        Subject.HIST -> Color(0xFF9E4FB0)   // 부드러운 보라
        Subject.UNIV -> Color(0xFF00A7B4)   // 부드러운 청록
        Subject.LANG2 -> Color(0xFFF08C8C)  // 부드러운 분홍
        Subject.VOC -> Color(0xFFB0B0B0)    // 부드러운 회색
    }

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateYMD(millis: Long?): String {
    return if (millis != null) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date(millis))
    } else {
        ""
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateYMDE(dateString: String): String {
    val parsedDate = LocalDate.parse(dateString) // "2025-03-22"
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E)")
    return parsedDate.format(formatter)
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        onClick()
    }
}