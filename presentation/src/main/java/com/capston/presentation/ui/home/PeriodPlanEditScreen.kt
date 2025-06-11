package com.capston.presentation.ui.home

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.Lecture
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.presentation.R
import com.capston.presentation.theme.LightPurple
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.chipGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.LectureViewModel
import com.capston.presentation.viewmodel.PlanEditViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Calendar.DAY_OF_WEEK를 DayOfWeek enum name으로 변환
fun calendarDayOfWeekToEnumName(calendarDayOfWeek: Int): String {
    return when (calendarDayOfWeek) {
        Calendar.SUNDAY -> DayOfWeek.SUN.name
        Calendar.MONDAY -> DayOfWeek.MON.name
        Calendar.TUESDAY -> DayOfWeek.TUE.name
        Calendar.WEDNESDAY -> DayOfWeek.WED.name
        Calendar.THURSDAY -> DayOfWeek.THU.name
        Calendar.FRIDAY -> DayOfWeek.FRI.name
        Calendar.SATURDAY -> DayOfWeek.SAT.name
        else -> ""
    }
}

// 선택한 요일들이 기간 내에 존재하는지 확인
fun checkSelectedDaysExistInPeriod(
    startDateStr: String,
    endDateStr: String,
    selectedDays: List<String>
): Boolean {
    if (startDateStr == "시작일 선택" || endDateStr == "종료일 선택") return false

    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = dateFormat.parse(startDateStr) ?: return false
        val endDate = dateFormat.parse(endDateStr) ?: return false

        val calendar = Calendar.getInstance()
        calendar.time = startDate

        val daysInPeriod = mutableSetOf<String>()

        // 시작일부터 종료일까지 모든 날짜의 요일을 수집
        while (!calendar.time.after(endDate)) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val enumName = calendarDayOfWeekToEnumName(dayOfWeek)
            daysInPeriod.add(enumName)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 선택한 요일 중 하나라도 기간 내에 존재하는지 확인
        return selectedDays.any { it in daysInPeriod }

    } catch (e: ParseException) {
        return false
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PeriodPlanEditScreen(
    planId: Int,  // 추가
    planEditViewModel: PlanEditViewModel,
    planViewModel: PlanViewModel,
    navController: NavController,
    loadingStateManager: LoadingStateManager
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val selectedLectureDto by lectureViewModel.selectedLecture.collectAsState()

    // 요청 상태 추적
    var requestSent by remember { mutableStateOf(false) }
    val planResponse by planViewModel.postNewPlanResponse.collectAsState()

    // HorizontalPager 관련 코드 모두 제거
    // val pagerState = rememberPagerState(pageCount = { 2 })

    // Convert LectureResponseDto to Lecture model
    val lecture = remember(selectedLectureDto) {
        selectedLectureDto?.let {
            Lecture(
                id = it.id,
                title = it.title,
                teacher = it.teacher,
                platform = it.platform.label,
                subject = it.subject.label,
                totalLessons = it.totalLessons,
                totalDuration = 0,
                tag = it.tag
            )
        } ?: Lecture()
    }

    // State for validation error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // planType을 PERIOD로 고정
    val planType = remember { mutableStateOf("PERIOD") }
    val startLessonId = remember { mutableIntStateOf(0) }
    val endLessonId = remember { mutableIntStateOf(0) }
    val studyDayOfWeeks = remember {
        mutableStateOf(
            listOf(
                DayOfWeek.MON.name,
                DayOfWeek.TUE.name,
                DayOfWeek.WED.name,
                DayOfWeek.THU.name,
                DayOfWeek.FRI.name
            )
        )
    }
    val dailyTime = remember { mutableIntStateOf(120) }
    val today = com.capston.presentation.ui.search.formatDate(System.currentTimeMillis())
    val startDate = remember { mutableStateOf(today) }
    val endDate = remember { mutableStateOf(today) }  // 편집 가능
    val playbackSpeed = remember { mutableDoubleStateOf(1.0) }  // 편집 가능

    // planId로 기존 계획 데이터 로드하는 LaunchedEffect 추가
    LaunchedEffect(planId) {
        if (planId > 0) {
            // 기존 계획 데이터를 로드하는 로직 추가
            // planEditViewModel.loadPlan(planId) 등
        }
    }

    // 응답을 관찰하여 액티비티 종료 처리
    LaunchedEffect(planResponse, requestSent) {
        if (requestSent && planResponse.message.isNotEmpty()) {
            delay(1000)
            loadingStateManager.hide()

            if (context is ComponentActivity) {
                context.setResult(Activity.RESULT_OK)
                context.finish()
            }
        }
    }

    // Load lessons when lecture is selected
    LaunchedEffect(lecture.id) {
        if (lecture.id != 0) {
            lectureViewModel.getLessonsByLectureId(lecture.id)
        }
    }

    // Validation functions - 간소화
    val validateInputs = {
        when {
            endDate.value == "종료일 선택" -> {
                errorMessage = "목표 완강일을 선택해주세요."
                false
            }
            startLessonId.intValue == 0 || endLessonId.intValue == 0 -> {
                errorMessage = "시작 강의와 마지막 강의를 모두 선택해주세요."
                false
            }
            startLessonId.intValue > endLessonId.intValue -> {
                errorMessage = "시작 강의는 마지막 강의보다 먼저여야 합니다."
                false
            }
            else -> true
        }
    }

    Scaffold(
        topBar = { MakePlanTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // HeaderSection에서 버튼 제거된 버전으로 변경
            SimplifiedHeaderSection(lecture = lecture)
            HorizontalDivider(color = dividerGray)

            // HorizontalPager 대신 직접 컨텐츠 표시
            Column(modifier = Modifier.padding(16.dp)) {
                // endDate와 playbackSpeed만 편집 가능
                EndDateSection(endDate)
                PlaybackSpeedSection(playbackSpeed)

                // 읽기 전용으로 표시할 정보들
                ReadOnlyInfoSection(
                    startLessonId = startLessonId,
                    endLessonId = endLessonId,
                    studyDayOfWeeks = studyDayOfWeeks,
                    startDate = startDate,
                    lectureViewModel = lectureViewModel
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = {
                        if (validateInputs()) {
                            requestSent = true
                            loadingStateManager.show()

                            // 수정 API 호출 (새로운 DTO 필요)
                            val dto = PostNewPlanDto(
                                lectureId = lecture.id,
                                planType = planType.value,
                                startLessonId = startLessonId.intValue,
                                endLessonId = endLessonId.intValue,
                                studyDayOfWeeks = studyDayOfWeeks.value,
                                dailyTime = dailyTime.intValue,
                                startDate = startDate.value,
                                endDate = endDate.value,
                                playbackSpeed = playbackSpeed.doubleValue
                            )

                            // 수정 API 호출로 변경 필요
                            planViewModel.updatePlan(planId, dto)  // 이 메서드는 추가 구현 필요
                        } else {
                            showErrorDialog = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("수정 완료")
                }
            }
        }
    }

    // Error dialog는 그대로 유지
    if (showErrorDialog) {
        AlertDialog(
            containerColor = Color.White,
            iconContentColor = Color.Black,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            tonalElevation = 0.dp,
            onDismissRequest = { showErrorDialog = false },
            title = { Text("입력 오류") },
            text = { Text(errorMessage ?: "입력 정보를 확인해주세요.") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text(
                        text = "확인",
                        color = MainPurple
                    )
                }
            }
        )
    }
}

// 새로운 간소화된 헤더 섹션
@Composable
fun SimplifiedHeaderSection(lecture: Lecture) {
    Column(
        modifier = Modifier
            .background(backgroundGray)
            .padding(16.dp)
    ) {
        Text(
            text = lecture.platform,
            style = MaterialTheme.typography.labelLarge,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = lecture.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "${lecture.teacher} · ${lecture.tag} · ${lecture.totalLessons}강",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 계획 수정 안내 텍스트
        Text(
            text = "목표 완강일과 배속만 수정할 수 있습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MainPurple,
            fontWeight = FontWeight.Medium
        )
    }
}

// 종료일만 편집 가능한 섹션
@Composable
fun EndDateSection(endDate: MutableState<String>) {
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = "목표 완강일",
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.titleSmall,
        )

        OutlinedTextField(
            value = endDate.value,
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showEndDatePicker = !showEndDatePicker }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_calendar),
                        contentDescription = "종료일 선택"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                endDate.value = com.capston.presentation.ui.search.formatDate(millis)
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

// 읽기 전용 정보 표시 섹션
@Composable
fun ReadOnlyInfoSection(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    studyDayOfWeeks: MutableState<List<String>>,
    startDate: MutableState<String>,
    lectureViewModel: LectureViewModel
) {
    Column {
        Text(
            text = "계획 정보",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 시작일 (읽기 전용)
        Text(
            text = "학습 시작일: ${startDate.value}",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 공부 요일 (읽기 전용)
        val dayLabels = studyDayOfWeeks.value.map { day ->
            DayOfWeek.entries.find { it.name == day }?.label ?: day
        }.joinToString(", ")

        Text(
            text = "공부 요일: $dayLabels",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}