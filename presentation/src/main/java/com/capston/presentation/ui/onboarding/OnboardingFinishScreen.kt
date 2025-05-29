package com.capston.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capston.presentation.R
import com.capston.presentation.theme.textGray

@Composable
fun OnboardingFinishScreen(
    onCompleteOnboarding: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 LANDR 로고
        Image(
            painter = painterResource(R.drawable.landr_title_iv),
            contentDescription = "과목명",
            modifier = Modifier
                .padding(top = 80.dp, start = 35.dp) // 원하는 만큼 아래로 내림
                .size(80.dp)
                .align(Alignment.TopStart)
        )

        // 텍스트 및 이미지 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp) // 로고보다 아래에 위치하도록
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "조은채님,\n맞춤형 인강 학습 준비가 완료됐어요!",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "LANDR가 완강까지 도와드릴게요:)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = textGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(100.dp))

            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset("firework.json")
            )
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }

        // 하단 버튼
        Button(
            onClick = onCompleteOnboarding,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("완료")
        }
    }
}