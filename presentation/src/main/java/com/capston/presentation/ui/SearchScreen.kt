package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.capston.domain.response.enum_class.Subject
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

    var offset by remember { mutableStateOf("10") }

    // 모든 아이템을 누적해서 저장
    var allItems by remember { mutableStateOf<List<LectureItemDto>>(emptyList()) }

    // 디버깅용 - 현재 로드된 아이템 수 출력
    LaunchedEffect(allItems.size, isLoadingMore) {
        Log.d("SearchScreen", "현재 표시 중인 아이템 수: ${allItems.size}, 로딩 중: $isLoadingMore")
    }

    // 컴포넌트가 처음 로드될 때 전체 강의 조회
    LaunchedEffect(shouldReloadData) {
        if (shouldReloadData) {
            Log.d("SearchScreen", "전체 강의 조회 (재로딩) 시작")
            isLoading = true
            allItems = emptyList() // 기존 항목 초기화
            cursorLectureId = "" // 커서 초기화
            cursorCreatedAt = "" // 커서 초기화
            hasMoreData = true  // 데이터가 더 있다고 가정

            // 첫 번째 페이지 로드
            lectureViewModel.getAllLecture(LectureDto(
                offset = offset,
                cursorLectureId = "",
                cursorCreatedAt = ""
            ))

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
            allItems = emptyList()

            delay(300) // 타이핑하는 동안 여러 번 API 호출하지 않도록 지연 (디바운싱)

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
                        val subjectEnum = runCatching { Subject.valueOf(lecture.subject.name) }.getOrNull()

                        LectureItemDto(
                            id = lecture.id,
                            title = lecture.title ?: "",
                            platform = lecture.platform,
                            teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject.label}",
                            imageResId = subjectEnum?.getImageRes() ?: R.drawable.screen_search_korean_iv,
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
                val subjectEnum = runCatching { Subject.valueOf(lecture.subject.name) }.getOrNull()

                LectureItemDto(
                    id = lecture.id,
                    title = lecture.title ?: "",
                    platform = lecture.platform,
                    teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject.label}",
                    imageResId = subjectEnum?.getImageRes() ?: R.drawable.screen_search_korean_iv,
                    createdAt = lecture.createdAt ?: ""
                )
            } ?: emptyList()

            // 첫 페이지인 경우 교체, 아닌 경우 추가
            if (cursorLectureId.isEmpty() || isLoading) {
                allItems = newItems
                Log.d("SearchScreen", "첫 페이지 로드: ${newItems.size}개 항목")
            } else if (isLoadingMore) {
                // 중복 방지를 위해 ID 기반으로 필터링
                val existingIds = allItems.map { it.id }.toSet()
                val uniqueNewItems = newItems.filter { it.id !in existingIds }

                // 새 아이템들을 기존 리스트에 추가
                allItems = allItems + uniqueNewItems

                // 로그 추가
                Log.d("SearchScreen", "추가 데이터 로드: 기존 ${allItems.size - uniqueNewItems.size}개, 새로 추가 ${uniqueNewItems.size}개, 총 ${allItems.size}개")
            }

            // 페이지네이션 정보 업데이트 - API의 hasNext 값을 직접 사용
            hasMoreData = searchLectureResponse.hasNext
            Log.d("SearchScreen", "다음 페이지 존재 여부: $hasMoreData")

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
        }
    }

    // 전체 목록 처리
    LaunchedEffect(allLectureResponse) {
        if (!isSearching && allLectureResponse.isNotEmpty()) {
            Log.d("SearchScreen", "전체 목록 응답 변경 감지: ${allLectureResponse.size}개 항목")
            // 전체 목록 처리 로직
            val newItems = allLectureResponse.map { lecture ->
                val subjectEnum = runCatching { Subject.valueOf(lecture.subject.name) }.getOrNull()

                LectureItemDto(
                    id = lecture.id,
                    title = lecture.title ?: "",
                    platform = lecture.platform,
                    teacher = "${lecture.teacher ?: ""} · [과목] ${lecture.subject.label}",
                    imageResId = subjectEnum?.getImageRes() ?: R.drawable.screen_search_korean_iv,
                    createdAt = lecture.createdAt ?: ""
                )
            }

            // 첫 요청이면 목록 교체, 아니면 추가
            if (cursorLectureId.isEmpty() || isLoading) {
                allItems = newItems
                Log.d("SearchScreen", "전체 목록 첫 페이지 로드: ${newItems.size}개 항목")
            } else if (isLoadingMore) {
                // 중복 방지를 위해 ID 기반으로 필터링
                val existingIds = allItems.map { it.id }.toSet()
                val uniqueNewItems = newItems.filter { it.id !in existingIds }

                // 새 아이템들을 기존 리스트에 추가
                allItems = allItems + uniqueNewItems

                // 로그 추가
                Log.d("SearchScreen", "전체 목록 추가 로드: 기존 ${allItems.size - uniqueNewItems.size}개, 새로 추가 ${uniqueNewItems.size}개, 총 ${allItems.size}개")
            }

            // 새로운 아이템이 없으면 더 이상 데이터가 없다고 판단
            hasMoreData = newItems.isNotEmpty()
            Log.d("SearchScreen", "전체 목록 다음 페이지 존재 여부: $hasMoreData")

            // 마지막 아이템의 ID와 생성일자를 커서로 사용
            if (newItems.isNotEmpty()) {
                val lastItem = newItems.last()
                cursorLectureId = lastItem.id.toString()
                cursorCreatedAt = lastItem.createdAt ?: ""
                Log.d("SearchScreen", "전체 목록 다음 커서 설정: id=${cursorLectureId}, createdAt=${cursorCreatedAt}")
            }

            isLoading = false
            isLoadingMore = false
        }
    }

    Column {
        SearchTopBar(searchQuery = searchQuery, onQueryChanged = { searchQuery = it }, lectureViewModel = lectureViewModel)

        // 개선된 무한 스크롤 리스트 구현
        SimplifiedInfiniteScrollList(
            navController = navController,
            lectureItems = allItems,
            searchQuery = searchQuery,
            hasMoreData = hasMoreData,
            isLoading = isLoading,
            isLoadingMore = isLoadingMore,
            onLoadMore = {
                if (hasMoreData && !isLoading && !isLoadingMore) {
                    scope.launch {
                        isLoadingMore = true
                        Log.d("SearchScreen", "추가 데이터 로드 요청: cursor=${cursorLectureId}, createdAt=${cursorCreatedAt}")

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
fun SimplifiedInfiniteScrollList(
    navController: NavController,
    lectureItems: List<LectureItemDto>,
    searchQuery: String,
    hasMoreData: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // 간소화된 스크롤 감지 로직
    LaunchedEffect(listState, lectureItems.size) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemCount = lectureItems.size

            // 마지막 아이템에 접근하면 더 로드
            lastVisibleItem >= totalItemCount - 5 && totalItemCount > 0
        }.collect { isAtEnd ->
            if (isAtEnd && hasMoreData && !isLoading && !isLoadingMore) {
                Log.d("InfiniteScroll", "하단에 근접 - 추가 데이터 로드 요청 (총 ${lectureItems.size}개 로드됨)")
                onLoadMore()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (lectureItems.isEmpty() && !isLoading) {
            // 검색 결과가 없을 때 메시지 표시
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (searchQuery.isBlank()) "강의 목록이 비어 있습니다." else "검색 결과가 없습니다.",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // 강의 목록 표시
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 각 아이템 표시
                itemsIndexed(
                    items = lectureItems,
                    key = { _, item -> item.id } // 고유 ID로 키 사용
                ) { index, item ->
                    SearchLectureItem(
                        lectureItem = item,
                        searchQuery = searchQuery,
                        onClick = {
                            navController.navigate("${Screen.Plan.title}/${item.title}")
                        }
                    )
                }

                // 로딩 인디케이터
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MainPurple,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // 더 이상 데이터가 없는 경우 메시지 표시
                if (!hasMoreData && lectureItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "모든 강의를 불러왔습니다 (총 ${lectureItems.size}개)",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 바닥에 약간의 여백 추가
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            // 첫 로딩 중일 때만 전체 로딩 인디케이터 표시
            if (isLoading && lectureItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MainPurple,
                        strokeWidth = 3.dp
                    )
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
            MakePlanScreen(Lecture(), planViewModel)
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
                withStyle(style = SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                    append(title.substring(searchPos, searchPos + query.length))
                }
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
            .padding(vertical = 10.dp, horizontal = 16.dp)
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
                    text = lectureItem.platform.label,
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
                    .size(40.dp)
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
                onSearchClick = {
                    // 검색 버튼을 클릭했을 때 강제로 검색 실행
                    if (searchQuery.isNotBlank()) {
                        lectureViewModel.getDistinctLecture(
                            LectureDto(
                                search = searchQuery,
                                cursorLectureId = "",
                                cursorCreatedAt = "",
                                offset = "20" // 검색 버튼 클릭 시에도 20개 가져오도록 수정
                            )
                        )
                    }
                }
            )
        }
    )
}

fun Subject.getImageRes(): Int {
    return when (this) {
        Subject.MATH -> R.drawable.screen_search_math_iv
        Subject.SCI -> R.drawable.screen_search_science_iv
        Subject.SOC -> R.drawable.screen_search_social_iv
        Subject.HIST -> R.drawable.screen_search_history_iv
        Subject.UNIV -> R.drawable.screen_search_common_iv
        Subject.LANG2 -> R.drawable.screen_search_foriegn_iv
        Subject.VOC -> R.drawable.screen_search_voca_iv
        Subject.ENG -> R.drawable.screen_search_english_iv
        Subject.KOR -> R.drawable.screen_search_korean_iv
    }
}