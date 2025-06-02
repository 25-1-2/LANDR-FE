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
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.home.CalenderScreen
import com.capston.presentation.ui.home.HomeScreen
import com.capston.presentation.ui.home.LectureRoomScreen
import com.capston.presentation.ui.home.NotificationScreen
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
                Box(modifier = Modifier.fillMaxWidth()) {
                    MainBottomBar(
                        homeViewModel = homeViewModel,
                        planViewModel = planViewModel,
                        dailyScheduleViewModel = dailyScheduleViewModel,
                        lectureRoomViewModel = lectureRoomViewModel,
                        loginViewModel = loginViewModel,
                        myPageViewModel = myPageViewModel,
                        loadingStateManager = loadingStateManager
                    )

                    // 전역 로딩 인디케이터
                    LoadingIndicator(loadingStateManager)
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
    loginViewModel: LoginViewModel,
    myPageViewModel: MyPageViewModel,
    loadingStateManager: LoadingStateManager
) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()
    val mainActivity = LocalActivity.current as MainActivity
    val context = LocalContext.current

    // BackHandler
    BackHandler {
        if (bottomNavState == 0) {
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
                    .fillMaxWidth()
            ) {
                // 기존 composable들...
                composable(Screen.Home.title) { HomeScreen(homeViewModel, planViewModel, navController) }
                composable(Screen.Calender.title) { CalenderScreen(homeViewModel, dailyScheduleViewModel) }
                composable(Screen.LectureRoom.title) {
                    LectureRoomScreen(
                        lectureRoomViewModel = lectureRoomViewModel,
                        onPlanClick = { plan ->
                            navController.navigate("${Screen.PlanDetail.title}/${plan.planId}")
                        },
                        onNotificationClick = { navController.navigate("notification") }
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
                        navController = navController,
                    )
                }
                composable(Screen.Profile.title) { ProfileScreen(loginViewModel = loginViewModel, myPageViewModel = myPageViewModel) }
                composable(Screen.Notification.title) { NotificationScreen() }
            }
        }

        FloatingActionButton(
            onClick = { mainActivity.startSearchActivity() },
            containerColor = MainPurple,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp),
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
        Divider(color = LightGray2, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
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
                        .clickable { onNavItemClick(index) },
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

    Column {
        Divider(color = LightGray2, thickness = 1.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 높이를 늘려서 FAB 공간 확보
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Row를 아래쪽에 정렬
                    .height(80.dp), // Row의 높이는 기존과 동일
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEachIndexed { index, item ->
                    if (index == items.size / 2) {
                        Box(Modifier.weight(1f)) // 중앙 간격 확보
                    }

                    NavigationBarItem(
                        selected = bottomNavState == index,
                        onClick = { onNavItemClick(index) },
                        icon = {
                            when (val icon = if (bottomNavState == index) item.selectedIcon else item.unselectedIcon) {
                                is ImageVector -> Icon(imageVector = icon, contentDescription = item.title)
                                is Int -> Image(painter = painterResource(icon), contentDescription = item.title)
                                else -> {}
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
                onClick = { onSearchClick() },
                containerColor = MainPurple,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopCenter) // 상단 중앙에 배치
                    .offset(y = 10.dp), // 약간만 아래로 (기존 -20dp에서 +10dp로)
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
}
