package com.capston.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.Typography
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.LectureRoomViewModel

@Composable
fun LectureRoomScreen(
    lectureRoomViewModel: LectureRoomViewModel,
    onPlanClick: ((GetPlanLectureRoomResponse) -> Unit)?
) {
    val lectures by lectureRoomViewModel.getPlanLectureRoom.collectAsState()

    // 화면이 처음 표시될 때 데이터를 로드
    LaunchedEffect(Unit) {
        lectureRoomViewModel.getPlanLectureRoom()
    }

    Scaffold(
        topBar = { LectureRoomTopBar(hasUnreadNotifications = true) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(lectures) { lecture ->
                LectureItem(lecture) { onPlanClick?.invoke(lecture) }
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureRoomTopBar(hasUnreadNotifications: Boolean) {
    Column {
        TopAppBar(
            title = {
//                Text(
//                    text = "나의 강의실",
//                    style = MaterialTheme.typography.titleLarge,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis     // 길어질 때 … 처리
//                )

                // 앱 이름
                Icon(
                    painter = painterResource(id = R.drawable.landr_title_iv),
                    contentDescription = "앱 이름",
                    modifier = Modifier.size(70.dp),
                    tint = Color.Unspecified
                )
            },
            navigationIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = "앱 로고",
                    modifier = Modifier
                        .height(50.dp)
                        .padding(start = 8.dp), // 바 내부 기본 여백만큼
                    tint = Color.Unspecified
                )
            },
            actions = {
                // 읽지 않은 알람이 있을 경우 빨간색 배지 표시
                if (hasUnreadNotifications) {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Image(
                            painter = painterResource(R.drawable.icon_notification_on),
                            contentDescription = "alarm icon",
                        )
                    }
                }

                else {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Image(
                            painter = painterResource(R.drawable.home_screen_notification_iv),
                            contentDescription = "alarm icon",
                        )
                    }
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray2)
    }
}

@Composable
fun LectureItem(lecture: GetPlanLectureRoomResponse, onClick: () -> Unit) {
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
            text = "${lecture.platform.label} · ${lecture.teacher}",
            style = Typography.labelMedium,
            color = MainPurple,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = lecture.lectureTitle,
                style = Typography.titleMedium,
                modifier = Modifier
                    .weight(1f)          // ← 폭을 제한하고
                    .padding(end = 16.dp)
            )
            Text(
                text = "${lecture.completedLessons}/${lecture.totalLessons}",
                style = MaterialTheme.typography.labelMedium,
                color = textGray
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