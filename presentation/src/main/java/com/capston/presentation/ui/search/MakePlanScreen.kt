package com.capston.presentation.ui.search

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.ParseException
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
    planViewModel: PlanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
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

    // State for validation error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val planType = remember { mutableStateOf("PERIOD") }
    val startLessonId = remember { mutableIntStateOf(0) }
    val endLessonId = remember { mutableIntStateOf(0) }
    val studyDayOfWeeks = remember { mutableStateOf(emptyList<String>()) }
    val dailyTime = remember { mutableIntStateOf(120) } // Default to 120 mins
    val startDate = remember { mutableStateOf("시작일 선택") }
    val endDate = remember { mutableStateOf("종료일 선택") }
    val playbackSpeed = remember { mutableDoubleStateOf(1.0) }

    val pagerState = rememberPagerState(pageCount = { 2 }) // 0: 기간, 1: 시간
    val coroutineScope = rememberCoroutineScope()

    // Load lessons when lecture is selected
    LaunchedEffect(lecture.id) {
        if (lecture.id != 0) {  // Check if a valid lecture is selected
            lectureViewModel.getLessonsByLectureId(lecture.id)
        }
    }

    // Initialize planType based on initial pager page
    LaunchedEffect(Unit) {
        planType.value = if (pagerState.currentPage == 0) "PERIOD" else "TIME"
    }

    // Update planType when page changes
    LaunchedEffect(pagerState.currentPage) {
        planType.value = if (pagerState.currentPage == 0) "PERIOD" else "TIME"
    }

    // Validation functions
    val validateInputs = {
        when {
            startDate.value == "시작일 선택" || endDate.value == "종료일 선택" -> {
                errorMessage = "시작일과 종료일을 모두 선택해주세요."
                false
            }
            !isStartDateBeforeEndDate(startDate.value, endDate.value) -> {
                errorMessage = "시작일은 종료일보다 이전이어야 합니다."
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
            studyDayOfWeeks.value.isEmpty() -> {
                errorMessage = "최소 하나 이상의 요일을 선택해주세요."
                false
            }
            dailyTime.intValue <= 0 && planType.value == "TIME" -> {
                errorMessage = "일일 학습 시간을 설정해주세요."
                false
            }
            else -> true
        }
    }

    Scaffold(
        topBar = { PlanTopBar(navController = navController) }
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
                        playbackSpeed = playbackSpeed,
                        lectureViewModel = lectureViewModel
                    )
                    1 -> TimePlanPage(
                        startLessonId = startLessonId,
                        endLessonId = endLessonId,
                        studyDayOfWeeks = studyDayOfWeeks,
                        dailyTime = dailyTime,
                        playbackSpeed = playbackSpeed,
                        lectureViewModel = lectureViewModel
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Button(
                    onClick = {
                        if (validateInputs()) {
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

                            planViewModel.postNewPlan(dto)

                            // 액티비티 종료 추가
                            (context as? ComponentActivity)?.finish()
                        } else {
                            showErrorDialog = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("완료")
                }
            }
        }
    }

    // Error dialog
    if (showErrorDialog) {
        AlertDialog(
            containerColor = Color.White,
            iconContentColor = Color.Black,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            tonalElevation = 0.dp, // 그림자 효과 제거
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

// Helper function to validate date order
fun isStartDateBeforeEndDate(startDateStr: String, endDateStr: String): Boolean {
    if (startDateStr == "시작일 선택" || endDateStr == "종료일 선택") return false

    try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDate = dateFormat.parse(startDateStr)
        val endDate = dateFormat.parse(endDateStr)

        return startDate?.before(endDate) ?: false
    } catch (e: ParseException) {
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanTopBar(navController: NavController) {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_arrow_back),
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
    playbackSpeed: MutableState<Double>,
    lectureViewModel: LectureViewModel
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        DurationSection(startDate, endDate)
        StudyDaysOfWeekSection(studyDayOfWeeks)
        StartEndLectureSection(startLessonId, endLessonId, lectureViewModel)
        PlaybackSpeedSection(playbackSpeed)
    }
}

@Composable
fun TimePlanPage(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    studyDayOfWeeks: MutableState<List<String>>,
    dailyTime: MutableState<Int>,
    playbackSpeed: MutableState<Double>,
    lectureViewModel: LectureViewModel
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        StudyTimeSection(dailyTime)
        StudyDaysOfWeekSection(studyDayOfWeeks)
        StartEndLectureSection(startLessonId, endLessonId, lectureViewModel)
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
                            painter = painterResource(id = R.drawable.icon_calendar),
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
                            painter = painterResource(id = R.drawable.icon_calendar),
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
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = Color.White,
            selectedDayContainerColor = MainPurple,
        ),
        tonalElevation = 0.dp, // 이걸 추가해줘야 배경에 투명 레이어 없음
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("확인", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Black)
            }
        },
    ) {
        DatePicker(
            title = null,
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .background(Color.White)
                .padding(top = 32.dp)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyDaysOfWeekSection(studyDayOfWeeks: MutableState<List<String>>) {
    // 현재 선택된 요일들을 표현하는 Set (enum의 name 값 - "MON", "TUE" 등)
    val selectedDays = remember { mutableStateOf(studyDayOfWeeks.value.toSet()) }

    Column(
        modifier = Modifier.padding(bottom = 16.dp),
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            Text(
                text = "공부 일정",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "모두 선택",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .clickable {
                        val allDays = DayOfWeek.entries.map { it.name }
                        val isAllSelected = selectedDays.value.containsAll(allDays)

                        selectedDays.value = if (isAllSelected) emptySet() else allDays.toSet()
                        studyDayOfWeeks.value = selectedDays.value.toList()
                    },
                color = MainPurple
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            DayOfWeek.entries.forEach { day ->
                // 현재 요일이 선택되어 있는지 확인
                val isSelected = selectedDays.value.contains(day.name)

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            // 선택/해제 토글
                            selectedDays.value = if (isSelected)
                                selectedDays.value - day.name
                            else
                                selectedDays.value + day.name

                            // 외부 상태 업데이트 - enum의 name 값을 리스트로 저장
                            // (예: ["MON", "WED", "FRI"])
                            studyDayOfWeeks.value = selectedDays.value.toList()
                        },
                        label = { Text(day.label) }, // 표시는 한글 레이블 ("월", "화", ...)
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
}

@Composable
fun StartEndLectureSection(
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    lectureViewModel: LectureViewModel
) {
    // Collect lessons from viewModel
    val lessons by lectureViewModel.lessonsByLectureId.collectAsState()

    // States to store selected lesson titles
    var startLessonTitle by remember { mutableStateOf("강의 선택") }
    var endLessonTitle by remember { mutableStateOf("강의 선택") }

    // Dropdown visibility states
    var startDropdownExpanded by remember { mutableStateOf(false) }
    var endDropdownExpanded by remember { mutableStateOf(false) }

    // Update titles when IDs change or lessons load
    LaunchedEffect(startLessonId.value, lessons) {
        if (startLessonId.value > 0) {
            val selectedLesson = lessons.find { it.id == startLessonId.value }
            if (selectedLesson != null) {
                startLessonTitle = selectedLesson.title
            }
        }
    }

    LaunchedEffect(endLessonId.value, lessons) {
        if (endLessonId.value > 0) {
            val selectedLesson = lessons.find { it.id == endLessonId.value }
            if (selectedLesson != null) {
                endLessonTitle = selectedLesson.title
            }
        }
    }

    // 시작 강의 / 마지막 강의
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "시작 강의",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box {
            OutlinedTextField(
                value = startLessonTitle,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { startDropdownExpanded = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_nav_arrow_down),
                            contentDescription = "시작 강의 선택"
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = startDropdownExpanded,
                onDismissRequest = { startDropdownExpanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .heightIn(max = 250.dp)
                    .width(with(LocalDensity.current) {
                        // Calculate width to match the text field
                        (LocalConfiguration.current.screenWidthDp.dp - 32.dp).toPx().toInt().toDp()
                    }),
            ) {
                if (lessons.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("강의를 불러오는 중...") },
                        onClick = { }
                    )
                } else {
                    lessons.forEach { lesson ->
                        DropdownMenuItem(
                            text = { Text(lesson.title) },
                            onClick = {
                                startLessonId.value = lesson.id
                                startLessonTitle = lesson.title
                                startDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = "마지막 강의",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box {
            OutlinedTextField(
                value = endLessonTitle,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { endDropdownExpanded = true }) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_nav_arrow_down),
                            contentDescription = "마지막 강의 선택"
                        )
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = endDropdownExpanded,
                onDismissRequest = { endDropdownExpanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .heightIn(max = 250.dp)
                    .width(with(LocalDensity.current) {
                        // Calculate width to match the text field
                        (LocalConfiguration.current.screenWidthDp.dp - 32.dp).toPx().toInt().toDp()
                    })
            ) {
                if (lessons.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("강의를 불러오는 중...") },
                        onClick = { }
                    )
                } else {
                    lessons.forEach { lesson ->
                        DropdownMenuItem(
                            text = { Text(lesson.title) },
                            onClick = {
                                endLessonId.value = lesson.id
                                endLessonTitle = lesson.title
                                endDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybackSpeedSection(playbackSpeed: MutableState<Double>) {
    var speed by remember { mutableFloatStateOf(1.0f) } // 기본값 1.0배속

    // Update the external state when the slider value changes
    LaunchedEffect(speed) {
        playbackSpeed.value = speed.toDouble()
    }

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