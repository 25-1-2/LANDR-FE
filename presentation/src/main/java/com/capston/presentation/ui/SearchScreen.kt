package com.capston.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.MainPurple

@SuppressLint("RememberReturnType")
@Composable
fun SearchScreen(searchQuery: String) {
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
    // 검색어가 포함된 항목만 필터링
    val filteredItems = allItems.filter { item ->
        item.title.contains(searchQuery, ignoreCase = true) ||
                item.com.contains(searchQuery, ignoreCase = true) ||
                item.teach.contains(searchQuery, ignoreCase = true)
    }

    Column {
        InfiniteScrollList(filteredItems, searchQuery)
    }
}

@Composable
fun SearchLectureItem(lectureItem: LectureItemDto, searchQuery: String) {
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
    ) {
        Column {
            // 회사명 표시
            Text(
                text = lectureItem.com,
                color = MainPurple,
                fontSize = 14.sp
            )

            // 하이라이트된 제목 출력
            Text(
                text = annotatedString,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // 추가 설명 텍스트
            Text(
                text = lectureItem.teach,
                color = LightGray40,
                fontSize = 14.sp
            )
        }
    }
}

private fun AnnotatedString.Builder.appendAnnotatedString(text: String, color: Color) {
    withStyle(style = SpanStyle(color = color)) {
        append(text)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    CapstonTheme {
        SearchScreen(searchQuery = "")
    }
}