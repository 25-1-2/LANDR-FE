package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray40
import com.capston.presentation.theme.LightGray3
import com.capston.presentation.theme.LightGray4
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.MainBlue
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.Purple40
import com.capston.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

val lectures = listOf(
    Pair("1. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair("2. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair( "3. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair( "4. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair( "5. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair( "6. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
    Pair( "7. 함수의 극한과 연속①","2026 현우진의 수분감 - 수학I (공통) 약 14분"),
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {

    // Collecting state from ViewModel
    val distinctHomeState by homeViewModel.getDistinctHome.collectAsState()
    Log.d("home state", distinctHomeState.result.message)

    // ModalBottomSheet의 boolean 상태를 기억
    var isBottomSheetVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 2.dp, color = LightGray4)
                    .background(LightGray3)
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
                            color = LightGray40, // 원하는 색상으로 설정
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
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        items(3) { index -> // TODO 개수 나중에 API로 받아서 수정
                            CircleGraph("전체")
                            Spacer(modifier = Modifier.width(16.dp)) // 그래프 간격 추가
                            CircleGraph("수분감")
                            Spacer(modifier = Modifier.width(16.dp))
                            CircleGraph("믿어봐")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp)) // 🌟 그래프와 강의 목록 사이 간격 추가

            Text(
                text = "⭐ 오늘의 강의 (총 ${lectures.size}강, 약 42분)",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            LessonList(330)
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
                onDismiss = { isBottomSheetVisible = false }
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
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = Modifier
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = bottomPadding)
            .fillMaxWidth()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            textAlign = TextAlign.Start,
            style = TextStyle(
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )
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
            // modalBottomSheetState를 LectureList에 전달
            LectureList(modalBottomSheetState)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                scope.launch {
                    modalBottomSheetState.hide()
                }.invokeOnCompletion {
                    onDismiss()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MainBlue,
                contentColor = Color.White,
                disabledContainerColor = Purple40,
                disabledContentColor = Color.White,
            ),
        ) {
            Text("수정 완료")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LectureList(modalBottomSheetState: SheetState) {
    // 강의 리스트 데이터
    val lectures = remember {
        mutableStateListOf(
            Pair("2026 현우진의 수분감 - 수학I (공통)", "2026 현우진"),
            Pair("2026 현우진의 수분감 - 수학II (공통)", "2026 현우진"),
            Pair("2026 현우진의 수분감 - 수학III (공통)", "2026 현우진"),
        )
    }

    // 강의 체크박스 상태 관리
    val checkedStates = remember { mutableStateListOf<Boolean>(false, false, false) }

    Column {
        lectures.forEachIndexed { index, lecture ->
            var lectureTitle by remember { mutableStateOf(lecture.second) } // 강의 제목을 수정할 변수
            var isEditing by remember { mutableStateOf(false) } // 수정 모드 여부

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(60.dp),
            ) {
                CheckBox()

                if (isEditing) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = lectureTitle,
                                onValueChange = { lectureTitle = it },
                                label = {
                                    Text(
                                        text = "강의 별칭",
                                        fontSize = 10.sp,
                                        color = MainBlue
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Red, // 포커스 되었을 때 테두리 색상
                                    unfocusedBorderColor = MainBlue, // 기본(포커스 안 된) 상태의 테두리 색상
                                    textColor = LightGray60
                                ),
                                textStyle = TextStyle(fontSize = 14.sp)
                            )

                            // 완료 버튼
                            Button(
                                onClick = {
                                    // 완료 버튼 클릭 시 수정된 내용 적용
                                    lectures[index] = lectures[index].first to lectureTitle
                                    isEditing = false  // 수정 모드 종료
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MainBlue,
                                    contentColor = Color.White,
                                ),
                                modifier = Modifier
                                    .padding(start = 16.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "완료",
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // 강의 제목 텍스트가 OutlinedTextField 아래에 보이도록
                        Spacer(modifier = Modifier.height(8.dp)) // 버튼과 텍스트 간격 추가
                        Text(
                            text = lecture.first, // 강의 첫 번째 텍스트
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                } else {
                    // 수정 모드가 아닐 때는 기존 강의 제목을 그대로 표시
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = lectureTitle, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = lecture.first,
                            style = MaterialTheme.typography.bodyMedium,
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
fun CheckBox() {
    var imageState by remember { mutableStateOf(true) }

    IconButton(
        onClick = {
            imageState = !imageState
        },
        modifier = Modifier
            .size(40.dp) // 이미지 버튼 크기 설정
            .padding(end = 16.dp) // 이미지와 텍스트 간의 간격 설정
    ) {
        // 상태에 따라 이미지 변경
        val imageRes = if (imageState) {
            R.drawable.home_screen_check_off // 기본 이미지
        } else {
            R.drawable.home_screen_check_on // 클릭된 이미지
        }

        Image(
            painter = painterResource(id = imageRes), // 상태에 따른 이미지 리소스 설정
            contentDescription = "Lecture Icon"
        )
    }
}

@Composable
fun LessonList(maxHeight: Int) {
    LazyColumn(
        modifier = Modifier.padding(start = 30.dp).heightIn(max = maxHeight.dp) // 최대 높이를 설정하여 스크롤 범위를 제한
    ) {

        // 강의가 없을 경우
        if (lectures.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Text("오늘 강의가 없어요 \uD83D\uDE0A\n" +
                        "푹 쉬고 내일 다시 달려보아요 \uD83C\uDFC3")
            }
        } else {
            // 강의가 있을 경우
            items(lectures) { lecture ->
                Row(
                    verticalAlignment = Alignment.CenterVertically, // 세로로 중앙 정렬
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                ) {

                    CheckBox()
                    Column {
                        Text(lecture.first, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = lecture.second,
                            style = MaterialTheme.typography.bodyLarge,
                            color = LightGray60
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun CircleGraph(name: String) {
    val animatedValue = remember { Animatable(0f) }

    // 특정 값으로 색을 채우는 Animation
    LaunchedEffect(Unit) {
        animatedValue.animateTo(
            targetValue = 100F,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        )
    }

    Canvas(
        modifier = Modifier.size(150.dp)
    ) {
        val sizeArc = size / 1.3F
        drawArc(
            color = Color(0xFFE1E2E9),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = true,
            topLeft = Offset((size.width - sizeArc.width) / 2f, (size.height - sizeArc.height) / 2f),
            size = sizeArc,
            style = Stroke(width = 30f)
        )

        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(
                    MainPurple, MainPurple
                ),
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
            style = Stroke(width = 30f, cap = StrokeCap.Round)
        )

        drawContext.canvas.nativeCanvas.drawText(
            name,  // 텍스트 내용
            size.width / 2,  // X 위치
            size.height / 2,  // Y 위치
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK  // 텍스트 색
                textAlign = android.graphics.Paint.Align.CENTER  // 텍스트 중앙 정렬
                textSize = 50f  // 텍스트 크기
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "1/50",  // 텍스트 내용
            size.width / 2,  // X 위치
            size.height / 2 + 70,  // Y 위치
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK  // 텍스트 색
                textAlign = android.graphics.Paint.Align.CENTER  // 텍스트 중앙 정렬
                textSize = 50f  // 텍스트 크기
            }
        )
    }
}

