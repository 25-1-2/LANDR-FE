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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray

@Composable
fun StudyPlanCompleteScreen(
    recommendResponses: List<RecommendResponse>, // 단일 객체 -> 리스트로 변경
    onStartLearning: () -> Unit
) {
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
                text = "맞춤형 강의 추천을 확인해보세요",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = textGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 추천 강의가 있는 경우
            if (recommendResponses.isNotEmpty()) {
                // 추천 강의 개수 표시
                Text(
                    text = "${recommendResponses.size}개의 맞춤형 강의를 찾았어요!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainPurple
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 추천 강의 목록 (스크롤 가능)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(recommendResponses) { recommend ->
                        RecommendedCourseCard(recommendResponse = recommend)
                    }
                }
            } else {
                // 로딩 상태 또는 추천 강의가 없는 경우
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MainPurple
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "맞춤형 강의를 찾고 있어요...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray
                    )
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
private fun RecommendedCourseCard(recommendResponse: RecommendResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 강의 이미지 플레이스홀더
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = MainPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (recommendResponse.subject.label) {
                            "국어" -> "📖"
                            "영어" -> "🔤"
                            "수학" -> "📊"
                            "사탐" -> "🌍"
                            "과탐" -> "🔬"
                            "한국사" -> "📜"
                            else -> "📚"
                        },
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 강의 정보
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recommendResponse.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = recommendResponse.teacher,
                        fontSize = 14.sp,
                        color = textGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 추천 점수와 플랫폼 정보
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 추천 점수
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MainPurple,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${recommendResponse.recommendScore.toInt()}점",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 플랫폼
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MainPurple.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = recommendResponse.platform.label,
                                fontSize = 12.sp,
                                color = MainPurple,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 난이도
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = recommendResponse.difficulty,
                                fontSize = 12.sp,
                                color = textGray
                            )
                        }
                    }
                }
            }

            // 추천 이유
            if (recommendResponse.recommendReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = recommendResponse.recommendReason,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        lineHeight = 18.sp
                    )
                }
            }

            // 강의 설명
            if (recommendResponse.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = recommendResponse.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }
        }
    }
}