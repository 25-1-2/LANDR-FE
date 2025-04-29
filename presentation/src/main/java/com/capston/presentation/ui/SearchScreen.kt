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
import androidx.compose.runtime.saveable.rememberSaveable
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

    var shouldReloadData by rememberSaveable { mutableStateOf(true) }

    // 명시적인 로딩 상태 추적
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) } // 추가 데이터 로딩 상태

    // 검색 중인지 여부를 추적
    var isSearching by remember { mutableStateOf(false) }

    // 페이지네이션 상태
    var cursorLectureId by remember { mutableStateOf("") }
    var cursorCreatedAt by remember { mutableStateOf("") }
    var hasMoreData by remember { mutableStateOf(true) }
    var offset by remember { mutableStateOf("10") } // 페이지 사이즈를 10으로 설정

    // 로드된 페이지 수 추적 (디버깅용)
    var pageCount by remember { mutableStateOf(0) }

    // 모든 아이템을 누적해서 저장
    var allItems by remember { mutableStateOf<List<LectureItemDto>>(emptyList()) }

    // 컴포넌트가 처음 로드될 때 전체 강의 조회
    LaunchedEffect(shouldReloadData) {
        if (shouldReloadData) {
            Log.d("SearchScreen", "전체 강의 조회 (재로딩) 시작")
            isLoading = true
            allItems = emptyList() // 기존 항목 초기화
            cursorLectureId = "" // 커서 초기화
            cursorCreatedAt = "" // 커서 초기화
            pageCount = 0 // 페이지 카운트 초기화
            lectureViewModel.getAllLecture(LectureDto(offset = offset))
            shouldReloadData = false
        }
    }

    // 화면에 다시 돌아왔을 때 재로딩 설정
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            Log.d("SearchScreen", "네비게이션 감지: ${destination.route}")
            if (destination.route == "search") {
                shouldReloadData = true
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
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
            pageCount = 0
            allItems = emptyList()

            delay(50) // 타이핑하는 동안 여러 번 API 호출하지 않도록 지연

            // 검색어가 비어있지 않은 경우만 검색 수행
            if (searchQuery.isNotBlank()) {
                Log.d("SearchScreen", "검색어 변경: $searchQuery")
                isSearching = true
                isLoading = true
                // 검색 API 호출 시 파라미터 전달
                lectureViewModel.getDistinctLecture(
                    LectureDto(
                        search = searchQuery,
                        cursorLectureId = "",  // 첫 검색은 항상 커서 없이
                        cursorCreatedAt = "",
                        offset = offset
                    )
                )
            } else {
                isSearching = false
                if (allLectureResponse.isNotEmpty()) {
                    // 이미 로드된 전체 목록이 있으면 바로 처리
                    allItems = allLectureResponse.map { lecture ->
                        LectureItemDto(
                            id = lecture.id,
                            title = lecture.title ?: "",
                            platform = lecture.platform?.label ?: "",
                            teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject ?: ""}",
                            imageResId = getImageForSubject(lecture.subject ?: ""),
                            createdAt = lecture.createdAt ?: ""
                        )
                    }
                    isLoading = false
                    Log.d("SearchScreen", "캐시된 전체 목록 처리: ${allItems.size}개 항목")
                } else {
                    // 없으면 새로 로드
                    isLoading = true
                    lectureViewModel.getAllLecture(LectureDto(offset = offset))
                }
            }
        }
    }

    // 검색 결과 처리
    LaunchedEffect(searchLectureResponse) {
        if (isSearching) {
            Log.d("SearchScreen", "검색 응답 처리 시작: nextCursor=${searchLectureResponse.nextCursor}, hasNext=${searchLectureResponse.hasNext}")

            val newItems = searchLectureResponse.data?.filterNotNull()?.map { lecture ->
                LectureItemDto(
                    id = lecture.id,
                    title = lecture.title ?: "",
                    platform = lecture.platform?.label ?: "",
                    teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject ?: ""}",
                    imageResId = getImageForSubject(lecture.subject ?: ""),
                    createdAt = lecture.createdAt ?: ""
                )
            } ?: emptyList()

            // 받은 데이터가 있으면 페이지 카운트 증가
            if (newItems.isNotEmpty()) {
                pageCount++
                Log.d("SearchScreen", "페이지 로드 성공: $pageCount 페이지, ${newItems.size}개 항목")
            }

            // 첫 페이지인 경우 교체, 아닌 경우 추가
            if (cursorLectureId.isEmpty() || isLoading) {
                allItems = newItems
            } else if (isLoadingMore) {
                // 중복 방지를 위해 ID 기반으로 필터링
                val existingIds = allItems.map { it.id }.toSet()
                val uniqueNewItems = newItems.filter { it.id !in existingIds }
                allItems = allItems + uniqueNewItems

                // 로그 추가
                Log.d("SearchScreen", "기존 아이템: ${allItems.size - uniqueNewItems.size}, 새 아이템: ${uniqueNewItems.size}")
            }

            // 페이지네이션 정보 업데이트 - API의 hasNext 값을 직접 사용
            hasMoreData = searchLectureResponse.hasNext

            // API로부터 온 커서 정보 사용 (null 안전하게)
            if (searchLectureResponse.nextCursor > 0) {
                cursorLectureId = searchLectureResponse.nextCursor.toString()
                Log.d("SearchScreen", "다음 커서 업데이트: $cursorLectureId")
            }

            // API에서 제공한 생성일자 사용
            val nextCreatedAt = searchLectureResponse.nextCreatedAt
            if (nextCreatedAt != null && nextCreatedAt.isNotEmpty()) {
                cursorCreatedAt = nextCreatedAt
                Log.d("SearchScreen", "다음 생성일자 업데이트: $cursorCreatedAt")
            }

            isLoading = false
            isLoadingMore = false

            Log.d("SearchScreen", "검색 결과 처리 완료: ${newItems.size}개 항목, 누적 ${allItems.size}개, 다음 페이지 있음: $hasMoreData")
        }
    }

    // 전체 목록 처리
    LaunchedEffect(allLectureResponse) {
        Log.d("SearchScreen", "전체 목록 응답 변경 감지: ${allLectureResponse.size}개 항목, 검색 중: $isSearching")
        if (!isSearching && allLectureResponse.isNotEmpty()) {
            // 전체 목록 처리 로직
            val newItems = allLectureResponse.map { lecture ->
                LectureItemDto(
                    id = lecture.id,
                    title = lecture.title ?: "",
                    platform = lecture.platform?.label ?: "",
                    teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject ?: ""}",
                    imageResId = getImageForSubject(lecture.subject ?: ""),
                    createdAt = lecture.createdAt ?: ""
                )
            }

            // 받은 데이터가 있으면 페이지 카운트 증가
            if (newItems.isNotEmpty()) {
                pageCount++
                Log.d("SearchScreen", "전체 목록 페이지 로드 성공: $pageCount 페이지, ${newItems.size}개 항목")
            }

            // 첫 요청이면 목록 교체, 아니면 추가
            if (cursorLectureId.isEmpty() || isLoading) {
                allItems = newItems
            } else if (isLoadingMore) {
                // 중복 방지를 위해 ID 기반으로 필터링
                val existingIds = allItems.map { it.id }.toSet()
                val uniqueNewItems = newItems.filter { it.id !in existingIds }
                allItems = allItems + uniqueNewItems

                // 로그 추가
                Log.d("SearchScreen", "전체 목록 - 기존 아이템: ${allItems.size - uniqueNewItems.size}, 새 아이템: ${uniqueNewItems.size}")
            }

            // 전체 목록은 항상 더 많은 데이터가 있다고 가정
            // API에서 명시적인 hasNext가 없지만, 빈 응답이 오지 않는 한 계속 로드
            hasMoreData = newItems.isNotEmpty()

            if (newItems.isEmpty()) {
                hasMoreData = false
                Log.d("SearchScreen", "더 이상 데이터가 없음: 빈 응답")
            } else {
                Log.d("SearchScreen", "더 데이터 있음: 항목 ${newItems.size}개 받음")
            }

            // 마지막 아이템의 ID와 생성일자를 커서로 사용
            if (newItems.isNotEmpty()) {
                val lastItem = newItems.last()
                cursorLectureId = lastItem.id.toString()
                cursorCreatedAt = lastItem.createdAt ?: ""
                Log.d("SearchScreen", "다음 페이지 커서 설정: id=${cursorLectureId}, createdAt=${cursorCreatedAt}")
            }

            isLoading = false
            isLoadingMore = false

            Log.d("SearchScreen", "전체 목록 처리 완료: ${allItems.size}개 항목, 다음 페이지 있음: $hasMoreData")
        }
    }

    Column {
        SearchTopBar(searchQuery = searchQuery, onQueryChanged = { searchQuery = it }, lectureViewModel = lectureViewModel)

        // 검색 결과 표시
        InfiniteScrollList(
            navController = navController,
            lectureItems = allItems,
            searchQuery = searchQuery,
            hasMoreData = hasMoreData,
            isLoading = isLoading || isLoadingMore,
            onLoadMore = {
                if (hasMoreData && !isLoading && !isLoadingMore) {
                    scope.launch {
                        isLoadingMore = true
                        Log.d("SearchScreen", "추가 데이터 로드 요청: cursor=${cursorLectureId}, createdAt=${cursorCreatedAt}, 페이지=${pageCount+1}")

                        if (isSearching) {
                            // 검색 모드에서 다음 페이지 로드
                            lectureViewModel.getDistinctLecture(
                                LectureDto(
                                    search = searchQuery,
                                    cursorLectureId = cursorLectureId,
                                    cursorCreatedAt = cursorCreatedAt,
                                    offset = offset
                                )
                            )
                        } else {
                            // 전체 목록 모드에서 다음 페이지 로드
                            lectureViewModel.getAllLecture(
                                LectureDto(
                                    cursorLectureId = cursorLectureId,
                                    cursorCreatedAt = cursorCreatedAt,
                                    offset = offset
                                )
                            )
                        }
                    }
                } else {
                    Log.d("SearchScreen", "추가 데이터 로드 무시: hasMore=$hasMoreData, isLoading=$isLoading, isLoadingMore=$isLoadingMore")
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
    isLoading: Boolean,
    onLoadMore: () -> Unit
) {
    var loading by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 스크롤 위치 감지를 더 효율적으로 개선
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            val lastVisibleItemIndex = visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = lectureItems.size

            // 마지막 아이템에서 5개 이내로 접근하면 더 로드
            lastVisibleItemIndex >= totalItemsCount - 5
        }.collect { shouldLoadMore ->
            // 더 로드해야 하고, 더 로드할 데이터가 있고, 현재 로딩 중이 아닐 때만 로드
            if (shouldLoadMore && hasMoreData && !isLoading && !loading && lectureItems.isNotEmpty()) {
                Log.d("InfiniteScrollList", "스크롤 감지: 더 많은 데이터 로드 요청")
                loading = true
                onLoadMore()
                delay(500) // 연속 호출 방지
                loading = false
            }
        }
    }

    // 초기 데이터 로드 후 자동으로 더 로드
    LaunchedEffect(lectureItems.size, hasMoreData, isLoading) {
        // 첫 페이지 데이터가 로드된 후 && 더 데이터가 있고 && 로딩 중이 아님
        if (lectureItems.isNotEmpty() && hasMoreData && !isLoading && !loading) {
            Log.d("InfiniteScrollList", "자동 더 로드 트리거: 항목 ${lectureItems.size}개, 더 로드할 데이터 있음")
            delay(300) // 첫 데이터 표시 후 잠시 대기
            loading = true
            onLoadMore()
            delay(500)
            loading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(25.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (lectureItems.isEmpty()) {
                item {
                    Text(
                        text = if (isLoading) "강의 목록을 불러오는 중..."
                        else if (searchQuery.isBlank()) "강의 목록이 비어 있습니다."
                        else "검색 결과가 없습니다.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(
                    items = lectureItems,
                    key = { it.id } // 고유 ID로 키 사용
                ) { item ->
                    SearchLectureItem(
                        lectureItem = item,
                        searchQuery = searchQuery,
                        onClick = {
                            navController.navigate("${Screen.Plan.title}/${item.title}")
                        }
                    )
                }

                // 로딩 인디케이터 또는 끝 메시지
                item {
                    if (hasMoreData || isLoading) {
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
                    } else {
                        Text(
                            text = if (lectureItems.size > 40)
                                "모든 강의를 불러왔습니다 (총 ${lectureItems.size}개)"
                            else "모든 강의를 불러왔습니다",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            textAlign = TextAlign.Center
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
    // null 안전하게 처리
    val title = lectureItem.title ?: ""
    val query = searchQuery ?: ""

    // 검색어가 포함된 부분을 하이라이트하는 함수
    val annotatedString = buildAnnotatedString {
        var startIndex = 0

        if (query.isNotEmpty()) {
            var searchPos = title.indexOf(query, ignoreCase = true)
            while (searchPos != -1) {
                append(title.substring(startIndex, searchPos))
                appendAnnotatedString(title.substring(searchPos, searchPos + query.length), MainPurple)
                startIndex = searchPos + query.length
                searchPos = title.indexOf(query, startIndex, ignoreCase = true)
            }
            append(title.substring(startIndex))
        } else {
            append(title)
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
                    text = lectureItem.platform ?: "",
                    color = MainPurple,
                    fontSize = 14.sp
                )

                Text(
                    text = annotatedString,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = lectureItem.teacher ?: "",
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
    onQueryChanged: (String) -> Unit,
    lectureViewModel: LectureViewModel
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
                onSearchClick = { lectureViewModel.getDistinctLecture(searchQuery) }
            )
        }
    )
}

private fun getImageForSubject(subject: String): Int {
    return when (subject) {
        "MATH" -> R.drawable.screen_search_math_iv
        "SCI" -> R.drawable.screen_search_science_iv
        "ENG" -> R.drawable.screen_search_english_iv
        "KOR" -> R.drawable.screen_search_korean_iv
        else -> R.drawable.screen_search_korean_iv // 기본 이미지
    }
}

private fun AnnotatedString.Builder.appendAnnotatedString(text: String, color: Color) {
    withStyle(style = SpanStyle(color = color)) {
        append(text)
    }
}