package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.GroupPlanViewModel
import com.capston.presentation.viewmodel.SinglePlanViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Int,
    screenType: String, // "single" 또는 "group"
    studyGroupId: Int? = null,
    navController: NavController,
    singlePlanViewModel: SinglePlanViewModel? = null,
    groupPlanViewModel: GroupPlanViewModel? = null

) {
    val groupId = if (studyGroupId == -1) null else studyGroupId

    var showStartDateDialog by remember { mutableStateOf(false) }
    var showEndDateDialog by remember { mutableStateOf(false) }


    // 기본값들 정의
    val defaultCurrentPlan = remember {
        GetPlanLectureRoomResponse(
            planId = planId,
            platform = Platform.MEGA,
            subject = Subject.KOR,
            lectureTitle = "",
            teacher = "",
            tag = "",
            completedLessons = 0,
            totalLessons = 0,
            studyGroup = false
        )
    }

    val defaultPlanDetailResponse = remember {
        PlanDetailResponse(
            planType = "PERIOD",
            startDate = "",
            endDate = "",
            dailyTime = 0,
            playbackSpeed = 1.0,
            dailySchedules = emptyList()
        )
    }

    // SinglePlan/GroupPlan 따라 다른 데이터 수집
    val currentPlan = when (screenType) {
        "single" -> {
            singlePlanViewModel?.currentPlan?.collectAsState() ?: remember { mutableStateOf(defaultCurrentPlan) }
        }
        "group" -> {
            groupPlanViewModel?.currentPlan?.collectAsState() ?: remember { mutableStateOf(defaultCurrentPlan) }
        }
        else -> remember { mutableStateOf(defaultCurrentPlan) }
    }

    val planDetailResponse = when (screenType) {
        "single" -> {
            singlePlanViewModel?.planDetailResponse?.collectAsState() ?: remember { mutableStateOf(defaultPlanDetailResponse) }
        }
        "group" -> {
            groupPlanViewModel?.planDetailResponse?.collectAsState() ?: remember { mutableStateOf(defaultPlanDetailResponse) }
        }
        else -> remember { mutableStateOf(defaultPlanDetailResponse) }
    }


    // 그룹 정보 (그룹에서 온 경우에만)
    val getOneStudyGroupResponse = if (screenType == "group" && groupPlanViewModel != null) {
        groupPlanViewModel.getOneStudyGroupResponse.collectAsState()
    } else {
        null
    }

    // 방장 여부 확인 (그룹에서 온 경우에만)
    val isLeader = if (screenType == "group" && getOneStudyGroupResponse != null) {
        val myMember = getOneStudyGroupResponse.value.members.find { it.planId == planId }
        myMember?.userId == getOneStudyGroupResponse.value.leaderId
    } else {
        true // 싱글 계획은 항상 본인이 관리
    }

    // 배경색 정의
    val backgroundColor = Color(0xFFF3F2F7)

    Scaffold(
        modifier = Modifier.background(backgroundColor),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "계획 정보/설정",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_arrow_back),
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // 계획 설정은 방장일 때만 표시
            if (isLeader) {
                Text(
                    text = "계획 설정",
                    style = MaterialTheme.typography.titleSmall,
                    color = textGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 계획 설정
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Column {
                        PlanDetailSettingItem(
                            title = "계획 유형",
                            value = when (planDetailResponse.value.planType) {
                                "PERIOD" -> "기간"
                                "TIME" -> "시간"
                                else -> planDetailResponse.value.planType
                            },
                            onClick = {},
                            showArrow = false
                        )

                        if (planDetailResponse.value.planType == "PERIOD") {
                            PlanDetailSettingItem(
                                title = "학습 시작일",
                                value = planDetailResponse.value.startDate,
                                onClick = { showStartDateDialog = true },                            )

                            PlanDetailSettingItem(
                                title = "목표 완강일",
                                value = planDetailResponse.value.endDate,
                                onClick = { showEndDateDialog = true },                            )
                        } else {
                            PlanDetailSettingItem(
                                title = "일일 학습 시간",
                                value = "${planDetailResponse.value.dailyTime}분",
                                onClick = { /* 시간 변경 */ },
                            )
                        }

                        PlanDetailSettingItem(
                            title = "공부 일정",
                            value = "월 화 수 토",
                            onClick = { /* 스터디룸 설정 */ },
                        )

                        PlanDetailSettingItem(
                            title = "시작 강의",
                            value = "OT - OT",
                            onClick = { /* 일정 설정 */ },
                        )

                        PlanDetailSettingItem(
                            title = "마지막 강의",
                            value = "13강 - 6장 미분의 활용 (2)",
                            onClick = { /* 일정 설정 */ },
                        )

                        PlanDetailSettingItem(
                            title = "배속",
                            value = "${planDetailResponse.value.playbackSpeed}배",
                            onClick = { /* 일정 설정 */ },
                            showDivider = false
                        )
                    }
                }

                // 민감 계획 설정
                if (screenType == "single") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Column {
                            PlanDetailSettingItem(
                                title = "계획 삭제",
                                onClick = { /* 그룹 멤버 관리 */ },
                                textColor = Color.Red,
                                showDivider = false
                            )
                        }
                    }
                }
            }

            // 그룹 메뉴 (그룹이고 방장인 경우에만 표시)
            if (screenType == "group" && getOneStudyGroupResponse != null) {
                Text(
                    text = "그룹 메뉴",
                    style = MaterialTheme.typography.titleSmall,
                    color = textGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 그룹 설정
                if (isLeader) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Column {
                            PlanDetailSettingItem(
                                title = "그룹명 변경",
                                value = getOneStudyGroupResponse.value.name,
                                onClick = { /* 그룹명 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "그룹원 관리",
                                onClick = { /* 그룹 소개/규칙 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "초대코드 변경",
                                value = getOneStudyGroupResponse.value.inviteCode,
                                onClick = { /* 비밀번호 변경 */ },
                                showDivider = false
                            )
                        }
                    }
                }

                // 민감 그룹 메뉴
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Column {
                        // 방장인 경우
                        if (isLeader) {
                            PlanDetailSettingItem(
                                title = "그룹장 위임",
                                onClick = { /* 그룹 멤버 관리 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "그룹 삭제",
                                onClick = { /* 한번에 깨우기 */ },
                                textColor = Color.Red,
                                showDivider = false
                            )
                        } else {
                            PlanDetailSettingItem(
                                title = "그룹 나가기",
                                onClick = { /* 한번에 깨우기 */ },
                                textColor = Color.Red,
                                showDivider = false
                            )
                        }
                    }
                }
            }
        }

        if (showStartDateDialog) {
            DateChangeDialog(
                title = "시작일 변경",
                currentDate = planDetailResponse.value.startDate,
                onDateSelected = { selectedDate ->
                    // TODO: 시작일 변경 API 호출
                    println("선택된 시작일: $selectedDate")
                },
                onDismiss = { showStartDateDialog = false }
            )
        }

        if (showEndDateDialog) {
            DateChangeDialog(
                title = "목표 완강일 변경",
                currentDate = planDetailResponse.value.endDate,
                onDateSelected = { selectedDate ->
                    // TODO: 목표 완강일 변경 API 호출
                    println("선택된 완강일: $selectedDate")
                },
                onDismiss = { showEndDateDialog = false }
            )
        }
    }
}

@Composable
fun PlanDetailSettingItem(
    title: String,
    value: String = "",
    onClick: () -> Unit,
    textColor: Color = Color.Black,
    showArrow: Boolean = true,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(end = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value.isNotEmpty()) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false) // 이 부분 추가
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (showArrow) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_nav_arrow_right),
                        contentDescription = null,
                        tint = materialGray,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(16.dp))  // 화살표 공간만큼 여백 유지
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = dividerGray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateChangeDialog(
    title: String, // "시작일 변경" 또는 "목표 완강일 변경" (실제로는 DatePickerDialog 자체 타이틀에 사용되지 않음)
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialDateMillis = parseDate(currentDate) ?: System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = Color.White,
            selectedDayContainerColor = MainPurple,
        ),
        tonalElevation = 0.dp, // 이걸 추가해줘야 배경에 투명 레이어 없음
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val selectedDate = formatDate(millis)
                    onDateSelected(selectedDate)
                }
                onDismiss()
            }) {
                Text("확인", color = MainPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Black)
            }
        },
    ) {
        DatePicker(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 32.dp)
                )
            },
            headline = {
                // 선택된 날짜를 작은 크기로 표시
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
                } ?: ""

                Text(
                    text = selectedDate,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                )
            },
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

// formatDate 함수
fun formatDate(millis: Long?): String {
    return if (millis != null) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.format(Date(millis))
    } else {
        ""
    }
}

// 날짜 문자열을 Long으로 변환하는 함수
fun parseDate(dateString: String): Long? {
    return try {
        if (dateString.isNotEmpty()) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString)?.time
        } else {
            null
        }
    } catch (e: ParseException) {
        null
    }
}