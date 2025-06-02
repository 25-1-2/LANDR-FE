package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CapstonTheme {
                SplashScreen()
            }
        }
    }

    @Preview
    @Composable
    fun SplashScreen(

    ) {
        val alpha = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = Unit) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(500)
            )
            delay(500L)

            Intent(this@SplashActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.let { intent ->
                startActivity(intent)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MainPurple)
        ) {
            // 중앙 텍스트 이미지
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(alpha.value),
                painter = painterResource(R.drawable.landr_title_2_iv),
                contentDescription = "앱 타이틀"
            )

            // 하단 왼쪽 아이콘 이미지
            Image(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .alpha(alpha.value),
                painter = painterResource(R.drawable.activity_splash_icon_iv),
                contentDescription = "앱 아이콘",
                contentScale = ContentScale.Fit
            )
        }
    }
}