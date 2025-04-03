package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.LightGray4
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.DailyScheduleViewModel
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

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
            val dailyScheduleViewModel: DailyScheduleViewModel by viewModels(

            )
            LaunchedEffect(Unit) {
                homeViewModel.getDistinctHome()
            }
            CapstonTheme {
                SettingTopBottomBar(homeViewModel, planViewModel, dailyScheduleViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(navController: NavController, searchQuery: String, onQueryChanged: (String) -> Unit) {
    TopAppBar(
        title = { SearchField(searchQuery, onQueryChanged) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
            }
        },
        actions = {
            IconButton(onClick = { /* 검색 버튼 동작 추가 가능 */ }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "검색")
            }
        }
    )
}

@Composable
fun InfiniteScrollList(filteredItems: List<LectureItemDto>, searchQuery: String) {
    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 필터링된 항목이 없을 경우 표시하지 않음
            if (filteredItems.isEmpty()) {
                item {
                    Text(
                        text = "검색 결과가 없습니다.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(filteredItems) { item ->
                    SearchLectureItem(lectureItem = item, searchQuery = searchQuery)
                }
            }

            // 로딩 상태 표시
            if (!loading && filteredItems.isNotEmpty()) {
                item {
                    LaunchedEffect(Unit) {
                        loading = true
                        delay(1500)
                        loading = false
                    }

                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            color = MainPurple,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(searchQuery: String, onQueryChanged: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(0.5.dp, Color.LightGray), shape = RoundedCornerShape(20.dp))
            .background(LightGray4_40, shape = RoundedCornerShape(20.dp)) // Box에 배경 색상 적용
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp, color = Color.DarkGray),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { /* 검색 동작 가능 */ }),
                modifier = Modifier.weight(1f)
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }, modifier = Modifier.size(20.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingTopBottomBar(homeViewModel: HomeViewModel, planViewModel: PlanViewModel, dailyScheduleViewModel: DailyScheduleViewModel) {
    var bottomNavState by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val navController = rememberNavController()

    val currentDestination = navController.currentBackStackEntryFlow.collectAsState(initial = null).value?.destination?.route

    Scaffold(
        topBar = {
            when (currentDestination) {
                Screen.Search.title -> SearchTopBar(navController, searchQuery, { searchQuery = it })
                else -> TopBar(true)
            }
        },
        bottomBar = {
            if (currentDestination != Screen.Search.title) {
                BottomBar(navController, bottomNavState, { index -> bottomNavState = index })
            }
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
                composable(Screen.Search.title) { SearchScreen(searchQuery) }
                composable(Screen.LectureList.title) { LectureRoomScreen(onLectureClick = {}) }
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

