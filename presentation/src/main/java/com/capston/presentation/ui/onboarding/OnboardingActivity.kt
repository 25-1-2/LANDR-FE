package com.capston.presentation.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.MainActivity
import com.capston.presentation.viewmodel.RecommendViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    @Inject
    lateinit var onboardingPreferenceStorage: OnboardingPreferenceStorage

    private val recommendViewModel: RecommendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapstonTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    context = this,
                    onboardingPreferenceStorage = onboardingPreferenceStorage,
                    recommendViewModel = recommendViewModel
                )
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    context: Context,
    onboardingPreferenceStorage: OnboardingPreferenceStorage,
    recommendViewModel: RecommendViewModel
) {
    // 온보딩 데이터를 관리할 ViewModel
    val onboardingDataViewModel: OnboardingDataViewModel = viewModel()
    val onboardingData by onboardingDataViewModel.onboardingData.collectAsState()
    val recommendResponses by recommendViewModel.postRecommendLectures.collectAsState()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(onCompleteOnboarding = {
                navController.navigate("school-year")
            })
        }

        composable("school-year") {
            SchoolYearScreen(
                onSetupComplete = { selectedGrade ->
                    onboardingDataViewModel.updateGrade(selectedGrade)
                    navController.navigate("subject-grade")
                }
            )
        }

        composable("subject-grade") {
            SubjectGradeScreen(
                onSetupComplete = { subjectGrades ->
                    onboardingDataViewModel.updateSubjectGrades(subjectGrades)
                    // LearningPreferenceScreen을 건너뛰고 바로 완료 화면으로 이동
                    navController.navigate("onboarding-finish")
                }
            )
        }

        // LearningPreferenceScreen은 제거됨 - 각 과목별로 설정하므로 불필요

        composable("onboarding-finish") {
            OnboardingFinishScreen(
                onCompleteOnboarding = {
                    // 모든 과목에 대한 추천 API 호출
                    val recommendRequests = onboardingDataViewModel.createRecommendRequests()

                    android.util.Log.d("OnboardingActivity", "=== 추천 요청 시작 ===")
                    android.util.Log.d("OnboardingActivity", "생성된 추천 요청 수: ${recommendRequests.size}")

                    recommendRequests.forEachIndexed { index, request ->
                        android.util.Log.d("OnboardingActivity", "요청 ${index + 1}: ${request.subject.label}")
                        android.util.Log.d("OnboardingActivity", "  - 학습방향: ${request.focus}")
                        android.util.Log.d("OnboardingActivity", "  - 학습목표: ${request.goal}")
                        android.util.Log.d("OnboardingActivity", "  - 학습스타일: ${request.styles}")
                    }

                    if (recommendRequests.isNotEmpty()) {
                        // 이전 추천 결과 초기화
                        recommendViewModel.clearRecommendations()
                        // 모든 과목에 대해 각각의 설정으로 추천 요청
                        recommendViewModel.postMultipleRecommendLectures(recommendRequests)
                    } else {
                        android.util.Log.w("OnboardingActivity", "유효한 추천 요청이 없습니다!")
                    }
                    navController.navigate("study-plan-complete")
                }
            )
        }

        composable("study-plan-complete") {
            StudyPlanCompleteScreen(
                recommendResponses = recommendResponses,
                onStartLearning = {
                    val userEmail = Firebase.auth.currentUser?.email
                    onboardingPreferenceStorage.setOnboardingCompleted(userEmail)
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                }
            )
        }
    }
}