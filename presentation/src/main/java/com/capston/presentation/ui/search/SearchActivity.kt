package com.capston.presentation.ui.search

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.LectureItemDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.common.Screen
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Intent에서 lectureId 받기
        val lectureId = intent.getIntExtra("LECTURE_ID", -1)
        val fromRecommendation = intent.getBooleanExtra("FROM_RECOMMENDATION", false)
        // 강의 정보 받기
        val lectureTitle = intent.getStringExtra("LECTURE_TITLE") ?: ""
        val lectureTeacher = intent.getStringExtra("LECTURE_TEACHER") ?: ""
        val lecturePlatform = intent.getStringExtra("LECTURE_PLATFORM") ?: ""
        val lectureSubject = intent.getStringExtra("LECTURE_SUBJECT") ?: ""
        val lectureTag = intent.getStringExtra("LECTURE_TAG") ?: ""
        val lectureTotalLessons = intent.getIntExtra("LECTURE_TOTAL_LESSONS", 0)

        setContent {
            val navController = rememberNavController()

            CapstonTheme {
                LaunchedEffect(lectureId) {
                    if (lectureId != 0) {
                        Log.d("PlanScreen", "Loading data for lectureId: $lectureId")
                        Log.d("PlanScreen", "Loading data for lectureTag: $lectureTag")
                        Log.d("PlanScreen", "Loading data for lectureTotalLessons: $lectureTotalLessons")

                        // 1. 레슨 정보 로드
                        lectureViewModel.getLessonsByLectureId(lectureId)

                        // 2. 받은 강의 정보로 LectureItemDto 생성하고 선택
                        val lectureItem = LectureItemDto(
                            id = lectureId,
                            title = lectureTitle,
                            teacher = lectureTeacher,
                            platform = Platform.entries.find { it.label == lecturePlatform } ?: Platform.MEGA,
                            subject = Subject.entries.find { it.label == lectureSubject } ?: Subject.KOR,
                            totalLessons = lectureTotalLessons,
                            tag = lectureTag,
                            createdAt = ""
                        )

                        lectureViewModel.selectLecture(lectureItem)
                        Log.d("PlanScreen", "Selected lecture from recommendation: $lectureTitle")

                        navController.navigate("${Screen.Plan.title}/$lectureId") {
                            popUpTo("search") { inclusive = true }
                        }
                    }
                }

                SearchNavHost(navController, planViewModel, lectureViewModel, loadingStateManager)

                // 전역 로딩 인디케이터
                LoadingIndicator(loadingStateManager)
            }
        }
    }
}
