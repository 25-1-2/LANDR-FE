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
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(homeViewModel: HomeViewModel, dailyScheduleViewModel: DailyScheduleViewModel) {
    // 0.0 (최소 주간 표시) ~ 1.0 (전체 달력 표시) 사이의 연속적인 값
    var calendarExpandRatio by remember { mutableStateOf(1f) }

    // 화면 크기에 맞춘 상대적 높이 설정
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxCalendarHeight = screenHeight * 0.35f // 화면 높이의 35%
    val minCalendarHeight = screenHeight * 0.12f // 화면 높이의 12%

    // 드래그에 따라 달력 높이를 계산
    val calendarHeight by animateDpAsState(
        targetValue = minCalendarHeight + (maxCalendarHeight - minCalendarHeight) * calendarExpandRatio
    )

    var selectedDate by remember {
        mutableStateOf(
            LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        )
    }

    Log.d("selected Date", selectedDate)
    LaunchedEffect(selectedDate) {
        dailyScheduleViewModel.getDailySchedule(selectedDate)
    }

    val dailyState by dailyScheduleViewModel.getDailySchedule.collectAsState()
    val todayLessonList = dailyState.lessonSchedules

    // 드래그로 달력 크기를 조절하기 위한 스크롤 핸들러
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 드래그 양에 비례해서 달력 크기 조절
                val delta = available.y * 0.005f // 드래그 감도 조절 계수

                if (source == NestedScrollSource.Drag) {
                    // 0.2에서 1.0 사이로 제한하여 항상 주간 뷰는 보이도록 함
                    calendarExpandRatio = (calendarExpandRatio + delta).coerceIn(0.2f, 1f)
                    // 드래그 이벤트 소비
                    return available
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            // 높이 비율을 전달하여 달력이 이에 맞게 표시되도록 함
            Calendar(
                calendarHeight = calendarHeight,
                expandRatio = calendarExpandRatio,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            Box(modifier = Modifier.weight(1f)) {
                if (todayLessonList != null) {
                    // LessonContainer도 화면 크기에 맞게 상대적 높이 조정
                    DraggableLessonContainer(
                        homeViewModel = homeViewModel,
                        maxHeight = (screenHeight * 0.45f).roundToPx(), // 화면 높이의 45%
                        todayLessonList = todayLessonList
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = screenHeight * 0.05f), // 화면 높이의 5%
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
        (screenHeight * 0.7f).toPx() // 화면 높이의 70%까지 확장 가능
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
        // 드래그 핸들 제거 (달력 내부의 핸들만 사용)

        // Using your existing LessonList but removing its drag handling
        ModifiedLessonList(
            homeViewModel = homeViewModel,
            maxHeight = with(density) { containerHeight.toInt() },
            todayLessonList = todayLessonList
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(calendarHeight: Dp, expandRatio: Float, selectedDate: String, onDateSelected: (String) -> Unit) {
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    fun updateMonth(year: Int, month: Int) {
        var newYear = year
        var newMonth = month

        if (newMonth < 1) {
            newYear -= 1
            newMonth = 12
        } else if (newMonth > 12) {
            newYear += 1
            newMonth = 1
        }

        currentYear = newYear
        currentMonth = newMonth
    }

    Column {
        CustomCalendar(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected,
            onMonthChanged = { newYear, newMonth -> updateMonth(newYear, newMonth) },
            calendarHeight = calendarHeight,
            expandRatio = expandRatio,
            onDatePickerClick = { showDatePickerDialog = true }
        )

        Divider(
            color = LightGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        // 선택된 날짜 표시
        Text(
            text = "$selectedDate (${getKoreanDayOfWeek(LocalDate.parse(selectedDate).dayOfWeek.value)})",
            Modifier.padding(start = 20.dp, top = 10.dp), fontSize = 20.sp
        )

        // DatePickerDialog 다이얼로그
        if (showDatePickerDialog) {
            DatePickerDialog(year = currentYear, month = currentMonth, onDateSelected = { year, month ->
                currentYear = year
                currentMonth = month
                showDatePickerDialog = false
            })
        }
    }
}

fun getKoreanDayOfWeek(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        1 -> "월" // Sunday
        2 -> "화" // Monday
        3 -> "수" // Tuesday
        4 -> "목" // Wednesday
        5 -> "금" // Thursday
        6 -> "토" // Friday
        7 -> "일" // Saturday
        else -> ""
    }
}

// Dp to pixels 확장 함수
fun Dp.roundToPx(): Int {
    return value.roundToInt()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    year: Int,
    month: Int,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    calendarHeight: Dp,
    expandRatio: Float,
    onDatePickerClick: () -> Unit
) {
    val today = LocalDate.now()
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0(일) ~ 6(토)

    // 달력의 모든 일자 준비
    val days = (1..daysInMonth).map { firstDayOfMonth.plusDays((it - 1).toLong()) }
    val emptyDays = List(firstDayOfWeek) { null }
    val formatter = DateTimeFormatter.ISO_DATE
    val dayOfWeekMap = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    // 현재 주를 계산
    val todayDate = LocalDate.parse(selectedDate)
    val currentWeekDays = getWeekDays(todayDate)

    Box(
        modifier = Modifier
            .background(color = LightGray3)
            .height(calendarHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 상단 년/월 및 이동 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChanged(year, month - 1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "이전 달")
                }
                Text(
                    text = "${year}년 ${month}월",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDatePickerClick() }
                )
                IconButton(onClick = { onMonthChanged(year, month + 1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "다음 달")
                }
            }

            // 요일 표시
            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                items(dayOfWeekMap) { day ->
                    Text(
                        text = day,
                        color = LightGray40,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp).fillMaxWidth()
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                // 날짜 그리드 (전체 달력 또는 주간 달력)
                if (expandRatio > 0.7f) {
                    // 전체 달력 표시 (expandRatio가 높을 때)
                    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxSize()) {
                        items(emptyDays + days) { date ->
                            if (date == null) {
                                Box(modifier = Modifier.height(40.dp))
                            } else {
                                RenderCalendarDay(date, today, selectedDate, formatter, onDateSelected)
                            }
                        }
                    }
                } else {
                    // 주간 달력만 표시 (expandRatio가 낮을 때)
                    LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                        items(currentWeekDays) { date ->
                            if (date != null) {
                                RenderCalendarDay(date, today, selectedDate, formatter, onDateSelected)
                            } else {
                                Box(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }
        }

        // 드래그 핸들 표시 - 항상 표시되도록 Box 내에 배치
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
fun RenderCalendarDay(
    date: LocalDate,
    today: LocalDate,
    selectedDate: String,
    formatter: DateTimeFormatter,
    onDateSelected: (String) -> Unit
) {
    val dateStr = date.format(formatter)
    val isToday = date == today
    val isSelected = dateStr == selectedDate
    val backgroundColor = if (isSelected) MainPurple else Color.Transparent
    val borderColor = if (isToday) MainPurple else Color.Transparent
    val textColor = when {
        isSelected -> Color.White
        isToday -> MainPurple
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, shape = RoundedCornerShape(20.dp))
            .clickable { onDateSelected(dateStr) },
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
fun getWeekDays(date: LocalDate): List<LocalDate?> {
    // 현재 주의 월요일 구하기
    val monday = date.minusDays((date.dayOfWeek.value).toLong())

    // 월요일부터 시작하는 일주일 날짜 목록
    return (0..6).map { monday.plusDays(it.toLong()) }
}

@Composable
fun DatePickerDialog(year: Int, month: Int, onDateSelected: (Int, Int) -> Unit) {
    var selectedYear by remember { mutableStateOf(year) }
    var selectedMonth by remember { mutableStateOf(month) }

    Dialog(onDismissRequest = { /* Handle dismiss */ }) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("이동하려는 연도와 날짜를 선택하세요", style = MaterialTheme.typography.bodyMedium)

                // 연도 선택
                Text("연도: $selectedYear")
                Slider(
                    value = selectedYear.toFloat(),
                    onValueChange = { selectedYear = it.toInt() },
                    valueRange = 2024f..2050f,
                    steps = 100
                )

                // 월 선택
                Text("월: $selectedMonth")
                Slider(
                    value = selectedMonth.toFloat(),
                    onValueChange = { selectedMonth = it.toInt() },
                    valueRange = 1f..12f,
                    steps = 10,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onDateSelected(selectedYear, selectedMonth)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("이동")
                }
            }
        }
    }
}