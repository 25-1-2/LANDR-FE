import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 알림 데이터 클래스
data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val time: LocalDateTime,
    val type: NotificationType,
    val isRead: Boolean = false
)

// 알림 타입 enum
enum class NotificationType(val icon: ImageVector, val color: Color) {
    MESSAGE(Icons.Default.MailOutline, Color(0xFF2196F3)),
    LIKE(Icons.Default.Favorite, Color(0xFFE91E63)),
    COMMENT(Icons.Default.Star, Color(0xFF4CAF50)),
    FOLLOW(Icons.Default.Person, Color(0xFF9C27B0)),
    SYSTEM(Icons.Default.Settings, Color(0xFF607D8B))
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen() {
    // 샘플 알림 데이터
    var notifications by remember {
        mutableStateOf(
            listOf(
                NotificationItem(
                    id = "1",
                    title = "새로운 메시지",
                    content = "김철수님이 메시지를 보냈습니다: 안녕하세요! 오늘 회의 시간이 어떻게 되나요?",
                    time = LocalDateTime.now().minusMinutes(5),
                    type = NotificationType.MESSAGE
                ),
                NotificationItem(
                    id = "2",
                    title = "좋아요 알림",
                    content = "박영희님이 회원님의 게시물을 좋아합니다.",
                    time = LocalDateTime.now().minusHours(1),
                    type = NotificationType.LIKE,
                    isRead = true
                ),
                NotificationItem(
                    id = "3",
                    title = "새로운 댓글",
                    content = "이민수님이 회원님의 게시물에 댓글을 달았습니다: 정말 유용한 정보네요!",
                    time = LocalDateTime.now().minusHours(2),
                    type = NotificationType.COMMENT
                ),
                NotificationItem(
                    id = "4",
                    title = "새로운 팔로워",
                    content = "정수진님이 회원님을 팔로우하기 시작했습니다.",
                    time = LocalDateTime.now().minusHours(3),
                    type = NotificationType.FOLLOW,
                    isRead = true
                ),
                NotificationItem(
                    id = "5",
                    title = "시스템 알림",
                    content = "앱이 새로운 버전으로 업데이트되었습니다. 새로운 기능을 확인해보세요!",
                    time = LocalDateTime.now().minusDays(1),
                    type = NotificationType.SYSTEM
                ),
                NotificationItem(
                    id = "6",
                    title = "메시지 알림",
                    content = "홍길동님이 그룹 채팅에 메시지를 보냈습니다.",
                    time = LocalDateTime.now().minusDays(1),
                    type = NotificationType.MESSAGE,
                    isRead = true
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 상단 앱바
        TopAppBar(
            title = {
                Text(
                    text = "알림",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                // 모두 읽음 처리 버튼
                TextButton(
                    onClick = {
                        notifications = notifications.map { it.copy(isRead = true) }
                    }
                ) {
                    Text("모두 읽음")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // 읽지 않은 알림 개수 표시
        val unreadCount = notifications.count { !it.isRead }
        if (unreadCount > 0) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "읽지 않은 알림 ${unreadCount}개",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // 알림 목록
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notifications) { notification ->
                NotificationItemCard(
                    notification = notification,
                    onMarkAsRead = { notificationId ->
                        notifications = notifications.map { item ->
                            if (item.id == notificationId) {
                                item.copy(isRead = true)
                            } else {
                                item
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationItemCard(
    notification: NotificationItem,
    onMarkAsRead: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!notification.isRead) {
                    onMarkAsRead(notification.id)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 알림 타입 아이콘
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(notification.type.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.icon,
                    contentDescription = null,
                    tint = notification.type.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 알림 내용
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 읽지 않음 표시 점
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatTime(notification.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// 시간 포맷팅 함수
@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(time: LocalDateTime): String {
    val now = LocalDateTime.now()
    val diff = java.time.Duration.between(time, now)

    return when {
        diff.toMinutes() < 1 -> "방금 전"
        diff.toMinutes() < 60 -> "${diff.toMinutes()}분 전"
        diff.toHours() < 24 -> "${diff.toHours()}시간 전"
        diff.toDays() < 7 -> "${diff.toDays()}일 전"
        else -> time.format(DateTimeFormatter.ofPattern("MM월 dd일"))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    MaterialTheme {
        NotificationScreen()
    }
}