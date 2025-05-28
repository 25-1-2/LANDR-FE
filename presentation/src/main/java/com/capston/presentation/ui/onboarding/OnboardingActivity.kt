package com.capston.presentation.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    @Inject
    lateinit var onboardingPreferenceStorage: OnboardingPreferenceStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapstonTheme {
                OnboardingScreen(
                    onCompleteOnboarding = {
                        // 온보딩 완료 처리
                        onboardingPreferenceStorage.setFirstLoginDone()

                        // MainActivity로 이동
                        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}