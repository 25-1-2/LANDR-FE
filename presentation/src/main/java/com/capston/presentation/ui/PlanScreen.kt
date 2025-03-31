package com.capston.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
    val daysOfWeek = listOf("월", "화", "수", "목", "금", "토", "일")
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
        PlanScreen()
    }
}
