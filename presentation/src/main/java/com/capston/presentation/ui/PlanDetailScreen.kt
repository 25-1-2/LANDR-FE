package com.capston.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.capston.domain.model.LessonSchedule
import com.capston.domain.response.plan.GetPlanDetailResponse


@Composable
fun PlanDetailScreen(planDetailResponse: GetPlanDetailResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TitleSection(planDetailResponse = planDetailResponse)

        // 날짜별 섹션
        planDetailResponse.dailySchedules.forEach { schedule ->
            OneDaySection(
                date = schedule.date,
                lessonSchedules = schedule.lessonSchedules
            )
        }
    }
}

@Composable
fun TitleSection(planDetailResponse: GetPlanDetailResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = planDetailResponse.lectureTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(10f),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_reschedule),
            contentDescription = "일정 변경",
            modifier = Modifier
                .clickable { /*…*/ }
        )
    }
}

@Composable
fun OneDaySection(date: String, lessonSchedules: List<LessonSchedule>) {
    Column(
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        lessonSchedules.forEach { lessonSchedule ->
            TaskItem(lessonSchedule = lessonSchedule)
        }
    }
}

@Composable
fun TaskItem(lessonSchedule: LessonSchedule) {
    // 각 체크박스의 상태를 기억합니다.
    var checked by remember { mutableStateOf(lessonSchedule.completed) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = lessonSchedule.lessonTitle,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    CapstonTheme {
        PlanDetailScreen(GetPlanDetailResponse())
    }
}
