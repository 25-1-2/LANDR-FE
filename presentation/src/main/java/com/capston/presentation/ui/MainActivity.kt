package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val homeViewModel: HomeViewModel by viewModels()
            val planViewModel: PlanViewModel by viewModels()
            LaunchedEffect(Unit) {
                homeViewModel.getDistinctHome()
            }
            CapstonTheme {
                SettingTopBottomBar(homeViewModel, planViewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingTopBottomBar(homeViewModel: HomeViewModel, planViewModel: PlanViewModel) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar(true) },
        bottomBar = { BottomBar(navController, bottomNavState, { index -> bottomNavState = index }) }
    ) { contentPadding -> // topBar와 bottomBar를 고려한 padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding) // topBar, bottomBar 높이만큼 padding 적용
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.title,
                modifier = Modifier.weight(1f) // NavHost가 남은 영역을 모두 차지하도록 설정
            ) {
                composable(Screen.Home.title) { HomeScreen(homeViewModel, planViewModel) }
                composable(Screen.Calender.title) { CalenderScreen(homeViewModel) }
                composable(Screen.Search.title) { SearchScreen() }
                composable(Screen.LectureList.title) { LectureRoomScreen() }
                composable(Screen.Profile.title) { ProfileScreen() }
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
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "alarm icon",
                            Modifier.size(30.dp)
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
        Screen.LectureList,
        Screen.Profile,
    )

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
                navController.navigate(Screen.Search.title)
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

