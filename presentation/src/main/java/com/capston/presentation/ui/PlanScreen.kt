package com.capston.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.capston.domain.model.Lecture
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long?): String {
    return if (millis != null) {
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        sdf.format(Date(millis))
    } else {
        ""
    }
}

@Composable
fun PlanScreen(lecture: Lecture) {
    val startDate = remember { mutableStateOf("") }
    val endDate = remember { mutableStateOf("") }
    val selectedDays = remember { mutableStateOf(emptyList<String>()) }
    val startLessonId = remember { mutableIntStateOf(0) }
    val endLessonId = remember { mutableIntStateOf(0) }
    val playbackSpeed = remember { mutableDoubleStateOf(1.0) }

    val pagerState = rememberPagerState(pageCount = { 2 }) // 0: 기간, 1: 시간
    val coroutineScope = rememberCoroutineScope()

    val postPlanDto = PostPlanDto(
        lectureId = lecture.id,
    )

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
                coroutineScope = coroutineScope
            )
            HorizontalDivider(color = dividerGray)

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.padding(bottom = 16.dp)
            ) { page ->
                when (page) {
                    0 -> PeriodPlanPage(
                        startDate = startDate,
                        endDate = endDate,
                        selectedDays = selectedDays,
                        startLessonId = startLessonId,
                        endLessonId = endLessonId,
                        playbackSpeed = playbackSpeed
                    )
                    1 -> TimePlanPage(
                        startDate = startDate,
                        endDate = endDate,
                        selectedDays = selectedDays,
                        startLessonId = startLessonId,
                        endLessonId = endLessonId,
                        playbackSpeed = playbackSpeed
                    )
                }
            }

            Button(
                onClick = {
                    val dto = PostPlanDto(
                        lectureId = lecture.id,
                        planType = if (pagerState.currentPage == 0) "PERIOD" else "TIME",
                        startLessonId = startLessonId.value,
                        endLessonId = endLessonId.value,
                        studyDayOfWeeks = selectedDays.value,
                        dailyTime = 120, // 시간 방식일 때 설정된 시간 (예시 값)
                        startDate = startDate.value,
                        endDate = endDate.value,
                        playbackSpeed = playbackSpeed.value
                    )

                    // TODO: 서버에 dto 전달하는 API 호출 작성
                    // ex) viewModel.postPlan(dto)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("완료")
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
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = Modifier
            .background(backgroundGray)
            .padding(16.dp)

    ) {
        // 인강 플랫폼
        Text(lecture.platform, color = MainPurple)
        // 제목
        Text(
            text = lecture.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // 세부정보
        Text(
            text = "${lecture.teacher}· ${lecture.tag} · ${lecture.totalLessons}강",
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
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPeriodSelected) materialGray else Color.White,
                    contentColor = if (isPeriodSelected) Color.White else materialGray
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
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimeSelected) materialGray else Color.White,
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
    startDate: MutableState<String>,
    endDate: MutableState<String>,
    selectedDays: MutableState<List<String>>,
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    playbackSpeed: MutableState<Double>
) {
    // 요일 선택 상태 예시
    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
    val (selectedDays, setSelectedDays) = remember { mutableStateOf(setOf<String>()) }

    // 날짜 상태
    var startDate by remember { mutableStateOf("시작일 선택") }
    var endDate by remember { mutableStateOf("종료일 선택") }

    // 모달 제어
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // 날짜 선택 UI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "학습 시작일",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
//                    label = { Text("DOB") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "목표 완강일",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
//                    label = { Text("DOB") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = !showEndDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    }
                )
            }
        }

        if (showStartDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    startDate = formatDate(millis)
                },
                onDismiss = { showStartDatePicker = false }
            )
        }

        if (showEndDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    endDate = formatDate(millis)
                },
                onDismiss = { showEndDatePicker = false }
            )
        }

        // 공부 일정
        Text(
            text = "공부 일정",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            daysOfWeek.forEach { day ->
                val isSelected = selectedDays.contains(day)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        setSelectedDays(
                            if (isSelected) selectedDays - day else selectedDays + day
                        )
                    },
                    label = { Text(day) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 시작 강의 / 마지막 강의
        Column {
            Text(
                text = "시작 강의",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = "강의 선택",
                onValueChange = { },
//                    label = { Text("DOB") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Text(
                text = "마지막 강의",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = "강의 선택",
                onValueChange = { },
//                    label = { Text("DOB") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 배속 선택
        PlaybackSpeedSlider()
    }
}



@Composable
fun TimePlanPage(
    startDate: MutableState<String>,
    endDate: MutableState<String>,
    selectedDays: MutableState<List<String>>,
    startLessonId: MutableState<Int>,
    endLessonId: MutableState<Int>,
    playbackSpeed: MutableState<Double>
) {
    // 요일 선택 상태 예시
    val daysOfWeek = DayOfWeek.entries.map { it.label }
    val (selectedDays, setSelectedDays) = remember { mutableStateOf(setOf<String>()) }

    // 날짜 상태
    var startDate by remember { mutableStateOf("시작일 선택") }
    var endDate by remember { mutableStateOf("종료일 선택") }

    // 모달 제어
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column {
        // 날짜 선택 UI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "일일 학습 시간",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = "학습 시간 설정",
                    onValueChange = { },
//                    label = { Text("DOB") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select date"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()

                )
            }
        }

        if (showStartDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    startDate = formatDate(millis)
                },
                onDismiss = { showStartDatePicker = false }
            )
        }

        if (showEndDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    endDate = formatDate(millis)
                },
                onDismiss = { showEndDatePicker = false }
            )
        }

        // 공부 일정
        Text(
            text = "공부 일정",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            daysOfWeek.forEach { day ->
                val isSelected = selectedDays.contains(day)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        setSelectedDays(
                            if (isSelected) selectedDays - day else selectedDays + day
                        )
                    },
                    label = { Text(day) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 시작 강의 / 마지막 강의
        Column {
            Text(
                text = "시작 강의",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = "강의 선택",
                onValueChange = { },
//                    label = { Text("DOB") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        Column {
            Text(
                text = "마지막 강의",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = "강의 선택",
                onValueChange = { },
//                    label = { Text("DOB") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = !showStartDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // 배속 선택
        PlaybackSpeedSlider()
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
fun PlaybackSpeedSlider() {
    var speed by remember { mutableFloatStateOf(1.0f) } // 기본값 1.0배속

    Column {
        // "배속" + 현재 배속 값
        Row(
            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "배속",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = String.format("%.1fx", speed),
                color = MainPurple,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 슬라이더 위 라벨 (1.0x ~ 2.0x)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1.0x", color = Color.Gray)
            Text("2.0x", color = Color.Gray)
        }

        Slider(
            value = speed,
            onValueChange = { speed = it },
            valueRange = 1.0f..2.0f,
            steps = 10 // 소수점 단위로 조절 (0.1 단위로 1.0 ~ 2.0)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    CapstonTheme {
        PlanScreen(Lecture())
    }
}
