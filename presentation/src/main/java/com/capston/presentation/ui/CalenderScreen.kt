package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.LightGray4
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.MainPurple
import java.time.LocalDate
import java.time.YearMonth
import com.capston.presentation.ui.BottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen() {
    var calendarHeight by remember { mutableStateOf(400) } // 달력의 초기 높이
    var lessonListHeight by remember { mutableStateOf(250) } // 할일 목록의 초기 높이


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Calendar(calendarHeight) { delta ->
                // lessonListHeight 변경 시, 그에 맞춰 calendarHeight도 조정
                lessonListHeight = (lessonListHeight + delta).coerceIn(100F, 600F).toInt()
                calendarHeight = (calendarHeight - delta).coerceIn(200F, 600F).toInt()
            }

            // 할일 목록을 크기 조정 가능하게 만드는 부분
            Box(modifier = Modifier.weight(1f)) {
                LessonList(lessonListHeight) // LessonList가 바텀 바 아래로 내려가지 않음
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendar(calendarHeight: Int, onDrag: (Float) -> Unit) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    val today = LocalDate.now()

    fun updateMonth(year: Int, month: Int) {
        var newYear = year
        var newMonth = month

        // 월이 0이면 이전 해로 이동
        if (newMonth < 1) {
            newYear -= 1
            newMonth = 12
        }
        // 월이 13이면 다음 해로 이동
        else if (newMonth > 12) {
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
            onDateSelected = { selectedDate = it },
            onMonthChanged = { newYear, newMonth -> updateMonth(newYear, newMonth) },
            onDrag = onDrag,
            calendarHeight = calendarHeight
        )

        // 선택된 날짜 표시
        selectedDate?.let {
            val dayOfWeek = it.dayOfWeek.name // 영어 요일
            val koreanDayOfWeek = getKoreanDayOfWeek(it.dayOfWeek.value) // 한국어 요일 변환
            Text("${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일 (${koreanDayOfWeek})",
                Modifier.padding(start = 20.dp, top = 10.dp), fontSize = 20.sp)
        } ?: run {
            // 선택된 날짜가 없으면 오늘 날짜 표시
            today?.let {
                val dayOfWeek = it.dayOfWeek.name // 영어 요일
                val koreanDayOfWeek = getKoreanDayOfWeek(it.dayOfWeek.value) // 한국어 요일 변환
                Text("${it.year}년 ${it.monthValue}월 ${it.dayOfMonth}일 (${koreanDayOfWeek})",
                    Modifier.padding(start = 20.dp, top = 10.dp), fontSize = 20.sp)
            }
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
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (Int, Int) -> Unit,
    onDrag: (Float) -> Unit,
    calendarHeight: Int
) {
    val today = LocalDate.now()
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0(일) ~ 6(토)

    val days = (1..daysInMonth).map { firstDayOfMonth.plusDays((it - 1).toLong()) }
    val emptyDays = List(firstDayOfWeek) { null } // 빈칸 채우기

    val dayOfWeekMap = listOf(
        "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" // 영어 요일
    )

    Box(
        modifier = Modifier
            .background(color = LightGray3) // 배경
            .border(width = 1.dp, color = LightGray4)
            .height(calendarHeight.dp)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onDrag(dragAmount.y) // 위아래 드래그 시 크기 변화
                }
            }
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
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onMonthChanged(year, month + 1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "다음 달")
                }
            }

            // 영어 요일 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dayOfWeekMap) { day ->
                    Text(
                        text = day, // 영어 요일 출력
                        color = LightGray40,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp).fillMaxWidth()
                    )
                }
            }

            // 날짜 표시
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(emptyDays + days) { date ->
                    if (date == null) {
                        Box(modifier = Modifier.height(40.dp)) // 빈칸 처리
                    } else {
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val backgroundColor = if (isSelected) MainPurple else Color.Transparent
                        val borderColor = if (isToday) MainPurple else Color.Transparent
                        val textColor = when {
                            isSelected -> Color.White   // 선택된 날짜: 흰색 글자
                            isToday -> MainPurple       // 오늘 날짜: 보라색 글자
                            else -> Color.Black         // 일반 날짜: 검정색 글자
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, borderColor, shape = RoundedCornerShape(20.dp))
                                .clickable { onDateSelected(date) },
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
                }
            }
        }

        // 고정된 크기
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter) // 캘린더 하단에 배치
                .padding(top = 10.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.calender_screen_adjust_line_iv),
                contentDescription = "adjust line",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp) // 이미지 크기 고정
                    .padding(top = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CapstonTheme {
        CalenderScreen()
    }
}