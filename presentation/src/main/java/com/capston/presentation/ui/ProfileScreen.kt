package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R

// 마이페이지 스크린
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp), // 최상단 + 왼쪽 여백
            verticalAlignment = Alignment.CenterVertically // 세로 가운데 정렬
        ) {
            Image(
                painter = painterResource(R.drawable.screen_profile_basic_profile_iv),
                contentDescription = "기본 프로필",
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Text(
                text = stringResource(R.string.mypage_title),
                fontSize = 20.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = "조은채님",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}