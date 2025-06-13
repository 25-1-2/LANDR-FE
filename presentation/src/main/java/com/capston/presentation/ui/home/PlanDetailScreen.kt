package com.capston.presentation.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    Scaffold(
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
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // 개인 설정 섹션
            item {
                Text(
                    text = "개인 설정",
                    style = MaterialTheme.typography.titleMedium,
                    color = textGray,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                )
            }

            // 계획 설정 항목들
            item {
                PlanDetailSettingItem(
                    title = "계획 유형",
                    value = when (planDetailResponse.planType) {
                        "PERIOD" -> "기간"
                        "TIME" -> "시간"
                        else -> planDetailResponse.planType
                    },
                    onClick = { /* 계획 유형 변경 */ }
                )
            }

            item {
                PlanDetailSettingItem(
                    title = "시작일/종료일",
                    value = "${planDetailResponse.startDate} ~ ${planDetailResponse.endDate}",
                    onClick = { /* 날짜 변경 */ }
                )
            }

            item {
                PlanDetailSettingItem(
                    title = "배속",
                    value = "${planDetailResponse.playbackSpeed}배속",
                    onClick = { /* 배속 변경 */ }
                )
            }

            // 추가 설정 항목들...

            // 그룹장 메뉴 섹션 (그룹인 경우에만 표시)
            if (currentPlan.studyGroup) {
                item {
                    Text(
                        text = "그룹장 메뉴",
                        style = MaterialTheme.typography.titleMedium,
                        color = textGray,
                        modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
                    )
                }

                item {
                    PlanDetailSettingItem(
                        title = "그룹원 관리",
                        onClick = { /* 그룹원 관리 화면으로 */ }
                    )
                }

                item {
                    PlanDetailSettingItem(
                        title = "그룹 해산",
                        textColor = Color.Red,
                        onClick = { /* 그룹 해산 */ }
                    )
                }
            }
        }
    }
}

@Composable
fun PlanDetailSettingItem(
    title: String,
    value: String = "",
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
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
                painter = painterResource(id = R.drawable.icon_arrow_right),
                contentDescription = null,
                tint = textGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}