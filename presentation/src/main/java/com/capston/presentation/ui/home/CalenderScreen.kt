package com.capston.presentation.ui.home

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray
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
                // 드래그 감지 시 (수직 스크롤)
                if (source == NestedScrollSource.Drag && abs(available.y) > 3f) {
                    val delta = available.y * 0.02f // 감도 조절 가능

                    // 드래그 방향 확인
                    val isDraggingUp = available.y < 0
                    val isDraggingDown = available.y > 0

                    // 달력 상태 확인
                    val isCalendarAtMax = calendarExpandRatio >= 1f
                    val isCalendarAtMin = calendarExpandRatio <= 0.2f

                    // 우선순위 1: 달력이 최대/최소가 아닐 때는 항상 달력 조절 먼저
                    if (!isCalendarAtMax && !isCalendarAtMin) {
                        calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                        return available // 이벤트 소비
                    }

                    // 우선순위 2: 달력이 최소 상태에서 아래로 드래그 (확장) 또는
                    // 최대 상태에서 위로 드래그 (축소)하는 경우 달력 조절
                    if ((isCalendarAtMin && isDraggingDown) || (isCalendarAtMax && isDraggingUp)) {
                        calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                        return available // 이벤트 소비
                    }

                    // 그 외 경우는 목록 스크롤을 위해 이벤트 통과시킴
                    return Offset.Zero
                }
                return Offset.Zero // 다른 스크롤 이벤트는 무시하지 않음
            }

            // 이 메서드는 자식 컴포넌트의 스크롤이 최대/최소에 도달했을 때 호출됨
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // 자식이 소비하지 않은 스크롤이 있고, 드래그 중일 때
                if (source == NestedScrollSource.Drag && abs(available.y) > 3f) {
                    val delta = available.y * 0.02f

                    // 달력 조절 (목록 스크롤이 끝난 후 남은 스크롤 활용)
                    calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                    return available // 남은 스크롤 소비
                }
                return Offset.Zero
            }
        }
    }

    // 날짜 선택 콜백을 클릭 시간과 함께 추적
    val onDateSelectedWithTracking: (String) -> Unit = { date ->
        lastClickTime = System.currentTimeMillis()
        selectedDate = date
    }

    Scaffold(
        topBar = { CalendarTopBar(hasUnreadNotifications = true) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 16.dp, start = 20.dp, end = 20.dp) // 상단 + 좌우 패딩 추가
                .nestedScroll(nestedScrollConnection)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        val currentTime = System.currentTimeMillis()
                        if (abs(dragAmount) > 3f) {
                            val delta = dragAmount * 0.003f
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
                        maxHeight = (screenHeight * 1f).roundToPx(), // 화면 높이의 100%
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
                        Image(
                            painter = painterResource(R.drawable.screen_calender_empty_iv),
                            contentDescription = "과목명",
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        Text(
                            text = "선택한 날짜에 계획된 강의가 없어요",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = textGray,
                            style = MaterialTheme.typography.bodyMedium
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(hasUnreadNotifications: Boolean) {
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
                        Image(
                            painter = painterResource(R.drawable.icon_notification_on),
                            contentDescription = "alarm icon",
                        )
                    }
                }

                else {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Image(
                            painter = painterResource(R.drawable.home_screen_notification_iv),
                            contentDescription = "alarm icon",
                        )
                    }
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray2)
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

    // 선택된 날짜의 연도와 월
    val selectedYear = selectedLocalDate.year
    val selectedMonth = selectedLocalDate.monthValue

    // 확장 상태 변경 시 선택된 날짜의 연도와 월을 업데이트
    // 이전 확장 상태 추적
    var prevExpandRatio by remember { mutableStateOf(expandRatio) }

    // 확장 상태가 변경되었을 때 실행
    LaunchedEffect(expandRatio) {
        // 월간 뷰에서 주간 뷰로 전환하거나 주간 뷰에서 월간 뷰로 전환할 때
        // 기준점은 0.7f (0.7 이하는 주간 뷰, 0.7 초과는 월간 뷰)
        if ((prevExpandRatio > 0.7f && expandRatio <= 0.7f) ||
            (prevExpandRatio <= 0.7f && expandRatio > 0.7f)) {
            // 현재 표시 중인 연도/월과 선택된 날짜의 연도/월이 다른 경우
            if (currentYear != selectedYear || currentMonth != selectedMonth) {
                // 선택된 날짜의 연도와 월로 업데이트
                onYearMonthChanged(selectedYear, selectedMonth)
            }
        }

        // 현재 확장 상태 저장
        prevExpandRatio = expandRatio
    }

    // 현재 달의 날짜들 계산
    val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

    // 날짜 목록 생성 (이전 달의 마지막 날짜들 + 현재 달 + 다음 달의 첫 날짜들)
    val startOffset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek // 7은 일요일

    // 현재 선택된 날짜가 속한 주 계산 - 여기서는 월요일부터 일요일까지
    val currentWeekDays = getWeekDaysFromMonday(selectedLocalDate)

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

    // 요일 헤더
    val dayOfWeekMap = DayOfWeek.entries.toTypedArray()

    Box(
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = LightGray60, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp)) // 내용이 테두리를 넘지 않도록 클립
            .fillMaxWidth()
            .height(calendarHeight)
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
                        val prevWeekWednesday = currentWeekDays.first().plusDays(3).minusDays(7)
                        onDateSelected(prevWeekWednesday.format(formatter))
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
                        val nextWeekWednesday = currentWeekDays.first().plusDays(3).plusDays(7)
                        onDateSelected(nextWeekWednesday.format(formatter))
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                dayOfWeekMap.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.name,
                            color = LightGray40,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 날짜 그리드 - LazyVerticalGrid 대신 Column과 Row 사용
            if (expandRatio > 0.7f) {
                // 월간 뷰 - 일반 Column과 Row로 구현
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // 모든 날짜를 포함할 Calendar Grid 생성
                    CalendarGrid(
                        startOffset = startOffset,
                        daysInMonth = daysInMonth,
                        currentYear = currentYear,
                        currentMonth = currentMonth,
                        today = today,
                        selectedDate = selectedDate,
                        formatter = formatter,
                        onDateSelected = onDateSelected
                    )
                }
            } else {
                // 주간 뷰
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    currentWeekDays.forEach { date ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                CalendarDay(
                                    date = date,
                                    isToday = date == today,
                                    isSelected = date.format(formatter) == selectedDate,
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
fun CalendarGrid(
    startOffset: Int,
    daysInMonth: Int,
    currentYear: Int,
    currentMonth: Int,
    today: LocalDate,
    selectedDate: String,
    formatter: DateTimeFormatter,
    onDateSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 오프셋을 포함한 총 날짜 계산
        val totalDays = startOffset + daysInMonth
        val numRows = (totalDays + 6) / 7 // 행 개수 celling

        // 모든 날짜 리스트
        val allDays = mutableListOf<LocalDate?>()

        repeat(startOffset) {
            allDays.add(null)
        }

        // 실제 달의 날짜들
        for (day in 1..daysInMonth) {
            allDays.add(LocalDate.of(currentYear, currentMonth, day))
        }

        // 캘린더 그리드
        for (row in 0 until numRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < allDays.size) {
                            val date = allDays[index]
                            if (date != null) {
                                CalendarDay(
                                    date = date,
                                    isToday = date == today,
                                    isSelected = date.format(formatter) == selectedDate,
                                    isCurrentMonth = true,
                                    onClick = { onDateSelected(date.format(formatter)) }
                                )
                            } else {
                                // Empty cell
                                Box(modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                }
            }
        }
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
    val dayOfWeek = date.dayOfWeek.value  // 월=1 ~ 일=7
    val monday = date.minusDays((dayOfWeek).toLong()) // 월요일 기준으로 보정
    return (0..6).map { monday.plusDays(it.toLong()) } // 월 ~ 일
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerDialog(
    year: Int,
    month: Int,
    onDateSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val resources = context.resources

    // DatePickerDialog 테마 설정을 위한 스타일
    val spinnerTheme = android.R.style.Theme_Holo_Light_Dialog_MinWidth

    // DatePickerDialog 생성
    val dialog = remember {
        object : android.app.DatePickerDialog(
            context,
            spinnerTheme,
            { _, selectedYear, selectedMonth, _ ->
                // 선택된 연도와 월 반환 (월은 0부터 시작하므로 +1)
                onDateSelected(selectedYear, selectedMonth + 1)
            },
            year,
            month - 1,  // 월은 0부터 시작하므로 -1
            1  // 기본 일(day)은 의미 없음
        ) {
            // 다이얼로그가 생성되기 전에 실행되는 메서드
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                // 일(day) 선택 스피너 숨기기
                try {
                    // DatePicker 찾기
                    val datePicker = datePicker

                    // 클래스와 리소스 ID 찾기 (Android 버전마다 다를 수 있음)
                    val daySpinnerId = resources.getIdentifier("day", "id", "android")
                    if (daySpinnerId > 0) {
                        // 일(day) 스피너 찾기
                        val daySpinner = datePicker.findViewById<View>(daySpinnerId)
                        if (daySpinner != null) {
                            // 숨기기
                            daySpinner.visibility = View.GONE
                        }
                    }

                    // DatePicker 헤더에서 날짜 텍스트 변경
                    val datePickerHeaderClass = Class.forName("android.widget.DatePickerCalendarDelegate")
                    val delegateField = datePicker.javaClass.getDeclaredField("mDelegate")
                    delegateField.isAccessible = true
                    val delegate = delegateField.get(datePicker)

                    if (delegate != null && delegate.javaClass.name.contains("DatePickerCalendarDelegate")) {
                        // 헤더 날짜 형식 변경 (년/월만 표시)
                        val headerTextField = datePickerHeaderClass.getDeclaredField("mHeaderText")
                        headerTextField.isAccessible = true
                        var headerText = headerTextField.get(delegate) as TextView
                        headerText = "${year}년 ${month}월" as TextView
                    }

                    // 색상 변경 시도 - MainPurple 색상으로
                    val mainPurpleColor = MainPurple.toArgb()

                    // OK 버튼 색상 변경
                    val okButton = getButton(DialogInterface.BUTTON_POSITIVE)
                    okButton?.setTextColor(mainPurpleColor)

                    // Cancel 버튼 색상 변경
                    val cancelButton = getButton(DialogInterface.BUTTON_NEGATIVE)
                    cancelButton?.setTextColor(mainPurpleColor)

                } catch (e: Exception) {
                    // 리플렉션 오류 무시 (일부 기기나 Android 버전에서는 작동하지 않을 수 있음)
                    e.printStackTrace()
                }
            }
        }
    }

    // 다이얼로그 취소 시 처리
    DisposableEffect(key1 = dialog) {
        dialog.setOnCancelListener { onDismiss() }

        // 다이얼로그 표시
        dialog.show()

        // 정리
        onDispose {
            dialog.dismiss()
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

    // 확장 상태 추적 (드래그로 확장했는지 여부)
    var isExpanded by remember { mutableStateOf(false) }

    // 컨테이너 높이 - 확장 상태에 따라 결정
    val initialHeight = screenHeight * 0.6f // 축소 상태 높이
    val expandedHeight = screenHeight * 0.9f // 확장 상태 높이

    // 현재 높이 상태
    val containerHeight by animateDpAsState(
        targetValue = if (isExpanded) expandedHeight else initialHeight
    )

    // 드래그 핸들러
    val dragState = rememberDraggableState { delta ->
        // 드래그 방향에 따라 확장 상태 변경 (빠른 전환 방식)
        if (delta < -10) { // 위로 드래그하면 확장
            isExpanded = true
        } else if (delta > 10) { // 아래로 드래그하면 축소
            isExpanded = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(containerHeight)
    ) {
        // 내용 컨테이너
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            // 수정된 ModifiedLessonList를 사용하여 내용 표시
            ModifiedLessonList(
                homeViewModel = homeViewModel,
                maxHeight = containerHeight.value.toInt(),
                todayLessonList = todayLessonList,
                isExpanded = isExpanded // 확장 상태 전달
            )

//            // 축소 상태일 때만 그라데이션 오버레이와 힌트 표시
//            if (!isExpanded && todayLessonList.size > 3) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .fillMaxWidth()
//                        .height(40.dp)
//                        .background(
//                            brush = Brush.verticalGradient(
//                                colors = listOf(
//                                    Color.Transparent,
//                                    Color.White.copy(alpha = 0.9f)
//                                )
//                            )
//                        )
//                )
//            }
        }
    }
}


fun getKoreanDayOfWeek(dayOfWeek: Int): String {
    return DayOfWeek.entries.getOrNull((dayOfWeek).coerceIn(0, 6))?.label ?: ""
}
