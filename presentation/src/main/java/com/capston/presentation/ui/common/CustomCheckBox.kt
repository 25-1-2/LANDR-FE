package com.capston.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.capston.presentation.R

@Composable
fun CustomCheckBox(
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    isReadOnly: Boolean = false  // 추가: 다른 사람 계획 여부
) {
    IconButton(
        onClick = if (isReadOnly) { {} } else onCheckedChange,  // 읽기 전용일 때 클릭 비활성화
        modifier = Modifier
            .size(40.dp)
            .padding(end = 16.dp)
    ) {
        val imageRes = when {
            isReadOnly && isChecked -> R.drawable.home_screen_check_on_gray  // 회색 체크 이미지
            isReadOnly && !isChecked -> R.drawable.home_screen_check_off   // 회색 미체크 (또는 다른 회색 이미지)
            isChecked -> R.drawable.home_screen_check_on
            else -> R.drawable.home_screen_check_off
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Lecture Icon"
        )
    }
}