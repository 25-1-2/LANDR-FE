package com.capston.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.response.enum_class.Subject
import com.capston.presentation.R
import com.capston.presentation.theme.CoolGray
import com.capston.presentation.theme.DustyRose
import com.capston.presentation.theme.LavenderGray
import com.capston.presentation.theme.LightGray5
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.MutePurple
import com.capston.presentation.theme.SkyLavender
import com.capston.presentation.theme.SoftPink
import com.capston.presentation.theme.SubPurple
import com.capston.presentation.theme.WarmBeige

// 마이페이지 스크린
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
    // 완료한 강의 및 공부 시간 컴포넌트의 확장 상태 관리
    var isLecturesExpanded by remember { mutableStateOf(false) }
    var isStudyTimeExpanded by remember { mutableStateOf(true) } // 기본값을 true로 설정하여 처음에는 펼쳐진 상태로 시작

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // 전체 화면 사용
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
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                val lectures = listOf(
                    "Java 프로그래밍 기초",
                    "Kotlin 입문",
                    "Android 개발 입문",
                    "Jetpack Compose 기초",
                    "UI/UX 디자인 기초"
                )

                // isLecturesExpanded 상태를 CompletedLecturesToggle로 전달
                CompletedLecturesToggle(
                    lectures = lectures,
                    isExpanded = isLecturesExpanded,
                    onToggle = { isLecturesExpanded = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 내 공부 기록 통계 제목
            Text(
                text = "내 공부 기록 통계",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Start)
            )

            // 회색 테두리 박스 - 둥근 모서리 (배경색 없음)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                // 접기/펼치기 기능이 있는 SubjectDistributionGraph 컴포넌트 호출
                SubjectDistributionGraph(
                    isExpanded = isStudyTimeExpanded,
                    onToggle = { isStudyTimeExpanded = it }
                )
            }
        }
    }
}

@Composable
fun CompletedLecturesToggle(
    lectures: List<String>,
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 헤더 부분 - 항상 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isExpanded)
                        Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                    else
                    // 접혔을 때는 Card 높이(60dp)에 맞춰 중앙 정렬
                        Modifier
                            .padding(horizontal = 24.dp)
                            .height(60.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
        ) {
            // 왼쪽 텍스트
            Text(
                text = "완료한 강의 보기",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            // 토글 버튼
            IconButton(
                onClick = { onToggle(!isExpanded) },
                modifier = Modifier.size(24.dp)
            ) {
                // 회전 애니메이션 추가
                val rotationState by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    label = "rotationAnimation"
                )

                Image(
                    painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                    contentDescription = if (isExpanded) "접기" else "펼치기",
                    modifier = Modifier.rotate(rotationState)
                )
            }

            // 가운데 공간
            Spacer(modifier = Modifier.weight(1f))

            // 완강 정보 텍스트
            Text(
                buildAnnotatedString {
                    append("총 ")
                    withStyle(style = SpanStyle(color = SubPurple)) {
                        append("${lectures.size}개")
                    }
                    append("를 완강했어요!")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // 확장된 경우 강의 목록 표시
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                lectures.forEachIndexed { index, lecture ->
                    LectureItem(
                        lecture = lecture,
                        isLastItem = index == lectures.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun LectureItem(lecture: String, isLastItem: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* 강의 상세 페이지로 이동하는 로직 추가 가능 */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 체크 마크 이미지 (보라색 배경의 원형 체크 아이콘)
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = MainPurple,
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.screen_profile_completed_iv),
                    contentDescription = "완료됨",
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 강의 제목
            Text(
                text = lecture,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            // 오른쪽 여백을 채우는 스페이서
            Spacer(modifier = Modifier.weight(1f))

            // 오른쪽 화살표 (옵션)
            Image(
                painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                contentDescription = "강의 상세보기",
                modifier = Modifier.size(16.dp)
            )
        }

        // 마지막 아이템이 아닌 경우에만 구분선 추가
        if (!isLastItem) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LightGray5)
            )
        }
    }
}

// Canvas 컨텍스트 내에서 말풍선 그리기 함수
private fun DrawScope.drawBubbleWithText(text: String, position: Offset) {
    // 말풍선 배경
    val bubbleWidth = 50.dp.toPx()
    val bubbleHeight = 30.dp.toPx()
    val cornerRadius = 10.dp.toPx()

    drawRoundRect(
        color = Color.White,
        topLeft = Offset(position.x - bubbleWidth / 2, position.y - bubbleHeight / 2),
        size = Size(bubbleWidth, bubbleHeight),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )

    // 말풍선 꼬리 부분 (삼각형)
    val trianglePath = Path().apply {
        val tipX = position.x
        val tipY = position.y + bubbleHeight / 2 + 5.dp.toPx()

        moveTo(tipX, tipY)
        lineTo(tipX - 5.dp.toPx(), position.y + bubbleHeight / 2)
        lineTo(tipX + 5.dp.toPx(), position.y + bubbleHeight / 2)
        close()
    }

    drawPath(
        path = trianglePath,
        color = Color.White
    )

    // 텍스트 그리기
    drawContext.canvas.nativeCanvas.drawText(
        text,
        position.x,
        position.y + 5.dp.toPx(), // 약간의 수직 중앙 정렬 조정
        android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 12.sp.toPx()
            isFakeBoldText = true
        }
    )
}

@Composable
fun SubjectDistributionGraph(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val rawSubjects = listOf(
        SubjectData(Subject.ENG, 0, MainPurple),
        SubjectData(Subject.UNIV, 0, MutePurple),
        SubjectData(Subject.KOR, 5, SubPurple),
        SubjectData(Subject.SCI, 5, WarmBeige),
        SubjectData(Subject.SOC, 5, SoftPink),
        SubjectData(Subject.HIST, 10, LavenderGray),
        SubjectData(Subject.LANG2, 5, DustyRose),
        SubjectData(Subject.MATH, 5, CoolGray),
        SubjectData(Subject.VOC, 5, SkyLavender),
    )

    //  0시간인 과목 제거
    val subjects = rawSubjects.filter { it.hours > 0 }

    // 총 공부 시간
    val totalHours = subjects.sumOf { it.hours }

    // 각 과목의 각도 계산 (비율 * 360도)
    val angles = subjects.map { (it.hours.toFloat() / totalHours) * 360f }

    // 각 과목의 퍼센트 계산
    val percentages = subjects.map { (it.hours.toFloat() / totalHours) * 100f }

    // 가장 많은 시간을 투자하는 과목 찾기
    val maxHours = subjects.maxOf { it.hours }
    val topSubjects = subjects.filter { it.hours == maxHours }

    // 가장 많은 시간을 투자하는 과목들의 이름을 쉼표로 구분하여 문자열 생성
    val topSubjectsText = topSubjects.joinToString(", ") { it.subject.label }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start // 전체 컬럼을 왼쪽 정렬로 변경
    ) {
        // 제목 행 (아이콘 추가)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isExpanded) 20.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Image(
                painter = painterResource(id = R.drawable.screen_profile_study_time_iv),
                contentDescription = "공부 시간 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp)
            )

            // 제목
            Text(
                text = "공부 시간",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(end = 5.dp)
            )

            // 오른쪽 화살표 - 클릭 가능한 토글 버튼으로 변경
            IconButton(
                onClick = { onToggle(!isExpanded) },
                modifier = Modifier.size(24.dp)
            ) {
                // 회전 애니메이션 추가
                val rotationState by animateFloatAsState(
                    targetValue = if (isExpanded) 90f else 0f,
                    label = "rotationAnimation"
                )

                Image(
                    painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                    contentDescription = if (isExpanded) "접기" else "펼치기",
                    modifier = Modifier.rotate(rotationState)
                )
            }

            // 나머지 공간 채우기
            Spacer(modifier = Modifier.weight(1f))

            // 총 공부 시간 표시 (펼치기 상태에 관계없이 표시)
            Text(
                text = "총 ${totalHours}시간",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        // 회색 테두리 박스 - 중앙 정렬로 변경
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(10.dp),
                shape = RoundedCornerShape(5.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                // 완강 정보 텍스트
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = SubPurple)) {
                            append(topSubjectsText)
                        }
                        append("에 가장 많은 시간을 투자하고 있어요")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // 확장된 경우에만 원형 그래프와 범례 표시
        AnimatedVisibility(visible = isExpanded) {
            Column {
                // 원형 그래프는 중앙 정렬
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(240.dp)
                        .padding(top = 40.dp)
                ) {
                    // 도넛 차트 (말풍선 포함)
                    SubjectPieChartWithBubbles(subjects, angles)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 범례 (퍼센트로 표시)
                SubjectLegendWithPercentage(subjects, percentages)
            }
        }
    }
}

@Composable
fun SubjectPieChartWithBubbles(subjects: List<SubjectData>, angles: List<Float>) {
    val animatedValues = List(subjects.size) { remember { Animatable(0f) } }

    // 애니메이션 적용
    LaunchedEffect(subjects) {
        animatedValues.forEachIndexed { index, animatable ->
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = angles[index],
                animationSpec = tween(
                    durationMillis = 1000, // 애니메이션 시간 복원
                    easing = LinearEasing,
                )
            )
        }
    }

    Canvas(
        modifier = Modifier.size(240.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width * 0.6f
        val innerRadius = radius * 0.4f // 도넛 모양을 위한 내부 반지름

        var startAngle = -90f // 12시 방향에서 시작

        // 각 과목별 부분 그리기
        subjects.forEachIndexed { index, subject ->
            val sweepAngle = animatedValues[index].value

            // 과목별 부분 그리기
            drawArc(
                color = subject.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )

            // 말풍선 위치 계산 (각 부분의 중간 지점, 도넛 안쪽)
            if (sweepAngle > 20f) { // 충분히 넓은 영역에만 말풍선 표시
                val midAngle = startAngle + (sweepAngle / 2)
                val midAngleRad = Math.toRadians(midAngle.toDouble())

                // 말풍선 위치 계산 (도넛 차트 바깥쪽과 중심 사이)
                val bubbleDistance = (innerRadius + radius) / 2f
                val bubbleX = center.x + bubbleDistance * kotlin.math.cos(midAngleRad).toFloat()
                val bubbleY = center.y + bubbleDistance * kotlin.math.sin(midAngleRad).toFloat()

                // 말풍선 배경 그리기
                drawBubbleWithText(subject.subject.label, Offset(bubbleX, bubbleY))
            }

            // 다음 시작 각도 업데이트
            startAngle += sweepAngle
        }

        // 도넛 모양을 위한 중앙 흰색 원
        drawCircle(
            color = Color.White,
            radius = innerRadius,
            center = center
        )
    }
}

@Composable
fun SubjectLegendWithPercentage(
    subjects: List<SubjectData>,
    percentages: List<Float>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        subjects.forEachIndexed { index, subject ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 색상 표시
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            subject.color,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 과목명 - 왼쪽 정렬
                Text(
                    text = subject.subject.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                // 퍼센트로 표시 (소수점 1자리)
                Text(
                    text = "${String.format("%.1f", percentages[index])}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

// 과목 데이터 클래스
data class SubjectData(
    val subject: Subject,
    val hours: Int,
    val color: Color
)

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