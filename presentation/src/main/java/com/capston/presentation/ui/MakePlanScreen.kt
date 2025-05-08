package com.capston.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.capston.domain.model.Lecture
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.presentation.R
import com.capston.presentation.theme.LightPurple
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.chipGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.LectureViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long?): String {
    return if (millis != null) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date(millis))
    } else {
        ""
    }
}

@Composable
fun MakePlanScreen(
    lectureViewModel: LectureViewModel,
    planViewModel: PlanViewModel
) {
    val selectedLectureDto by lectureViewModel.selectedLecture.collectAsState()

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
                totalDuration = 0, // Default since not available in DTO
                tag = it.tag
            )
        } ?: Lecture() // Fallback to empty lecture if none selected
    }

    val planType = remember { mutableStateOf("PERIOD") }
    val startLessonId = remember { mutableIntStateOf(0) }
    val endLessonId = remember { mutableIntStateOf(0) }
    val studyDayOfWeeks = remember { mutableStateOf(emptyList<String>()) }
    val dailyTime = remember { mutableIntStateOf(0) }
    val startDate = remember { mutableStateOf("시작일 선택") }
    val endDate = remember { mutableStateOf("종료일 선택") }
    val playbackSpeed = remember { mutableDoubleStateOf(1.0) }

    val pagerState = rememberPagerState(pageCount = { 2 }) // 0: 기간, 1: 시간
    val coroutineScope = rememberCoroutineScope()

    val postPlanDto = PostPlanDto(
        lectureId = lecture.id,
    )

    // Initialize planType based on initial pager page
    LaunchedEffect(Unit) {
        planType.value = if (pagerState.currentPage == 0) "PERIOD" else "TIME"
    }

    Scaffold(
        topBar = { PlanTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold 패딩 추가
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(
                lecture = lecture,
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                planType = planType
            )
            HorizontalDivider(color = dividerGray)

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
            ) { page ->
                when (page) {
                    0 -> PeriodPlanPage(
                        startLessonId = startLessonId,
                        endLessonId = endLessonId,
                        studyDayOfWeeks = studyDayOfWeeks,
                        startDate = startDate,
                        endDate = endDate,
                        playbackSpeed = playbackSpeed
                    )
                    1 -> TimePlanPage(
                        startLessonId = startLessonId,
                        endLessonId = endLessonId,
                        studyDayOfWeeks = studyDayOfWeeks,
                        dailyTime = dailyTime,
                        playbackSpeed = playbackSpeed
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = {
                        val dto = PostPlanDto(
                            lectureId = lecture.id,
                            planType = planType.value,
                            startLessonId = startLessonId.intValue,
                            endLessonId = endLessonId.intValue,
                            studyDayOfWeeks = studyDayOfWeeks.value,
                            dailyTime = 120, // 시간 방식일 때 설정된 시간 (예시 값)
                            startDate = startDate.value,
                            endDate = endDate.value,
                            playbackSpeed = playbackSpeed.doubleValue
                        )

                        // TODO: 서버에 dto 전달하는 API 호출 작성
                        planViewModel.postPlanDetail(dto)
                        // 이후 원래 화면으로 돌아온다
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("완료")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanTopBar() {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
                    // 뒤로가기 or 닫기 로직
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_arrow_back), // 너가 추가한 xml 이름
                        contentDescription = "뒤로 가기")
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
fun HeaderSection(
    lecture: Lecture,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    planType: MutableState<String>
) {
    Column(
        modifier = Modifier
            .background(backgroundGray)
            .padding(16.dp)

    ) {
        // 인강 플랫폼
        Text(
            text = lecture.platform,
            style = MaterialTheme.typography.labelLarge,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // 제목
        Text(
            text = lecture.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // 세부정보
        Text(
            text = "${lecture.teacher} · ${lecture.tag} · ${lecture.totalLessons}강",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isPeriodSelected = pagerState.currentPage == 0
            val isTimeSelected = pagerState.currentPage == 1

            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(0)
                        planType.value = "PERIOD"
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPeriodSelected) MainPurple else Color.White,
                    contentColor = if (isPeriodSelected) Color.White else textGray
                ),
                border = if (isPeriodSelected) null else ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f),
            ) {
                Text("기간으로 계획하기")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(1)
                        planType.value = "TIME"
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimeSelected) MainPurple else Color.White,
                    contentColor = if (isTimeSelected) Color.White else textGray
                ),
                border = if (isTimeSelected) null else ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.weight(1f),
            ) {
                Text("시간으로 계획하기")
            }
        }
    }
}


@Composable
fun PeriodPlanPage(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    studyDayOfWeeks: MutableState<List<String>>,
    startDate: MutableState<String>,
    endDate: MutableState<String>,
    playbackSpeed: MutableState<Double>
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        DurationSection(startDate, endDate)
        StudyDaysOfWeekSection(studyDayOfWeeks)
        StartEndLectureSection(startLessonId, endLessonId)
        PlaybackSpeedSection(playbackSpeed)
    }
}



@Composable
fun TimePlanPage(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    studyDayOfWeeks: MutableState<List<String>>,
    dailyTime: MutableState<Int>,
    playbackSpeed: MutableState<Double>
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        StudyTimeSection(dailyTime)
        StudyDaysOfWeekSection(studyDayOfWeeks)
        StartEndLectureSection(startLessonId, endLessonId)
        PlaybackSpeedSection(playbackSpeed)
    }
}

@Composable
fun DurationSection(
    startDate: MutableState<String>,
    endDate: MutableState<String>
) {
    // 모달 제어
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // 날짜 선택 UI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "학습 시작일",
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.titleSmall,
            )

            OutlinedTextField(
                value = startDate.value,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_calendar), // 너가 추가한 xml 이름
                            contentDescription = "시작일 선택"
                        )
                    }
                }
            )
        }

        Column(modifier = Modifier.weight(1f)) {
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
                            painter = painterResource(id = R.drawable.icon_calendar), // 너가 추가한 xml 이름
                            contentDescription = "종료일 선택"
                        )
                    }
                }
            )
        }
    }

    if (showStartDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                startDate.value = formatDate(millis)
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                endDate.value = formatDate(millis)
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun StudyTimeSection(dailyTime: MutableState<Int>) {
    // 하루에 공부할 시간
    var studyMins by remember { mutableStateOf(dailyTime.value.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "일일 학습 시간 (분)",
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.titleSmall,
            )

            OutlinedTextField(
                value = studyMins,
                onValueChange = { newValue ->
                    // 숫자만 허용
                    if (newValue.all { it.isDigit() }) {
                        studyMins = newValue
                        dailyTime.value = newValue.toIntOrNull() ?: 0
                    }
                },
                placeholder = { Text("예: 120") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StudyDaysOfWeekSection(studyDayOfWeeks: MutableState<List<String>>) {
    val selectedDays = remember { mutableStateOf(studyDayOfWeeks.value.toSet()) }

    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "공부 일정",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            DayOfWeek.entries.forEach { day ->
                val isSelected = selectedDays.value.contains(day.name)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedDays.value = if (isSelected)
                            selectedDays.value - day.name
                        else
                            selectedDays.value + day.name

                        // 외부 상태 업데이트 (enum.name 기준으로 저장)
                        studyDayOfWeeks.value = selectedDays.value.toList()
                    },
                    label = { Text(day.label) }, // 표시는 label ("월", "화", ...)
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LightPurple,
                        containerColor = chipGray,
                        selectedLabelColor = MainPurple,
                        labelColor = Color.Black
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = MainPurple,
                    )
                )
            }
        }
    }
}

@Composable
fun StartEndLectureSection(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>
) {
    // 시작 강의 / 마지막 강의
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "시작 강의",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = "강의 선택",
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { /* 강의 목록 드롭다운 */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_nav_arrow_down),
                        contentDescription = "시작 강의 선택"
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "마지막 강의",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = "강의 선택",
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { /* 강의 목록 드롭다운 */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_nav_arrow_down),
                        contentDescription = "마지막 강의 선택"
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PlaybackSpeedSection(playbackSpeed: MutableState<Double>) {
    var speed by remember { mutableFloatStateOf(1.0f) } // 기본값 1.0배속

    Column {
        // "배속" + 현재 배속 값
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "배속",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = String.format("%.1fx", speed),
                style = MaterialTheme.typography.titleSmall,
                color = MainPurple,
            )
        }

        // 슬라이더 위 라벨 (1.0x ~ 2.0x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1.0x", color = Color.Gray, style = MaterialTheme.typography.titleSmall)
            Text("2.0x", color = Color.Gray, style = MaterialTheme.typography.titleSmall)
        }

        Slider(
            value = speed,
            onValueChange = { speed = it },
            valueRange = 1.0f..2.0f,
            steps = 10, // 소수점 단위로 조절 (0.1 단위로 1.0 ~ 2.0)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PlanScreenPreview() {
//    CapstonTheme {
//        PlanScreen(Lecture())
//    }
//}
