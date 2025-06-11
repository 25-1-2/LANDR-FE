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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchTimePlanDto
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
fun TimePlanEditScreen(
    planId: Int,
    planEditViewModel: PlanEditViewModel,
    navController: NavController,
    loadingStateManager: LoadingStateManager
) {
    val context = LocalContext.current
    val planDetailResponse by planEditViewModel.planDetailResponse.collectAsState()
    val patchResponse by planEditViewModel.patchTimePlanResponse.collectAsState()

    // 요청 상태 추적
    var requestSent by remember { mutableStateOf(false) }

    // State for validation error messages
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // 편집 가능한 상태들
    val dailyTime = remember { mutableIntStateOf(120) }
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
            dailyTime.value = planDetailResponse.dailyTime
            playbackSpeed.value = planDetailResponse.playbackSpeed.toDouble()
        }
    }

    // 응답을 관찰하여 액티비티 종료 처리
    LaunchedEffect(patchResponse, requestSent) {
        if (requestSent && patchResponse.message.isNotEmpty()) {
            delay(1000)
            loadingStateManager.hide()

            if (context is ComponentActivity) {
                context.setResult(Activity.RESULT_OK)
                context.finish()
            }
        }
    }

    // Validation functions
    val validateInputs = {
        when {
            dailyTime.value <= 0 -> {
                errorMessage = "일일 학습 시간을 설정해주세요."
                false
            }
            else -> true
        }
    }

    Scaffold(
        topBar = { TimePlanEditTopBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 헤더 섹션
            SimplifiedHeaderSection(
                lectureTitle = planDetailResponse.lectureTitle,
                teacher = planDetailResponse.teacher,
                platform = planDetailResponse.platform
            )
            HorizontalDivider(color = dividerGray)

            // 편집 가능한 섹션들
            Column(modifier = Modifier.padding(16.dp)) {
                // 일일 학습 시간 편집
                StudyTimeSection(dailyTime)

                // 배속 편집
                PlaybackSpeedSection(playbackSpeed)

                // 읽기 전용 정보들
                ReadOnlyInfoSection(
                    startDate = planDetailResponse.startDate,
                    planType = planDetailResponse.planType
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 수정 완료 버튼
                Button(
                    onClick = {
                        if (validateInputs()) {
                            requestSent = true
                            loadingStateManager.show()

                            val dto = PatchTimePlanDto(
                                dailyTime = dailyTime.value,
                                playbackSpeed = playbackSpeed.value
                            )

                            planEditViewModel.patchTimePlan(planId, dto)
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
fun TimePlanEditTopBar(navController: NavController) {
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
fun SimplifiedHeaderSection(
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
            text = "일일 학습 시간과 배속만 수정할 수 있습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MainPurple,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StudyTimeSection(dailyTime: MutableState<Int>) {
    // 하루에 공부할 시간
    var studyMins by remember { mutableStateOf(dailyTime.value.toString()) }

    // dailyTime 값이 변경되면 텍스트도 업데이트
    LaunchedEffect(dailyTime.value) {
        studyMins = dailyTime.value.toString()
    }

    Column(modifier = Modifier.padding(bottom = 16.dp)) {
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
            placeholder = {
                Text(
                    text = "예: 120",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReadOnlyInfoSection(
    startDate: String,
    planType: String
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
    }
}