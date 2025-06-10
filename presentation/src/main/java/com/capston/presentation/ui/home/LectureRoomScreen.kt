package com.capston.presentation.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.Typography
import com.capston.presentation.theme.textGray
import com.capston.presentation.ui.search.SearchActivity
import com.capston.presentation.viewmodel.LectureRoomViewModel
import kotlinx.coroutines.launch

@Composable
fun LectureRoomScreen(
    lectureRoomViewModel: LectureRoomViewModel,
    onSinglePlanClick: ((GetPlanLectureRoomResponse) -> Unit)?,
    onGroupPlanClick: ((GetPlanLectureRoomResponse) -> Unit)?,
    onNotificationClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteCode by remember { mutableStateOf("") }

    val lectures by lectureRoomViewModel.getPlanLectureRoomResponse.collectAsState()
    val joinResponse by lectureRoomViewModel.postJoinStudyGroupResponse.collectAsState()

    // 화면이 처음 표시될 때 데이터를 로드
    LaunchedEffect(Unit) {
        lectureRoomViewModel.getPlanLectureRoom()
    }

    // 스터디그룹 가입 성공 감지
    LaunchedEffect(joinResponse) {
        if (joinResponse.message.isNotEmpty()) {
            lectureRoomViewModel.getPlanLectureRoom() // 목록 새로고침
            showInviteDialog = false
            inviteCode = ""
        }
    }

    Scaffold(
        topBar = {
            LectureRoomTopBar(
                hasUnreadNotifications = true,
                onNotificationClick = onNotificationClick
            )
        },
    ) { innerPadding ->
        if (lectures.isEmpty()) {
            // 빈 상태 화면
            EmptyLectureRoomSection(
                context = context,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            )
        } else {
            // 강의 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = lectures,
                    key = { lecture -> lecture.planId } // 성능 최적화를 위한 key 추가
                ) { lecture ->
                    LectureItem(lecture) {
                        if (lecture.studyGroup) {
                            onGroupPlanClick?.invoke(lecture)
                        } else {
                            onSinglePlanClick?.invoke(lecture)
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        if (showInviteDialog) {
            InviteCodeDialog(
                inviteCode = inviteCode,
                onInviteCodeChange = { inviteCode = it },
                onConfirm = {
                    if (inviteCode.isNotEmpty()) {
                        // 초대코드를 DTO에 담아서 전달
                        lectureRoomViewModel.postJoinStudyGroup(
                            JoinStudyGroupDto(inviteCode = inviteCode)
                        )
                    }
                },
                onDismiss = {
                    showInviteDialog = false
                    inviteCode = ""
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureRoomTopBar(
    hasUnreadNotifications: Boolean,
    onNotificationClick: () -> Unit
) {
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
                Image(
                    painter = painterResource(id = R.drawable.landr_title_iv),
                    contentDescription = "앱 이름",
                    modifier = Modifier.size(70.dp),
                )
            },
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_launcher),
                    contentDescription = "앱 로고",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(48.dp),
                )
            },
            actions = {
                // 읽지 않은 알람이 있을 경우 빨간색 배지 표시
                if (hasUnreadNotifications) {
                    IconButton(onClick = onNotificationClick) {
                        Image(
                            painter = painterResource(R.drawable.icon_notification_on),
                            contentDescription = "alarm icon",
                        )
                    }
                }

                else {
                    IconButton(onClick = onNotificationClick) {
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
fun EmptyLectureRoomSection(
    context: Context,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 빈 상태 아이콘 (선택적으로 추가)
        Image(
            painter = painterResource(R.drawable.image_empty_lecture_room), // 적절한 아이콘으로 변경
            contentDescription = "빈 상태",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 16.dp),
//            tint = textGray
        )

        Text(
            text = "아직 계획된 강의가 없어요.",
            style = Typography.titleMedium,
            color = textGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "계획 생성하러 가기",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = MainPurple,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    val intent = Intent(context, SearchActivity::class.java)
                    context.startActivity(intent)
                }
        )
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = lecture.platform.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MainPurple,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .border(
                            width = 1.dp,
                            color = MainPurple,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Text(
                    text = lecture.subject.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = lecture.subject.borderColor,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .background(
                            color = lecture.subject.bgColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = lecture.subject.borderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                )
            }

            // 오른쪽 칩들
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 완강 칩 (완료된 경우에만 표시)
                if (lecture.completedLessons == lecture.totalLessons) {
                    Text(
                        text = "완강",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE53E3E),
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE53E3E),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }

                // 그룹 칩 (스터디그룹인 경우에만 표시)
                if (lecture.studyGroup) {
                    Text(
                        text = "그룹",
                        style = MaterialTheme.typography.labelMedium,
                        color = MainPurple,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Text(
            text = lecture.lectureTitle,
            style = Typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${lecture.teacher} · ${lecture.tag}",
                style = MaterialTheme.typography.labelMedium,
                color = textGray
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

@Composable
fun InviteCodeDialog(
    inviteCode: String,
    onInviteCodeChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "스터디그룹 가입",
                style = Typography.titleMedium
            )
        },
        text = {
            Column {
                Text(
                    text = "초대코드를 입력해주세요",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = onInviteCodeChange,
                    label = { Text("초대코드") },
                    placeholder = { Text("초대코드를 입력하세요") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = inviteCode.isNotEmpty()
            ) {
                Text("확인", color = MainPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = textGray)
            }
        }
    )
}

val Subject.bgColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFFD8D8)   // 연한 핑크
        Subject.ENG -> Color(0xFFC5D9FF)   // 연한 하늘색
        Subject.MATH -> Color(0xFFD3F7D3)  // 연한 민트
        Subject.SOC -> Color(0xFFFFF4C6)   // 연한 노랑
        Subject.SCI -> Color(0xFFC1E8F7)   // 연한 파랑
        Subject.HIST -> Color(0xFFE1B5E8)  // 연한 보라
        Subject.UNIV -> Color(0xFFC7F6F9)  // 연한 청록
        Subject.LANG2 -> Color(0xFFFFD8E6) // 연한 분홍
        Subject.VOC -> Color(0xFFF0F0F0)   // 연한 회색
    }

val Subject.borderColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFF6B6B)   // 부드러운 빨강
        Subject.ENG -> Color(0xFF5D9CFF)    // 부드러운 파랑
        Subject.MATH -> Color(0xFF5BBF63)   // 부드러운 초록
        Subject.SOC -> Color(0xFFFFC046)    // 부드러운 노랑
        Subject.SCI -> Color(0xFF1EB0D2)    // 부드러운 하늘색
        Subject.HIST -> Color(0xFF9E4FB0)   // 부드러운 보라
        Subject.UNIV -> Color(0xFF00A7B4)   // 부드러운 청록
        Subject.LANG2 -> Color(0xFFF08C8C)  // 부드러운 분홍
        Subject.VOC -> Color(0xFFB0B0B0)    // 부드러운 회색
    }

//@Preview(showBackground = true)
//@Composable
//fun LectureRoomPreview() {
//    CapstonTheme {
//        LectureRoomScreen(PlanViewModel(), null)
//    }
//}