package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.capston.presentation.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.ui.common.CustomCheckBox
import com.capston.presentation.ui.common.LandrUtil.Companion.formatDateYMDE
import com.capston.presentation.viewmodel.LectureRoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlanDetailScreen(
    planId: Int,
    lectureRoomViewModel: LectureRoomViewModel,
    navController: NavController  // loadingStateManager 파라미터 제거
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDropdown by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val planDetailResponse by lectureRoomViewModel.getPlanDetailResponse.collectAsState()

    LaunchedEffect(planId) {
        lectureRoomViewModel.getPlanDetail(planId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isLoading) Modifier.blur(8.dp) else Modifier
                ),
            topBar = {
                SinglePlanTopBar(
                    navController = navController,
                    showMenu = showDeleteDropdown,
                    onMenuClick = { showDeleteDropdown = !showDeleteDropdown },
                    onMenuDismiss = { showDeleteDropdown = false },
                    onDeleteClick = { showDeleteConfirmDialog = true }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SinglePlanTitleSection(
                    planId = planId,
                    lectureRoomViewModel = lectureRoomViewModel,
                    planDetailResponse = planDetailResponse,
                    coroutineScope = coroutineScope,
                    isLoading = isLoading,
                    onLoadingChange = { isLoading = it }
                )

                // 날짜별 섹션
                planDetailResponse.dailySchedules.forEach { schedule ->
                    OneDaySection(
                        date = schedule.date,
                        planDetailLessonSchedules = schedule.lessonSchedules,
                        lectureRoomViewModel = lectureRoomViewModel
                    )
                }
            }

            // 삭제 확인 다이얼로그
            if (showDeleteConfirmDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    iconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black,
                    tonalElevation = 0.dp, // 그림자 효과 제거
                    onDismissRequest = { showDeleteConfirmDialog = false },
                    title = { Text("계획 삭제") },
                    text = { Text("이 계획을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // 삭제 로직 실행
                                lectureRoomViewModel.deleteOnePlan(planId)
                                navController.popBackStack() // 이전 화면으로 돌아가기
                                showDeleteConfirmDialog = false
                            }
                        ) {
                            Text("삭제", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text("취소")
                        }
                    }
                )
            }
        }

        // 로딩 오버레이
        if (isLoading) {
            val composition by rememberLottieComposition(LottieCompositionSpec.Asset("loading_dot.json"))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .zIndex(999f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .background(
//                            Color.White.copy(alpha = 0.95f),
//                            shape = RoundedCornerShape(16.dp)
//                        )
//                        .padding(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.screen_home_todaylesson_empty_iv),
                        contentDescription = "재스케줄링 이미지",
                        modifier = Modifier.padding(start = 40.dp)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "계획을 수정하고 있어요...",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinglePlanTopBar(
    navController: NavController,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_arrow_back),
                        contentDescription = "뒤로 가기"
                    )
                }
            },
            actions = {
                IconButton(onClick = onMenuClick) {
                    Image(
                        painter = painterResource(R.drawable.icon_more_horizontal),
                        contentDescription = "alarm icon",
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = onMenuDismiss,
                    modifier = Modifier
                        .background(Color.White)
                        .width(150.dp)
                ) {
                    // 계획 수정
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_edit_pencil),
                                    contentDescription = "수정",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("수정하기", color = Color.Black)
                            }
                        },
                        onClick = {
                            onMenuDismiss()
//                            onDeleteClick()
                        }
                    )

                    // 계획 삭제
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_trash), // 삭제 아이콘 리소스 필요
                                    contentDescription = "삭제",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("삭제하기", color = Color.Red)
                            }
                        },
                        onClick = {
                            onMenuDismiss()
                            onDeleteClick()
                        }
                    )
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray2)
    }
}

@Composable
fun SinglePlanTitleSection(
    planId: Int,
    lectureRoomViewModel: LectureRoomViewModel,
    planDetailResponse: GetPlanDetailResponse,
    coroutineScope: CoroutineScope,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = planDetailResponse.lectureTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(10f),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 재스케줄링 버튼
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        onLoadingChange(true)
                        try {
                            // 재스케줄링을 시작하고 완료될 때까지 기다립니다
                            val rescheduleJob = lectureRoomViewModel.postPlanReschedule(planId)
                            rescheduleJob.join()

                            // 이제 업데이트된 데이터 가져오기
                            lectureRoomViewModel.getPlanDetail(planId)

                            delay(1000)
                        } finally {
                            onLoadingChange(false)
                        }
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        width = 1.dp,
                        color = MainPurple,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_reschedule),
                    contentDescription = "재스케줄링",
                    tint = MainPurple,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 그룹원 추가 버튼
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        width = 1.dp,
                        color = MainPurple,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.icon_group),
                    contentDescription = "그룹 추가",
                    tint = MainPurple,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { /*…*/ }
                )
            }
        }


    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OneDaySection(
    date: String,
    planDetailLessonSchedules: List<PlanDetailLessonSchedule>,
    lectureRoomViewModel: LectureRoomViewModel
) {
    val totalMinutes = planDetailLessonSchedules.sumOf { it.adjustedDuration }

    Column {
        Text(
            text = formatDateYMDE(date),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .border(
                    width = 1.dp,
                    color = dividerGray,
                    shape = RoundedCornerShape(12.dp)
                )
                // border 내부 패딩
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 하루 강의 개수
                Text(
                    text = "총 ${planDetailLessonSchedules.size}강",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                // 하루 강의 시간
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.icon_clock_filled),
                        contentDescription = "시간",
                        tint = materialGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "약 ${totalMinutes}분",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray
                    )
                }
            }

            HorizontalDivider(
//                modifier = Modifier.padding(bottom = 8.dp)
            )

            planDetailLessonSchedules.forEach { lessonSchedule ->
                TaskItem(
                    planDetailLessonSchedule = lessonSchedule,
                    lectureRoomViewModel = lectureRoomViewModel
                )
            }
        }

    }
}

@Composable
fun TaskItem(
    planDetailLessonSchedule: PlanDetailLessonSchedule,
    lectureRoomViewModel: LectureRoomViewModel
) {
    // 각 체크박스의 상태를 remember로 관리하되, 초기값은 서버 데이터 사용
    var isChecked by remember(planDetailLessonSchedule.id, planDetailLessonSchedule.completed) {
        mutableStateOf(planDetailLessonSchedule.completed)
    }

    // 서버 데이터가 변경되면 로컬 상태도 동기화
    LaunchedEffect(planDetailLessonSchedule.completed) {
        isChecked = planDetailLessonSchedule.completed
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        ) {
            CustomCheckBox(
                isChecked = isChecked,
                onCheckedChange = {
                    // 즉시 UI 업데이트 (사용자 경험 향상)
                    isChecked = !isChecked

                    // 서버 업데이트 (백그라운드에서 실행)
                    lectureRoomViewModel.patchLessonSchedulesCheckToggle(planDetailLessonSchedule.id)
                }
            )
            Text(
                text = planDetailLessonSchedule.lessonTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                lineHeight = 28.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Text(
            text = "${planDetailLessonSchedule.adjustedDuration}분",
            style = MaterialTheme.typography.labelMedium,
            color = MainPurple,
            modifier = Modifier
                .padding(bottom = 6.dp)
                .border(
                    width = 1.dp,
                    color = MainPurple,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 4.dp, vertical = 4.dp)
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
