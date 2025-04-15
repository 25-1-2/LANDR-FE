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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.capston.domain.model.Lecture
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.delay

@SuppressLint("RememberReturnType")
@Composable
fun SearchScreen(
    navController: NavController,
    initialQuery: String = "",
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }

    Column {
        SearchTopBar(searchQuery = searchQuery, onQueryChanged = { searchQuery = it })

        val allItems = remember {
            List(50) {
                LectureItemDto(
                    title = "2026 현우진의 수분감 - 수학I (공통)",
                    com = "메가스터디",
                    teach = "현우진 · [고3·2·N수] 수능 (문제풀이) · 50강"
                )
            } + listOf(
                LectureItemDto(
                    title = "괜찮아 너만 모르는 건 아니야",
                    com = "메가스터디",
                    teach = "조정식 · 수능 대비 강좌"
                )
            )
        }

        val filteredItems = allItems.filter { item ->
            item.title.contains(searchQuery, ignoreCase = true) ||
                    item.com.contains(searchQuery, ignoreCase = true) ||
                    item.teach.contains(searchQuery, ignoreCase = true)
        }

        InfiniteScrollList(navController, filteredItems, searchQuery)
    }
}

@Composable
fun InfiniteScrollList(navController: NavController, filteredItems: List<LectureItemDto>, searchQuery: String) {
    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = rememberLazyListState(),
            contentPadding = PaddingValues(25.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                    SearchLectureItem(
                        lectureItem = item,
                        searchQuery = searchQuery,
                        onClick = {
                            navController.navigate("${Screen.Plan.title}/${item.title}")
                        }
                    )
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
fun SearchNavHost(navController: NavHostController, planViewModel: PlanViewModel) {
    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        composable("search") {
            SearchScreen(navController = navController)
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
        var endIndex = 0

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
                    text = lectureItem.com,
                    color = MainPurple,
                    fontSize = 14.sp
                )

                Text(
                    text = annotatedString,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = lectureItem.teach,
                    color = LightGray40,
                    fontSize = 14.sp
                )
            }

            Image(
                painter = painterResource(R.drawable.screen_search_math_iv),
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

private fun AnnotatedString.Builder.appendAnnotatedString(text: String, color: Color) {
    withStyle(style = SpanStyle(color = color)) {
        append(text)
    }
}