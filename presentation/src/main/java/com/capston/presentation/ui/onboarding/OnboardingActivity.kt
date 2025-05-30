package com.capston.presentation.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.MainActivity
import com.capston.presentation.ui.home.HomeScreen
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
                val navController = rememberNavController()
                AppNavHost(navController = navController, this)
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, context: Context) {
    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(onCompleteOnboarding = {
                navController.navigate("school-year")
            })
        }
        composable("school-year") {
            SchoolYearScreen(onSetupComplete = {
                navController.navigate("subject-grade")
            })
        }

        composable("subject-grade") {
            SubjectGradeScreen(onSetupComplete = {
                navController.navigate("onboarding-finish")
            })
        }

        composable("onboarding-finish") {
            OnboardingFinishScreen(onCompleteOnboarding = {
                navController.navigate("study-plan-complete")
            })
        }

        composable("study-plan-complete") {
            StudyPlanCompleteScreen(onStartLearning = {
                context.startActivity(Intent(context, MainActivity::class.java))
                (context as? Activity)?.finish()
            })
        }
    }
}
