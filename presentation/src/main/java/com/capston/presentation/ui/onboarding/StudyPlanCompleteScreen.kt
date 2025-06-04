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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray

@Composable
fun StudyPlanCompleteScreen(
    recommendResponses: List<RecommendResponse>,
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

            // 추천 강의가 있는 경우
            if (recommendResponses.isNotEmpty()) {
                // 과목별로 그룹화
                val subjectGroups = recommendResponses
                    .groupBy { it.subject }
                    .mapValues { entry ->
                        // 각 과목 내에서 점수 순으로 정렬
                        entry.value.sortedByDescending { it.recommendScore }
                    }

                Text(
                    text = "${subjectGroups.size}개 과목에서 ${recommendResponses.size}개의 맞춤형 강의를 찾았어요!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = textGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 과목별 추천 강의 목록
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    subjectGroups.forEach { (subject, recommendations) ->
                        item {
                            SubjectRecommendationSection(
                                subject = subject,
                                recommendations = recommendations
                            )
                        }
                    }
                }
            } else {
//                // 로딩 상태
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.weight(1f)
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(48.dp),
//                        color = MainPurple
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    Text(
//                        text = "모든 과목의 맞춤형 강의를 찾고 있어요...",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = textGray,
//                        textAlign = TextAlign.Center
//                    )
//                }
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("loading_dot.json") // assets 폴더 내 Lottie JSON 파일
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(50.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "모든 과목의 맞춤형 강의를 찾고 있어요...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textGray,
                        textAlign = TextAlign.Center
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
private fun SubjectRecommendationSection(
    subject: com.capston.domain.response.enum_class.Subject,
    recommendations: List<RecommendResponse>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = MainPurple.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // 과목 헤더
            SubjectHeader(subject = subject, recommendationCount = recommendations.size)

            Spacer(modifier = Modifier.height(16.dp))

            // 해당 과목의 추천 강의들
            recommendations.forEachIndexed { index, recommendation ->
                RecommendedCourseItem(
                    recommendResponse = recommendation,
                    isLast = index == recommendations.size - 1
                )

                if (index < recommendations.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SubjectHeader(
    subject: com.capston.domain.response.enum_class.Subject,
    recommendationCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 과목 아이콘
            Text(
                text = when (subject.label) {
                    "국어" -> "📖"
                    "영어" -> "🔤"
                    "수학" -> "📊"
                    "사탐" -> "🌍"
                    "과탐" -> "🔬"
                    "한국사" -> "📜"
                    else -> "📚"
                },
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "${subject.label} 추천 강의",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "${recommendationCount}개의 추천 강의",
                    fontSize = 14.sp,
                    color = textGray
                )
            }
        }

        // 과목별 배지
        Box(
            modifier = Modifier
                .background(
                    color = MainPurple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = subject.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainPurple
            )
        }
    }
}

@Composable
private fun RecommendedCourseItem(
    recommendResponse: RecommendResponse,
    isLast: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 강의 이미지 플레이스홀더
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        color = MainPurple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
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
                    fontSize = 28.sp
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
                    color = Color.Black,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = recommendResponse.teacher,
                    fontSize = 14.sp,
                    color = textGray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 태그들
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 추천 점수
                    Box(
                        modifier = Modifier
                            .background(
                                color = MainPurple,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "적합도 ${recommendResponse.recommendScore}%",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 플랫폼
                    Box(
                        modifier = Modifier
                            .background(
                                color = MainPurple.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = recommendResponse.platform.label,
                            fontSize = 12.sp,
                            color = MainPurple,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // 난이도
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "난이도: ${recommendResponse.difficulty}",
                            fontSize = 12.sp,
                            color = textGray
                        )
                    }
                }

                // 추천 이유
                if (recommendResponse.recommendReason.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡",
                            fontSize = 14.sp
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
            }
        }

        // 강의들 사이 구분선 (마지막 항목이 아닌 경우)
        if (!isLast) {
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}