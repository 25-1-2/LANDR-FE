package com.capston.presentation.ui.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnboardingFinishScreen(onSetupComplete: () -> Unit) {
    Button(onClick = onSetupComplete) {
        Text("온보딩 끝")
    }
}