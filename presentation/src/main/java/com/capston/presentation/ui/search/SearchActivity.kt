package com.capston.presentation.ui.search

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.LectureItemDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray4_40
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
                    if (lectureId != -1 && fromRecommendation) {
                        // 추천에서 넘어온 경우에만 바로 Plan으로 이동
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
                        lectureViewModel.getLessonsByLectureId(lectureId)

                        navController.navigate("${Screen.Plan.title}/$lectureId") {
                            popUpTo("search") { inclusive = true }
                        }
                    }
                }

                // 기본은 Search 화면부터 시작
                SearchNavHost(navController, planViewModel, lectureViewModel, loadingStateManager)
                LoadingIndicator(loadingStateManager)
            }
        }
    }
}