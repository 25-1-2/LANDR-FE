package com.capston.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.capston.domain.manager.LoadingStateManager

@Composable
fun LoadingIndicator(
    loadingStateManager: LoadingStateManager,
    backgroundColor: Color = Color.Transparent,
    indicatorSize: Float = 50f
) {
    val isLoading by loadingStateManager.isLoading.collectAsState()

    if (isLoading) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset("loading_dot.json") // assets 폴더 내 Lottie JSON 파일
        )
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(indicatorSize.dp)
            )
        }
    }
}
