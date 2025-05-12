package com.capston.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.core.app.ActivityCompat
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
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.LoginViewModel
import com.capston.presentation.viewmodel.MyPageViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val homeViewModel: HomeViewModel by viewModels()
    val planViewModel: PlanViewModel by viewModels()
    val dailyScheduleViewModel: DailyScheduleViewModel by viewModels()

    @Inject
    lateinit var loadingStateManager: LoadingStateManager

    // Declare the launcher at the top of your Activity/Fragment:
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

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 알림 권한 묻기
        askNotificationPermission()

        // UI 설정
        setContent {
            val homeViewModel: HomeViewModel by viewModels()
            val planViewModel: PlanViewModel by viewModels()
            val dailyScheduleViewModel: DailyScheduleViewModel by viewModels()
            val loginViewModel: LoginViewModel by viewModels()
            val myPageViewModel: MyPageViewModel by viewModels()

            LaunchedEffect(Unit) {
                homeViewModel.getDistinctHome()
                homeViewModel.getDistinctHome.collectLatest { homeData ->
                    if (homeData.dday.ddayId > 0) {
                        homeViewModel.getDDay(homeData.dday.ddayId)
                    }
                }
                loginViewModel.getUserProfile()
            }

            CapstonTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    SettingTopBottomBar(homeViewModel, planViewModel, dailyScheduleViewModel, loginViewModel, myPageViewModel)

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
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기", tint = Color.Gray)
            }

            BasicTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.DarkGray),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "계획 생성하고 싶은 강의를 선택하세요",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    innerTextField()
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

            IconButton(onClick = onSearchClick, modifier = Modifier.size(24.dp)) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "검색", tint = Color.Gray)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingTopBottomBar(homeViewModel: HomeViewModel, planViewModel: PlanViewModel, dailyScheduleViewModel: DailyScheduleViewModel, loginViewModel: LoginViewModel, myPageViewModel: MyPageViewModel) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(true)
        },
        bottomBar = {
            BottomBar(navController, bottomNavState, { index -> bottomNavState = index})
        }
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
                composable(Screen.Home.title) { HomeScreen(homeViewModel, planViewModel) }
                composable(Screen.Calender.title) { CalenderScreen(homeViewModel, dailyScheduleViewModel) }
                composable(Screen.LectureRoom.title) {
                    LectureRoomScreen(
                        viewModel = planViewModel,
                        onPlanClick = { plan ->
                            // plan.planId를 경로 파라미터로 사용하여 상세 화면으로 이동
                            navController.navigate("${Screen.PlanDetail.title}/${plan.planId}")
                        }
                    )
                }
                composable(
                    route = "${Screen.PlanDetail.title}/{planId}",
                    arguments = listOf(navArgument("planId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val planId = backStackEntry.arguments?.getInt("planId") ?: 0
                    // 실제 앱에서는 강의 ID 또는 전체 Lecture 객체를 전달할 수 있도록 개선 필요
                    val planDetailResponse = GetPlanDetailResponse()
                    PlanDetailScreen(
                        planId = planId,
                        planViewModel = planViewModel,
                        homeViewModel = homeViewModel
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
                IconButton(onClick = { /* 메뉴 클릭 */ }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu icon",
                        Modifier.size(30.dp)
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
    onNavItemClick: (Int) -> Unit
) {
    val items: List<Screen> = listOf(
        Screen.Home,
        Screen.Calender,
        Screen.LectureRoom,
        Screen.Profile,
    )
    val context = LocalContext.current

    Divider(color = LightGray2, thickness = 1.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp) // BottomNavigationBar 높이 설정
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
                        navController.navigate(item.title) // 클릭 시 해당 화면으로 이동
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
                val intent = Intent(context, SearchActivity::class.java)
                context.startActivity(intent)
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
