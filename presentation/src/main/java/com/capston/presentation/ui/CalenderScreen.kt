package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt

// Dp to pixels 확장 함수
fun Dp.roundToPx(): Int {
    return value.roundToInt()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(homeViewModel: HomeViewModel, dailyScheduleViewModel: DailyScheduleViewModel) {
    // 0.0 (최소 주간 표시) ~ 1.0 (전체 달력 표시) 사이의 연속적인 값
    var calendarExpandRatio by remember { mutableStateOf(1f) }

    // 화면 크기에 맞춘 상대적 높이 설정
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxCalendarHeight = screenHeight * 0.35f // 화면 높이의 35%
    val minCalendarHeight = screenHeight * 0.15f // 화면 높이의 15%

    // 드래그에 따라 달력 높이를 계산
    val calendarHeight by animateDpAsState(
        targetValue = minCalendarHeight + (maxCalendarHeight - minCalendarHeight) * calendarExpandRatio
    )

    // 날짜 관련 상태 - 현재 월과 선택된 날짜를 함께 관리
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var selectedDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE)) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    // 마지막 클릭 시간을 추적 - 클릭과 드래그 구분을 위해
    var lastClickTime by remember { mutableStateOf(0L) }
    val clickCooldownMs = 200 // 클릭 후 드래그 무시 시간 (ms)

    // 선택된 날짜가 변경되면 일정 데이터 로드
    LaunchedEffect(selectedDate) {
        dailyScheduleViewModel.getDailySchedule(selectedDate)
    }

    val dailyState by dailyScheduleViewModel.getDailySchedule.collectAsState()
    val todayLessonList = dailyState.lessonSchedules

    // 드래그로 달력 크기를 조절하기 위한 스크롤 핸들러
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 현재 시간 체크
                val currentTime = System.currentTimeMillis()

                // 클릭 후 일정 시간 내의 드래그는 무시하지 않음
                if (source == NestedScrollSource.Drag && abs(available.y) > 3f) {
                    // 드래그 감도 계수
                    val delta = available.y * 0.007f  // 감도 약간 증가

                    // 드래그 양에 비례해서 달력 크기 조절 (0.2~1.0 범위)
                    calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                    return available // 드래그 이벤트 소비
                }
                return Offset.Zero // 다른 스크롤 이벤트는 무시
            }
        }
    }

    // 날짜 선택 콜백을 클릭 시간과 함께 추적
    val onDateSelectedWithTracking: (String) -> Unit = { date ->
        lastClickTime = System.currentTimeMillis()
        selectedDate = date
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
                // 전체 화면에 포인터 입력 처리 추가 (더 안정적인 드래그 처리)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume() // 제스처 소비

                        // 현재 시간 체크
                        val currentTime = System.currentTimeMillis()

                        // 클릭 후 짧은 시간 내의 드래그도 처리하기
                        if (abs(dragAmount) > 3f) {
                            // 드래그 방향에 따라 캘린더 크기 조절
                            val delta = dragAmount * 0.003f  // 드래그 효과 조절 계수
                            calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                        }
                    }
                }
        ) {
            // 달력 부분
            SimpleCalendar(
                calendarHeight = calendarHeight,
                expandRatio = calendarExpandRatio,
                currentYear = currentYear,
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                onYearMonthChanged = { year, month ->
                    currentYear = year
                    currentMonth = month
                },
                onDateSelected = onDateSelectedWithTracking,
                onDatePickerClick = { showDatePickerDialog = true }
            )

            // 선택된 날짜 표시 및 구분선
            Divider(
                color = LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val localDate = LocalDate.parse(selectedDate)
                Text(
                    text = "$selectedDate (${getKoreanDayOfWeek(localDate.dayOfWeek.value)})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            // 일정 목록
            Box(modifier = Modifier.weight(1f)) {
                if (todayLessonList != null && todayLessonList.isNotEmpty()) {
                    // LessonContainer도 화면 크기에 맞게 상대적 높이 조정
                    DraggableLessonContainer(
                        homeViewModel = homeViewModel,
                        maxHeight = (screenHeight * 1f).roundToPx(), // 화면 높이의 45%
                        todayLessonList = todayLessonList
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = screenHeight * 0.05f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "선택한 날짜에 계획된 강의가 없어요 \uD83D\uDE0A\n",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = LightGray60
                        )
                    }
                }
            }

            // DatePickerDialog
            if (showDatePickerDialog) {
                DatePickerDialog(
                    year = currentYear,
                    month = currentMonth,
                    onDateSelected = { year, month ->
                        currentYear = year
                        currentMonth = month
                        // 선택한 달의 1일로 날짜 변경
                        val firstDayOfMonth = LocalDate.of(year, month, 1)
                        selectedDate = firstDayOfMonth.format(DateTimeFormatter.ISO_DATE)
                        showDatePickerDialog = false
                    },
                    onDismiss = {
                        showDatePickerDialog = false
                    }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SimpleCalendar(
    calendarHeight: Dp,
    expandRatio: Float,
    currentYear: Int,
    currentMonth: Int,
    selectedDate: String,
    onYearMonthChanged: (Int, Int) -> Unit,
    onDateSelected: (String) -> Unit,
    onDatePickerClick: () -> Unit
) {
    val selectedLocalDate = LocalDate.parse(selectedDate)
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ISO_DATE

    // 현재 달의 날짜들 계산
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

    // 날짜 목록 생성 (이전 달의 마지막 날짜들 + 현재 달 + 다음 달의 첫 날짜들)
    val startOffset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek // 7은 일요일

    val days = (1..daysInMonth).map { day ->
        LocalDate.of(currentYear, currentMonth, day)
    }

    val emptyDaysBefore = (0 until startOffset).map { null }

    // 현재 선택된 날짜가 속한 주 계산 - 여기서는 월요일부터 일요일까지
    val currentWeekDays = getWeekDaysFromMonday(selectedLocalDate)

    // 요일 헤더
    val dayOfWeekMap = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    // 주간 뷰일 때 제목에 표시할 문자열
    val weekRangeText = if (currentWeekDays.isNotEmpty()) {
        val start = currentWeekDays.first()
        val end = currentWeekDays.last()
        if (start != null && end != null) {
            "${start.year}년 ${start.monthValue}월 ${start.dayOfMonth}일 - ${end.monthValue}월 ${end.dayOfMonth}일"
        } else {
            "${currentYear}년 ${currentMonth}월"
        }
    } else {
        "${currentYear}년 ${currentMonth}월"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(calendarHeight)
            .background(color = LightGray3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 달력 헤더 - 년월 또는 주간 범위 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이전 달/주 버튼
                IconButton(onClick = {
                    if (expandRatio <= 0.7f) {
                        // 주간 뷰일 때: 이전 주로 이동
                        val prevWeekStart = currentWeekDays.firstOrNull()?.minusDays(7)
                        if (prevWeekStart != null) {
                            onDateSelected(prevWeekStart.format(formatter))
                        }
                    } else {
                        // 월간 뷰일 때: 이전 달로 이동
                        val newMonth = if (currentMonth == 1) {
                            onYearMonthChanged(currentYear - 1, 12)
                            12
                        } else {
                            onYearMonthChanged(currentYear, currentMonth - 1)
                            currentMonth - 1
                        }
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "이전")
                }

                // 년월 또는 주간 범위 표시
                Text(
                    text = if (expandRatio <= 0.7f) weekRangeText else "${currentYear}년 ${currentMonth}월",
                    fontSize = if (expandRatio <= 0.7f) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDatePickerClick() }
                )

                // 다음 달/주 버튼
                IconButton(onClick = {
                    if (expandRatio <= 0.7f) {
                        // 주간 뷰일 때: 다음 주로 이동
                        val nextWeekStart = currentWeekDays.lastOrNull()?.plusDays(1)
                        if (nextWeekStart != null) {
                            onDateSelected(nextWeekStart.format(formatter))
                        }
                    } else {
                        // 월간 뷰일 때: 다음 달로 이동
                        val newMonth = if (currentMonth == 12) {
                            onYearMonthChanged(currentYear + 1, 1)
                            1
                        } else {
                            onYearMonthChanged(currentYear, currentMonth + 1)
                            currentMonth + 1
                        }
                    }
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "다음")
                }
            }

            // 요일 헤더
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false
            ) {
                items(dayOfWeekMap) { day ->
                    Text(
                        text = day,
                        color = LightGray40,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }

            // 날짜 그리드 - userScrollEnabled를 false로 설정하여 드래그 이벤트가 LazyVerticalGrid에서 소비되지 않도록 함
            Box(modifier = Modifier.weight(1f)) {
                if (expandRatio > 0.7f) {
                    // 월간 뷰
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false
                    ) {
                        items(emptyDaysBefore + days) { date ->
                            if (date == null) {
                                // 빈 셀
                                Box(modifier = Modifier.size(40.dp))
                            } else {
                                // 현재 월의 날짜 표시 - 클릭 이벤트만 처리하고 드래그 이벤트는 상위 컴포넌트로 전달
                                val isCurrentMonth = date.monthValue == currentMonth
                                CalendarDay(
                                    date = date,
                                    isToday = date == today,
                                    isSelected = date.format(formatter) == selectedDate,
                                    isCurrentMonth = isCurrentMonth,
                                    onClick = { onDateSelected(date.format(formatter)) }
                                )
                            }
                        }
                    }
                } else {
                    // 주간 뷰
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxWidth(),
                        userScrollEnabled = false
                    ) {
                        items(currentWeekDays) { date ->
                            if (date != null) {
                                CalendarDay(
                                    date = date,
                                    isToday = date == today,
                                    isSelected = date.format(formatter) == selectedDate,
                                    // 주간 뷰에서는 월 구분을 시각적으로 표시하지 않음
                                    isCurrentMonth = true,
                                    onClick = { onDateSelected(date.format(formatter)) }
                                )
                            } else {
                                Box(modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                }
            }
        }

        // 드래그 핸들 (항상 보이도록 Box의 맨 위에 배치)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .width(60.dp)
                .height(4.dp)
                .background(Color.Gray, RoundedCornerShape(2.dp))
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDay(
    date: LocalDate,
    isToday: Boolean,
    isSelected: Boolean,
    isCurrentMonth: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MainPurple else Color.Transparent
    val borderColor = if (isToday) MainPurple else Color.Transparent
    val textColor = when {
        isSelected -> Color.White
        isToday -> MainPurple
        !isCurrentMonth -> LightGray60
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            // interactionSource만 사용하고 indication은 null로 설정
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // ripple 효과 대신 null 사용
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getWeekDaysFromMonday(date: LocalDate): List<LocalDate> {
    // 월요일(1) ~ 일요일(7)
    val dayOfWeek = date.dayOfWeek.value

    // 현재 날짜가 속한 주의 월요일 계산
    val monday = date.minusDays((dayOfWeek - 1).toLong())

    // 월요일부터 일요일까지 7일 반환
    return (0..6).map { monday.plusDays(it.toLong()) }
}

@Composable
fun DatePickerDialog(
    year: Int,
    month: Int,
    onDateSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(year) }
    var selectedMonth by remember { mutableStateOf(month) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "이동하려는 연도와 날짜를 선택하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 연도 선택
                Text("연도: $selectedYear", fontWeight = FontWeight.Medium)
                Slider(
                    value = selectedYear.toFloat(),
                    onValueChange = { selectedYear = it.toInt() },
                    valueRange = 2020f..2030f,
                    steps = 9  // 10년 간격 (2020-2030)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 월 선택
                Text("월: $selectedMonth", fontWeight = FontWeight.Medium)
                Slider(
                    value = selectedMonth.toFloat(),
                    onValueChange = { selectedMonth = it.toInt() },
                    valueRange = 1f..12f,
                    steps = 11,  // 12개월
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("취소")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onDateSelected(selectedYear, selectedMonth) }
                    ) {
                        Text("이동")
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableLessonContainer(
    homeViewModel: HomeViewModel,
    maxHeight: Int,
    todayLessonList: List<LessonScheduleResponse>
) {
    val density = LocalDensity.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 기본 높이를 화면 비율로 설정
    var containerHeight by remember {
        mutableStateOf(with(density) { maxHeight.toFloat() * 1.2f })
    }

    // 최대 컨테이너 높이도 화면 높이에 상대적으로 설정
    val maxContainerHeight = with(density) {
        (screenHeight * 0.8f).toPx() // 화면 높이의 80%까지 확장 가능
    }

    val dragState = rememberDraggableState { delta ->
        // Expand container based on drag amount, limited between maxHeight and screen height
        containerHeight = (containerHeight - delta).coerceIn(
            maxHeight.toFloat(),
            maxContainerHeight
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(density) { containerHeight.toDp() })
            .draggable(
                state = dragState,
                orientation = Orientation.Vertical
            )
    ) {
        // Using your existing LessonList but removing its drag handling
        ModifiedLessonList(
            homeViewModel = homeViewModel,
            maxHeight = with(density) { containerHeight.toInt() },
            todayLessonList = todayLessonList
        )
    }
}

fun getKoreanDayOfWeek(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "월" // Monday
        2 -> "화" // Tuesday
        3 -> "수" // Wednesday
        4 -> "목" // Thursday
        5 -> "금" // Friday
        6 -> "토" // Saturday
        7 -> "일" // Sunday
        else -> ""
    }
}