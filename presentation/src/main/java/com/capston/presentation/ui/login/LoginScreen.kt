package com.capston.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.capston.presentation.R
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
            textAlign = TextAlign.Center
        )

        Image(
            painter = painterResource(R.drawable.screen_login_google_iv),
            contentDescription = "로그인",
            modifier = Modifier.clickable {
                onLoginButtonClick()
            }
                .size(200.dp),
        )
    }
}