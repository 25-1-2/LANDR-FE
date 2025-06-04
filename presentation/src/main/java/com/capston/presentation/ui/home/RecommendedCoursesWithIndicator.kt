package com.capston.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.viewmodel.HomeViewModel

data class RecommendedCourse(
    val id: Int,
    val title: String,
    val instructor: String,
    val duration: String,
    val level: String,
    val progress: Int = 0, // 진행률 (0-100)
    val thumbnailEmoji: String = "📚"
)

@Composable
fun RecommendedCoursesWithIndicator(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    // 임시 데이터 - 실제로는 homeViewModel에서 가져와야 함
    val recommendedCourses = listOf(
        RecommendedCourse(
            id = 1,
            title = "수학 미적분 집중강의",
            instructor = "김수학 선생님",
            duration = "120분",
            level = "고3",
            progress = 25,
            thumbnailEmoji = "📐"
        ),
        RecommendedCourse(
            id = 2,
            title = "영어 문법 완전정복",
            instructor = "박영어 선생님",
            duration = "90분",
            level = "고2-3",
            progress = 60,
            thumbnailEmoji = "📖"
        ),
        RecommendedCourse(
            id = 3,
            title = "한국사 필수개념 정리",
            instructor = "이역사 선생님",
            duration = "75분",
            level = "고등",
            progress = 10,
            thumbnailEmoji = "📜"
        ),
        RecommendedCourse(
            id = 4,
            title = "물리학I 핵심정리",
            instructor = "최물리 선생님",
            duration = "100분",
            level = "고2",
            progress = 0,
            thumbnailEmoji = "⚛️"
        )
    )

    val listState = rememberLazyListState()

    // 현재 보이는 아이템 인덱스 계산 (더 간단한 방법)
    val currentPage = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }

    Column(modifier = modifier) {
        // 가로 스크롤 카드 리스트
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(recommendedCourses) { course ->
                RecommendedCourseCard(
                    course = course,
                    onCourseClick = { courseId ->
                        // 강의 클릭 이벤트
                        // homeViewModel.navigateToCourse(courseId)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 페이지 인디케이터
        PageIndicator(
            totalPages = recommendedCourses.size,
            currentPage = currentPage.value,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun PageIndicator(
    totalPages: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) MainPurple else LightGray60.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun RecommendedCourseCard(
    course: RecommendedCourse,
    onCourseClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCourseClick(course.id) }
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 썸네일
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MainPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(
                        text = course.thumbnailEmoji,
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 강의 정보
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = course.instructor,
                        fontSize = 14.sp,
                        color = LightGray60
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 레벨 태그
                Text(
                    text = course.level,
                    fontSize = 12.sp,
                    color = LightGray60,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )

                // 시간 태그
                Text(
                    text = course.duration,
                    fontSize = 12.sp,
                    color = MainPurple,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            color = MainPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}