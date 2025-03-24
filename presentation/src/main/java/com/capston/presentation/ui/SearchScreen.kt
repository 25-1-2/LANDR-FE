package com.capston.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.capston.presentation.theme.MainPurple
import kotlinx.coroutines.delay

@Composable
fun SearchScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center // 정중앙에 배치
        ) {
            InfiniteScrollList()
        }
    }
}

@Composable
fun InfiniteScrollList() {
    var items by remember { mutableStateOf(List(1000) { "Item #$it" }) }
    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 상하단 100dp의 영역을 제한
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight().padding(top = 20.dp, bottom = 20.dp, start = 40.dp)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    Text(text = item, )
                }

                // 무한 스크롤 감지: 마지막 아이템에 도달하면 데이터 로딩
                if (!loading) {
                    item {
                        val listState = rememberLazyListState()

                        // 리스트 끝에 도달했을 때 새 데이터 로드
                        LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
                            if (listState.firstVisibleItemIndex == items.size - 1) {
                                loading = true
                                delay(1500) // 데이터 로드 대기 시간 시뮬레이션
                                val newItems = List(20) { "New Item #$it" }
                                items = items + newItems
                                loading = false
                            }
                        }

                        // 로딩 상태 표시
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
}