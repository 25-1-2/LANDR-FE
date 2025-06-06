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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.MainActivity
import com.capston.presentation.viewmodel.LoginViewModel
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
    private val loginViewModel: LoginViewModel by viewModels()

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
                    recommendViewModel = recommendViewModel,
                    loginViewModel = loginViewModel
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
    recommendViewModel: RecommendViewModel,
    loginViewModel: LoginViewModel
) {
    // 온보딩 데이터를 관리할 ViewModel
    val onboardingDataViewModel: OnboardingDataViewModel = viewModel()
    val onboardingData by onboardingDataViewModel.onboardingData.collectAsState()
    val recommendResponses by recommendViewModel.postRecommendLectures.collectAsState()
    val userProfile by loginViewModel.getUserProfile.collectAsState()

    // 완료 상태 관리
    var isAllComplete by remember { mutableStateOf(false) }
    var expectedSubjectCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        loginViewModel.getUserProfile()
    }

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(onCompleteOnboarding = {
                navController.navigate("school-year")
            },
                userProfile = userProfile
            )
        }

        composable("school-year") {
            SchoolYearScreen(
                onSetupComplete = { selectedGrade ->
                    onboardingDataViewModel.updateGrade(selectedGrade)
                    navController.navigate("subject-grade")
                },
                userProfile = userProfile
            )
        }

        composable("subject-grade") {
            SubjectGradeScreen(
                onSetupComplete = { subjectGrades ->
                    onboardingDataViewModel.updateSubjectGrades(subjectGrades)
                    // LearningPreferenceScreen을 건너뛰고 바로 완료 화면으로 이동
                    navController.navigate("onboarding-finish")
                },
                userProfile = userProfile
            )
        }

        composable("onboarding-finish") {
            OnboardingFinishScreen(
                onCompleteOnboarding = {
                    // 모든 과목에 대한 추천 API 호출
                    val recommendRequests = onboardingDataViewModel.createRecommendRequests()
                    if (recommendRequests.isNotEmpty()) {
                        // 예상되는 과목 수 저장
                        expectedSubjectCount = recommendRequests.size
                        // 완료 상태 초기화
                        isAllComplete = false
                        // 이전 추천 결과 초기화
                        recommendViewModel.clearRecommendations()
                        // 모든 과목에 대해 각각의 설정으로 추천 요청
                        recommendViewModel.postMultipleRecommendLectures(recommendRequests)
                    }
                    navController.navigate("study-plan-complete")
                },
                userProfile = userProfile
            )
        }

        composable("study-plan-complete") {
            // 추천 응답이 변경될 때마다 완료 여부 확인
            LaunchedEffect(recommendResponses) {
                if (recommendResponses.isNotEmpty() && expectedSubjectCount > 0) {
                    // 응답받은 과목 수로 판단
                    val receivedSubjectCount = recommendResponses.groupBy { it.subject }.size
                    isAllComplete = receivedSubjectCount >= expectedSubjectCount
                }
            }

            StudyPlanCompleteScreen(
                recommendResponses = recommendResponses,
                isAllRecommendationsComplete = isAllComplete,
                onStartLearning = {
                    val userEmail = Firebase.auth.currentUser?.email
                    onboardingPreferenceStorage.setOnboardingCompleted(userEmail)
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                },
                userProfile = userProfile
            )
        }
    }
}