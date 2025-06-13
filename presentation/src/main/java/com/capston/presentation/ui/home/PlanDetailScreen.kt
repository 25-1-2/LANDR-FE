package com.capston.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.capston.presentation.R
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.SinglePlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Int,
    navController: NavController,
    singlePlanViewModel: SinglePlanViewModel
) {
    val currentPlan by singlePlanViewModel.currentPlan.collectAsState()
    val planDetailResponse by singlePlanViewModel.planDetailResponse.collectAsState()

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // 개인 설정 섹션
            item {
                Text(
                    text = "계획 설정",
                    style = MaterialTheme.typography.titleMedium,
                    color = textGray,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
            }

            // 개인 설정 카드
            item {
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
                            title = "계획 유형",
                            value = when (planDetailResponse.planType) {
                                "PERIOD" -> "기간"
                                "TIME" -> "시간"
                                else -> planDetailResponse.planType
                            },
                            onClick = { /* 계획 유형 변경 */ },
                            showDivider = true
                        )

                        if (planDetailResponse.planType == "PERIOD") {
                            PlanDetailSettingItem(
                                title = "학습 시작일",
                                value = planDetailResponse.startDate,
                                onClick = { /* 기간 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "목표 완강일",
                                value = planDetailResponse.endDate,
                                onClick = { /* 기간 변경 */ },
                                showDivider = true
                            )
                        } else {
                            PlanDetailSettingItem(
                                title = "일일 학습 시간",
                                value = "${planDetailResponse.dailyTime}분",
                                onClick = { /* 시간 변경 */ },
                                showDivider = true
                            )
                        }

                        PlanDetailSettingItem(
                            title = "공부 일정",
                            value = "그사징",
                            onClick = { /* 스터디룸 설정 */ },
                            showDivider = true
                        )

                        PlanDetailSettingItem(
                            title = "시작 강의",
                            onClick = { /* 일정 설정 */ },
                            showDivider = true
                        )

                        PlanDetailSettingItem(
                            title = "마지막 강의",
                            onClick = { /* 일정 설정 */ },
                            showDivider = true
                        )

                        PlanDetailSettingItem(
                            title = "배속",
                            onClick = { /* 일정 설정 */ },
                            showDivider = false
                        )
                    }
                }
            }

            // 계획 미션 섹션
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "계획 미션",
                    style = MaterialTheme.typography.titleMedium,
                    color = textGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
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
                    PlanDetailSettingItem(
                        title = "계획 미션",
                        value = "0",
                        onClick = { /* 계획 미션 */ },
                        showDivider = false
                    )
                }
            }

            // 그룹장 메뉴 섹션 (그룹인 경우에만 표시)
            if (currentPlan.studyGroup) {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "그룹장 메뉴",
                        style = MaterialTheme.typography.titleMedium,
                        color = textGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
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
                                title = "그룹명 변경",
                                onClick = { /* 그룹명 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "그룹 소개/규칙",
                                value = "A+를 향하여",
                                onClick = { /* 그룹 소개/규칙 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "카테고리 변경",
                                value = "대학생",
                                onClick = { /* 카테고리 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "일일 목표시간 변경",
                                value = "1시간",
                                onClick = { /* 목표시간 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "모집인원 변경",
                                value = "2명",
                                onClick = { /* 모집인원 변경 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "비밀번호 변경",
                                value = "공개",
                                onClick = { /* 비밀번호 변경 */ },
                                showDivider = false
                            )
                        }
                    }
                }

                // 추가 그룹장 메뉴
                item {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                title = "그룹 멤버 관리",
                                onClick = { /* 그룹 멤버 관리 */ },
                                showDivider = true
                            )

                            PlanDetailSettingItem(
                                title = "한번에 깨우기",
                                onClick = { /* 한번에 깨우기 */ },
                                showDivider = false
                            )
                        }
                    }
                }
            }

            // 하단 여백
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PlanDetailSettingItem(
    title: String,
    value: String = "",
    onClick: () -> Unit,
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
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value.isNotEmpty()) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(
                    painter = painterResource(id = R.drawable.icon_nav_arrow_right),
                    contentDescription = null,
                    tint = textGray,
                    modifier = Modifier.size(16.dp)
                )
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