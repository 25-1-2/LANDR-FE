package com.capston.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainBlue
import com.capston.presentation.theme.MainPurple
import kotlinx.coroutines.delay

@Composable
fun SearchScreen() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 검색 리스트 표시
            InfiniteScrollList()
        }
    }
}

@Composable
fun InfiniteScrollList() {
    var items by remember { mutableStateOf(List(1000) {
        "2026 현우진의 수분감 - 수학I (공통)"
    }) }
    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 20.dp, start = 20.dp)
        ) {
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    SearchLectureItem(title = item) // 새롭게 분리한 Composable 사용
                }

                if (!loading) {
                    item {
                        val listState = rememberLazyListState()

                        LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
                            if (listState.firstVisibleItemIndex == items.size - 1) {
                                loading = true
                                delay(1500)
                                val newItems = List(20) { "New Item #$it" }
                                items = items + newItems
                                loading = false
                            }
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
}

@Composable
fun SearchLectureItem(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // 아이템 간격 추가
    ) {
        Column {
            Text(
                text = "메가스터디",
                color = MainPurple,
                fontSize = 14.sp)
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ) // title을 전달받아 표시
            Text(
                text = "현우진 · [고3·2·N수] 수능 (문제풀이) · 50강",
                color = LightGray40,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SearchBar() {
    var textState by remember { mutableStateOf("") } // 상태 저장
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = MainPurple,
                textColor = LightGray60
            ),
            textStyle = TextStyle(
                fontSize = 20.sp
            ),
            modifier = Modifier
                .weight(1f) // OutlinedTextField가 남는 공간을 차지하도록 설정
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CapstonTheme {
        SearchScreen()
    }
}