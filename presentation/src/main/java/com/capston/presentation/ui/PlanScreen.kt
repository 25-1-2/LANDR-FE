package com.capston.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.MainPurple
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
fun PlanScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 }) // 0: 기간, 1: 시간
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("메가스터디", color = MainPurple)

        Text(
            text = "2026 현우진의 수분감 - 수학 (공통)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "현우진 [고3·N수] 수능 (문제풀이) · 50강",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("기간으로 계획하기")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("시간으로 계획하기")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalPager(
            state = pagerState,
//            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PeriodPlanPage() // 기간으로 계획하기 화면
                1 -> TimePlanPage()   // 시간으로 계획하기 화면
            }
        }
    }
}

@Composable
fun PeriodPlanPage() {
    // 요일 선택 상태 예시
    val daysOfWeek = listOf("월", "화", "수", "목", "금")
    val (selectedDays, setSelectedDays) = remember { mutableStateOf(setOf<String>()) }

    // 날짜 상태
    var startDate by remember { mutableStateOf("시작일 선택") }
    var endDate by remember { mutableStateOf("종료일 선택") }

    // 모달 제어
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // 배속 선택 상태 예시
    val speeds = listOf("1.3x", "2.0x")
    val (selectedSpeed, setSelectedSpeed) = remember { mutableStateOf(speeds.first()) }

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
// 시작 강의 / 마지막 강의
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "시작 강의",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Placeholder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            Column {
                Text(
                    text = "마지막 강의",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Placeholder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                Button(
                    onClick = {
                        setSelectedDays(
                            if (isSelected) selectedDays - day else selectedDays + day
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(50) // pill 모양
                ) {
                    Text(day)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // 배속 선택
        Text(
            text = "배속",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            speeds.forEach { speed ->
                val isSelected = speed == selectedSpeed
                Button(
                    onClick = { setSelectedSpeed(speed) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(speed)
                }
            }
        }
    }
    // 여기에 날짜 선택 등 로직 작성

}

@Composable
fun TimePlanPage() {
    Text("시간으로 계획하는 화면입니다.")
    // 여기에 시간 분배 등 로직 작성
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


@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    CapstonTheme {
        PlanScreen()
    }
}
