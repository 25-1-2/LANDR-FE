package com.capston.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.MainPurple
import kotlinx.coroutines.launch

@Composable
fun PlanScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 }) // 0: 기간, 1: 시간

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            modifier = Modifier.fillMaxSize()
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
    Text("기간으로 계획하는 화면입니다.")
    // 여기에 날짜 선택 등 로직 작성
}

@Composable
fun TimePlanPage() {
    Text("시간으로 계획하는 화면입니다.")
    // 여기에 시간 분배 등 로직 작성
}


@Preview(showBackground = true)
@Composable
fun PlanScreenPreview() {
    CapstonTheme {
        PlanScreen()
    }
}
