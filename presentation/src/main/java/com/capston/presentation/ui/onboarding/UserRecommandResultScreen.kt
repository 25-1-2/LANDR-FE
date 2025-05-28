package com.capston.presentation.ui.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UserRecommandResultScreen(onSetupComplete: () -> Unit) {
    Button(onClick = onSetupComplete) {
        Text("추천 결과")
    }
}