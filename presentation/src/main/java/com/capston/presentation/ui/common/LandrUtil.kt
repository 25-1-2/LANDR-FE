package com.capston.presentation.ui.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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