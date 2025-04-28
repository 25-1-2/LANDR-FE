package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
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
import com.capston.domain.response.lecture.LectureResponseDto
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
    val searchLectureResponse by lectureViewModel.distinctLecture.collectAsState()
    val allLectureResponse by lectureViewModel.allLectureList.collectAsState()

    // 검색 중인지 여부를 추적
    var isSearching by remember { mutableStateOf(false) }

    // 페이지네이션 상태
    var cursorLectureId by remember { mutableStateOf("") }
    var cursorCreatedAt by remember { mutableStateOf("") }
    var hasMoreData by remember { mutableStateOf(true) }

    // 모든 아이템을 누적해서 저장
    var allItems by remember { mutableStateOf<List<LectureItemDto>>(emptyList()) }

    // 컴포넌트가 처음 로드될 때 전체 강의 조회
    LaunchedEffect(Unit) {
        Log.d("SearchScreen", "초기 전체 강의 조회")
        lectureViewModel.getAllLecture()
    }

    // 검색어 변경 시 API 호출
    var searchJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = scope.launch {
            // 검색 상태 초기화
            cursorLectureId = ""
            cursorCreatedAt = ""
            hasMoreData = true
            allItems = emptyList()

            delay(50) // 타이핑하는 동안 여러 번 API 호출하지 않도록 지연

            // 검색어가 비어있지 않은 경우만 검색 수행
            if (searchQuery.isNotBlank()) {
                Log.d("SearchScreen", "검색어 변경: $searchQuery")
                isSearching = true
                lectureViewModel.getDistinctLecture(searchQuery)
            } else {
                isSearching = false
            }
        }
    }

    // 검색 결과 처리
    LaunchedEffect(searchLectureResponse) {
        if (isSearching) {
            val newItems = searchLectureResponse.data?.map { lecture ->
                LectureItemDto(
                    title = lecture.title,
                    platform = lecture.platform.label,
                    teacher = "${lecture.teacher} · [과목] ${lecture.subject}",
                    imageResId = getImageForSubject(lecture.subject)
                )
            } ?: emptyList()

            // 첫 페이지인 경우 교체, 아닌 경우 추가
            if (cursorLectureId.isEmpty()) {
                allItems = newItems
            } else {
                allItems = allItems + newItems
            }

            // 페이지네이션 정보 업데이트
            hasMoreData = searchLectureResponse.hasNext
            if (searchLectureResponse.nextCursor > 0) {
                cursorLectureId = searchLectureResponse.nextCursor.toString()
            }
            cursorCreatedAt = searchLectureResponse.nextCreateAt

            Log.d("SearchScreen", "검색 결과 처리: ${newItems.size}개 항목, 누적 ${allItems.size}개, 다음 페이지 ${hasMoreData}")
        }
    }

    // 전체 목록 처리
    LaunchedEffect(allLectureResponse) {
        if (!isSearching && allItems.isEmpty() && allLectureResponse.isNotEmpty()) {
            allItems = allLectureResponse.map { lecture ->
                LectureItemDto(
                    title = lecture.title,
                    platform = lecture.platform.label,
                    teacher = "${lecture.teacher} · [과목] ${lecture.subject}",
                    imageResId = getImageForSubject(lecture.subject)
                )
            }
            Log.d("SearchScreen", "전체 목록 로드: ${allItems.size}개 항목")
        }
    }

    Column {
        SearchTopBar(searchQuery = searchQuery, onQueryChanged = { searchQuery = it })

        // 검색 결과 표시
        InfiniteScrollList(
            navController = navController,
            lectureItems = allItems,
            searchQuery = searchQuery,
            hasMoreData = hasMoreData,
            onLoadMore = {
                if (hasMoreData && isSearching) {
                    scope.launch {
                        Log.d("SearchScreen", "추가 데이터 로드: cursor=${cursorLectureId}")
                        lectureViewModel.getDistinctLecture(searchQuery)
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

    // 스크롤 위치 감지 및 추가 데이터 로드
    LaunchedEffect(listState.layoutInfo.visibleItemsInfo) {
        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
        if (lastVisibleItem != null) {
            val lastIndex = lastVisibleItem.index
            val total = lectureItems.size

            // 마지막 3개 아이템에 도달하면 추가 데이터 로드
            if (hasMoreData && !loading && total > 0 && lastIndex >= total - 3) {
                loading = true
                onLoadMore()
                delay(1000) // 연속 호출 방지
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
            // 필터링된 항목이 없을 경우 표시
            if (lectureItems.isEmpty()) {
                item {
                    Text(
                        text = if (searchQuery.isBlank()) "강의 목록을 불러오는 중..." else "검색 결과가 없습니다.",
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

                // 로딩 인디케이터
                if (hasMoreData) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MainPurple,
                                strokeWidth = 2.dp
                            )
                        }
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