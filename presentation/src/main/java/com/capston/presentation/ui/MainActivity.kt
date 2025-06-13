package com.capston.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.chipGray
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.common.bgColor
import com.capston.presentation.ui.common.borderColor
import com.capston.presentation.ui.common.noRippleClickable
import com.capston.presentation.ui.home.CalenderScreen
import com.capston.presentation.ui.home.GroupPlanScreen
import com.capston.presentation.ui.home.HomeScreen
import com.capston.presentation.ui.home.LectureRoomScreen
import com.capston.presentation.ui.home.NotificationScreen
import com.capston.presentation.ui.home.ProfileScreen
import com.capston.presentation.ui.home.SinglePlanScreen
import com.capston.presentation.ui.home.PlanDetailScreen
import com.capston.presentation.ui.search.SearchActivity
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.GroupPlanViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.LectureRoomViewModel
import com.capston.presentation.viewmodel.LoginViewModel
import com.capston.presentation.viewmodel.MyPageViewModel
import com.capston.presentation.viewmodel.PlanDetailViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import com.capston.presentation.viewmodel.RecommendViewModel
import com.capston.presentation.viewmodel.SinglePlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val loginViewModel: LoginViewModel by viewModels()
    val homeViewModel: HomeViewModel by viewModels()
    val planViewModel: PlanViewModel by viewModels()
    val dailyScheduleViewModel: DailyScheduleViewModel by viewModels()
    val lectureRoomViewModel: LectureRoomViewModel by viewModels()
    val singlePlanViewModel: SinglePlanViewModel by viewModels()
    val groupPlanViewModel: GroupPlanViewModel by viewModels()
    val planDetailViewModel: PlanDetailViewModel by viewModels()
    val myPageViewModel: MyPageViewModel by viewModels()
    val recommendViewModel: RecommendViewModel by viewModels()

    // 뒤로가기 두 번 누르기 위한 변수들
    var backPressedTime: Long = 0
    val BACK_PRESS_INTERVAL: Long = 2000 // 2초 내에 두 번 눌러야 함

    @Inject
    lateinit var loadingStateManager: LoadingStateManager

    // 알림 권한 승인을 위한 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    // 검색 액티비티 실행 및 종료 시 OK를 받기 위한 런처
    @RequiresApi(Build.VERSION_CODES.O)
    private val startSearchForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh all views when returning with RESULT_OK
            refreshAllData()
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 시스템 레이아웃 설정
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        loginViewModel.checkAccessToken()

        // ViewModel 간 동기화 설정
        setupViewModelSynchronization()

        // 알림 권한 묻기
        askNotificationPermission()

        // UI 설정
        setContent {
            LaunchedEffect(Unit) {
                homeViewModel.getDistinctHome()
            }

            CapstonTheme {
                Scaffold(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.systemBars) // ⬅️ 상태바 + 네비게이션 바 여백 포함
                ) { paddingValues ->
                    Box(modifier = Modifier
                        .consumeWindowInsets(paddingValues)
                    ) {
                        MainBottomBar(
                            homeViewModel = homeViewModel,
                            planViewModel = planViewModel,
                            dailyScheduleViewModel = dailyScheduleViewModel,
                            lectureRoomViewModel = lectureRoomViewModel,
                            singlePlanViewModel = singlePlanViewModel,
                            groupPlanViewModel = groupPlanViewModel,
                            planDetailViewModel = planDetailViewModel,
                            loginViewModel = loginViewModel,
                            myPageViewModel = myPageViewModel,
                            recommendViewModel = recommendViewModel,
                            loadingStateManager = loadingStateManager
                        )

                        // 전역 로딩 인디케이터
                        LoadingIndicator(loadingStateManager)
                    }
                }
            }
        }
    }

    // ViewModel 간 동기화 설정
    private fun setupViewModelSynchronization() {
        // LectureRoomViewModel에서 데이터가 변경되면 다른 ViewModel들도 새로고침
        lectureRoomViewModel.onDataChanged = {
            homeViewModel.forceRefresh()

            // DailyScheduleViewModel도 현재 날짜로 새로고침
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dailyScheduleViewModel.getDailySchedule(
                    LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            }
        }

        // DailyScheduleViewModel에서 데이터가 변경되면 HomeViewModel도 새로고침
        dailyScheduleViewModel.onDataChanged = {
            homeViewModel.forceRefresh()
        }

        // 여기에 추가 ↓
        singlePlanViewModel.onHomeDataChanged = {
            homeViewModel.forceRefresh()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dailyScheduleViewModel.forceRefresh(
                    LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            }
        }

        singlePlanViewModel.onLectureRoomDataChanged = {
            lectureRoomViewModel.getPlanLectureRoom() // 강의실 목록 새로고침
        }

        groupPlanViewModel.onDataChanged = {
            lectureRoomViewModel.getPlanLectureRoom() // 강의실 목록 새로고침
            homeViewModel.forceRefresh()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dailyScheduleViewModel.getDailySchedule(
                    LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            }
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startSearchForResult.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshAllData() {
        homeViewModel.getDistinctHome()
        dailyScheduleViewModel.getDailySchedule(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        lectureRoomViewModel.getPlanLectureRoom()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainBottomBar(
    homeViewModel: HomeViewModel,
    planViewModel: PlanViewModel,
    dailyScheduleViewModel: DailyScheduleViewModel,
    lectureRoomViewModel: LectureRoomViewModel,
    singlePlanViewModel: SinglePlanViewModel,
    groupPlanViewModel: GroupPlanViewModel,
    planDetailViewModel: PlanDetailViewModel,
    loginViewModel: LoginViewModel,
    myPageViewModel: MyPageViewModel,
    recommendViewModel: RecommendViewModel,
    loadingStateManager: LoadingStateManager
) {
    val context = LocalContext.current
    val mainActivity = LocalActivity.current as MainActivity
    val navController = rememberNavController()

    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    var fabExpanded by remember { mutableStateOf(false) }
    var showInviteCodeDialog by remember { mutableStateOf(false) }
    var showGroupCreationBottomSheet by remember { mutableStateOf(false) }

    // BackHandler
    BackHandler {
        if (fabExpanded) {
            fabExpanded = false
        } else if (bottomNavState == 0) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - mainActivity.backPressedTime < mainActivity.BACK_PRESS_INTERVAL) {
                mainActivity.finish()
            } else {
                mainActivity.backPressedTime = currentTime
                Toast.makeText(
                    context,
                    "한 번 더 뒤로가기를 누르면 앱이 종료됩니다",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            bottomNavState = 0
            navController.navigate(Screen.Home.title) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomBarWithoutFAB(
                    navController = navController,
                    bottomNavState = bottomNavState,
                    onNavItemClick = { index ->
                        if (bottomNavState != index) {
                            bottomNavState = index
                            val destination = when (index) {
                                0 -> Screen.Home.title
                                1 -> Screen.Calender.title
                                2 -> Screen.LectureRoom.title
                                3 -> Screen.Profile.title
                                else -> Screen.Home.title
                            }
                            navController.navigate(destination) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = contentPadding.calculateTopPadding(),
                        bottom = 60.dp // FAB를 고려한 적절한 하단 패딩 (80dp 바텀바 - 40dp FAB offset)
                    )
            ) {
                composable(Screen.Home.title) {
                    HomeScreen(homeViewModel, planViewModel, recommendViewModel, navController)
                }

                composable(Screen.Calender.title) {
                    CalenderScreen(homeViewModel, dailyScheduleViewModel)

                }
                composable(Screen.LectureRoom.title) {
                    LectureRoomScreen(
                        lectureRoomViewModel = lectureRoomViewModel,
                        onSinglePlanClick = { plan ->
                            // ViewModel에 plan 객체 저장
                            singlePlanViewModel.setPlanData(plan)
                            navController.navigate("${Screen.SinglePlan.title}/${plan.planId}")
                        },
                        onGroupPlanClick = { plan ->
                            navController.navigate("${Screen.GroupPlan.title}/${plan.studyGroupId}/${plan.planId}")
                        },
                        onNotificationClick = { navController.navigate("notification") }
                    )
                }

                composable(
                    route = "${Screen.SinglePlan.title}/{planId}",
                    arguments = listOf(navArgument("planId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                    val planDetailResponse = PlanDetailResponse()
                    SinglePlanScreen(
                        planId = planId,
                        singlePlanViewModel = singlePlanViewModel,
                        navController = navController,
                    )
                }

                composable(
                    route = "${Screen.GroupPlan.title}/{studyGroupId}/{planId}",
                    arguments = listOf(
                        navArgument("planId") { type = NavType.IntType },
                        navArgument("studyGroupId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                    val studyGroupId = backStackEntry.arguments?.getInt("studyGroupId") ?: 0
                    GroupPlanScreen(
                        planId = planId,
                        studyGroupId = studyGroupId,
                        groupPlanViewModel = groupPlanViewModel,
                        navController = navController,
                    )
                }

                composable(
                    "${Screen.PlanDetail.title}/{planId}/{screenType}?studyGroupId={studyGroupId}",
                    arguments = listOf(
                        navArgument("planId") { type = NavType.IntType },
                        navArgument("screenType") { type = NavType.StringType },
                        navArgument("studyGroupId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                    val screenType = backStackEntry.arguments?.getString("screenType") ?: "single"
                    val studyGroupId = backStackEntry.arguments?.getInt("studyGroupId") ?: -1

                    PlanDetailScreen(
                        planId = planId,
                        screenType = screenType,
                        studyGroupId = studyGroupId,
                        navController = navController,
                        planDetailViewModel = planDetailViewModel,
                        singlePlanViewModel = if (screenType == "single") singlePlanViewModel else null,
                        groupPlanViewModel = if (screenType == "group") groupPlanViewModel else null
                    )
                }

                composable(Screen.Profile.title) {
                    ProfileScreen(
                        loginViewModel = loginViewModel,
                        myPageViewModel = myPageViewModel
                    )
                }

                composable(Screen.Notification.title) { NotificationScreen() }
            }
        }

        // 배경 오버레이 추가
        if (fabExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.8f))
                    .noRippleClickable { fabExpanded = false }
            )
        }

        // 검색 FAB
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp),
        ) {
            ExpandingFAB(
                modifier = Modifier.fillMaxSize(),
                fabColor = MainPurple,
                expanded = fabExpanded, // 상태 전달
                onExpandedChange = { fabExpanded = it }, // 상태 변경 콜백
                onInviteCodeClick = { showInviteCodeDialog = true },
                onGroupCreationClick = { showGroupCreationBottomSheet = true }
            )
        }

        if (showInviteCodeDialog) {
            InviteCodeDialog(
                onDismiss = { showInviteCodeDialog = false },
                onConfirm = { inviteCode ->
                    lectureRoomViewModel.postJoinStudyGroup(
                        JoinStudyGroupDto(inviteCode = inviteCode)
                    )
                    showInviteCodeDialog = false
                }
            )
        }

        // 그룹 생성 바텀시트 추가
        if (showGroupCreationBottomSheet) {
            GroupCreationBottomSheet(
                lectureRoomViewModel = lectureRoomViewModel,
                singlePlanViewModel = singlePlanViewModel,
                onDismiss = { showGroupCreationBottomSheet = false }
            )
        }
    }
}

@Composable
fun BottomBarWithoutFAB(
    navController: NavController,
    bottomNavState: Int,
    onNavItemClick: (Int) -> Unit
) {
    val items: List<Screen> = listOf(
        Screen.Home,
        Screen.Calender,
        Screen.LectureRoom,
        Screen.Profile,
    )

    // 완전 커스텀으로 만들기
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        HorizontalDivider(color = dividerGray)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                if (index == 2) {
                    Spacer(modifier = Modifier.width(60.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .noRippleClickable { onNavItemClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    when (val icon = if (bottomNavState == index) item.selectedIcon else item.unselectedIcon) {
                        is ImageVector -> Icon(
                            imageVector = icon,
                            contentDescription = item.title,
                            tint = if (bottomNavState == index) MainPurple else Color.Gray
                        )
                        is Int -> Image(
                            painter = painterResource(icon),
                            contentDescription = item.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandingFAB(
    modifier: Modifier = Modifier,
    fabColor: Color = MainPurple,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onInviteCodeClick: () -> Unit,
    onGroupCreationClick: () -> Unit
) {
    val mainActivity = LocalActivity.current as MainActivity

    val rotationYValue by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOutQuart
        ),
        label = "rotationY"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        // 왼쪽 대각선 위 (-100dp x, -100dp y) - 강의 검색
        AnimatedSmallFAB(
            visible = expanded,
            targetOffset = IntOffset(-180, -180), // 이 offset은 중앙을 기준으로 조절해야 할 수 있음
            icon = R.drawable.icon_search,
            label = "강의 검색",
            onClick = {
                onExpandedChange(false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mainActivity.startSearchActivity()
                }
            },
        )

        // 바로 위 (0dp x, -120dp y) - 그룹 생성
        AnimatedSmallFAB(
            visible = expanded,
            targetOffset = IntOffset(0, -240),
            icon = R.drawable.icon_group,
            label = "그룹 생성",
            onClick = {
                onExpandedChange(false)
                // SearchActivity 대신 바텀시트 표시 로직으로 변경
                onGroupCreationClick() // 새로운 콜백 추가 필요
            },
        )

        // 오른쪽 대각선 위 (100dp x, -100dp y) - 그룹 검색
        AnimatedSmallFAB(
            visible = expanded,
            targetOffset = IntOffset(180, -180), // 이 offset은 중앙을 기준으로 조절해야 할 수 있음
            icon = R.drawable.icon_community,
            label = "그룹 참여",
            onClick = {
                onExpandedChange(false)
                onInviteCodeClick()
            },
        )

        // 메인 FAB (뒤집기 효과)
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            containerColor = fabColor,
            shape = RoundedCornerShape(32.dp), // 더 둥글게
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    rotationY = rotationYValue
                }
        ) {
            Icon(
                painter = if (expanded) painterResource(id = R.drawable.icon_xmark) else painterResource(id = R.drawable.icon_search),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AnimatedSmallFAB(
    visible: Boolean,
    targetOffset: IntOffset,
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    val density = LocalDensity.current

    // 위치 애니메이션 - 메인 FAB 위치(0,0)에서 시작해서 목표 위치로 이동
    val animatedOffset by animateIntOffsetAsState(
        targetValue = if (visible) targetOffset else IntOffset(0, 0),
        animationSpec = tween(
            durationMillis = 300, // 400ms -> 300ms로 단축
            easing = EaseOutQuart
        ),
        label = "fabOffset"
    )

    // 스케일과 투명도를 하나의 애니메이션으로 통합
    val animationProgress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 250, // 단축
            delayMillis = if (visible) 50 else 0, // 지연 시간 단축
            easing = EaseOutQuart
        ),
        label = "fabAnimation"
    )

    // 스케일을 0.3에서 시작하도록 조정 (완전히 납작하지 않게)
    val scale = 0.3f + (animationProgress * 0.7f)
    val alpha = animationProgress

    if (visible || animationProgress > 0f) {
        Box(
            modifier = Modifier
                .offset { animatedOffset }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.alpha = alpha
                },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 라벨을 항상 위쪽에 배치
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1, // 한 줄로 제한
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .wrapContentWidth() // 텍스트 너비에 맞춤
                )

                FloatingActionButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(28.dp),
                    containerColor = Color.White.copy(alpha = 0.95f),
                    contentColor = MainPurple,
                    modifier = Modifier
                        .size(56.dp)
                        .aspectRatio(1f), // 정사각형 비율 강제
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCreationBottomSheet(
    lectureRoomViewModel: LectureRoomViewModel,
    singlePlanViewModel: SinglePlanViewModel,
    onDismiss: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedPlan by remember { mutableStateOf<GetPlanLectureRoomResponse?>(null) }

    val lectures by lectureRoomViewModel.getPlanLectureRoomResponse.collectAsState()
    val individualPlans = lectures.filter { !it.studyGroup } // 개별 계획만 필터링

    // 바텀시트가 열릴 때 데이터 로드
    LaunchedEffect(Unit) {
        lectureRoomViewModel.getPlanLectureRoom()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 헤더
            Text(
                text = "그룹으로 전환할 계획 선택",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (individualPlans.isEmpty()) {
                // 빈 상태
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = "그룹으로 전환할 수 있는 개별 계획이 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 개별 계획 목록
                LazyColumn {
                    items(individualPlans) { plan ->
                        PlanSelectionItem(
                            plan = plan,
                            onSelectClick = {
                                selectedPlan = plan
                                showConfirmDialog = true
                            }
                        )
                        if (plan != individualPlans.last()) {
                            HorizontalDivider(color = dividerGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 확인 다이얼로그
    if (showConfirmDialog && selectedPlan != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("그룹 전환 확인") },
            text = {
                Text("'${selectedPlan!!.lectureTitle}' 계획을\n스터디 그룹으로 전환하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        singlePlanViewModel.postNewStudyGroup(selectedPlan!!.planId)
                        showConfirmDialog = false
                        onDismiss()
                    }
                ) {
                    Text("확인", color = MainPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("취소", color = textGray)
                }
            }
        )
    }
}

@Composable
fun PlanSelectionItem(
    plan: GetPlanLectureRoomResponse,
    onSelectClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 플랫폼과 선생님 태그
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = plan.platform.label,
                    style = MaterialTheme.typography.labelSmall,
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
                    text = plan.teacher,
                    style = MaterialTheme.typography.labelSmall,
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
                    text = plan.subject.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = plan.subject.borderColor,
                    modifier = Modifier
                        .background(
                            color = plan.subject.bgColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = plan.subject.borderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Text(
                    text = "${plan.completedLessons}/${plan.totalLessons}강",
                    style = MaterialTheme.typography.labelSmall,
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
                text = plan.lectureTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 선택 버튼
        TextButton(
            onClick = onSelectClick,
            colors = ButtonDefaults.textButtonColors(
                containerColor = MainPurple,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(36.dp)
                .width(60.dp)
        ) {
            Text(
                text = "선택",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun InviteCodeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier.width(240.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "그룹 참여",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 구분선
                HorizontalDivider(color = dividerGray)

                Spacer(modifier = Modifier.height(16.dp))

                // 초대코드 입력
                TextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it },
                    singleLine = true,
                    placeholder = { Text("초대코드를 입력하세요", fontSize = 14.sp, color = Color.Gray) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = MainPurple,
                        unfocusedIndicatorColor = LightGray60
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼들
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { onDismiss() },
                        label = { Text("취소", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipGray,
                            labelColor = Color.Black
                        ),
                        border = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilterChip(
                        selected = false,
                        onClick = {
                            if (inviteCode.isNotBlank()) {
                                onConfirm(inviteCode.trim())
                            }
                        },
                        label = { Text("참여", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MainPurple,
                            labelColor = Color.White
                        ),
                        border = null
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}