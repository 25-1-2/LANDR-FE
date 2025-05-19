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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    var selectedPlatforms by remember { mutableStateOf<List<Platform>>(emptyList()) }
    var selectedSubjects by remember { mutableStateOf<List<Subject>>(emptyList()) }

    // 디버깅용 - 현재 로드된 아이템 수 출력
    LaunchedEffect(allItems.size, isLoadingMore) {
        Log.d("SearchScreen", "현재 표시 중인 아이템 수: ${allItems.size}, 로딩 중: $isLoadingMore")
    }

    // 필터가 변경될 때마다 데이터를 새로 로드하기 위한 Effect
    // Modify the LaunchedEffect for filter changes:
    LaunchedEffect(selectedPlatforms, selectedSubjects) {
        Log.d("SearchScreen", "필터 변경됨: 플랫폼=${selectedPlatforms.map { it.label }}, 과목=${selectedSubjects.map { it.label }}, 검색모드=$isSearching, 검색어=$searchQuery")

        // Reset loading state
        isLoading = true
        allItems = emptyList()
        cursorLectureId = ""
        cursorCreatedAt = ""
        hasMoreData = true

        // Critical - NEVER reset isSearching here, preserve the exact search state and query

        // Use current search state without modification
        val dto = LectureDto(
            search = if (isSearching) searchQuery else "",
            cursorLectureId = "",
            cursorCreatedAt = "",
            offset = offset,
            platform = selectedPlatforms.firstOrNull(),
            subject = selectedSubjects.firstOrNull()
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
                cursorCreatedAt = ""
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
    // 3. Fix search to work properly with filters
    // Modify searchQuery LaunchedEffect to ensure proper state handling
    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        searchJob = scope.launch {
            delay(300) // Debounce

            Log.d("SearchScreen", "검색 요청 시작: 검색어='$searchQuery', 현재 isSearching=$isSearching")

            if (searchQuery.isBlank()) {
                // 이전 isSearching 상태 저장
                val wasSearching = isSearching

                // 검색모드 해제
                isSearching = false
                isLoading = true

                val dto = LectureDto(
                    search = "",
                    cursorLectureId = "",
                    cursorCreatedAt = "",
                    offset = offset,
                    platform = selectedPlatforms.firstOrNull(),
                    subject = selectedSubjects.firstOrNull()
                )

                lectureViewModel.getAllLecture(dto)
            } else {
                // 이전 isSearching 상태 저장
                val wasSearching = isSearching

                // 검색모드 설정
                isSearching = true
                isLoading = true

                Log.d("SearchScreen", "검색모드 설정: 이전=$wasSearching, 현재=$isSearching")

                val dto = LectureDto(
                    search = searchQuery,
                    cursorLectureId = "",
                    cursorCreatedAt = "",
                    offset = offset,
                    platform = selectedPlatforms.firstOrNull(),
                    subject = selectedSubjects.firstOrNull()
                )

                lectureViewModel.getDistinctLecture(dto)
            }
        }
    }

    // 검색 결과 처리
    LaunchedEffect(searchLectureResponse) {
        // isSearching 체크 제거하고 로그만 추가
        Log.d("SearchScreen", "검색 응답 수신: isSearching=$isSearching")

        val hasData = searchLectureResponse.data != null && searchLectureResponse.data!!.isNotEmpty()
        Log.d("SearchScreen", "검색 응답 내용: data=${searchLectureResponse.data != null}, 아이템수=${searchLectureResponse.data?.size ?: 0}, nextCursor=${searchLectureResponse.nextCursor}, hasNext=${searchLectureResponse.hasNext}")

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

        Log.d("SearchScreen", "검색 결과 아이템 수: ${newItems.size}")

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

    // 전체 목록 처리
    LaunchedEffect(allLectureResponse) {
        if (!isSearching && allLectureResponse.isNotEmpty()) {
            Log.d("SearchScreen", "전체 목록 응답 변경 감지: ${allLectureResponse.size}개 항목")
            // 전체 목록 처리 로직
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
        SearchTopBar(
            searchQuery = searchQuery,
            onQueryChanged = { searchQuery = it },
            lectureViewModel = lectureViewModel,
            onSearchClick = {
                if (searchQuery.isNotBlank()) {
                    Log.d("SearchScreen", "검색 버튼 클릭: 검색어='$searchQuery', 검색 모드 전=${isSearching}")

                    // 검색 모드 설정
                    isSearching = true
                    isLoading = true

                    Log.d("SearchScreen", "검색 모드 설정: 검색 모드 후=${isSearching}")

                    // Reset pagination
                    allItems = emptyList()
                    cursorLectureId = ""
                    cursorCreatedAt = ""
                    hasMoreData = true

                    // 검색 API 호출
                    val dto = LectureDto(
                        search = searchQuery,
                        cursorLectureId = "",
                        cursorCreatedAt = "",
                        offset = offset,
                        platform = selectedPlatforms.firstOrNull(),
                        subject = selectedSubjects.firstOrNull()
                    )

                    Log.d("SearchScreen", "검색 API 호출 요청: search=${dto.search}, platform=${dto.platform?.label}, subject=${dto.subject?.label}")
                    lectureViewModel.getDistinctLecture(dto)
                }
            }
        )

        // 필터 바 컴포넌트
        LectureFilterBarDropdown(
            selectedPlatforms = selectedPlatforms,
            onPlatformSelected = { platform ->
                // 토글 로직: 이미 선택되어 있으면 제거, 없으면 추가
                selectedPlatforms = if (selectedPlatforms.contains(platform)) {
                    selectedPlatforms - platform
                } else {
                    // 하나만 선택 가능하도록 변경
                    listOf(platform)
                }
            },
            selectedSubjects = selectedSubjects,
            onSubjectSelected = { subject ->
                // 토글 로직: 이미 선택되어 있으면 제거, 없으면 추가
                selectedSubjects = if (selectedSubjects.contains(subject)) {
                    selectedSubjects - subject
                } else {
                    // 하나만 선택 가능하도록 변경
                    listOf(subject)
                }
            },
        )

        // 개선된 무한 스크롤 리스트 구현
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

                        // 스크롤로 추가 데이터 로드 시 필터 적용
                        if (selectedPlatforms.isNotEmpty() || selectedSubjects.isNotEmpty()) {
                            loadFilteredLectures(
                                isSearchMode = isSearching,
                                lectureViewModel = lectureViewModel,
                                selectedPlatforms = selectedPlatforms,
                                selectedSubjects = selectedSubjects,
                                searchQuery = if (isSearching) searchQuery else "",
                                cursorLectureId = cursorLectureId,
                                cursorCreatedAt = cursorCreatedAt,
                                offset = offset
                            )
                        } else {
                            // 필터 없이 추가 데이터 로드
                            if (isSearching) {
                                lectureViewModel.getDistinctLecture(
                                    LectureDto(
                                        search = searchQuery,
                                        cursorLectureId = cursorLectureId,
                                        cursorCreatedAt = cursorCreatedAt,
                                        offset = offset
                                    )
                                )
                            } else {
                                lectureViewModel.getAllLecture(
                                    LectureDto(
                                        cursorLectureId = cursorLectureId,
                                        cursorCreatedAt = cursorCreatedAt,
                                        offset = offset
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Log.d("SearchScreen", "추가 데이터 로드 무시: hasMore=$hasMoreData, isLoading=$isLoading, isLoadingMore=$isLoadingMore")
                }
            },
            loadingStateManager = loadingStateManager
        )
    }
}

@Composable
fun LectureFilterBarDropdown(
    selectedPlatforms: List<Platform>,
    onPlatformSelected: (Platform) -> Unit,
    selectedSubjects: List<Subject>,
    onSubjectSelected: (Subject) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 왼쪽: 강의 사이트 필터
        CompactFilterDropdown(
            items = Platform.entries,
            selectedItems = selectedPlatforms,
            labelMapper = { it.label },
            placeholderText = if (selectedPlatforms.isNotEmpty()) selectedPlatforms.first().label else "강의 사이트",
            onItemSelected = { platform ->
                // 이미 선택된 항목이면 제거, 그렇지 않으면 추가
                onPlatformSelected(platform)
            },
            // 필터 해제 버튼 추가
            onClearSelection = {
                // Log filter removal
                //Log.d("SearchScreen", "플랫폼 필터 제거: 현재 검색모드=$isSearching, 검색어='$searchQuery'")

                // Just clear the selected platform
                if (selectedPlatforms.isNotEmpty()) {
                    onPlatformSelected(selectedPlatforms.first())
                }

            },
            modifier = Modifier.weight(1f)
        )

        // 오른쪽: 과목 필터
        CompactFilterDropdown(
            items = Subject.entries,
            selectedItems = selectedSubjects,
            labelMapper = { it.label },
            placeholderText = if (selectedSubjects.isNotEmpty()) selectedSubjects.first().label else "과목",
            onItemSelected = { subject ->
                // 이미 선택된 항목이면 제거, 그렇지 않으면 추가
                onSubjectSelected(subject)
            },
            // 필터 해제 버튼 추가
            onClearSelection = {
                // Log filter removal
                //Log.d("SearchScreen", "과목 필터 제거: 현재 검색모드=$isSearching, 검색어='$searchQuery'")

                // Just clear the selected subject
                if (selectedSubjects.isNotEmpty()) {
                    onSubjectSelected(selectedSubjects.first())
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun <T> CompactFilterDropdown(
    items: List<T>,
    selectedItems: List<T>,
    labelMapper: (T) -> String,
    placeholderText: String,
    onItemSelected: (T) -> Unit,
    onClearSelection: () -> Unit,
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
                    color = if (selectedItems.isEmpty())
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.primary,
                    fontWeight = if (selectedItems.isEmpty()) FontWeight.Normal else FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // 선택된 항목이 있으면 닫기 버튼 표시
                if (selectedItems.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "필터 해제",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable(
                                onClick = {
                                    // Call the clear selection callback
                                    onClearSelection()
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
                val isSelected = selectedItems.contains(item)
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        // Only close if selecting a new item (not toggling an existing one)
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
    selectedPlatforms: List<Platform>,
    selectedSubjects: List<Subject>,
    searchQuery: String,
    cursorLectureId: String,
    cursorCreatedAt: String,
    offset: String
) {
    try {
        // Enhanced logging for debugging
        Log.d("SearchScreen", "로드 요청: 모드=${if(isSearchMode) "검색" else "전체목록"}, 검색어='$searchQuery', 필터=${selectedPlatforms.size}개 플랫폼, ${selectedSubjects.size}개 과목")

        // Create DTO with current state
        val dto = LectureDto(
            search = searchQuery,
            cursorLectureId = cursorLectureId,
            cursorCreatedAt = cursorCreatedAt,
            offset = offset,
            platform = selectedPlatforms.firstOrNull(),
            subject = selectedSubjects.firstOrNull()
        )

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
    loadingStateManager: LoadingStateManager
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
                Image(
                    painter = painterResource(R.drawable.screen_search_empty_iv),
                    contentDescription = "과목명",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = if (searchQuery.isBlank()) "강의 목록이 비어 있습니다." else "검색 결과가 없습니다.",
                    fontSize = 18.sp,
                    color = materialGray,
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
                            // Store the selected lecture before navigating
                            lectureViewModel.selectLecture(item)
                            // Navigate with lecture ID instead of title
                            navController.navigate("${Screen.Plan.title}/${item.id}")                        }
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
                            LoadingIndicator(loadingStateManager)
                        }
                    }
                }

                // 더 이상 데이터가 없는 경우 메시지 표시
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

                // 바닥에 약간의 여백 추가
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
    val query = searchQuery

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
                        text = annotatedString,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f) // 제목이 차지할 공간 확보
                            .padding(end = 8.dp) // "몇 강"과의 간격
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
                    Text(
                        text = lectureItem.teacher,
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