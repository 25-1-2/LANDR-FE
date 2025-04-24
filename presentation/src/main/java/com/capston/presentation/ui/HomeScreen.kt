package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.home.LectureProgressResponse
import com.capston.domain.response.home.LessonScheduleResponse
import com.capston.presentation.R
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainBlue
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.viewmodel.HomeViewModel
import com.capston.presentation.viewmodel.PlanViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition"
)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, planViewModel: PlanViewModel) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val homeState by homeViewModel.getDistinctHome.collectAsState()

    // 나의 학습 현황
    val totalCompletedLessons = homeState.userProgress.totalCompletedLessons // 들은 강의 개수
    val totalLessons = homeState.userProgress.totalLessons // 전체 강의 개수

    // 오늘의 강의
    var todayLessonList = homeState.todaySchedule.lessonSchedules
    var todayTotalLesson = homeState.todaySchedule.totalLessons
    var todayTotalDuration = homeState.todaySchedule.totalDuration

    val lectureProgressList = homeState.userProgress.lectureProgress
    val patchData by planViewModel.patchPlanName.collectAsState()

    // ModalBottomSheet의 boolean 상태를 기억
    var isBottomSheetVisible by rememberSaveable { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = backgroundGray,
                shadowElevation = 10.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundGray)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, // 세로로 정렬
                            horizontalArrangement = Arrangement.SpaceBetween, // 양 끝에 배치
                            modifier = Modifier.fillMaxWidth() // Row를 최대 너비로 설정
                        ) {
                            Text(
                                text = stringResource(R.string.home_status),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                            )
                            Text(
                                text = stringResource(R.string.home_edit),
                                color = MainPurple,
                                modifier = Modifier
                                    .padding(top = 25.dp, end = 20.dp)
                                    .clickable {
                                        // 편집 버튼 클릭 시 동작
                                        isBottomSheetVisible = true // 편집 버튼 클릭 시 bottom sheet 열기
                                    }
                            )
                        }

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth(),

                            ) {
                            item {
                                CircleGraph("전체", totalCompletedLessons, totalLessons)
                            }

                            items(lectureProgressList) { item ->
                                Spacer(modifier = Modifier.width(16.dp)) // 그래프 간격 추가

                                // PATCH 요청 응답을 받아서 name 업데이트
                                val currentLectureName = if (item.planId == patchData.planId) {
                                    patchData.lectureAlias
                                } else {
                                    item.lectureAlias
                                }
                                CircleGraph(
                                    name = currentLectureName,
                                    cleared = item.completedLessons,
                                    total = item.totalLessons
                                )
                            }
                        }
                    }
                }
            }

            Divider(
                color = LightGray,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp)) // 그래프와 강의 목록 사이 간격 추가

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "⭐ 오늘의 강의",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .border(width = 1.dp, color = LightGray40, shape = RoundedCornerShape(8.dp))
                        .background(color = Transparent, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 강의 수 아이콘 + 텍스트
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.home_screen_total_count_iv), // 강의 아이콘
                                contentDescription = "총 강의 수",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "총 ${todayTotalLesson}강",
                                fontSize = 14.sp,
                                color = LightGray60
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 시간 아이콘 + 텍스트
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.home_screen_total_duration_iv), // 시간 아이콘
                                contentDescription = "총 시간",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "약 ${todayTotalDuration}분",
                                fontSize = 14.sp,
                                color = LightGray60
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (todayLessonList != null) {
                ModifiedLessonList(homeViewModel, 330, todayLessonList)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize().padding(bottom = 50.dp), // 화면 전체를 차지하도록 설정
                    verticalArrangement = Arrangement.Center, // 세로 중앙 정렬
                    horizontalAlignment = Alignment.CenterHorizontally // 가로 중앙 정렬
                ) {
                    Image(
                        painter = painterResource(R.drawable.home_screen_empty),
                        contentDescription = "과목명",
                        modifier = Modifier.size(100.dp)
                    )
                    Text(
                        text = "오늘 강의가 없어요",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        color = LightGray60
                    )
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = "계획 생성하러 가기",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MainPurple,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(context, SearchActivity::class.java)
                                context.startActivity(intent)
                            }
                    )
                }
            }

        }
    }

    // 바텀 시트
    if (isBottomSheetVisible) {

        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false }
        ) {
            CustomBottomSheetDialog(
                title = "강의 목록",
                description = "수강 중인 강의를 선택하세요.",
                modalBottomSheetState = modalBottomSheetState,
                onDismiss = { isBottomSheetVisible = false },
                lectureProgressList = lectureProgressList?: emptyList(),
                planViewModel = planViewModel,
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBottomSheetDialog(
    title: String,
    description: String,
    modalBottomSheetState: SheetState,
    onDismiss: () -> Unit,
    lectureProgressList: List<LectureProgressResponse>,
    planViewModel: PlanViewModel,
) {
    val scope = rememberCoroutineScope()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var errorMessage by remember { mutableStateOf("") }
    var vibrationTrigger by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = bottomPadding)
            .fillMaxWidth()
            .height(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
        ) {
            // Title 중앙 정렬
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // 뒤로가기 텍스트 + 아이콘 (우측 정렬)
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        scope.launch {
                            modalBottomSheetState.hide()
                        }.invokeOnCompletion {
                            onDismiss()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "뒤로가기",
                    color = MainPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = MainPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            textAlign = TextAlign.Center,
            style = TextStyle(
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LectureList(
                lectureProgressList = lectureProgressList,
                planViewModel = planViewModel,
            )
        }
    }
}

@Composable
fun LectureList(
    lectureProgressList: List<LectureProgressResponse>,
    planViewModel: PlanViewModel,
) {
    Column {
        lectureProgressList.forEachIndexed { index, lecture ->
            var isEditing by remember { mutableStateOf(false) } // 수정 모드 여부
            var showError by remember { mutableStateOf(false) }  // 오류 메시지를 표시할지 여부
            var aliasState by remember { mutableStateOf(lecture.lectureAlias) } // 강의 별칭 상태

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (isEditing) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = aliasState,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 8) {
                                        showError = false // 오류 숨기기
                                        aliasState = newValue

                                    } else {
                                        showError = true // 오류 표시
                                    }
                                },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = if (aliasState.length == 8) Color.Red else MainBlue,
                                    unfocusedBorderColor = if (showError) Color.Red else MainBlue,
                                    textColor = LightGray60,
                                ),
                                textStyle = TextStyle(fontSize = 14.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            Button(
                                onClick = {
                                    isEditing = false
                                    planViewModel.patchPlanName(
                                        lecture.planId,
                                        PatchPlanDto(lectureAlias = aliasState)
                                    )
                                    lecture.lectureAlias = aliasState
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MainBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                            ) {
                                Text(text = "완료", fontSize = 14.sp)
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            // 글자 수 표시
                            Text(
                                text = "${aliasState.length} / 8(자)",
                                color = if (aliasState.length == 8) Color.Red else Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )

                            // 강의명 표시
                            Text(
                                text = lecture.lectureName,
                                color = LightGray60,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = aliasState)
                        Text(
                            text = lecture.lectureName,
                            fontSize = 12.sp,
                            color = LightGray60
                        )
                    }

                    // 수정 버튼
                    IconButton(
                        onClick = {
                            isEditing = true // 수정 모드로 전환
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_screen_edit_iv),
                            contentDescription = "Edit Mode"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckBox(isChecked: Boolean, onCheckedChange: () -> Unit) {
    IconButton(
        onClick = onCheckedChange,
        modifier = Modifier
            .size(40.dp) // 이미지 버튼 크기 설정
            .padding(end = 16.dp) // 이미지와 텍스트 간의 간격 설정
    ) {
        val imageRes = if (isChecked) R.drawable.home_screen_check_on
        else R.drawable.home_screen_check_off

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Lecture Icon"
        )
    }
}

@Composable
fun ModifiedLessonList(
    homeViewModel: HomeViewModel,
    maxHeight: Int,
    todayLessonList: List<LessonScheduleResponse>,
    isExpanded: Boolean = true // 확장 상태 여부를 매개변수로 받음
) {
    // 제스처 차단을 위한 InputTransformer 생성
    val inputModifier = Modifier.pointerInput(Unit) {
        detectVerticalDragGestures { _, _ ->
            // 아무것도 하지 않음으로써 드래그 제스처를 소비만 함
        }
    }

    // 스크롤 가능한 LazyColumn 사용
    LazyColumn(
        modifier = Modifier
            .padding(start = 30.dp)
            .fillMaxWidth()  // 너비 꽉 채우기
            .fillMaxHeight() // 가능한 한 높이 모두 사용
            // 확장 상태일 때만 스크롤 가능하도록 설정
            .then(
                if (!isExpanded) {
                    Modifier.disableScrolling()
                } else {
                    Modifier
                }
            )
            // 스크롤 가능하지만 드래그는 불가능하도록 설정
            .nestedScroll(remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        // 드래그를 위한 스크롤은 차단
                        return if (source == NestedScrollSource.Drag) {
                            available // 드래그 소비
                        } else {
                            Offset.Zero // 다른 스크롤은 통과
                        }
                    }
                }
            })
    ) {
        items(todayLessonList) { lesson ->
            var isChecked by remember { mutableStateOf(lesson.completed) }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = true,
                        onClick = { /* 클릭만 허용 */ }
                    )
            ) {
                CheckBox(
                    isChecked = isChecked,
                    onCheckedChange = {
                        homeViewModel.patchLessonSchedulesCheckToggle(lesson.id)
                        isChecked = !isChecked
                    }
                )

                // 텍스트 + 시간 박스를 수평으로 정렬
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // 왼쪽 텍스트, 오른쪽 박스 정렬
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = lesson.lessonTitle,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp
                        )
                        Text(
                            text = lesson.lectureName,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightGray60,
                            fontSize = 14.sp
                        )
                    }

                    // 오른쪽 시간 박스
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .border(
                                width = 1.dp,
                                color = MainPurple,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = Transparent, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${lesson.adjustedDuration}분",
                            fontSize = 12.sp,
                            color = MainPurple
                        )
                    }
                }
            }

        }
    }
}

// 스크롤 비활성화 확장 함수
fun Modifier.disableScrolling() = composed {
    val clipModifier = clip(RectangleShape)
    clipModifier.pointerInput(Unit) {
        detectVerticalDragGestures { _, _ -> }
    }
}

@Composable
fun CircleGraph(name: String, cleared: Int, total: Int) {
    val animatedValue = remember { Animatable(0f) }

    val targetValue = if (total > 0) {
        (cleared.toFloat() / total.toFloat()) * 360f
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
        modifier = Modifier.size(150.dp)
    ) {
        val sizeArc = size / 1.3F
        val arcStrokeWidth = 30f

        // 내부 색 채우기
        drawCircle(
            color = Color.White,
            radius = (sizeArc.minDimension / 2f) - (arcStrokeWidth / 2f),
            center = center
        )

        drawArc(
            color = LightGray40,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset((size.width - sizeArc.width) / 2f, (size.height - sizeArc.height) / 2f),
            size = sizeArc,
            style = Stroke(width = arcStrokeWidth)
        )

        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(MainPurple, MainPurple),
                start = Offset.Zero,
                end = Offset.Infinite,
            ),
            startAngle = 270f,
            sweepAngle = animatedValue.value,
            useCenter = false,
            topLeft = Offset(
                (size.width - sizeArc.width) / 2f,
                (size.height - sizeArc.height) / 2f
            ),
            size = sizeArc,
            style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        drawContext.canvas.nativeCanvas.drawText(
            name,
            size.width / 2,
            size.height / 2,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "${cleared}/${total}",
            size.width / 2,
            size.height / 2 + 70,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textAlign = android.graphics.Paint.Align.CENTER
                textSize = 50f
            }
        )
    }
}

