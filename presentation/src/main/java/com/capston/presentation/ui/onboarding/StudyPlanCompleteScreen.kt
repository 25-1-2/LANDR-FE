package com.capston.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray

data class RecommendedCourse(
    val title: String,
    val instructor: String,
    val duration: String,
    val level: String
)

@Composable
fun StudyPlanCompleteScreen(onStartLearning: () -> Unit) {
    val recommendedCourses = listOf(
        RecommendedCourse("수학 미적분 집중강의", "김수학 선생님", "3개월", "고3"),
        RecommendedCourse("영어 문법 완전정복", "박영어 선생님", "2개월", "고2-3"),
        RecommendedCourse("한국사 필수개념 정리", "이역사 선생님", "1개월", "고등")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 LANDR 로고
        Image(
            painter = painterResource(R.drawable.landr_title_iv),
            contentDescription = "LANDR 로고",
            modifier = Modifier
                .padding(top = 60.dp, start = 20.dp)
                .size(80.dp)
                .align(Alignment.TopStart)
        )

        // 메인 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp, bottom = 80.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 완성 메시지
            Text(
                text = "조은채님에게 딱 맞는\n플랜이 완성됐어요!",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "목표 도달까지 5달 걸릴 것으로 예상돼요",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = textGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 목표 달성 날짜
            Text(
                text = "6월 9일이면 목표 등급을 달성할거예요",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainPurple
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 진행률 그래프
            ProgressGraph(
                progress = 0.2f, // 20% 진행
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 강의 추천 섹션
            Text(
                text = "나에게 맞는 강의를 추천해드려요",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainPurple,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 추천 강의 카드들
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recommendedCourses) { course ->
                    CourseRecommendationCard(course = course)
                }
            }
        }

        // 하단 시작하기 버튼
        Button(
            onClick = onStartLearning,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainPurple
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = "시작하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ProgressGraph(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                // 진행률 바
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(3.dp)
                        )
                        .align(Alignment.Center)
                ) {
                    // 진행된 부분
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .background(
                                color = MainPurple,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }

                // 역삼각형과 세로 점선
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    val progressOffset = progress - 0.02f

                    // 역삼각형
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        Spacer(modifier = Modifier.fillMaxWidth(progressOffset))
                        Text(
                            text = "▼",
                            color = MainPurple,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // 세로 점선
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        Spacer(modifier = Modifier.fillMaxWidth(progress - 0.005f))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(top = 30.dp)
                        ) {
                            repeat(8) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(2.dp)
                                        .background(
                                            color = MainPurple.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 진행률 텍스트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "현재 등급",
                    fontSize = 12.sp,
                    color = textGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "목표 등급",
                    fontSize = 12.sp,
                    color = textGray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun CourseRecommendationCard(course: RecommendedCourse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 강의 이미지 플레이스홀더
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MainPurple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = "📚",
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 강의 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = course.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.instructor,
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = course.duration,
                        fontSize = 12.sp,
                        color = MainPurple,
                        modifier = Modifier
                            .background(
                                color = MainPurple.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = course.level,
                        fontSize = 12.sp,
                        color = textGray,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudyPlanCompleteScreenPreview() {
    CapstonTheme {
        StudyPlanCompleteScreen(onStartLearning = {})
    }
}