package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.capston.domain.model.Lecture
import com.capston.domain.request.LectureDto
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.LectureViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@Composable
fun SearchScreen(
    navController: NavController,
    lectureViewModel: LectureViewModel,
    initialQuery: String = "",
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val scope = rememberCoroutineScope()
    val allLecturesResponse by lectureViewModel.allLectureList.collectAsState()

    // Pagination state
    var cursorLectureId by remember { mutableStateOf("") }
    var cursorCreatedAt by remember { mutableStateOf("") }
    var hasMoreData by remember { mutableStateOf(true) }
    var isInitialLoad by remember { mutableStateOf(true) }

    // Keep track of lecture items
    var allItems by remember { mutableStateOf<List<LectureItemDto>>(emptyList()) }

    // Call API when search query changes (with debounce)
    var searchJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = scope.launch {
            // Reset pagination for new search
            cursorLectureId = ""
            cursorCreatedAt = ""
            allItems = emptyList()
            isInitialLoad = true
            hasMoreData = true

            delay(500) // Debounce for 500ms
            lectureViewModel.getAllLecture(
                LectureDto(
                    search = searchQuery,
                    cursorLectureId = cursorLectureId,
                    cursorCreatedAt = cursorCreatedAt,
                    offset = "10" // Load 10 items per page
                )
            )
        }
    }

    // Process API response
    LaunchedEffect(allLecturesResponse) {
        // Map API response to UI model
        val newItems = allLecturesResponse.map { lecture ->
            LectureItemDto(
                title = lecture.title,
                platform = lecture.platform.label,
                teacher = lecture.teacher,
                imageResId = getImageForSubject(lecture.subject)
            )
        }

        if (isInitialLoad) {
            // First load replaces all data
            allItems = newItems
            isInitialLoad = false
        } else {
            // Subsequent loads append data
            allItems = allItems + newItems
        }
    }

    Column {
        SearchTopBar(searchQuery = searchQuery, onQueryChanged = { searchQuery = it })

        InfiniteScrollList(
            navController = navController,
            lectureItems = allItems,
            searchQuery = searchQuery,
            hasMoreData = hasMoreData,
            onLoadMore = {
                if (hasMoreData) {
                    scope.launch {
                        lectureViewModel.getAllLecture(
                            LectureDto(
                                search = searchQuery,
                                cursorLectureId = cursorLectureId,
                                cursorCreatedAt = cursorCreatedAt,
                                offset = "10" // Load 10 items per page
                            )
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun InfiniteScrollList(
    navController: NavController,
    lectureItems: List<LectureItemDto>,
    searchQuery: String,
    hasMoreData: Boolean,
    onLoadMore: () -> Unit
) {
    var loading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Detect when we're near the end of the list to load more
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
        if (lastVisibleItem != null) {
            val lastIndex = lastVisibleItem.index
            val total = lectureItems.size
            // Load more when we reach the last 3 items
            if (hasMoreData && !loading && lastIndex >= total - 3) {
                loading = true
                onLoadMore()
                delay(1000) // Prevent multiple load requests
                loading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(25.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 필터링된 항목이 없을 경우 표시하지 않음
            if (lectureItems.isEmpty()) {
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
                items(lectureItems) { item ->
                    SearchLectureItem(
                        lectureItem = item,
                        searchQuery = searchQuery,
                        onClick = {
                            navController.navigate("${Screen.Plan.title}/${item.title}")
                        }
                    )
                }

                // Show loading indicator at the bottom when loading more
                if (hasMoreData) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentSize(Alignment.Center),
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
fun SearchNavHost(navController: NavHostController, planViewModel: PlanViewModel, lectureViewModel: LectureViewModel) {
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(
                navController = navController,
                lectureViewModel = lectureViewModel
            )
        }
        composable(
            route = "plan/{lectureTitle}",
            arguments = listOf(navArgument("lectureTitle") { type = NavType.StringType })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("lectureTitle") ?: ""
            PlanScreen(Lecture(), planViewModel)
        }
    }
}

@Composable
fun SearchLectureItem(lectureItem: LectureItemDto, searchQuery: String, onClick: () -> Unit) {
    // 검색어가 포함된 부분을 하이라이트하는 함수
    val annotatedString = buildAnnotatedString {
        var startIndex = 0

        if (searchQuery.isNotEmpty()) {
            var searchPos = lectureItem.title.indexOf(searchQuery, ignoreCase = true)
            while (searchPos != -1) {
                append(lectureItem.title.substring(startIndex, searchPos))
                appendAnnotatedString(lectureItem.title.substring(searchPos, searchPos + searchQuery.length), MainPurple)
                startIndex = searchPos + searchQuery.length
                searchPos = lectureItem.title.indexOf(searchQuery, startIndex, ignoreCase = true)
            }
            append(lectureItem.title.substring(startIndex))
        } else {
            append(lectureItem.title)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable{ onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lectureItem.platform,
                    color = MainPurple,
                    fontSize = 14.sp
                )

                Text(
                    text = annotatedString,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = lectureItem.teacher,
                    color = LightGray40,
                    fontSize = 14.sp
                )
            }

            // 과목에 맞는 이미지 출력
            Image(
                painter = painterResource(lectureItem.imageResId),
                contentDescription = "과목명",
                modifier = Modifier
                    .padding(start = 8.dp) // 텍스트와 살짝 간격 주기
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit
) {
    val context = LocalContext.current

    TopAppBar(
        title = {
            SearchFieldWithIcons(
                searchQuery = searchQuery,
                onQueryChanged = onQueryChanged,
                onBackClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                onSearchClick = { /* TODO: 검색 동작 */ }
            )
        }
    )
}

private fun getImageForSubject(subject: String): Int {
    return when (subject) {
        "수학" -> R.drawable.screen_search_math_iv
        "과학" -> R.drawable.screen_search_science_iv
        "영어" -> R.drawable.screen_search_english_iv
        "국어" -> R.drawable.screen_search_korean_iv
        else -> R.drawable.screen_search_korean_iv // 기본 이미지
    }
}

private fun AnnotatedString.Builder.appendAnnotatedString(text: String, color: Color) {
    withStyle(style = SpanStyle(color = color)) {
        append(text)
    }
}