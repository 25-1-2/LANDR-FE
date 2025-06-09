package com.capston.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.dividerGray
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.common.noRippleClickable
import com.capston.presentation.ui.home.CalenderScreen
import com.capston.presentation.ui.home.GroupPlanScreen
import com.capston.presentation.ui.home.HomeScreen
import com.capston.presentation.ui.home.LectureRoomScreen
import com.capston.presentation.ui.home.NotificationScreen
import com.capston.presentation.ui.home.ProfileScreen
import com.capston.presentation.ui.home.SinglePlanScreen
import com.capston.presentation.ui.search.SearchActivity
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.GroupPlanViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.LectureRoomViewModel
import com.capston.presentation.viewmodel.LoginViewModel
import com.capston.presentation.viewmodel.MyPageViewModel
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
    val singlePlanViewModel: SinglePlanViewModel by viewModels()  // 추가
    val groupPlanViewModel: GroupPlanViewModel by viewModels()
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
        singlePlanViewModel.onDataChanged = {
            lectureRoomViewModel.getPlanLectureRoom() // 강의실 목록 새로고침
            homeViewModel.forceRefresh()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dailyScheduleViewModel.getDailySchedule(
                    LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            }
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

@Composable
fun SearchFieldWithIcons(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .height(40.dp)
            .border(BorderStroke(0.5.dp, Color.LightGray), shape = RoundedCornerShape(20.dp))
            .background(LightGray4_40, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize() // Use fillMaxSize to take up the full height
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기", tint = Color.Gray)
            }

            BasicTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.DarkGray),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .wrapContentHeight(Alignment.CenterVertically), // Center text vertically
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "계획을 생성하고 싶은 강의 또는 선생님을 검색하세요",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChanged("") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray
                    )
                }
            }

            IconButton(onClick = { onSearchClick() }, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "검색", tint = Color.Gray)
            }
        }
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
    loginViewModel: LoginViewModel,
    myPageViewModel: MyPageViewModel,
    recommendViewModel: RecommendViewModel,
    loadingStateManager: LoadingStateManager
) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    var fabExpanded by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val mainActivity = LocalActivity.current as MainActivity
    val context = LocalContext.current

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
                android.widget.Toast.makeText(
                    context,
                    "한 번 더 뒤로가기를 누르면 앱이 종료됩니다",
                    android.widget.Toast.LENGTH_SHORT
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
                composable(Screen.Home.title) { HomeScreen(homeViewModel, planViewModel, recommendViewModel, navController) }
                composable(Screen.Calender.title) { CalenderScreen(homeViewModel, dailyScheduleViewModel) }
                composable(Screen.LectureRoom.title) {
                    LectureRoomScreen(
                        lectureRoomViewModel = lectureRoomViewModel,
                        onSinglePlanClick = { plan ->
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
                    val planDetailResponse = GetPlanDetailResponse()
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
                onExpandedChange = { fabExpanded = it } // 상태 변경 콜백
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
    onExpandedChange: (Boolean) -> Unit
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mainActivity.startSearchActivity()
                }
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mainActivity.startSearchActivity()
                }
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
                        painter = painterResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}