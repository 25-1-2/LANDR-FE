package com.capston.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource


@Composable
fun LectureDetailScreen(lecture: Lecture?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row {
            // 상단 제목
            Text(
                text = "2026 현우진의 수분감 - 수학(공통)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(bottom = 16.dp)
            )
            // SVG 아이콘 추가 (벡터 에셋을 사용)
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.icon_reschedule),
                contentDescription = "아이콘",
                modifier = Modifier.minimumInteractiveComponentSize()
            )

        }


        // 날짜별 섹션
        DateSection(
            date = "2022년 8월 20일",
            tasks = listOf("함수의 극한과 연속 ①", "함수의 극한과 연속 ②", "함수의 극한과 연속 ③")
        )
        Spacer(modifier = Modifier.height(16.dp))
        DateSection(
            date = "2022년 8월 21일",
            tasks = listOf("함수의 극한과 연속 ①", "함수의 극한과 연속 ②", "함수의 극한과 연속 ③")
        )
        Spacer(modifier = Modifier.height(16.dp))
        DateSection(
            date = "2022년 8월 22일",
            tasks = listOf("함수의 극한과 연속 ①", "함수의 극한과 연속 ②", "함수의 극한과 연속 ③")
        )
    }
}

@Composable
fun DateSection(date: String, tasks: List<String>) {
    Column {
        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        tasks.forEach { task ->
            TaskItem(taskText = task)
        }
    }
}

@Composable
fun TaskItem(taskText: String) {
    // 각 체크박스의 상태를 기억합니다.
    var checked by remember { mutableStateOf(false) }
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
            text = taskText,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    CapstonTheme {
        LectureDetailScreen(null)
    }
}
