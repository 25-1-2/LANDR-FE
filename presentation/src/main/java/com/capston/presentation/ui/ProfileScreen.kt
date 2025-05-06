package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray5
import com.capston.presentation.theme.MainPurple

// 마이페이지 스크린
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp) // 최상단 + 왼쪽 정렬
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.screen_profile_basic_profile_iv),
                    contentDescription = "기본 프로필",
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.mypage_title),
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "조은채님",
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 마이페이지 아래 여백 추가 - 충분한 공간 확보를 위해 여백 늘림
            Spacer(modifier = Modifier.height(20.dp))

            // 오늘 계획 원형 그래프 추가 - 중앙 정렬
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                TodayCircleGraph(
                    name = "오늘 계획",
                    cleared = 7,
                    total = 10
                )
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