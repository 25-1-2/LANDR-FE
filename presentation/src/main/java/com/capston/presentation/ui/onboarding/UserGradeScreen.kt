package com.capston.presentation.ui.onboarding

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UserGradeScreen(onSetupComplete: () -> Unit) {
    Button(onClick = onSetupComplete) {
        Text("현재 학년 입력")
    }
}