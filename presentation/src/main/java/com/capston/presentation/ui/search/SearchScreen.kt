package com.capston.presentation.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LectureDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.domain.model.LectureItemDto
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.MainActivity
import com.capston.presentation.ui.common.Screen
import com.capston.presentation.ui.SearchFieldWithIcons
import com.capston.presentation.viewmodel.LectureViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@SuppressLint("RememberReturnType")
@Composable
fun SearchScreen(
    navController: NavController,
    lectureViewModel: LectureViewModel,
    initialQuery: String = "",
    loadingStateManager: LoadingStateManager
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

    // Filter state
    var selectedPlatform by remember { mutableStateOf<Platform?>(null) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }

    // 디버깅용 - 현재 로드된 아이템 수 출력
    LaunchedEffect(allItems.size, isLoadingMore) {
        Log.d("SearchScreen", "현재 표시 중인 아이템 수: ${allItems.size}, 로딩 중: $isLoadingMore")
    }

    // 필터가 변경될 때마다 데이터를 새로 로드하기 위한 Effect
    LaunchedEffect(selectedPlatform, selectedSubject) {
        Log.d("SearchScreen", "필터 변경됨: 플랫폼=${selectedPlatform?.label ?: "없음"}, 과목=${selectedSubject?.label ?: "없음"}, 검색모드=$isSearching, 검색어=$searchQuery")

        // Reset loading state
        isLoading = true
        allItems = emptyList()
        cursorLectureId = ""
        cursorCreatedAt = ""
        hasMoreData = true

        // Create DTO with current state - directly use the nullable platform and subject
        val dto = LectureDto(
            search = if (isSearching) searchQuery else "",
            cursorLectureId = "",
            cursorCreatedAt = "",
            offset = offset,
            platform = selectedPlatform,
            subject = selectedSubject
        )

        // Call appropriate API based on current search mode
        if (isSearching) {
            lectureViewModel.getDistinctLecture(dto)
        } else {
            lectureViewModel.getAllLecture(dto)
        }
    }

    // 컴포넌트가 처음 로드될 때 전체 강의 조회
    LaunchedEffect(shouldReloadData) {
        if (shouldReloadData) {
            Log.d("SearchScreen", "전체 강의 조회 (재로딩) 시작")
            isLoading = true
            isSearching = false // 명시적으로 검색 모드 해제
            allItems = emptyList() // 기존 항목 초기화
            cursorLectureId = "" // 커서 초기화
            cursorCreatedAt = "" // 커서 초기화
            hasMoreData = true  // 데이터가 더 있다고 가정

            val currentTime : Long = System.currentTimeMillis()
            val dataFormat4 = SimpleDateFormat("HH:mm:ss.sss")

            Log.d("페이지 로드 전 시간: ", dataFormat4.format(currentTime))

            // 첫 번째 페이지 로드
            lectureViewModel.getAllLecture(LectureDto(
                offset = offset,
                cursorLectureId = "",
                cursorCreatedAt = "",
                search = "" // 명시적으로 빈 검색어 지정
            ))

            Log.d("페이지 로드 후 시간: ", dataFormat4.format(currentTime))

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
    var searchJob by remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300) // Debounce

            Log.d("SearchScreen", "검색 요청 시작: 검색어='$searchQuery', 현재 isSearching=$isSearching")

            if (searchQuery.isBlank()) {
                isSearching = false
                isLoading = true
                shouldReloadData = true
            } else {
                isSearching = true
                isLoading = true

                // 기존 아이템 초기화 추가
                allItems = emptyList()

                val dto = LectureDto(
                    search = searchQuery,
                    cursorLectureId = "",
                    cursorCreatedAt = "",
                    offset = offset,
                    platform = selectedPlatform,
                    subject = selectedSubject
                )

                lectureViewModel.getDistinctLecture(dto)
            }

            // 로딩 인디케이터 타이머
            delay(1000) // 타임아웃
            if (isLoading) {
                Log.d("SearchScreen", "검색 타임아웃 - 로딩 상태 강제 해제: 검색어='$searchQuery'")
                isLoading = false
            }
        }
    }

    val handleSearchClick = {
        hasMoreData = true

        if (searchQuery.isBlank()) {
            isSearching = false

            val dto = LectureDto(
                search = "",
                cursorLectureId = "",
                cursorCreatedAt = "",
                offset = offset,
                platform = selectedPlatform,
                subject = selectedSubject
            )

            // 로그 추가
            Log.d("SearchScreen", "전체 강의 목록 요청")
            lectureViewModel.getAllLecture(dto)
        } else {
            isSearching = true

            val dto = LectureDto(
                search = searchQuery,
                cursorLectureId = "",
                cursorCreatedAt = "",
                offset = offset,
                platform = selectedPlatform,
                subject = selectedSubject
            )

            // 로그 추가
            Log.d("SearchScreen", "검색 요청: '$searchQuery'")
            lectureViewModel.getDistinctLecture(dto)
        }
    }

    // 검색 결과 처리
    LaunchedEffect(searchLectureResponse) {
        val hasData = searchLectureResponse.data != null && searchLectureResponse.data!!.isNotEmpty()

        val newItems = searchLectureResponse.data?.filterNotNull()?.map { lecture ->
            LectureItemDto(
                id = lecture.id,
                title = lecture.title,
                platform = lecture.platform,
                teacher = lecture.teacher,
                createdAt = lecture.createdAt,
                subject = lecture.subject,
                tag = lecture.tag,
                totalLessons = lecture.totalLessons
            )
        } ?: emptyList()

        if (cursorLectureId.isEmpty() || isLoading) {
            allItems = newItems
        } else if (isLoadingMore && newItems.isNotEmpty()) {
            val existingIds = allItems.map { it.id }.toSet()
            val uniqueNewItems = newItems.filter { it.id !in existingIds }
            allItems = allItems + uniqueNewItems
        }

        hasMoreData = searchLectureResponse.hasNext

        if (searchLectureResponse.nextCursor > 0) {
            cursorLectureId = searchLectureResponse.nextCursor.toString()
        }

        val nextCreatedAt = searchLectureResponse.nextCreatedAt
        if (nextCreatedAt != null && nextCreatedAt.isNotEmpty()) {
            cursorCreatedAt = nextCreatedAt
        }

        // 로딩 상태 초기화
        isLoading = false
        isLoadingMore = false
    }

    // 전체 목록 처리
    LaunchedEffect(allLectureResponse) {
        Log.d("SearchScreen", "전체 목록 응답 변경 감지: ${allLectureResponse.size}개 항목")

        val newItems = allLectureResponse.map { lecture ->
            LectureItemDto(
                id = lecture.id,
                title = lecture.title,
                platform = lecture.platform,
                teacher = lecture.teacher,
                subject = lecture.subject,
                createdAt = lecture.createdAt,
                tag = lecture.tag,
                totalLessons = lecture.totalLessons
            )
        }

        if (cursorLectureId.isEmpty() || isLoading) {
            allItems = newItems
            Log.d("SearchScreen", "전체 목록 첫 페이지 로드: ${newItems.size}개 항목")
        } else if (isLoadingMore && newItems.isNotEmpty()) {
            val existingIds = allItems.map { it.id }.toSet()
            val uniqueNewItems = newItems.filter { it.id !in existingIds }

            allItems = allItems + uniqueNewItems
            Log.d("SearchScreen", "전체 목록 추가 로드: 기존 ${allItems.size - uniqueNewItems.size}개, 새로 추가 ${uniqueNewItems.size}개, 총 ${allItems.size}개")
        }

        hasMoreData = newItems.isNotEmpty()
        Log.d("SearchScreen", "전체 목록 다음 페이지 존재 여부: $hasMoreData")

        if (newItems.isNotEmpty()) {
            val lastItem = newItems.last()
            cursorLectureId = lastItem.id.toString()
            cursorCreatedAt = lastItem.createdAt ?: ""
            Log.d("SearchScreen", "전체 목록 다음 커서 설정: id=${cursorLectureId}, createdAt=${cursorCreatedAt}")
        }

        // Reset loading states
        isLoading = false
        isLoadingMore = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // 키보드 패딩 추가
    ) {
        // 기본 UI 컨텐츠
        Column {
            // 상단바
            SearchTopBar(
                searchQuery = searchQuery,
                onQueryChanged = { searchQuery = it },
                lectureViewModel = lectureViewModel,
                onSearchClick = handleSearchClick
            )

            // 필터 바 컴포넌트
            LectureFilterBarDropdown(
                selectedPlatform = selectedPlatform,
                onPlatformSelected = { platform ->
                    selectedPlatform = if (selectedPlatform == platform) null else platform
                },
                selectedSubject = selectedSubject,
                onSubjectSelected = { subject ->
                    selectedSubject = if (selectedSubject == subject) null else subject
                }
            )

            // 리스트 컨텐츠를 포함하는 Box
            Box(modifier = Modifier.weight(1f)) {
                // 무한 스크롤 리스트
                SimplifiedInfiniteScrollList(
                    navController = navController,
                    lectureViewModel = lectureViewModel,
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

                                loadFilteredLectures(
                                    isSearchMode = isSearching,
                                    lectureViewModel = lectureViewModel,
                                    selectedPlatform = selectedPlatform,
                                    selectedSubject = selectedSubject,
                                    searchQuery = if (isSearching) searchQuery else "",
                                    cursorLectureId = cursorLectureId,
                                    cursorCreatedAt = cursorCreatedAt,
                                    offset = offset
                                )
                            }
                        } else {
                            Log.d("SearchScreen", "추가 데이터 로드 무시: hasMore=$hasMoreData, isLoading=$isLoading, isLoadingMore=$isLoadingMore")
                        }
                    },
                    loadingStateManager = loadingStateManager,
                    isSearching = isSearching
                )

                // 추가 로딩 인디케이터 (하단에 떠있는 형태)
                if (isLoadingMore) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                            .size(60.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.Asset("loading_dot.json")
                        )
                        val progress by animateLottieCompositionAsState(
                            composition,
                            iterations = LottieConstants.IterateForever
                        )
                        LottieAnimation(
                            composition = composition,
                            progress = { progress },
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

        // 초기 로딩 인디케이터 (전체 화면 오버레이)
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        // 이벤트 처리는 하되 아무 동작도 하지 않음 (이벤트 전파 허용)
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                // 이벤트를 소비하지 않음 (하위 뷰로 전달)
                            }
                        }
                    }
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("loading_dot.json")
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun LectureFilterBarDropdown(
    selectedPlatform: Platform?,
    onPlatformSelected: (Platform) -> Unit,
    selectedSubject: Subject?,
    onSubjectSelected: (Subject) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 왼쪽: 강의 사이트 필터
        SingleSelectFilterDropdown(
            items = Platform.entries,
            selectedItem = selectedPlatform,
            labelMapper = { it.label },
            placeholderText = selectedPlatform?.label ?: "강의 사이트",
            onItemSelected = onPlatformSelected,
            modifier = Modifier.weight(1f)
        )

        // 오른쪽: 과목 필터
        SingleSelectFilterDropdown(
            items = Subject.entries,
            selectedItem = selectedSubject,
            labelMapper = { it.label },
            placeholderText = selectedSubject?.label ?: "과목",
            onItemSelected = onSubjectSelected,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun <T> SingleSelectFilterDropdown(
    items: List<T>,
    selectedItem: T?,
    labelMapper: (T) -> String,
    placeholderText: String,
    onItemSelected: (T) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedCard(
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clickable(onClick = { expanded = true })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        leadingIcon()
                    }
                }

                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedItem == null)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.primary,
                    fontWeight = if (selectedItem == null) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // 선택된 항목이 있으면 닫기 버튼 표시
                if (selectedItem != null) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "필터 해제",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable(
                                onClick = {
                                    // Call the onItemSelected with the current item to toggle it off
                                    onItemSelected(selectedItem)
                                    // Close the dropdown
                                    expanded = false
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "펼치기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .heightIn(max = 200.dp)
                .background(White)
        ) {
            items.forEach { item ->
                val isSelected = selectedItem == item
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        // Close the dropdown after selection if not toggling off
                        if (!isSelected) {
                            expanded = false
                        }
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = labelMapper(item),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

// 필터링된 강의 로드 함수 - 단일 플랫폼 및 단일 과목 지원
fun loadFilteredLectures(
    isSearchMode: Boolean,
    lectureViewModel: LectureViewModel,
    selectedPlatform: Platform?,
    selectedSubject: Subject?,
    searchQuery: String,
    cursorLectureId: String,
    cursorCreatedAt: String,
    offset: String
) {
    try {
        // Enhanced logging for debugging
        Log.d("SearchScreen", "로드 요청: 모드=${if(isSearchMode) "검색" else "전체목록"}, 검색어='$searchQuery', 플랫폼=${selectedPlatform?.label ?: "없음"}, 과목=${selectedSubject?.label ?: "없음"}")

        // Create DTO with current state
        val dto = LectureDto(
            search = searchQuery,
            cursorLectureId = cursorLectureId,
            cursorCreatedAt = cursorCreatedAt,
            offset = offset,
            platform = selectedPlatform,  // Pass directly as nullable
            subject = selectedSubject     // Pass directly as nullable
        )

        // Log the exact parameters being sent
        Log.d("SearchScreen", "API 요청 파라미터: search='${dto.search}', platform=${dto.platform?.label ?: "null"}, subject=${dto.subject?.label ?: "null"}, cursor=${dto.cursorLectureId}, createdAt=${dto.cursorCreatedAt}")

        // Use search mode to determine API
        if (isSearchMode) {
            Log.d("SearchScreen", "검색 API 호출: '${dto.search}', 플랫폼=${dto.platform?.label ?: "없음"}, 과목=${dto.subject?.label ?: "없음"}")
            lectureViewModel.getDistinctLecture(dto)
        } else {
            Log.d("SearchScreen", "전체 목록 API 호출: 플랫폼=${dto.platform?.label ?: "없음"}, 과목=${dto.subject?.label ?: "없음"}")
            lectureViewModel.getAllLecture(dto)
        }
    } catch (e: Exception) {
        Log.e("SearchScreen", "API 호출 중 오류 발생: ${e.message}", e)
    }
}

@Composable
fun SimplifiedInfiniteScrollList(
    navController: NavController,
    lectureViewModel: LectureViewModel,
    lectureItems: List<LectureItemDto>,
    searchQuery: String,
    hasMoreData: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    loadingStateManager: LoadingStateManager,
    isSearching: Boolean
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, lectureItems.size) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemCount = lectureItems.size
            lastVisibleItem >= totalItemCount - 5 && totalItemCount > 0
        }.collect { isAtEnd ->
            if (isAtEnd && hasMoreData && !isLoading && !isLoadingMore) {
                Log.d("InfiniteScroll", "하단에 근접 - 추가 데이터 로드 요청 (총 ${lectureItems.size}개 로드됨)")
                onLoadMore()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // 키보드 높이에 따라 자동으로 패딩 적용
            .navigationBarsPadding() // 네비게이션 바도 고려
    ) {
        if (!isLoading && lectureItems.isEmpty()) {
            // 검색 결과가 없을 때 - isLoading 조건 제거
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.screen_search_empty_iv),
                    contentDescription = "검색 결과 없음",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))

                // 검색 모드에 따라 메시지 표시
                Text(
                    text = "검색 결과가 없습니다.",
                    fontSize = 18.sp,
                    color = materialGray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // 결과가 있을 때 리스트 표시
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = lectureItems,
                    key = { _, item -> item.id }
                ) { index, item ->
                    SearchLectureItem(
                        lectureItem = item,
                        searchQuery = searchQuery,
                        onClick = {
                            lectureViewModel.selectLecture(item)
                            navController.navigate("${Screen.Plan.title}/${item.id}")
                        }
                    )
                }

                // 로딩 인디케이터는 제거 - 외부에서 처리

                if (!hasMoreData && lectureItems.isNotEmpty()) {
                    item {
                        Text(
                            text = "모든 강의를 불러왔습니다 (총 ${lectureItems.size}개)",
                            fontSize = 14.sp,
                            color = materialGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchNavHost(navController: NavHostController, planViewModel: PlanViewModel, lectureViewModel: LectureViewModel, loadingStateManager: LoadingStateManager) {
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(
                navController = navController,
                lectureViewModel = lectureViewModel,
                loadingStateManager = loadingStateManager
            )
        }
        composable(
            route = "${Screen.Plan.title}/{lectureId}",
            arguments = listOf(navArgument("lectureId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lectureId = backStackEntry.arguments?.getInt("lectureId") ?: 0
            MakePlanScreen(
                planViewModel = planViewModel,
                lectureViewModel = lectureViewModel,
                navController = navController,
                loadingStateManager = loadingStateManager
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchLectureItem(
    lectureItem: LectureItemDto,
    searchQuery: String,
    onClick: () -> Unit
) {
    // null 안전하게 처리
    val title = lectureItem.title
    val teacher = lectureItem.teacher
    val query = searchQuery

    // 검색어가 포함된 부분을 하이라이트하는 함수 (재사용 가능하도록 추출)
    fun getHighlightedText(text: String, searchQuery: String): AnnotatedString {
        return buildAnnotatedString {
            if (searchQuery.isNotEmpty()) {
                var startIndex = 0
                var searchPos = text.indexOf(searchQuery, ignoreCase = true)

                while (searchPos != -1) {
                    append(text.substring(startIndex, searchPos))
                    withStyle(style = SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append(text.substring(searchPos, searchPos + searchQuery.length))
                    }
                    startIndex = searchPos + searchQuery.length
                    searchPos = text.indexOf(searchQuery, startIndex, ignoreCase = true)
                }
                append(text.substring(startIndex))
            } else {
                append(text)
            }
        }
    }

    // 제목과 선생님 이름에 검색어 하이라이트 적용
    val highlightedTitle = getHighlightedText(title, query)
    val highlightedTeacher = getHighlightedText(teacher, query)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                // platform + totalLessons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lectureItem.platform.label,
                            fontSize = 12.sp,
                            color = MainPurple
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .border(
                                width = 1.dp,
                                color = lectureItem.subject.borderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = lectureItem.subject.bgColor, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 5.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = lectureItem.subject.label,
                            fontSize = 12.sp,
                            color = lectureItem.subject.borderColor
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = highlightedTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = materialGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${lectureItem.totalLessons}강",
                            color = materialGray,
                            fontSize = 12.sp
                        )
                    }
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                ) {
                    // 여기서 선생님 이름에 하이라이트 적용
                    Text(
                        text = highlightedTeacher,
                        color = textGray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = " · [${lectureItem.tag}]",
                        color = textGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    lectureViewModel: LectureViewModel,
    onSearchClick: () -> Unit
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
                    // 검색 버튼을 클릭했을 때 검색 실행
                    onSearchClick()
                }
            )
        }
    )
}


val Subject.bgColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFFD8D8)   // 연한 핑크
        Subject.ENG -> Color(0xFFC5D9FF)   // 연한 하늘색
        Subject.MATH -> Color(0xFFD3F7D3)  // 연한 민트
        Subject.SOC -> Color(0xFFFFF4C6)   // 연한 노랑
        Subject.SCI -> Color(0xFFC1E8F7)   // 연한 파랑
        Subject.HIST -> Color(0xFFE1B5E8)  // 연한 보라
        Subject.UNIV -> Color(0xFFC7F6F9)  // 연한 청록
        Subject.LANG2 -> Color(0xFFFFD8E6) // 연한 분홍
        Subject.VOC -> Color(0xFFF0F0F0)   // 연한 회색
    }

val Subject.borderColor: Color
    get() = when (this) {
        Subject.KOR -> Color(0xFFFF6B6B)   // 부드러운 빨강
        Subject.ENG -> Color(0xFF5D9CFF)    // 부드러운 파랑
        Subject.MATH -> Color(0xFF5BBF63)   // 부드러운 초록
        Subject.SOC -> Color(0xFFFFC046)    // 부드러운 노랑
        Subject.SCI -> Color(0xFF1EB0D2)    // 부드러운 하늘색
        Subject.HIST -> Color(0xFF9E4FB0)   // 부드러운 보라
        Subject.UNIV -> Color(0xFF00A7B4)   // 부드러운 청록
        Subject.LANG2 -> Color(0xFFF08C8C)  // 부드러운 분홍
        Subject.VOC -> Color(0xFFB0B0B0)    // 부드러운 회색
    }