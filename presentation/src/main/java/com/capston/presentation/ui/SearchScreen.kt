package com.capston.presentation.ui

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
import com.capston.presentation.theme.LightGray2
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.MainPurple

@Composable
fun SearchScreen(searchQuery: String) {
    Column {
        InfiniteScrollList(searchQuery)
    }
}

@Composable
fun SearchLectureItem(title: String, searchQuery: String) {
    val annotatedString = buildAnnotatedString {
        var startIndex = 0
        var endIndex = 0

        if (searchQuery.isNotEmpty()) {
            var searchPos = title.indexOf(searchQuery, ignoreCase = true)
            while (searchPos != -1) {
                append(title.substring(startIndex, searchPos))
                appendAnnotatedString(title.substring(searchPos, searchPos + searchQuery.length), MainPurple)
                startIndex = searchPos + searchQuery.length
                searchPos = title.indexOf(searchQuery, startIndex, ignoreCase = true)
            }
            append(title.substring(startIndex))
        } else {
            append(title)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(text = "메가스터디", color = MainPurple, fontSize = 14.sp)
        Text(text = annotatedString, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "현우진 · [고3·2·N수] 수능 (문제풀이) · 50강", color = LightGray2, fontSize = 14.sp)
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