package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color.Companion.White
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
import com.capston.presentation.theme.LightGray4_40
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.WarmPurple_20
import com.capston.presentation.theme.backgroundGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
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
    val todayLessonList = homeState.todaySchedule.lessonSchedules
    val todayTotalLesson = homeState.todaySchedule.totalLessons
    val todayTotalDuration = homeState.todaySchedule.totalDuration

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
                .verticalScroll(rememberScrollState()), // 전체 화면 사용
        ) {
            // 학습 현황 부분 - UserProgress 표시 영역
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = WarmPurple_20
                ),
                border = BorderStroke(1.dp, color = MainPurple)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 16.dp, horizontal = 10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.screen_profile_learning_status_iv),
                                    contentDescription = null,
                                    tint = Color.Unspecified, // 컬러 적용 원하지 않으면 Unspecified
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(end = 12.dp)
                                )

                                Text(
                                    text = stringResource(R.string.home_status),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }

                            // 편집 버튼
                            Card(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .clickable { /* 편집 기능 */ },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, LightGray60),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Text(
                                    text = stringResource(R.string.home_edit),
                                    color = MainPurple,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            isBottomSheetVisible = true
                                        },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        // lectureProgressList가 비어있지 않은지 확인 후 표시
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            item {
                                CircleGraph("전체", totalCompletedLessons, totalLessons)
                            }

                            // lectureProgressList가 null이 아니고 비어있지 않을 때만 항목 표시
                            if (lectureProgressList.isNotEmpty()) {
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
            }

            // 오늘의 강의 부분 - 카드로 감싸기
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 오늘의 강의 제목 섹션
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
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
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // 강의 수 아이콘 + 텍스트
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.home_screen_total_count_iv),
                                        contentDescription = "총 강의 수",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Unspecified
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "총 ${todayTotalLesson}강",
                                        fontSize = 14.sp,
                                        color = textGray
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // 시간 아이콘 + 텍스트
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.home_screen_total_duration_iv),
                                        contentDescription = "총 시간",
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Unspecified
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "약 ${todayTotalDuration}분",
                                        fontSize = 14.sp,
                                        color = textGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 오늘의 강의 목록 섹션 - todayLessonList에 따라 조건부 렌더링
                    if (todayLessonList != null && todayLessonList.isNotEmpty()) {
                        ModifiedLessonList(homeViewModel, 330, todayLessonList)
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.home_screen_empty),
                                contentDescription = "과목명",
                                modifier = Modifier.size(80.dp)
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

            // 오늘의 강의 Card 다음에 추가
            Spacer(modifier = Modifier.height(5.dp))


            // 시험 디데이와 인강사이트 목록을 가로로 나란히 배치
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 시험 디데이 카드
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MainPurple
                    ),
                    border = BorderStroke(1.dp, color = LightGray4_40)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.screen_profile_learning_status_iv),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp)
                                )

                                Text(
                                    text = "시험 디데이",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // 편집 버튼
                            Card(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .clickable { /* 편집 기능 */ },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, MainPurple),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Text(
                                    text = stringResource(R.string.home_edit),
                                    fontSize = 12.sp,
                                    color = MainPurple,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 디데이 내용 (예시)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "D-7",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "알고리즘 중간고사",
                                fontSize = 14.sp,
                                color = White
                            )
                        }
                    }
                }

                // 인강사이트 목록 카드
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    ),
                    border = BorderStroke(1.dp, color = LightGray60)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.home_screen_total_count_iv),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text = "인강 사이트",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 인강사이트 목록 (각각 카드로 구현)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(1f) // 너비 85%로 제한하여 가운데 정렬 효과
                                .wrapContentSize(), // 내용물에 맞게 크기 조정
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally // 가운데 정렬
                        ) {
                            val etoosPackageName = stringResource(R.string.package_etoos)
                            val megaPackageName = stringResource(R.string.package_megastudy)
                            val mimacPackageName = stringResource(R.string.package_mimac)

                            // 이투스 카드
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 앱으로 이동 시도
                                        openAppOrPlayStore(context, etoosPackageName)
                                    },
                                colors = CardDefaults.cardColors(containerColor = WarmPurple_20.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center // 중앙 정렬
                                ) {
                                    Text(
                                        text = "이투스",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // 메가스터디 카드
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 앱으로 이동 시도
                                        openAppOrPlayStore(context, megaPackageName)
                                    },
                                colors = CardDefaults.cardColors(containerColor = WarmPurple_20.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center // 중앙 정렬
                                ) {
                                    Text(
                                        text = "메가스터디",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            // 대성마이맥 카드
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // 앱으로 이동 시도
                                        openAppOrPlayStore(context, mimacPackageName)
                                    },
                                colors = CardDefaults.cardColors(containerColor = WarmPurple_20.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center // 중앙 정렬
                                ) {
                                    Text(
                                        text = "대성마이맥",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 바텀 시트
    if (isBottomSheetVisible) {
        ModalBottomSheet(
            sheetState = modalBottomSheetState,
            onDismissRequest = { isBottomSheetVisible = false },
            containerColor = White,
            dragHandle = null
        ) {
            CustomBottomSheetDialog(
                title = "강의 목록",
                description = "강의 별칭을 작성해 주세요.",
                modalBottomSheetState = modalBottomSheetState,
                onDismiss = { isBottomSheetVisible = false },
                lectureProgressList = lectureProgressList ?: emptyList(),
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White) // 전체 배경
            .navigationBarsPadding() // 소프트 키패드 영역까지 패딩 적용
            .imePadding() // 키보드 올라올 때 고려
    ) {
        // 커스텀 drag handle
        Box(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 8.dp)
                .size(width = 36.dp, height = 4.dp)
                .background(materialGray, RoundedCornerShape(2.dp))
                .align(Alignment.CenterHorizontally)
        )

        // 컨텐츠 영역
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(268.dp - 12.dp - 8.dp), // 전체 높이에서 drag handle 높이 제외
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 제목 박스
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LectureList(
                    lectureProgressList = lectureProgressList,
                    planViewModel = planViewModel,
                )
            }
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
                                    focusedBorderColor = if (aliasState.length == 8) Color.Red else MainPurple,
                                    unfocusedBorderColor = if (showError) Color.Red else MainPurple,
                                    textColor = textGray,
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
                                    containerColor = MainPurple,
                                    contentColor = White
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
                                color = if (aliasState.length == 8) Color.Red else textGray,
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
                CustomCheckBox (
                    isChecked = isChecked,
                    onCheckedChange = {
                        homeViewModel.patchLessonSchedulesCheckToggle(lesson.id)
                        isChecked = !isChecked
                    }
                )

                // 텍스트 + 시간 박스를 수평으로 정렬
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween, // 왼쪽 텍스트, 오른쪽 박스 정렬
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
            color = White,
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

// 앱 실행 또는 Play Store 이동을 위한 함수
fun openAppOrPlayStore(context: Context, packageName: String) {
    var intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent==null) {
        val link = "https://play.google.com/store/apps/details?id=$packageName"
        intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }
        context.startActivity(intent)
        return
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
