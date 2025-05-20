package com.capston.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray

@Composable
fun LoginScreen(
    onLoginButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.activity_splash_icon_iv),
            contentDescription = "앱 로고",
            modifier = Modifier
                .size(130.dp)
                .padding(start = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.landr_title_iv),
            contentDescription = "앱 타이틀",
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        )
        Text(
            text = "효과적인 인강 학습, LANDR와 함께\nLearn & Run",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = 2f / 3f)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = materialGray,
                    shape = RoundedCornerShape(30.dp)
                )
                .background(color = Transparent, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .clickable {
                    onLoginButtonClick()
                }
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 구글 로고 (왼쪽 고정)
                Image(
                    painter = painterResource(R.drawable.activity_login_google_symbol_iv),
                    contentDescription = "구글 로고",
                    modifier = Modifier.size(24.dp)
                )

                // Spacer를 하나 넣고, 남은 공간에 가운데 정렬된 텍스트
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Google 계정으로 로그인",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}