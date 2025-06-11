package com.capston.presentation.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capston.domain.response.plan.PlanDetailLessonSchedule
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.WarmPurple_20
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.ui.common.CustomCheckBox
import com.capston.presentation.ui.common.formatDateYMDE
import com.capston.presentation.viewmodel.GroupPlanViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupPlanScreen(
    planId: Int,
    studyGroupId: Int,
    groupPlanViewModel: GroupPlanViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDropdown by remember { mutableStateOf(false) }
    var showPlanDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showGroupDeleteConfirmDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // 선택된 멤버의 planId 상태
    var selectedPlanId by remember { mutableIntStateOf(planId) }

    val getOneStudyGroupResponse by groupPlanViewModel.getOneStudyGroupResponse.collectAsState()
    val planDetailResponse by groupPlanViewModel.planDetailResponse.collectAsState()
    val deleteOneStudyGroupResponse by groupPlanViewModel.deleteOneStudyGroupResponse.collectAsState()

    val myMember = getOneStudyGroupResponse.members.find { it.planId == planId }
    val isLeader = myMember?.userId == getOneStudyGroupResponse.leaderId

    LaunchedEffect(studyGroupId) {
        groupPlanViewModel.getOneStudyGroup(studyGroupId)
    }

    // 선택된 planId가 변경될 때마다 해당 플랜 상세 정보 가져오기
    LaunchedEffect(planId) {
        groupPlanViewModel.getPlanDetail(planId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isLoading) Modifier.blur(8.dp) else Modifier
                ),
            topBar = {
                GroupPlanTopBar(
                    navController = navController,
                    showMenu = showDeleteDropdown,
                    onMenuClick = { showDeleteDropdown = !showDeleteDropdown },
                    onMenuDismiss = { showDeleteDropdown = false },
                    onPlanDeleteClick = { showPlanDeleteConfirmDialog = true },
                    onGroupDeleteClick = { showGroupDeleteConfirmDialog = true },
                    isLeader = isLeader
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                GroupPlanTitleSection(
                    planId = planId,
                    studyGroupId = studyGroupId,
                    groupPlanViewModel = groupPlanViewModel,
                    getOneStudyGroupResponse = getOneStudyGroupResponse,
                    coroutineScope = coroutineScope,
                    isLoading = isLoading,
                    onLoadingChange = { isLoading = it },
                    selectedPlanId = selectedPlanId,
                    onMemberClick = { memberPlanId ->
                        selectedPlanId = memberPlanId
                        groupPlanViewModel.getPlanDetail(memberPlanId)
                    },
                )

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp)) // 간격 추가

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                ) {
                    planDetailResponse.dailySchedules.forEach { schedule ->
                        GroupPlanOneDaySection(
                            date = schedule.date,
                            planDetailLessonSchedules = schedule.lessonSchedules,
                            groupPlanViewModel = groupPlanViewModel,
                            isReadOnly = selectedPlanId != planId  // 추가: 내 계획이 아니면 읽기 전용
                        )
                    }
                }

            }

            // 삭제 확인 다이얼로그
            if (showPlanDeleteConfirmDialog) {
                AlertDialog(
                    containerColor = Color.White,
                    iconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    textContentColor = Color.Black,
                    tonalElevation = 0.dp, // 그림자 효과 제거
                    onDismissRequest = { showPlanDeleteConfirmDialog = false },
                    title = { Text("계획 삭제") },
                    text = { Text("이 계획을 삭제하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                // 삭제 로직 실행
                                groupPlanViewModel.deleteOnePlan(planId)
                                navController.popBackStack() // 이전 화면으로 돌아가기
                                showPlanDeleteConfirmDialog = false
                            }
                        ) {
                            Text("삭제", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPlanDeleteConfirmDialog = false }) {
                            Text("취소")
                        }
                    }
                )
            }

            // 그룹 삭제 다이얼로그
            if (showGroupDeleteConfirmDialog) {
                AlertDialog(
                    // 다이얼로그 내용
                    onDismissRequest = { showGroupDeleteConfirmDialog = false },
                    title = { Text("그룹 삭제") },
                    text = { Text("그룹을 삭제하시겠습니까?\n그룹원까지 모두 계획이 삭제됩니다.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                groupPlanViewModel.deleteOneStudyGroup(studyGroupId)
                                navController.popBackStack() // 이전 화면으로 돌아가기
                                showGroupDeleteConfirmDialog = false
                            }
                        ) {
                            Text("삭제", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showGroupDeleteConfirmDialog = false }) {
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
fun GroupPlanTopBar(
    navController: NavController,
    showMenu: Boolean,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onPlanDeleteClick: () -> Unit,
    onGroupDeleteClick: () -> Unit,
    isLeader: Boolean
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
                    if (isLeader) {
                        // 방장일 경우 메뉴들
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
                            onClick = { onMenuDismiss() }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_group),
                                        contentDescription = "그룹원 관리",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("그룹원 관리", color = Color.Black)
                                }
                            },
                            onClick = { onMenuDismiss() }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_user_crown),
                                        contentDescription = "그룹장 위임",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("그룹장 위임", color = Color.Black)
                                }
                            },
                            onClick = {
                                onMenuDismiss()
//                                onDeleteClick()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_user_xmark),
                                        contentDescription = "그룹 삭제",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("그룹 삭제", color = Color.Red)
                                }
                            },
                            onClick = {
                                onMenuDismiss()
                                onGroupDeleteClick()
                            }
                        )
                    } else {
                        // 그룹원일 경우 메뉴
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon_user_xmark),
                                        contentDescription = "그룹 나가기",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("그룹 나가기", color = Color.Red)
                                }
                            },
                            onClick = {
                                onMenuDismiss()
                                onPlanDeleteClick()
                            }
                        )
                    }
                }
            }
        )
        HorizontalDivider(thickness = 1.dp, color = LightGray2)
    }
}

@Composable
fun GroupPlanTitleSection(
    planId: Int,
    studyGroupId: Int,
    groupPlanViewModel: GroupPlanViewModel,
    getOneStudyGroupResponse: OneStudyGroupResponse,
    coroutineScope: CoroutineScope,
    isLoading: Boolean,
    onLoadingChange: (Boolean) -> Unit,
    selectedPlanId: Int,
    onMemberClick: (Int) -> Unit
) {
    // 내가 방장인지 확인
    val myMember = getOneStudyGroupResponse.members.find { it.planId == planId }
    val isLeader = myMember?.userId == getOneStudyGroupResponse.leaderId

    // 내 계획을 보고 있는지 확인
    val isMyPlan = selectedPlanId == planId

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = WarmPurple_20)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth()
                    .weight(10f),
            ) {
                Text(
                    text = "${getOneStudyGroupResponse.name} (#${getOneStudyGroupResponse.inviteCode})",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = getOneStudyGroupResponse.lectureName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textGray
                )
            }

            // 버튼들
            Column{
                if (isMyPlan) {
                    // 재스케줄링 버튼 - 내 계획일 때만 표시
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                onLoadingChange(true)
                                try {
                                    // 재스케줄링을 시작하고 완료될 때까지 기다립니다
                                    val rescheduleJob = groupPlanViewModel.postPlanReschedule(planId)
                                    rescheduleJob.join()

                                    // 이제 업데이트된 데이터 가져오기
                                    groupPlanViewModel.getPlanDetail(planId)

                                    delay(1000)
                                } finally {
                                    onLoadingChange(false)
                                }
                        }
                        },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = CircleShape
                            )
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_reschedule),
                            contentDescription = "재스케줄링",
                            tint = MainPurple,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (isLeader) {
                    // 그룹 이름 편집 버튼
                    IconButton(
                        onClick = {
//                    coroutineScope.launch {
//                        onLoadingChange(true)
//                        try {
//                            // 재스케줄링을 시작하고 완료될 때까지 기다립니다
//                            val rescheduleJob = lectureRoomViewModel.postPlanReschedule(planId)
//                            rescheduleJob.join()
//
//                            // 이제 업데이트된 데이터 가져오기
//                            lectureRoomViewModel.getPlanDetail(planId)
//
//                            delay(1000)
//                        } finally {
//                            onLoadingChange(false)
//                        }
//                    }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = CircleShape
                            )
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.icon_edit_pencil),
                            contentDescription = "이름 편집",
                            tint = MainPurple,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        // 프로필 목록
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 먼저 나를 표시
            val myMember = getOneStudyGroupResponse.members.find { it.planId == planId }
            myMember?.let { member ->
                ProfileItem(
                    name = "나",
                    isMe = true,
                    isCrown = member.userId == getOneStudyGroupResponse.leaderId,
                    isSelected = selectedPlanId == member.planId,
                    onClick = { onMemberClick(member.planId) }
                )
            }

            // 세로 구분선
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(68.dp)
                    .background(dividerGray)
            )

            // 나머지 멤버들 표시
            getOneStudyGroupResponse.members
                .filter { it.planId != planId }
                .forEach { member ->
                    ProfileItem(
                        name = member.userName,
                        isMe = false,
                        isCrown = member.userId == getOneStudyGroupResponse.leaderId,
                        isSelected = selectedPlanId == member.planId,
                        onClick = { onMemberClick(member.planId) }
                    )
                }
        }
    }

}

@Composable
fun ProfileItem(
    name: String,
    isMe: Boolean = false,
    isCrown: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable { onClick() }
    ) {
        // 프로필 이미지 컨테이너
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = Color(0xFFE8F4FD), // 연한 파란색 배경
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,  // 선택시 테두리 두께 변경
                    color = if (isSelected) MainPurple else Color(0xFFD1E7F5),  // 선택시 MainPurple
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 사람 아이콘
            Icon(
                imageVector = Icons.Default.Person,
//                painter = painterResource(id = R.drawable.), // 사람 아이콘 리소스 필요
                contentDescription = "프로필",
                tint = Color(0xFF7BB3D9),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 이름 텍스트
        Text(
            text = if (isCrown) "👑$name" else name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MainPurple else Color.Black,  // 선택시 색상 변경
            fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupPlanOneDaySection(
    date: String,
    planDetailLessonSchedules: List<PlanDetailLessonSchedule>,
    groupPlanViewModel: GroupPlanViewModel,
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
                GroupPlanTaskItem(
                    planDetailLessonSchedule = lessonSchedule,
                    groupPlanViewModel = groupPlanViewModel,
                    isReadOnly = isReadOnly
                )
            }
        }
    }
}

@Composable
fun GroupPlanTaskItem(
    planDetailLessonSchedule: PlanDetailLessonSchedule,
    groupPlanViewModel: GroupPlanViewModel,
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
                        groupPlanViewModel.patchLessonSchedulesCheckToggle(planDetailLessonSchedule.id)
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
                .padding(horizontal = 4.dp, vertical = 4.dp)
        )
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun GroupPlanScreenPreview() {
//    CapstonTheme {
//        GroupPlanScreen()
//    }
//}
