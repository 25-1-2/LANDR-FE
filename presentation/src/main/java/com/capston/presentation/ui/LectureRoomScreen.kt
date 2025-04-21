package com.capston.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.capston.domain.model.Lecture
import com.capston.domain.model.MyLecture
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.Typography
import com.capston.presentation.viewmodel.PlanViewModel

@Composable
fun LectureRoomScreen(
    viewModel: PlanViewModel,
    onLectureClick: ((MyLecture) -> Unit)?
) {
    val lectures = viewModel.getPlanLectureRoom
    viewModel.getPlanLectureRoom()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🎓 나의 강의실",
            style = Typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn {
            items(lectures.value) { lecture ->
                LectureItem(lecture = lecture, onClick = {
                    if (onLectureClick != null) { onLectureClick(lecture) }
                })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun LectureItem(lecture: MyLecture, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null, // Ripple 제거
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "${lecture.platform} · ${lecture.teacher}",
            style = Typography.labelMedium,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = lecture.lectureTitle,
                style = Typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${lecture.completedLessons}/${lecture.totalLessons}",
                style = MaterialTheme.typography.labelSmall
            )
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

//@Preview(showBackground = true)
//@Composable
//fun LectureRoomPreview() {
//    CapstonTheme {
//        LectureRoomScreen(PlanViewModel(), null)
//    }
//}