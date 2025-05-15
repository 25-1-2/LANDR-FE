package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capston.presentation.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import com.capston.domain.model.LessonSchedule
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.presentation.ui.common.CustomCheckBox
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.PlanViewModel

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String): String {
    val parsedDate = LocalDate.parse(dateString) // "2025-03-22"
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    return parsedDate.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlanDetailScreen(
    planId: Int,
    homeViewModel: HomeViewModel,
    planViewModel: PlanViewModel
) {
    val planDetailResponse by planViewModel.getPlanDetail.collectAsState()
    planViewModel.getPlanDetail(planId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleSection(planDetailResponse = planDetailResponse)

        // 날짜별 섹션
        planDetailResponse.dailySchedules.forEach { schedule ->
            OneDaySection(
                date = schedule.date,
                lessonSchedules = schedule.lessonSchedules,
                homeViewModel = homeViewModel
            )
        }
    }
}

@Composable
fun TitleSection(planDetailResponse: GetPlanDetailResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = planDetailResponse.lectureTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.weight(10f),
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_reschedule),
            contentDescription = "일정 변경",
            tint = Color(0xFF3F15EA),
            modifier = Modifier
                .clickable { /*…*/ }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OneDaySection(
    date: String,
    lessonSchedules: List<LessonSchedule>,
    homeViewModel: HomeViewModel
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = formatDate(date),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        lessonSchedules.forEach { lessonSchedule ->
            TaskItem(
                lessonSchedule = lessonSchedule,
                homeViewModel = homeViewModel
            )
        }
    }
}

@Composable
fun TaskItem(
    lessonSchedule: LessonSchedule,
    homeViewModel: HomeViewModel
) {
    // 각 체크박스의 상태를 기억합니다.
    var isChecked by remember { mutableStateOf(lessonSchedule.completed) }
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        CustomCheckBox (
            isChecked = isChecked,
            onCheckedChange = {
                homeViewModel.patchLessonSchedulesCheckToggle(lessonSchedule.id)
                isChecked = !isChecked
            }
        )
        Text(
            text = lessonSchedule.lessonTitle,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            lineHeight = 28.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DetailScreenPreview() {
//    CapstonTheme {
//        PlanDetailScreen(0, PlanViewModel())
//    }
//}
