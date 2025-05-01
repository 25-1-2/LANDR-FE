package com.capston.presentation.ui

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
fun CustomCheckBox(isChecked: Boolean, onCheckedChange: () -> Unit) {
    IconButton(
        onClick = onCheckedChange,
        modifier = Modifier
            .size(40.dp) // 이미지 버튼 크기 설정
            .padding(end = 16.dp) // 이미지와 텍스트 간의 간격 설정
    ) {
        val imageRes = if (isChecked) R.drawable.home_screen_check_on
        else R.drawable.home_screen_check_off

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Lecture Icon"
        )
    }
}