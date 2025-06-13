package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.ui.common.CustomCheckBox
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.common.bgColor
import com.capston.presentation.ui.common.borderColor
import com.capston.presentation.ui.common.formatDateYMDE
import com.capston.presentation.viewmodel.SinglePlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SinglePlanScreen(
    planId: Int,
    singlePlanViewModel: SinglePlanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showTaskBottomSheet by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<PlanDetailLessonSchedule?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val currentPlan by singlePlanViewModel.currentPlan.collectAsState()
    val planDetailResponse by singlePlanViewModel.planDetailResponse.collectAsState()

    LaunchedEffect(planId) {
        singlePlanViewModel.getPlanDetail(planId)
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
                    onMenuClick = {
                        navController.navigate("${Screen.PlanDetail.title}/${planId}")
                    }
                )
            },
            // 재스케줄링 FAB 추가
            floatingActionButton = {
                if (!isLoading) { // 로딩 중이 아닐 때만 표시
                    ExtendedFloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    // 재스케줄링을 시작하고 완료될 때까지 기다립니다
                                    val rescheduleJob = singlePlanViewModel.postPlanReschedule(planId)
                                    rescheduleJob.join()

                                    // 이제 업데이트된 데이터 가져오기
                                    singlePlanViewModel.getPlanDetail(planId)

                                    delay(1000)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        containerColor = MainPurple,
                        contentColor = Color.White
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_reschedule),
                            contentDescription = "재스케줄링",
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("재스케줄링")
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 재스케줄링 버튼을 제거한 SinglePlanTitleSection
                SinglePlanTitleSection(
                    currentPlan = currentPlan,
                )

                // 날짜별 섹션
                planDetailResponse.dailySchedules.forEach { schedule ->
                    OneDaySection(
                        date = schedule.date,
                        planDetailLessonSchedules = schedule.lessonSchedules,
                        singlePlanViewModel = singlePlanViewModel,
                        onTaskLongPress = { task ->
                            selectedTask = task
                            showTaskBottomSheet = true
                        }
                    )
                }
            }

            // TaskItem 바텀시트 추가 (기존 바텀시트들 다음에)
            if (showTaskBottomSheet && selectedTask != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showTaskBottomSheet = false
                        selectedTask = null
                    },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    containerColor = Color.White,
                    dragHandle = {
                        // 드래그 핸들 영역 전체를 흰색 배경으로
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White) // 핸들 뒤쪽 배경을 흰색으로
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(4.dp)
                                    .background(
                                        color = Color.Gray.copy(alpha = 0.3f), // 핸들 자체는 회색
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }
                ) {
                    TaskActionBottomSheet(
                        taskItem = selectedTask!!,
                        onMoveToPreviousDay = {
                            // 전날로 이동 로직
//                            singlePlanViewModel.moveTaskToPreviousDay(selectedTask!!.id)
//                            showTaskBottomSheet = false
//                            selectedTask = null
                        },
                        onMoveToNextDay = {
                            // 다음날로 이동 로직
//                            singlePlanViewModel.moveTaskToNextDay(selectedTask!!.id)
//                            showTaskBottomSheet = false
//                            selectedTask = null
                        },
                        onDelete = {
                            // 삭제 로직
//                            singlePlanViewModel.deleteTask(selectedTask!!.id)
//                            showTaskBottomSheet = false
//                            selectedTask = null
                        }
                    )
                }
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
    onMenuClick: () -> Unit,
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
            }
        )
        HorizontalDivider(thickness = 1.dp, color = dividerGray)
    }
}

@Composable
fun SinglePlanTitleSection(
    currentPlan: GetPlanLectureRoomResponse,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        // 플랫폼 태그
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = currentPlan.platform.label,
                style = MaterialTheme.typography.labelMedium,
                color = MainPurple,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MainPurple,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )

            Text(
                text = currentPlan.subject.label,
                style = MaterialTheme.typography.labelMedium,
                color = currentPlan.subject.borderColor,
                modifier = Modifier
                    .background(
                        color = currentPlan.subject.bgColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = currentPlan.subject.borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )

            Text(
                text = "${currentPlan.completedLessons}/${currentPlan.totalLessons}강",
                style = MaterialTheme.typography.labelMedium,
                color = textGray,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = textGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }

        // 강의 제목
        Text(
            text = currentPlan.lectureTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 강의 제목
        Text(
            text = "${currentPlan.teacher} · ${currentPlan.tag}",
            style = MaterialTheme.typography.labelMedium,
            color = textGray
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OneDaySection(
    date: String,
    planDetailLessonSchedules: List<PlanDetailLessonSchedule>,
    singlePlanViewModel: SinglePlanViewModel,
    isReadOnly: Boolean = false,
    onTaskLongPress: (PlanDetailLessonSchedule) -> Unit = {}
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

            HorizontalDivider()

            planDetailLessonSchedules.forEach { lessonSchedule ->
                TaskItem(
                    planDetailLessonSchedule = lessonSchedule,
                    singlePlanViewModel = singlePlanViewModel,
                    isReadOnly = isReadOnly,
                    onLongPress = onTaskLongPress
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    planDetailLessonSchedule: PlanDetailLessonSchedule,
    singlePlanViewModel: SinglePlanViewModel,
    isReadOnly: Boolean = false,
    onLongPress: (PlanDetailLessonSchedule) -> Unit = {}
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
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    if (!isReadOnly) {
                        onLongPress(planDetailLessonSchedule)
                    }
                }
            )
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

                    if (!isReadOnly) {
                        // 체크박스 상태 변경 로직 (서버 업데이트, 백그라운드에서 실행)
                        singlePlanViewModel.patchLessonSchedulesCheckToggle(planDetailLessonSchedule.id)
                    }
                },
                isReadOnly = isReadOnly
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
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PlanDetailBottomSheet(
    planDetailResponse: PlanDetailResponse,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // 바텀시트 제목
        Text(
            text = "계획 상세 정보",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 계획 정보 목록
        PlanInfoItem(
            label = "계획 유형",
            value = when (planDetailResponse.planType) {
                "PERIOD" -> "기간"
                "TIME" -> "시간"
                else -> planDetailResponse.planType
            }
        )

        PlanInfoItem(
            label = "시작일",
            value = planDetailResponse.startDate
        )

        PlanInfoItem(
            label = "종료일",
            value = planDetailResponse.endDate
        )

        PlanInfoItem(
            label = "배속",
            value = "${planDetailResponse.playbackSpeed}배속"
        )

        // 구분선
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp),
            color = dividerGray
        )

        // 버튼들
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 수정하기 버튼
            TextButton(
                onClick = onEditClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = MainPurple,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_edit_pencil),
                    contentDescription = "수정",
                    tint = MainPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "수정하기",
                    color = MainPurple,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // 삭제하기 버튼
            TextButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_trash),
                    contentDescription = "삭제",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "삭제하기",
                    color = Color.Red,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // 바텀시트 하단 여백
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PlanInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TaskActionBottomSheet(
    taskItem: PlanDetailLessonSchedule,
    onMoveToPreviousDay: () -> Unit,
    onMoveToNextDay: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // 바텀시트 제목
        Text(
            text = "강의 관리",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 선택된 강의 제목
        Text(
            text = taskItem.lessonTitle,
            style = MaterialTheme.typography.bodyMedium,
            color = textGray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 구분선
        HorizontalDivider(
            color = dividerGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 액션 버튼들
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 전날로 이동
            TaskActionItem(
                icon = R.drawable.icon_arrow_back,
                text = "전날로 이동",
                onClick = onMoveToPreviousDay
            )

            // 다음날로 이동
            TaskActionItem(
                icon = R.drawable.icon_arrow_right, // 적절한 아이콘으로 변경
                text = "다음날로 이동",
                onClick = onMoveToNextDay
            )

            // 삭제
            TaskActionItem(
                icon = R.drawable.icon_trash,
                text = "삭제",
                textColor = Color.Red,
                iconTint = Color.Red,
                onClick = onDelete
            )
        }

        // 바텀시트 하단 여백
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TaskActionItem(
    icon: Int,
    text: String,
    textColor: Color = Color.Black,
    iconTint: Color = materialGray,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}