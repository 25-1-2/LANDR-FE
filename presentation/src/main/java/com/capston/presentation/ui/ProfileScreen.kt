package com.capston.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray5
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.SubPurple

// 마이페이지 스크린
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), // 전체 화면 사용
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 프로필 정보
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp), // 최상단 + 왼쪽 여백
                verticalAlignment = Alignment.CenterVertically // 세로 가운데 정렬
            ) {
                Image(
                    painter = painterResource(R.drawable.screen_profile_basic_profile_iv),
                    contentDescription = "기본 프로필",
                    modifier = Modifier
                        .padding(end = 10.dp)
                )
                Text(
                    text = stringResource(R.string.mypage_title),
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Text(
                    text = "조은채님",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 오늘 계획 원형 그래프 추가 - 중앙 정렬
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                TodayCircleGraph(
                    name = stringResource(R.string.mypage_today_plan),
                    cleared = 7,
                    total = 10
                )
            }

            // 그래프와 보라색 박스 사이의 여백
            Spacer(modifier = Modifier.height(10.dp))

            // 보라색 박스 - 둥근 모서리
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(120.dp), // 직접 높이 설정
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = SubPurple // 보라색 배경
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 첫 번째 - 완료한 강의
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.screen_profile_completed_iv),
                            contentDescription = "완료한 강의",
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.mypage_completed_lecture),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "5개",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 18.sp
                        )
                    }

                    // 두 번째 - 연속 일수
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.screen_profile_sequence_iv),
                            contentDescription = "연속 n일째",
                        )
                        Text(
                            text = stringResource(R.string.mypage_sequence_day),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "3일째",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 18.sp
                        )
                    }

                    // 세 번째 - 수강중인 강의
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.screen_profile_course_iv),
                            contentDescription = "수강 중인 강의",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.mypage_current_lecture),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "3개",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // 보라색 박스와 회색 테두리 박스 사이의 여백
            Spacer(modifier = Modifier.height(20.dp))

            // 회색 테두리 박스 - 둥근 모서리 (배경색 없음)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(60.dp), // 직접 높이 설정
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 왼쪽 텍스트
                    Text(
                        text = "완료한 강의 보기",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )

                    IconButton(onClick = {}, modifier = Modifier.padding(start = 5.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                            contentDescription = "Edit Mode"
                        )
                    }

                    Text(
                        buildAnnotatedString {
                            append("총 ")
                            withStyle(style = SpanStyle(color = SubPurple)) { // 원하는 색상 코드
                                append("5개")
                            }
                            append("를 완강했어요!")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray // 나머지 텍스트 색상
                    )
                }
            }
        }
    }
}

@Composable
fun TodayCircleGraph(name: String, cleared: Int, total: Int) {
    val animatedValue = remember { Animatable(0f) }

    // 원의 70%는 252도 (360 × 0.7)
    val fullArcAngle = 252f
    // 시작 각도는 144도 (시작각 + 끝각 = 360, 끝각이 252도이므로)
    val startAngle = 144f

    val targetValue = if (total > 0) {
        (cleared.toFloat() / total.toFloat()) * fullArcAngle
    } else {
        0f
    }

    LaunchedEffect(targetValue) {
        animatedValue.snapTo(0f)
        animatedValue.animateTo(
            targetValue = targetValue,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        )
    }

    Canvas(
        modifier = Modifier.size(180.dp, 180.dp) // 원형 그래프용 정사각형 크기
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width * 0.4f
        // 그래프 두께 더 두껍게 변경
        val arcStrokeWidth = 50f

        // 배경 원호
        drawArc(
            color = LightGray5,
            startAngle = startAngle,
            sweepAngle = fullArcAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // 진행 원호
        val progressAngle = animatedValue.value
        drawArc(
            color = MainPurple,
            startAngle = startAngle,
            sweepAngle = progressAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // 텍스트를 원의 중앙에 배치
        drawContext.canvas.nativeCanvas.drawText(
            name,
            center.x,
            center.y - 35, // 약간 위에 표시
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 30f
            }
        )

        // 백분율로 표시
        val percentage = if (total > 0) {
            (cleared.toFloat() / total.toFloat() * 100).toInt()
        } else { 0 }

        drawContext.canvas.nativeCanvas.drawText(
            "$percentage%",
            center.x,
            center.y + 35, // 이름 아래에 표시
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 72f
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                isFakeBoldText = true // 추가적인 볼드 효과
            }
        )

        // 원호 끝에 도넛 모양 인디케이터 추가 - 진행률이 0보다 클 때만 표시
        if (progressAngle > 0f) {
            // 인디케이터 위치 계산
            val endAngleRadians = Math.toRadians((startAngle + progressAngle).toDouble())
            val indicatorX = center.x + radius * kotlin.math.cos(endAngleRadians).toFloat()
            val indicatorY = center.y + radius * kotlin.math.sin(endAngleRadians).toFloat()

            // 인디케이터의 외부 원 그리기
            val indicatorOuterRadius = arcStrokeWidth * 0.7f
            drawCircle(
                color = MainPurple,
                radius = indicatorOuterRadius,
                center = Offset(indicatorX, indicatorY)
            )

            // 인디케이터의 내부 흰색 원 그리기 (도넛 모양을 만들기 위함)
            drawCircle(
                color = androidx.compose.ui.graphics.Color.White,
                radius = indicatorOuterRadius * 0.5f,
                center = Offset(indicatorX, indicatorY)
            )
        }
    }
}