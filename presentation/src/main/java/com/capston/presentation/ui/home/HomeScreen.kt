package com.capston.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.response.home.LectureProgressResponse
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.WarmPurple_20
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import com.capston.domain.model.DayAchievementDto
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray5
import com.capston.presentation.theme.WarmPurple
import com.capston.presentation.ui.common.CustomCheckBox
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.search.SearchActivity
import com.capston.presentation.ui.search.bgColor
import com.capston.presentation.ui.search.borderColor
import com.capston.presentation.viewmodel.RecommendViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition"
)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, planViewModel: PlanViewModel, recommendViewModel: RecommendViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val homeState by homeViewModel.getDistinctHome.collectAsState()
    val dDayState by homeViewModel.dDay.collectAsState()
    val recommendResponses by recommendViewModel.postRecommendLectures.collectAsState() // 추천 결과 상태

    // 현재 D-Day 데이터 안전하게 가져오기
    val currentDDayData = remember(dDayState, homeState) {
        try {
            when {
                dDayState != null -> dDayState
                homeState != null && homeState.dday != null -> homeState.dday
                else -> null
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error accessing D-Day data: ${e.message}")
            null
        }
    }

    // 나의 학습 현황
    val totalCompletedLessons = homeState.userProgress.totalCompletedLessons // 들은 강의 개수
    val totalLessons = homeState.userProgress.totalLessons // 전체 강의 개수

    // 오늘의 강의
    val todayLessonList = homeState.todaySchedule.lessonSchedules
    val todayTotalLesson = homeState.todaySchedule.totalLessons
    val todayTotalDuration = homeState.todaySchedule.totalDuration

    val lectureProgressList = homeState.userProgress.lectureProgress
    val patchData by planViewModel.patchPlanName.collectAsState()

    // ModalBottomSheet의 boolean 상태를 기억
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 시험 디데이 바텀 시트 관련 상태
    var isExamBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val examModalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var examTitle by remember { mutableStateOf("") }
    var examDate by remember { mutableStateOf("") }
    var dDay by remember { mutableStateOf("D-Day") }

    // 안전한 D-Day ID 체크 함수
    fun getSafeDDayId(): Int? {
        return try {
            currentDDayData?.ddayId?.takeIf { it > 0 }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error getting D-Day ID: ${e.message}")
            null
        }
    }

    // 안전한 D-Day 존재 여부 체크 함수
    fun hasSafeDDay(): Boolean {
        return try {
            getSafeDDayId() != null
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error checking D-Day existence: ${e.message}")
            false
        }
    }

    fun calculateDDay(dateString: String?): String {
        return try {
            // isBlank() 대신 isNullOrBlank() 사용
            if (dateString.isNullOrBlank()) return "D-Day"

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val examDateTime = LocalDate.parse(dateString, formatter)
            val today = LocalDate.now()
            val daysDifference = ChronoUnit.DAYS.between(today, examDateTime).toInt()

            when {
                daysDifference > 0 -> "D-$daysDifference"
                daysDifference < 0 -> "D+${-daysDifference}"
                else -> "D-Day"
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error calculating D-Day: ${e.message}")
            "D-Day"
        }
    }

    // D-Day 상태 업데이트 - 현재 D-Day 데이터가 변경될 때마다 실행
    LaunchedEffect(currentDDayData) {
        if (currentDDayData != null) {
            examTitle = currentDDayData.title ?: ""
            examDate = currentDDayData.goalDate ?: ""
            dDay = calculateDDay(currentDDayData.goalDate)
        } else {
            examTitle = ""
            examDate = ""
            dDay = "D-Day"
        }
    }

    // 디버깅을 위한 로그 추가
    LaunchedEffect(recommendResponses) {
        Log.d("HomeScreen", "=== 추천 데이터 상태 확인 ===")
        Log.d("HomeScreen", "추천 결과 개수: ${recommendResponses.size}")
        if (recommendResponses.isNotEmpty()) {
            Log.d("HomeScreen", "첫 번째 추천: ${recommendResponses.first().title}")
            Log.d("HomeScreen", "모든 추천 제목들: ${recommendResponses.map { it.title }}")
        } else {
            Log.d("HomeScreen", "추천 결과가 비어있음")
        }
    }

    // 이번주 학습 성취율 섹션 확장/축소 상태
    var isWeeklyExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { HomeTopBar(hasUnreadNotifications = false) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 학습 현황 카드
            LearningStatusCard(
                totalCompletedLessons = totalCompletedLessons,
                totalLessons = totalLessons,
                lectureProgressList = lectureProgressList,
                patchData = patchData,
                onEditClick = { isBottomSheetVisible = true },
                navController = navController
            )

            if (todayLessonList == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, color = LightGray60)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // 추천 강의 헤더
                        RecommendedCoursesHeader()

                        Spacer(modifier = Modifier.height(20.dp))

                        // 추천 강의 카드 (수정된 부분)
                        RecommendedCoursesCard(
                            recommendResponses = recommendResponses,
                            navController = navController,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            context = context
                        )
                    }
                }
            }
            else {
                TodayLectureCard(
                    todayLessonList = todayLessonList,
                    todayTotalLesson = todayTotalLesson,
                    todayTotalDuration = todayTotalDuration,
                    homeViewModel = homeViewModel,
                    context = context
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // 시험 디데이 및 인강사이트 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 시험 디데이 카드
                ExamDdayCard(
                    examTitle = examTitle,
                    dDay = dDay,
                    onEditClick = { isExamBottomSheetVisible = true },
                    modifier = Modifier.weight(1f)
                )

                // 인강사이트 목록 카드
                OnlineLectureCard(
                    context = context,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 이번주 학습 성취율 섹션 추가
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                WeeklyAchievementGraph(
                    isExpanded = isWeeklyExpanded,
                    onToggle = { isWeeklyExpanded = it },
                    homeState = homeState
                )
            }
        }
    }

    var showDeleteCompleteDialog by remember { mutableStateOf(false) }

    // 바텀 시트 처리 - with additional null safety
    ShowBottomSheets(
        isBottomSheetVisible = isBottomSheetVisible,
        modalBottomSheetState = modalBottomSheetState,
        onDismissBottomSheet = { isBottomSheetVisible = false },
        isExamBottomSheetVisible = isExamBottomSheetVisible,
        examModalBottomSheetState = examModalBottomSheetState,
        onDismissExamBottomSheet = { isExamBottomSheetVisible = false },
        lectureProgressList = lectureProgressList ?: emptyList(),
        planViewModel = planViewModel,
        examTitle = examTitle,
        examDate = examDate,
        onSaveExam = { newTitle, newDate ->
            try {
                val dDayId = dDayState?.ddayId
                if (dDayId != null && dDayId > 0) {
                    // Update existing D-Day
                    homeViewModel.patchDDay(dDayId, UpdateDDayRequest(newTitle, newDate))
                } else {
                    // Create new D-Day
                    homeViewModel.postDDay(UpdateDDayRequest(newTitle, newDate))
                }

                // 저장 후 데이터 새로고침
                scope.launch {
                    kotlinx.coroutines.delay(50) // 서버 처리 시간 대기
                    homeViewModel.getDistinctHome() // distinct home 데이터 새로고침
                }

            } catch (e: Exception) {
                Log.e("HomeScreen", "Error saving D-Day: ${e.message}", e)
            }
            isExamBottomSheetVisible = false
        },
        onDeleteExam = {
            try {
                val ddayId = getSafeDDayId()
                Log.d("HomeScreen", "Deleting D-Day: ddayId=$ddayId")

                if (ddayId != null) {
                    homeViewModel.deleteDDay(ddayId)
                    showDeleteCompleteDialog = true

                    // 삭제 후 데이터 새로고침
                    scope.launch {
                        kotlinx.coroutines.delay(500) // 서버 처리 시간 대기
                        homeViewModel.getDistinctHome()
                    }
                } else {
                    Log.w("HomeScreen", "Cannot delete D-Day: no valid ID found")
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error deleting D-Day: ${e.message}", e)
            }
            isExamBottomSheetVisible = false
        },
        hasDDay = hasSafeDDay() // 안전한 체크 함수 사용
    )

    // 바텀시트 외부에 삭제 완료 다이얼로그 표시
    DeleteCompleteDialog(
        show = showDeleteCompleteDialog,
        onDismiss = {
            showDeleteCompleteDialog = false
        }
    )
}

@Composable
fun TodayLectureCard(
    todayLessonList: List<LessonScheduleResponse>?,
    todayTotalLesson: Int,
    todayTotalDuration: Int,
    homeViewModel: HomeViewModel,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, color = LightGray60)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 카드 헤더 (제목 + 정보)
            if (todayLessonList != null && todayLessonList.isNotEmpty()) {
                TodayLectureHeader(todayTotalLesson, todayTotalDuration)
            }
            else {
                RecommendedCoursesHeader()
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 강의 목록 또는 빈 상태
            if (todayLessonList != null && todayLessonList.isNotEmpty()) {
                ModifiedLessonList(homeViewModel, 330, todayLessonList)
            } else {
                RecommendedCoursesWithIndicator(
                    homeViewModel = homeViewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(hasUnreadNotifications: Boolean) {
    Column {
        TopAppBar(
            title = {
                // 앱 이름
                Image(
                    painter = painterResource(id = R.drawable.landr_title_iv),
                    contentDescription = "앱 이름",
                    modifier = Modifier.size(70.dp),
                )
            },
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = "앱 로고",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(48.dp),
                )
            },
            actions = {
                // 읽지 않은 알람이 있을 경우 빨간색 배지 표시
                if (hasUnreadNotifications) {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_notification_on),
                            contentDescription = "alarm icon"
                        )
                    }
                }

                else {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_notification_off),
                            contentDescription = "alarm icon"
                        )
                    }
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray2)
    }
}

// ProfileScreen에서 가져온 이번주 학습 성취율 관련 컴포넌트들
@SuppressLint("DefaultLocale")
@Composable
fun WeeklyAchievementGraph(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    homeState: DistinctHomeIdResponse
) {
    // 요일별 학습 성취 데이터 (true: 달성, false: 미달성)
    val weekDaysData = listOf(
        DayAchievementDto(DayOfWeek.MON, homeState.weeklyAchievement.mondayAchieved),
        DayAchievementDto(DayOfWeek.TUE, homeState.weeklyAchievement.tuesdayAchieved),
        DayAchievementDto(DayOfWeek.WED, homeState.weeklyAchievement.wednesdayAchieved),
        DayAchievementDto(DayOfWeek.THU, homeState.weeklyAchievement.thursdayAchieved),
        DayAchievementDto(DayOfWeek.FRI, homeState.weeklyAchievement.fridayAchieved),
        DayAchievementDto(DayOfWeek.SAT, homeState.weeklyAchievement.saturdayAchieved),
        DayAchievementDto(DayOfWeek.SUN, homeState.weeklyAchievement.sundayAchieved)
    )

    // 성취율 계산
    val achievedDays = weekDaysData.count { it.isAchieved }
    val totalDays = weekDaysData.size
    val achievementRate = (achievedDays.toFloat() / totalDays) * 100

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 제목 행 (아이콘 추가)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isExpanded) 20.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Image(
                painter = painterResource(id = R.drawable.screen_profile_week_completion),
                contentDescription = "이번주 학습 성취율 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp)
            )

            // 제목
            Text(
                text = "이번주 학습 성취율",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(end = 5.dp)
            )

            // 오른쪽 화살표 - 클릭 가능한 토글 버튼으로 변경
            IconButton(
                onClick = { onToggle(!isExpanded) },
                modifier = Modifier.size(24.dp)
            ) {
                // 회전 애니메이션 추가
                val rotationState by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    label = "rotationAnimation"
                )

                Image(
                    painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                    contentDescription = if (isExpanded) "접기" else "펼치기",
                    modifier = Modifier.rotate(rotationState)
                )
            }
        }

        // 회색 테두리 박스 - 중앙 정렬로 변경
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(10.dp),
                shape = RoundedCornerShape(5.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                // 연속 달성일 정보 텍스트
                Text(
                    buildAnnotatedString {
                        append("이번주 ")
                        withStyle(style = SpanStyle(color = MainPurple)) {
                            append("${achievedDays}일")
                        }
                        append(" 학습 목표를 달성했어요")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // 확장된 경우에만 요일별 체크 아이콘 표시
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 요일별 체크 표시
                DayAchievementChecks(weekDaysData = weekDaysData)

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DayAchievementChecks(weekDaysData: List<DayAchievementDto>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 요일별 체크 아이콘 표시를 위해 너비를 제한하여 모든 요일이 표시되도록 함
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 각 요일별 아이템
                weekDaysData.forEach { dayData ->
                    // 각 아이템은 고정된 너비를 가지도록 설정
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        DayAchievementItem(
                            day = dayData.day,
                            isAchieved = dayData.isAchieved
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayAchievementItem(day: DayOfWeek, isAchieved: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 2.dp) // 여백 줄임
    ) {
        // 요일 표시
        Text(
            text = day.label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 체크 아이콘 배경
        Box(
            modifier = Modifier
                .size(36.dp) // 크기 약간 줄임
                .background(
                    color = if (isAchieved) MainPurple else LightGray5,
                    shape = androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // 달성했을 경우에만 체크 아이콘 표시
            if (isAchieved) {
                Image(
                    painter = painterResource(R.drawable.screen_profile_check_iv),
                    contentDescription = "달성 완료",
                    modifier = Modifier.size(18.dp) // 아이콘 크기 조정
                )
            }
        }
    }
}

@Composable
fun LearningStatusCard(
    totalCompletedLessons: Int,
    totalLessons: Int,
    lectureProgressList: List<LectureProgressResponse>,
    patchData: PatchPlanAliasResponse,
    onEditClick: () -> Unit,
    navController: NavController // NavController 추가
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WarmPurple_20),
        border = BorderStroke(1.dp, color = WarmPurple)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                // 헤더 행 (아이콘 + 제목 + 편집 버튼)
                LearningStatusHeader(onEditClick = onEditClick)

                // 원형 그래프 목록
                LearningProgressGraphs(
                    totalCompletedLessons = totalCompletedLessons,
                    totalLessons = totalLessons,
                    lectureProgressList = lectureProgressList,
                    patchData = patchData,
                    navController = navController // NavController 전달
                )
            }
        }
    }
}

@Composable
fun LearningStatusHeader(onEditClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
    ) {
        // 아이콘 + 제목
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.screen_profile_study_time_iv),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 12.dp)
            )

            Text(
                text = stringResource(R.string.home_status),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 5.dp)
            )
        }

        // 편집 버튼
        EditButton(onClick = onEditClick)
    }
}

@Composable
fun EditButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, WarmPurple),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_edit),
                color = MainPurple,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LearningProgressGraphs(
    totalCompletedLessons: Int,
    totalLessons: Int,
    lectureProgressList: List<LectureProgressResponse>,
    patchData: PatchPlanAliasResponse,
    navController: NavController // NavController 추가
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            CircleGraph(
                "전체",
                totalCompletedLessons,
                totalLessons,
                onPlanClick = null, // 전체는 클릭 불가
                lectureProgressList = lectureProgressList
            )
        }

        // lectureProgressList가 비어있지 않을 때만 항목 표시
        if (lectureProgressList.isNotEmpty()) {
            items(lectureProgressList) { item ->
                Spacer(modifier = Modifier.width(10.dp)) // 그래프 간격 추가

                // PATCH 요청 응답을 받아서 name 업데이트
                val currentLectureName = if (item.planId == patchData.planId) {
                    patchData.lectureAlias
                } else {
                    item.lectureAlias
                }
                CircleGraph(
                    name = currentLectureName,
                    cleared = item.completedLessons,
                    total = item.totalLessons,
                    onPlanClick = { planId ->
                        // planId만 사용하여 바로 이동
                        navController.navigate("${Screen.SinglePlan.title}/$planId")
                    },
                    lectureProgressList = lectureProgressList
                )
            }
        }
    }
}

@Composable
fun CircleGraph(
    name: String,
    cleared: Int,
    total: Int,
    onPlanClick: ((Int) -> Unit)?, // MyLecture 객체 대신 planId를 직접 전달하도록 변경
    lectureProgressList: List<LectureProgressResponse> = emptyList()
) {
    val animatedValue = remember { Animatable(0f) }

    val targetValue = if (total > 0) {
        (cleared.toFloat() / total.toFloat()) * 360f
    } else {
        0f
    }

    LaunchedEffect(targetValue) {
        animatedValue.snapTo(0f)
        animatedValue.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        )
    }

    Canvas(
        modifier = Modifier
            .size(150.dp)
            .clickable(enabled = onPlanClick != null) {
                if (onPlanClick != null) {
                    // 이름으로 현재 lectureProgressList에서 일치하는 항목 찾기
                    val matchingLecture = lectureProgressList.find { it.lectureAlias == name }

                    if (matchingLecture != null) {
                        // planId만 전달하여 간단하게 처리
                        onPlanClick(matchingLecture.planId)
                    }
                }
            }
    ) {
        // 나머지 Canvas 그래픽 코드는 동일하게 유지
        val sizeArc = size / 1.3F
        val arcStrokeWidth = 30f

        // 내부 색 채우기
        drawCircle(
            color = White,
            radius = (sizeArc.minDimension / 2f) - (arcStrokeWidth / 2f),
            center = center
        )

        drawArc(
            color = LightGray40,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset((size.width - sizeArc.width) / 2f, (size.height - sizeArc.height) / 2f),
            size = sizeArc,
            style = Stroke(width = arcStrokeWidth)
        )

        // 진행 그래프
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(MainPurple, MainPurple),
                start = Offset.Zero,
                end = Offset.Infinite,
            ),
            startAngle = 270f,
            sweepAngle = animatedValue.value,
            useCenter = false,
            topLeft = Offset(
                (size.width - sizeArc.width) / 2f,
                (size.height - sizeArc.height) / 2f
            ),
            size = sizeArc,
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // 텍스트 그리기
        drawContext.canvas.nativeCanvas.drawText(
            name,
            size.width / 2,
            size.height / 2,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "${cleared}/${total}",
            size.width / 2,
            size.height / 2 + 70,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
            }
        )

        // 인디케이터 그리기
        val progressAngle = animatedValue.value
        if (progressAngle > 0f) {
            val endAngleRadians = Math.toRadians((270f + progressAngle).toDouble())
            val radius = sizeArc.minDimension / 2
            val indicatorX = center.x + radius * kotlin.math.cos(endAngleRadians).toFloat()
            val indicatorY = center.y + radius * kotlin.math.sin(endAngleRadians).toFloat()

            val indicatorOuterRadius = arcStrokeWidth * 0.7f
            drawCircle(
                color = MainPurple,
                radius = indicatorOuterRadius,
                center = Offset(indicatorX, indicatorY)
            )

            drawCircle(
                color = White,
                radius = indicatorOuterRadius * 0.5f,
                center = Offset(indicatorX, indicatorY)
            )
        }
    }
}

@Composable
fun ModifiedLessonList(
    homeViewModel: HomeViewModel,
    maxHeight: Int,
    todayLessonList: List<LessonScheduleResponse>,
    isExpanded: Boolean = true
) {
    Column(
        modifier = Modifier
            .padding(start = 10.dp)
            .fillMaxWidth()
            .heightIn(max = maxHeight.dp)
            .verticalScroll(rememberScrollState())
    ) {
        todayLessonList.forEach { lesson ->
            // 각 체크박스의 상태를 remember로 관리하되, 초기값은 서버 데이터 사용
            var isChecked by remember(lesson.id, lesson.completed) {
                mutableStateOf(lesson.completed)
            }

            // 서버 데이터가 변경되면 로컬 상태도 동기화
            LaunchedEffect(lesson.completed) {
                isChecked = lesson.completed
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = true,
                        onClick = { /* 클릭만 허용 */ }
                    )
            ) {
                CustomCheckBox(
                    isChecked = isChecked,
                    onCheckedChange = {
                        // 즉시 UI 업데이트 (사용자 경험 향상)
                        isChecked = !isChecked

                        // 서버 업데이트 (백그라운드에서 실행)
                        homeViewModel.patchLessonSchedulesCheckToggle(lesson.id)
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = lesson.lessonTitle,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp
                        )
                        Text(
                            text = lesson.lectureName,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray60,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${lesson.adjustedDuration}분",
                            fontSize = 12.sp,
                            color = MainPurple
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayLectureHeader(todayTotalLesson: Int, todayTotalDuration: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "⭐ 오늘의 강의",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .border(width = 1.dp, color = LightGray40, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 강의 수 아이콘 + 텍스트
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_screen_total_count_iv),
                        contentDescription = "총 강의 수",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "총 ${todayTotalLesson}강",
                        fontSize = 14.sp,
                        color = textGray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 시간 아이콘 + 텍스트
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.home_screen_total_duration_iv),
                        contentDescription = "총 시간",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "약 ${todayTotalDuration}분",
                        fontSize = 14.sp,
                        color = textGray
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLectureState(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.screen_home_todaylesson_empty_iv),
            contentDescription = "과목명",
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "오늘 강의가 없어요",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = textGray
        )
        Spacer(Modifier.height(10.dp))

        Text(
            text = "계획 생성하러 가기",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MainPurple,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    val intent = Intent(context, SearchActivity::class.java)
                    context.startActivity(intent)
                }
        )
    }
}

@Composable
fun ExamDdayCard(
    examTitle: String,
    dDay: String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MainPurple),
        border = BorderStroke(1.dp, color = LightGray4_40)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 헤더 (제목 + 편집 버튼)
            ExamDdayHeader(onEditClick)

            Spacer(modifier = Modifier.height(12.dp))

            // 디데이 내용
            ExamDdayContent(examTitle, dDay)
        }
    }
}

@Composable
fun ExamDdayHeader(onEditClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.screen_profile_calender_iv),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
            )

            Text(
                text = "디데이",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = White
            )
        }

        // 편집 버튼
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .clickable(onClick = onEditClick),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, White),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_edit),
                    fontSize = 12.sp,
                    color = MainPurple
                )
            }
        }
    }
}

@Composable
fun ExamDdayContent(examTitle: String, dDay: String) {
    val displayTitle = if (examTitle == "") "시험을 추가해보세요!" else examTitle

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dDay,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = displayTitle,
            fontSize = 14.sp,
            color = White
        )
    }
}

@Composable
fun OnlineLectureCard(
    context: Context,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, color = LightGray60)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 헤더
            OnlineLectureHeader()

            Spacer(modifier = Modifier.height(12.dp))

            // 인강 사이트 목록
            OnlineLectureSiteList(context)
        }
    }
}

@Composable
fun OnlineLectureHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.screen_profile_learning_status_iv),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        Text(
            text = "인강 바로가기",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun OnlineLectureSiteList(context: Context) {
    // 인강 사이트 패키지명
    val etoosPackageName = stringResource(R.string.package_etoos)
    val megaPackageName = stringResource(R.string.package_megastudy)
    val daesungPackageName = stringResource(R.string.package_mimac)
    val ebsiPackageName = stringResource(R.string.package_ebsi)

    // 사이트 목록을 2x2 그리드로 배치
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 첫 번째 행: 이투스, 메가
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OnlineLectureSiteItem(
                name = stringResource(R.string.etoos),
                packageName = etoosPackageName,
                context = context,
                modifier = Modifier.weight(1f)
            )
            OnlineLectureSiteItem(
                name = stringResource(R.string.megastudy),
                packageName = megaPackageName,
                context = context,
                modifier = Modifier.weight(1f)
            )
        }

        // 두 번째 행: 대성, ebsi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OnlineLectureSiteItem(
                name = stringResource(R.string.mimac),
                packageName = daesungPackageName,
                context = context,
                modifier = Modifier.weight(1f)
            )
            OnlineLectureSiteItem(
                name = stringResource(R.string.ebsi),
                packageName = ebsiPackageName,
                context = context,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun OnlineLectureSiteItem(
    name: String,
    packageName: String,
    context: Context,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(48.dp) // 정사각형으로 만들기
            .clickable { openAppOrPlayStore(context, packageName) },
        colors = CardDefaults.cardColors(containerColor = WarmPurple_20.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, WarmPurple),
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.headlineMedium,
                color = MainPurple,
                textAlign = TextAlign.Center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheets(
    isBottomSheetVisible: Boolean,
    modalBottomSheetState: SheetState,
    onDismissBottomSheet: () -> Unit,
    isExamBottomSheetVisible: Boolean,
    examModalBottomSheetState: SheetState,
    onDismissExamBottomSheet: () -> Unit,
    lectureProgressList: List<LectureProgressResponse>,
    planViewModel: PlanViewModel,
    examTitle: String,
    examDate: String,
    onSaveExam: (String, String) -> Unit,
    onDeleteExam: () -> Unit = {}, // Add delete callback
    hasDDay: Boolean = false // Flag to show if there's an existing D-Day
) {
    // 강의 목록 바텀 시트
    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = onDismissBottomSheet,
            containerColor = White,
            dragHandle = null
        ) {
            CustomBottomSheetDialog(
                title = "강의 목록",
                description = "강의 별칭을 작성해 주세요.",
                modalBottomSheetState = modalBottomSheetState,
                onDismiss = onDismissBottomSheet,
                lectureProgressList = lectureProgressList,
                planViewModel = planViewModel,
            )
        }
    }

    /// 시험 일정 바텀 시트
    if (isExamBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = examModalBottomSheetState,
            onDismissRequest = onDismissExamBottomSheet,
            containerColor = White,
            dragHandle = null
        ) {
            ExamBottomSheetContent(
                title = "시험 일정",
                description = "시험 정보를 입력해 주세요.",
                modalBottomSheetState = examModalBottomSheetState,
                onDismiss = onDismissExamBottomSheet,
                currentTitle = examTitle,
                currentDate = examDate,
                onSave = onSaveExam,
                onDelete = onDeleteExam,
                showDeleteButton = hasDDay
            )
        }
    }
}

// 앱 실행 또는 Play Store 이동을 위한 함수
fun openAppOrPlayStore(context: Context, packageName: String) {
    var intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent == null) {
        val link = "https://play.google.com/store/apps/details?id=$packageName"
        intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }
        context.startActivity(intent)
        return
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDialog(
    title: String,
    description: String,
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    lectureProgressList: List<LectureProgressResponse>,
    planViewModel: PlanViewModel,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White) // 전체 배경
            .navigationBarsPadding() // 소프트 키패드 영역까지 패딩 적용
            .imePadding() // 키보드 올라올 때 고려
    ) {
        // 커스텀 drag handle
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 8.dp)
                .size(width = 36.dp, height = 4.dp)
                .background(materialGray, RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        // 컨텐츠 영역
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 제목 박스
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            scope.launch {
                                modalBottomSheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {}
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp, max = 450.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LectureList(
                    lectureProgressList = lectureProgressList,
                    planViewModel = planViewModel,
                )
            }
        }
    }
}

@Composable
fun LectureList(
    lectureProgressList: List<LectureProgressResponse>,
    planViewModel: PlanViewModel,
) {
    Column {
        lectureProgressList.forEachIndexed { index, lecture ->
            var isEditing by remember { mutableStateOf(false) } // 수정 모드 여부
            var showError by remember { mutableStateOf(false) }  // 오류 메시지를 표시할지 여부
            var aliasState by remember { mutableStateOf(lecture.lectureAlias) } // 강의 별칭 상태

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isEditing) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = aliasState,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 8 || lecture.lectureName == lecture.lectureAlias) {
                                        // 글자수가 8자 이하 or 초기 계획 생성 시
                                        showError = false // 오류 숨기기
                                        aliasState = newValue

                                    } else {
                                        showError = true // 오류 표시
                                    }
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = if (aliasState.length == 8) Color.Red else MainPurple,
                                    unfocusedBorderColor = if (showError) Color.Red else MainPurple,
                                    textColor = textGray,
                                ),
                                textStyle = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            Button(
                                onClick = {
                                    isEditing = false
                                    planViewModel.patchPlanName(
                                        lecture.planId,
                                        PatchPlanAliasDto(lectureAlias = aliasState)
                                    )
                                    lecture.lectureAlias = aliasState
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MainPurple,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                            ) {
                                Text(text = "완료", fontSize = 14.sp)
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            // 글자 수 표시
                            Text(
                                text = "${aliasState.length} / 8(자)",
                                color = if (aliasState.length == 8) Color.Red else textGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )

                            // 강의명 표시
                            Text(
                                text = lecture.lectureName,
                                color = LightGray60,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = aliasState)
                        Text(
                            text = lecture.lectureName,
                            fontSize = 12.sp,
                            color = LightGray60,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // 수정 버튼
                    IconButton(
                        onClick = {
                            isEditing = true // 수정 모드로 전환
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_screen_edit_iv),
                            contentDescription = "Edit Mode"
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamBottomSheetContent(
    title: String,
    description: String,
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    currentTitle: String,
    currentDate: String,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit = {}, // Add delete callback
    showDeleteButton: Boolean = false // Show delete button for existing D-Days
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // "예정된 계획이 없어요"인 경우 빈 문자열로 시작하도록 처리
    var examTitle by remember {
        mutableStateOf(if (currentTitle == "예정된 계획이 없어요.") "" else currentTitle)
    }
    var examDate by remember { mutableStateOf(currentDate) }

    // 날짜 선택 모달 표시 여부
    var showDatePicker by remember { mutableStateOf(false) }

    // 삭제 확인 다이얼로그 표시 여부
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // 삭제 완료 다이얼로그 표시 여부
    var showDeleteCompleteDialog by remember { mutableStateOf(false) }

    // 날짜 포맷터
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")

    // 표시용 날짜 문자열
    val displayDate = try {
        if (examDate != "시작일 선택") {
            LocalDate.parse(examDate, dateFormatter).format(displayFormatter)
        } else {
            "시험일 선택"
        }
    } catch (e: Exception) {
        "시험일 선택"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // 커스텀 drag handle
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 8.dp)
                .size(width = 36.dp, height = 4.dp)
                .background(materialGray, RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        // 컨텐츠 영역
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(360.dp), // 바텀 시트 높이 조정
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 제목 박스
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = materialGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            scope.launch {
                                modalBottomSheetState.hide()
                            }.invokeOnCompletion {
                                onDismiss()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 시험 제목 입력 필드
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "시험 이름",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                OutlinedTextField(
                    value = examTitle,
                    onValueChange = { examTitle = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MainPurple,
                        unfocusedBorderColor = LightGray60,
                        textColor = Color.Black
                    ),
                    textStyle = TextStyle(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text(text = "시험 이름을 입력하세요", style = MaterialTheme.typography.bodyMedium) },
                )

                // 시험 날짜 선택기
                Text(
                    text = "시험 날짜",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = displayDate,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_calendar),
                                contentDescription = "날짜 선택"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 버튼 영역 - 삭제와 저장 버튼을 가로로 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 삭제 버튼 (기존 D-Day가 있을 때만 표시)
                if (showDeleteButton) {
                    Button(
                        onClick = {
                            // 삭제 확인 다이얼로그 표시
                            showDeleteConfirmDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White.copy(alpha = 0.8f),
                            contentColor = MainPurple,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MainPurple)
                    ) {
                        Text(
                            text = "삭제하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // 저장 버튼
                Button(
                    onClick = {
                        onSave(examTitle, examDate)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainPurple,
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "저장하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    // DatePicker 다이얼로그 표시 - MakePlanScreen 스타일로 구현
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = White,
                selectedDayContainerColor = MainPurple,
            ),
            tonalElevation = 0.dp, // 배경에 투명 레이어 없음
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        examDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("확인", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("취소", color = Color.Black)
                }
            },
        ) {
            DatePicker(
                title = null,
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = White
                ),
                modifier = Modifier
                    .background(White)
                    .padding(top = 32.dp)
            )
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("삭제 확인", fontWeight = FontWeight.Bold) },
            text = { Text("정말 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete()  // 분리된 삭제 처리 함수 호출
                    }
                ) {
                    Text("삭제", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("취소")
                }
            }
        )

    }
}

// 바텀 시트 외부에 별도 컴포저블로 삭제 완료 다이얼로그 구현
@Composable
fun DeleteCompleteDialog(
    show: Boolean,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("삭제 완료", fontWeight = FontWeight.Bold) },
            text = { Text("성공적으로 삭제되었습니다.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("확인")
                }
            }
        )
    }
}

// 새로운 추천 강의 카드 컴포넌트
@Composable
fun RecommendedCoursesCard(
    recommendResponses: List<RecommendResponse>,
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 추천 강의 헤더
        //RecommendedCoursesHeader()

        Spacer(modifier = Modifier.height(20.dp))

        if (recommendResponses.isNotEmpty()) {
            // 추천 강의가 있는 경우
            RecommendedCoursesList(
                recommendResponses = recommendResponses,
                navController = navController,
                modifier = Modifier.fillMaxWidth(),
                context = context
            )
        } else {
            // 추천 강의가 없는 경우
            EmptyRecommendationsState()
        }
    }
}

@Composable
fun RecommendedCoursesHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "💡 맞춤 추천 강의",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun RecommendedCoursesList(
    recommendResponses: List<RecommendResponse>,
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context
) {
    if (recommendResponses.isEmpty()) {
        EmptyRecommendationsState()
        return
    }

    var currentPage by remember { mutableStateOf(0) }
    val lazyListState = rememberLazyListState()

    // 현재 페이지 추적
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        currentPage = lazyListState.firstVisibleItemIndex
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 좌우 스와이프 가능한 추천 강의 리스트
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // 고정 높이
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(recommendResponses) { index, recommendation ->
                SingleRecommendationCard(
                    recommendation = recommendation,
                    navController = navController,
                    modifier = Modifier
                        .fillParentMaxWidth() // 전체 너비 사용
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp),
                    context = context
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 원형 페이지 인디케이터
        if (recommendResponses.size > 1) {
            PageIndicator(
                pageCount = recommendResponses.size,
                currentPage = currentPage,
                onPageSelected = { selectedPage ->
                    currentPage = selectedPage
                }
            )
        }
    }
}

// 단일 추천강의 카드 컴포넌트:
@Composable
fun SingleRecommendationCard(
    recommendation: RecommendResponse,
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context
) {
    Card(
        modifier = modifier
            .clickable {
                // 디버깅용 로그 추가
                Log.d("RecommendationCard", "recommendation.id: ${recommendation.id}")
                Log.d("RecommendationCard", "recommendation.lectureId: ${recommendation.id}")
                Log.d("RecommendationCard", "recommendation.tag: ${recommendation.tag}")
                Log.d("RecommendationCard", "recommendation.totalLessons: ${recommendation.totalLessons}")

                // SearchActivity로 이동하면서 lectureId 전달
                val intent = Intent(context, SearchActivity::class.java).apply {
                    putExtra("LECTURE_ID", recommendation.id)
                    putExtra("FROM_RECOMMENDATION", true)
                    putExtra("LECTURE_TITLE", recommendation.title)
                    putExtra("LECTURE_TEACHER", recommendation.teacher)
                    putExtra("LECTURE_PLATFORM", recommendation.platform.label)
                    putExtra("LECTURE_SUBJECT", recommendation.subject.label)
                    putExtra("LECTURE_TAG", recommendation.tag)
                    putExtra("LECTURE_TOTAL_LESSONS", recommendation.totalLessons)
                }
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color = LightGray60.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단: 과목 정보
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 과목 아이콘과 이름
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (recommendation.subject.label) {
                                "국어" -> "📖"
                                "영어" -> "🔤"
                                "수학" -> "📊"
                                "사탐" -> "🌍"
                                "과탐" -> "🔬"
                                "한국사" -> "📜"
                                else -> "📚"
                            },
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 플랫폼 정보
                        Box(
                            modifier = Modifier
                                .background(
                                    color = White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = recommendation.platform.label,
                                fontSize = 14.sp,
                                color = MainPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.padding(5.dp))

                        // 플랫폼 정보
                        Box(
                            modifier = Modifier
                                .background(
                                    color = recommendation.subject.bgColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = recommendation.subject.label,
                                fontSize = 14.sp,
                                color = recommendation.subject.borderColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                }

                Spacer(modifier = Modifier.height(24.dp))

                // 강의 제목
                Text(
                    // 강의 제목이 길 경우 처리
                    text = if (recommendation.title.length >= 40) {
                        recommendation.title.take(40) + "..."
                    } else {
                        recommendation.title
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 28.sp,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 선생님 이름
                Text(
                    text = recommendation.teacher,
                    fontSize = 16.sp,
                    color = textGray,
                    fontWeight = FontWeight.Medium
                )
            }

            // 하단: 플랫폼 정보
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 적합도 배지
                    Box(
                        modifier = Modifier
                            .background(
                                color = MainPurple,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${recommendation.recommendScore}% 적합",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 더보기 아이콘
                    Icon(
                        painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                        contentDescription = "자세히 보기",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { /* 추후 상세 페이지 이동 */ },
                        tint = MainPurple
                    )
                }
            }
        }
    }
}

// 페이지 인디케이터 컴포넌트:
@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            IndicatorDot(
                isSelected = index == currentPage,
                onClick = {
                    // 클릭 시 해당 페이지로 스크롤하는 기능은 제거
                    // 스와이프로만 이동하도록 함
                }
            )
        }
    }
}

@Composable
fun IndicatorDot(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedSize by animateFloatAsState(
        targetValue = if (isSelected) 10f else 6f,
        animationSpec = tween(300),
        label = "dot_size_animation"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.4f,
        animationSpec = tween(300),
        label = "dot_alpha_animation"
    )

    Box(
        modifier = modifier
            .size(animatedSize.dp)
            .background(
                color = MainPurple.copy(alpha = animatedAlpha),
                shape = CircleShape
            )
    )
}

@Composable
fun EmptyRecommendationsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.screen_search_empty_iv),
            contentDescription = "추천 없음",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "맞춤 추천 강의가 없어요",
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = textGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "학습 정보를 설정하고\n강의를 추천 받아 보세요",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = textGray.copy(alpha = 0.7f),
            maxLines = 2
        )
    }
}