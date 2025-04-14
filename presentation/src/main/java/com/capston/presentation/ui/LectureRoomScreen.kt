package com.capston.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.capston.domain.model.Lecture
import com.capston.presentation.theme.CapstonTheme

@Composable
fun LectureRoomScreen(onLectureClick: ((Lecture) -> Unit)?) {
    val lectures = listOf(""
//        Lecture("2026 현우진의 수분감 - 수학Ⅰ (공통)", "메가스터디", "현우진", 14, 50),
//        Lecture("2026 현우진의 수분감 - 수학Ⅱ (공통)", "메가스터디", "현우진", 14, 50),
//        Lecture("믿어봐! 문장 읽는 법을 알려줄게", "메가스터디",  "조정식", 14, 50),
//        Lecture("이디지도 설레는 동아시아사", "메가스터디", "이다지", 14, 50),
//        Lecture("2026 파노라마 독서 기출 총론", "대성마이맥", "유대종", 50, 50),
//        Lecture("LIM IT - 생활과 윤리", "대성마이맥", "임정환", 50, 50)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🎓 나의 강의실", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(lectures) { lecture ->
//                LectureItem(lecture = lecture, onClick = {
//                    if (onLectureClick != null) {
//                        onLectureClick(lecture)
//                    }
//                })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun LectureItem(lecture: Lecture, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
//        Text(text = "${lecture.platform} · ${lecture.instructor}", style = MaterialTheme.typography.labelMedium, color = Color(0xFF4E6EF2))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Text(text = lecture.title, style = MaterialTheme.typography.bodyLarge)
//            Text("${lecture.progress}/${lecture.total}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun StatusChip(status: String, isCompleted: Boolean) {
    val backgroundColor = when {
        isCompleted -> Color(0xFFFFE0E0)
        status == "그룹" -> Color(0xFFEDF1FF)
        else -> Color.LightGray
    }
    val textColor = when {
        isCompleted -> Color.Red
        else -> Color(0xFF4E6EF2)
    }

    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = textColor, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(showBackground = true)
@Composable
fun LectureRoomPreview() {
    CapstonTheme {
        LectureRoomScreen(null)
    }
}