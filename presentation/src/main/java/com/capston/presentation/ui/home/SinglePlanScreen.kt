package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.graphics.Color
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SinglePlanScreen(
    planId: Int,
    singlePlanViewModel: SinglePlanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDropdown by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showGroupConfirmDialog by remember { mutableStateOf(false) }
    var showGroupCodeDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val planDetailResponse by singlePlanViewModel.planDetailResponse.collectAsState()
    val studyGroupResponse by singlePlanViewModel.postNewStudyGroupResponse.collectAsState()

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
                    showMenu = showDeleteDropdown,
                    onMenuClick = { showDeleteDropdown = !showDeleteDropdown },
                    onMenuDismiss = { showDeleteDropdown = false },
                    onDeleteClick = { showDeleteConfirmDialog = true },
                    onEditClick = {
                        val editRoute = when (planDetailResponse.planType) {
                            "PERIOD" -> "${Screen.PeriodPlanEdit.title}/${planId}"
                            "TIME" -> "${Screen.TimePlanEdit.title}/${planId}"
                            else -> "${Screen.PeriodPlanEdit.title}/${planId}" // 기본값
                        }
                        navController.navigate(editRoute)
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
                    planDetailResponse = planDetailResponse,
                    onGroupClick = { showGroupConfirmDialog = true }
                )

                // 날짜별 섹션
                planDetailResponse.dailySchedules.forEach { schedule ->
                    OneDaySection(
                        date = schedule.date,
                        planDetailLessonSchedules = schedule.lessonSchedules,
                        singlePlanViewModel = singlePlanViewModel
                    )
                }
            }

            // 기존 다이얼로그들...
            if (showDeleteConfirmDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    iconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black,
                    tonalElevation = 0.dp,
                    onDismissRequest = { showDeleteConfirmDialog = false },
                    title = { Text("계획 삭제") },
                    text = { Text("이 계획을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                singlePlanViewModel.deleteOnePlan(planId)
                                navController.popBackStack()
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

            if (showGroupConfirmDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    iconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black,
                    tonalElevation = 0.dp,
                    onDismissRequest = { showGroupConfirmDialog = false },
                    title = { Text("스터디 그룹 생성") },
                    text = {
                        Text("이 계획을 스터디 그룹으로 전환하시겠습니까?\n다른 사람들과 함께 공부할 수 있습니다.")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showGroupConfirmDialog = false
                                singlePlanViewModel.postNewStudyGroup(planId)
                                showGroupCodeDialog = true
                            }
                        ) {
                            Text("확인", color = MainPurple)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showGroupConfirmDialog = false }) {
                            Text("취소")
                        }
                    }
                )
            }

            if (showGroupCodeDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    iconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black,
                    tonalElevation = 0.dp,
                    onDismissRequest = { },
                    title = {
                        Text(
                            "스터디 그룹이 생성되었습니다!",
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "아래 코드를 친구들에게 공유하세요",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = studyGroupResponse.inviteCode,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MainPurple,
                                modifier = Modifier
                                    .background(
                                        Color.Gray.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showGroupCodeDialog = false
                                navController.popBackStack()
                            }
                        ) {
                            Text("확인", color = MainPurple)
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
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
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
                            onEditClick()
                        }
                    )

                    // 계획 삭제
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_trash),
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
    planDetailResponse: PlanDetailResponse,
    onGroupClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        // 플랫폼 태그
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = planDetailResponse.platform.label,
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
        }

        // 강의 제목
        Text(
            text = planDetailResponse.lectureTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 강의 제목
        Text(
            text = "${planDetailResponse.teacher} ·",
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
    isReadOnly: Boolean = false
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
                    isReadOnly = isReadOnly
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    planDetailLessonSchedule: PlanDetailLessonSchedule,
    singlePlanViewModel: SinglePlanViewModel,
    isReadOnly: Boolean = false
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