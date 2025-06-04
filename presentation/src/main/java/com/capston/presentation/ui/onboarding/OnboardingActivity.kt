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
    val recommendResponses by recommendViewModel.postRecommendLectures.collectAsState() // 리스트로 변경

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
                    navController.navigate("learning-preference")
                }
            )
        }

        composable("learning-preference") {
            LearningPreferenceScreen(
                onSetupComplete = { focus, goal, styles ->
                    onboardingDataViewModel.updateLearningPreferences(focus, goal, styles)
                    navController.navigate("onboarding-finish")
                }
            )
        }

        composable("onboarding-finish") {
            OnboardingFinishScreen(
                onCompleteOnboarding = {
                    // 추천 API 호출
                    onboardingDataViewModel.createRecommendRequest()?.let { recommendDto ->
                        recommendViewModel.postRecommendLectures(recommendDto)
                    }
                    navController.navigate("study-plan-complete")
                }
            )
        }

        composable("study-plan-complete") {
            StudyPlanCompleteScreen(
                recommendResponses = recommendResponses, // 단일 객체 -> 리스트로 변경
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