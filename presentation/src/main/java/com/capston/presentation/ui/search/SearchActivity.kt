package com.capston.presentation.ui.search

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.viewmodel.LectureViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    val lectureViewModel: LectureViewModel by viewModels()
    val planViewModel: PlanViewModel by viewModels()

    @Inject
    lateinit var loadingStateManager: LoadingStateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            CapstonTheme {
                SearchNavHost(navController, planViewModel, lectureViewModel, loadingStateManager)

                // 전역 로딩 인디케이터
                LoadingIndicator(loadingStateManager)
            }
        }
    }
}
