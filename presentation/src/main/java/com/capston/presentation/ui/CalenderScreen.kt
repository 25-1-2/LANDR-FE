package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(homeViewModel: HomeViewModel, dailyScheduleViewModel: DailyScheduleViewModel) {
    var isCollapsed by remember { mutableStateOf(false) }
    val calendarHeight by animateDpAsState(targetValue = if (isCollapsed) 0.dp else 300.dp)

    var selectedDate by remember {
        mutableStateOf(
            LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        )
    } // 선택한 날짜 (String)

    Log.d("selected Date", selectedDate)
    // 선택한 날짜가 변경될 때 ViewModel을 통해 일정 가져오기
    LaunchedEffect(selectedDate) {
        dailyScheduleViewModel.getDailySchedule(selectedDate)
    }

    val dailyState by dailyScheduleViewModel.getDailySchedule.collectAsState()
    val todayLessonList = dailyState.lessonSchedules // 선택한 날짜의 일정

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 위로 스크롤 (접기)
                if (available.y < -10 && !isCollapsed) {
                    isCollapsed = true
                    return Offset.Zero
                }
                // 아래로 스크롤 (펼치기)
                if (available.y > 10 && isCollapsed) {
                    isCollapsed = false
                    return Offset.Zero
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
            Calendar(calendarHeight, selectedDate, onDateSelected = { date ->
                selectedDate = date // 선택한 날짜 업데이트
            })

            Box(modifier = Modifier.weight(1f)) {
                if (todayLessonList != null) {
                    LessonList(homeViewModel, 330, todayLessonList)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 50.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "선택한 날짜에 계획된 강의가 없어요 \uD83D\uDE0A\n",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = LightGray60
//                        )
                        /* 테스트용 더미 데이터
                        LazyColumn {
                            itemsIndexed(
                                listOf(100, 200, 300) // 1. 아이템 3개 생성
                            ) { index, item ->
                                KotlinWorldCard(order = item)
                            }
                        }
                         */
                    }
                }
            }
        }
    }

}

/* 테스트용 더미데이터
@Composable
fun KotlinWorldCard(order: Int) {
    Card(
        Modifier
            .padding(12.dp)
            .border(width = 4.dp, color = Color.Black)
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("Kotlin World ${order}")
        }
    }
}
*/

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(calendarHeight: Dp, selectedDate: String, onDateSelected: (String) -> Unit) {
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var showDatePickerDialog by remember { mutableStateOf(false) } // 다이얼로그 표시 여부

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
            onDateSelected = onDateSelected, // 선택한 날짜 업데이트
            onMonthChanged = { newYear, newMonth -> updateMonth(newYear, newMonth) },
            calendarHeight = calendarHeight,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    year: Int,
    month: Int,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    calendarHeight: Dp,
    onDatePickerClick: () -> Unit
) {
    val today = LocalDate.now()
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0(일) ~ 6(토)

    val days = (1..daysInMonth).map { firstDayOfMonth.plusDays((it - 1).toLong()) }
    val emptyDays = List(firstDayOfWeek) { null } // 빈칸 채우기

    val formatter = DateTimeFormatter.ISO_DATE // "YYYY-MM-DD" 포맷
    val dayOfWeekMap = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    Box(
        modifier = Modifier
            .background(color = LightGray3)
            .height(calendarHeight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                items(emptyDays + days) { date ->
                    if (date == null) {
                        Box(modifier = Modifier.height(40.dp))
                    } else {
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
                            Text(text = date.dayOfMonth.toString(), color = textColor, fontSize = 16.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
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