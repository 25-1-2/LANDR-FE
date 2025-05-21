package com.capston.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
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
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.home.CalenderScreen
import com.capston.presentation.ui.home.HomeScreen
import com.capston.presentation.ui.home.LectureRoomScreen
import com.capston.presentation.ui.home.PlanDetailScreen
import com.capston.presentation.ui.home.ProfileScreen
import com.capston.presentation.ui.search.SearchActivity
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.LectureRoomViewModel
import com.capston.presentation.viewmodel.LoginViewModel
import com.capston.presentation.viewmodel.MyPageViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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
    val myPageViewModel: MyPageViewModel by viewModels()

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

    // Register for activity results using the new Activity Result API
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
        enableEdgeToEdge()

        loginViewModel.checkAccessToken()

        // 알림 권한 묻기
        askNotificationPermission()

        // UI 설정
        setContent {
            LaunchedEffect(Unit) {
                delay(500)
                homeViewModel.getDistinctHome()
                homeViewModel.getDistinctHome.collectLatest { homeData ->
                    val dday = homeData.dday
                    if (dday != null && dday.ddayId > 0) {
                        homeViewModel.getDDay(dday.ddayId)
                    } else {
                        // Handle the case when there's no D-Day
                        Log.d("HomeScreen", "No D-Day information available")
                    }
                }

                loginViewModel.getUserProfile()
            }

            CapstonTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    SettingTopBottomBar(
                        homeViewModel = homeViewModel,
                        planViewModel = planViewModel,
                        dailyScheduleViewModel = dailyScheduleViewModel,
                        lectureRoomViewModel = lectureRoomViewModel,
                        loginViewModel = loginViewModel,
                        myPageViewModel = myPageViewModel)

                    // 전역 로딩 인디케이터
                    LoadingIndicator(loadingStateManager)
                }
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
fun SettingTopBottomBar(
    homeViewModel: HomeViewModel,
    planViewModel: PlanViewModel,
    dailyScheduleViewModel: DailyScheduleViewModel,
    lectureRoomViewModel: LectureRoomViewModel,
    loginViewModel: LoginViewModel,
    myPageViewModel: MyPageViewModel
) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val mainActivity = LocalActivity.current as MainActivity
    val context = LocalContext.current

    // 뒤로가기 처리 추가 - 상태를 기반으로 처리
    BackHandler {
        if (bottomNavState == 0) {
            // 홈 화면에서는 원래 코드대로 앱 종료 로직 실행
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
            // 어떤 화면에서든 항상 홈으로 강제 이동
            bottomNavState = 0

            // 백스택을 완전히 비우고 홈으로 이동
            navController.navigate(Screen.Home.title) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    Scaffold(
//        topBar = {
//            TopBar(true)
//        },
        bottomBar = {
            BottomBar(
                navController = navController,
                bottomNavState = bottomNavState,
                onNavItemClick = { index ->
                    // 이전 상태와 현재 선택한 상태가 같지 않을 때만 처리
                    if (bottomNavState != index) {
                        bottomNavState = index
                        val destination = when (index) {
                            0 -> Screen.Home.title
                            1 -> Screen.Calender.title
                            2 -> Screen.LectureRoom.title
                            3 -> Screen.Profile.title
                            else -> Screen.Home.title
                        }

                        // 이동 시 백스택을 완전히 비우고 새 화면으로 교체
                        navController.navigate(destination) {
                            // 백스택 완전히 비우기 - 그래프 ID를 사용
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                onSearchClick = { mainActivity.startSearchActivity() }
            )
        },
        // 하단 시스템 바를 고려하도록 contentWindowInsets 설정
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.title,
                modifier = Modifier.weight(1f)
            ) {
                // 기존 코드와 동일한 부분 유지
                composable(Screen.Home.title) { HomeScreen(homeViewModel, planViewModel, navController) }
                composable(Screen.Calender.title) { CalenderScreen(homeViewModel, dailyScheduleViewModel) }
                composable(Screen.LectureRoom.title) {
                    LectureRoomScreen(
                        lectureRoomViewModel = lectureRoomViewModel,
                        onPlanClick = { plan ->
                            navController.navigate("${Screen.PlanDetail.title}/${plan.planId}")
                        }
                    )
                }
                composable(
                    route = "${Screen.PlanDetail.title}/{planId}",
                    arguments = listOf(navArgument("planId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                    val planDetailResponse = GetPlanDetailResponse()
                    PlanDetailScreen(
                        planId = planId,
                        lectureRoomViewModel = lectureRoomViewModel,
                        navController = navController
                    )
                }
                composable(Screen.Profile.title) { ProfileScreen(loginViewModel = loginViewModel, myPageViewModel = myPageViewModel) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(hasUnreadNotifications: Boolean) {
    Column {
        TopAppBar(
            title = {},
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .height(80.dp),
            navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 앱 로고
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(50.dp),
                        tint = Color.Unspecified
                    )

                    // 간격 추가
                    Spacer(modifier = Modifier.width(5.dp))

                    // 앱 이름
                    Icon(
                        painter = painterResource(id = R.drawable.landr_title_iv),
                        contentDescription = "앱 이름",
                        modifier = Modifier.size(70.dp),
                        tint = Color.Unspecified
                    )
                }
            },
            actions = {
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = { /* 알람 클릭 */ }) {
                        Icon(
                            painter = painterResource(R.drawable.home_screen_notification_iv),
                            contentDescription = "alarm icon",
                        )
                    }

                    // 읽지 않은 알람이 있을 경우 빨간색 배지 표시
                    if (hasUnreadNotifications) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-12).dp, y = (10).dp) // 위치 조정
                                .background(Color.Red, shape = CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
        )
        Divider(color = LightGray2, thickness = 1.dp)
    }
}

@Composable
fun BottomBar(
    navController: NavController,
    bottomNavState: Int,
    onNavItemClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val items: List<Screen> = listOf(
        Screen.Home,
        Screen.Calender,
        Screen.LectureRoom,
        Screen.Profile,
    )
    val context = LocalContext.current

    // 시스템 하단 네비게이션 높이 가져오기
    val navigationBarsHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Divider(color = LightGray2, thickness = 1.dp)
    Box(
        Modifier
            .fillMaxWidth()
            // 시스템 네비게이션 바 높이만큼 패딩 추가
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEachIndexed { index, item ->
                if (index == items.size / 2) {
                    Box(Modifier.weight(1f)) // 중앙 간격을 확보하기 위해 빈 Box 추가
                }

                NavigationBarItem(
                    selected = bottomNavState == index,
                    onClick = {
                        onNavItemClick(index)
                        //navController.navigate(item.title) // 클릭 시 해당 화면으로 이동
                    },
                    icon = {
                        when (val icon = if (bottomNavState == index) item.selectedIcon else item.unselectedIcon) {
                            is ImageVector -> Icon(imageVector = icon, contentDescription = item.title)
                            is Int -> Image(painter = painterResource(icon), contentDescription = item.title)
                            else -> {} // 예외 처리
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MainPurple,
                        selectedTextColor = MainPurple,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }

        FloatingActionButton(
            onClick = {
//                val intent = Intent(context, SearchActivity::class.java)
//                context.startActivity(intent)

                onSearchClick()
            },
            containerColor = MainPurple,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
                .offset(y = -20.dp), // FAB이 NavigationBar 위로 떠 있도록 설정
            shape = RoundedCornerShape(50)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "search",
                tint = Color.White
            )
        }
    }
}
