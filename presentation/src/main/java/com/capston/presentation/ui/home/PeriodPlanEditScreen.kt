package com.capston.presentation.ui.home

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.PlanEditViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PeriodPlanEditScreen(
    planId: Int,
    planEditViewModel: PlanEditViewModel,
    navController: NavController,
    loadingStateManager: LoadingStateManager
) {
    val context = LocalContext.current
    val planDetailResponse by planEditViewModel.planDetailResponse.collectAsState()
    val patchResponse by planEditViewModel.patchPeriodPlanResponse.collectAsState()

    // 요청 상태 추적
    var requestSent by remember { mutableStateOf(false) }

    // State for validation error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // 편집 가능한 상태들
    val endDate = remember { mutableStateOf("") }
    val playbackSpeed = remember { mutableDoubleStateOf(1.0) }

    // planId로 기존 계획 데이터 로드
    LaunchedEffect(planId) {
        if (planId > 0) {
            planEditViewModel.getPlanDetail(planId)
        }
    }

    // planDetailResponse가 로드되면 상태 초기화
    LaunchedEffect(planDetailResponse.planId) {
        if (planDetailResponse.planId > 0) {
            endDate.value = planDetailResponse.endDate
            playbackSpeed.doubleValue = planDetailResponse.playbackSpeed.toDouble()
        }
    }

    // 응답을 관찰하여 이전 화면으로 돌아가기
    LaunchedEffect(patchResponse, requestSent) {
        if (requestSent && patchResponse.message.isNotEmpty()) {
            delay(1000)
            loadingStateManager.hide()

            // 네비게이션 백스택 pop
            navController.popBackStack()
        }
    }

    // Validation functions
    val validateInputs = {
        when {
            endDate.value.isEmpty() -> {
                errorMessage = "목표 완강일을 선택해주세요."
                false
            }
            else -> true
        }
    }

    Scaffold(
        topBar = { PeroidPlanEditTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 헤더 섹션
            PeriodPlanEditHeaderSection(
                lectureTitle = planDetailResponse.lectureTitle,
                teacher = planDetailResponse.teacher,
                platform = planDetailResponse.platform
            )
            HorizontalDivider(color = dividerGray)

            // 편집 가능한 섹션들
            Column(modifier = Modifier.padding(16.dp)) {
                // 목표 완강일 편집
                EndDateSection(endDate)

                // 배속 편집
                PlaybackSpeedSection(playbackSpeed)

                // 읽기 전용 정보들
                ReadOnlyInfoSection(
                    startDate = planDetailResponse.startDate,
                    planType = planDetailResponse.planType,
                    dailyTime = planDetailResponse.dailyTime
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 수정 완료 버튼
                Button(
                    onClick = {
                        if (validateInputs()) {
                            requestSent = true
                            loadingStateManager.show()

                            val dto = PatchPeriodPlanDto(
                                endDate = endDate.value,
                                playbackSpeed = playbackSpeed.doubleValue
                            )

                            planEditViewModel.patchPeriodPlan(planId, dto)
                        } else {
                            showErrorDialog = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("수정 완료")
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
            tonalElevation = 0.dp,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeroidPlanEditTopBar(navController: NavController) {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_arrow_back),
                        contentDescription = "뒤로 가기"
                    )
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}

@Composable
fun PeriodPlanEditHeaderSection(
    lectureTitle: String,
    teacher: String,
    platform: String
) {
    Column(
        modifier = Modifier
            .background(backgroundGray)
            .padding(16.dp)
    ) {
        Text(
            text = platform,
            style = MaterialTheme.typography.labelLarge,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = lectureTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = teacher,
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 계획 수정 안내 텍스트
        Text(
            text = "목표 완강일과 배속만 수정할 수 있습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MainPurple,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EndDateSection(endDate: MutableState<String>) {
    var showEndDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
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
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showEndDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                endDate.value = com.capston.presentation.ui.search.formatDate(millis)
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

    androidx.compose.material3.DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = Color.White,
            selectedDayContainerColor = MainPurple,
        ),
        tonalElevation = 0.dp,
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
fun PlaybackSpeedSection(playbackSpeed: MutableState<Double>) {
    // 0부터 10까지의 인덱스를 배속 값으로 매핑 (0 → 1.0, 10 → 2.0)
    var speed by remember { mutableIntStateOf(0) }

    // playbackSpeed 값이 변경되면 slider 값도 업데이트
    LaunchedEffect(playbackSpeed.value) {
        speed = ((playbackSpeed.value - 1.0) * 10).toInt().coerceIn(0, 10)
    }

    // Update the external state when the slider value changes
    LaunchedEffect(speed) {
        playbackSpeed.value = 1.0 + speed * 0.1
    }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
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
                text = String.format(Locale.KOREA, "%.1fx", 1.0 + speed * 0.1),
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
            value = speed.toFloat(),
            onValueChange = { speed = it.toInt() },
            valueRange = 0f..10f,
            steps = 9, // 10단계 → 중간 9개의 간격
        )
    }
}

@Composable
fun ReadOnlyInfoSection(
    startDate: String,
    planType: String,
    dailyTime: Int
) {
    Column {
        Text(
            text = "계획 정보",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 계획 유형
        Text(
            text = "계획 유형: ${if (planType == "PERIOD") "기간 계획" else "시간 계획"}",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 시작일 (읽기 전용)
        Text(
            text = "학습 시작일: $startDate",
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 일일 학습 시간 (시간 계획인 경우에만 표시)
        if (planType == "TIME" && dailyTime > 0) {
            Text(
                text = "일일 학습 시간: ${dailyTime}분",
                style = MaterialTheme.typography.bodyMedium,
                color = textGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}