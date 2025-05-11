package com.capston.presentation.ui

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.enum_class.DayOfWeek
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.mypage.CompletedPlanDto
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.response.mypage.SubjectAchievementDto
import com.capston.domain.usecase.mypage.GetMonthlyStatisticsUseCase
import com.capston.presentation.R
import com.capston.presentation.theme.CoolGray
import com.capston.presentation.theme.DustyRose
import com.capston.presentation.theme.LavenderGray
import com.capston.presentation.theme.LightGray5
import com.capston.presentation.theme.LightGray60
import com.capston.presentation.theme.LightPurple
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.MutePurple
import com.capston.presentation.theme.SkyLavender
import com.capston.presentation.theme.SoftPink
import com.capston.presentation.theme.SubPurple
import com.capston.presentation.theme.WarmPurple
import com.capston.presentation.theme.chipGray
import com.capston.presentation.theme.materialGray
import com.capston.presentation.theme.textGray
import com.capston.presentation.viewmodel.LoginViewModel
import com.capston.presentation.viewmodel.MyPageViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// 마이페이지 스크린
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(loginViewModel: LoginViewModel, myPageViewModel: MyPageViewModel) {
    LaunchedEffect(Unit) {
        myPageViewModel.getDistinctMyPage()
    }

    val mypageState by myPageViewModel.getDistinctMyPage.collectAsState()
    val myStatisticsState by myPageViewModel.getMyPageStatistics.collectAsState()


    // 완료한 강의 및 공부 시간 컴포넌트의 확장 상태 관리
    var isLecturesExpanded by remember { mutableStateOf(false) }
    var isStudyTimeExpanded by remember { mutableStateOf(true) } // 기본값을 true로 설정하여 처음에는 펼쳐진 상태로 시작
    var isStudyStatusExpanded by remember { mutableStateOf(true) }
    var isSubjectExpanded by remember { mutableStateOf(true) }

    var showEditDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    // 다이얼로그에서 편집 중인 이름
    var editUserName by remember { mutableStateOf("") }
    var displayName  = mypageState.userName

    if (showEditDialog) {
        EditNameDialog(
            initialName = displayName,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                if (newName.length < 3 || newName.length > 9) {
                    showWarningDialog = true
                } else {
                    Log.d("ProfileScreen", "Submitting name change: $newName")
                    loginViewModel.patchUserName(UserNameDto(name = newName))
                    showEditDialog = false
                }
            }
        )
    }

    // 경고 다이얼로그
    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("이름 입력 오류") },
            text = { Text("이름은 3자 이상 9자 이하로 입력해주세요.") },
            confirmButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("확인")
                }
            }
        )
    }

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
                    text = "${displayName}님",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.weight(1f))

                // 내 정보 수정 텍스트 버튼
                Text(
                    text = "내 정보 수정",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        editUserName = displayName  // 현재 이름으로 초기화
                        showEditDialog = true    // 다이얼로그 표시
                    }
                )

                // 토글 버튼
                IconButton(
                    onClick = {
                        editUserName = displayName  // 현재 이름으로 초기화
                        showEditDialog = true    // 다이얼로그 표시
                    },
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                        contentDescription = "내 정보 수정"
                    )
                }
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
                    cleared = mypageState.completedLectureCount,
                    total = mypageState.todayTotalLessonCount
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
                            text = "${mypageState.completedLectureCount}개",
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
                            text = "${mypageState.studyStreak} 일째",
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
                            text = "${mypageState.subjectAchievementList.size} 개",
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

                // isLecturesExpanded 상태를 CompletedLecturesToggle로 전달
                CompletedLecturesToggle(
                    lectures = mypageState.completedPlanList,
                    isExpanded = isLecturesExpanded,
                    onToggle = { isLecturesExpanded = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 내 공부 기록 통계 섹션 (제목 + 달력 선택 박스)
            CalendarSelectionRow(myPageViewModel)

            // 회색 테두리 박스 - 둥근 모서리 (배경색 없음)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
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

            // 회색 테두리 박스 - 둥근 모서리 (배경색 없음)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                // 접기/펼치기 기능이 있는 WeeklyStudyStatisticsGraph 컴포넌트 호출
                WeeklyStudyStatisticsGraph(
                    isExpanded = isStudyStatusExpanded,
                    onToggle = { isStudyStatusExpanded = it }
                )
            }

            // 회색 테두리 박스 - 둥근 모서리 (배경색 없음)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                shape = RoundedCornerShape(10.dp), // 둥근 모서리
                colors = CardDefaults.cardColors(
                    containerColor = Color.White // 흰색 배경
                ),
                border = BorderStroke(1.dp, color = LightGray60)
            ) {
                SubjectAchievementGraph(
                    isExpanded = isSubjectExpanded,
                    onToggle = { isSubjectExpanded = it },
                    mypageState
                )
            }

            // 하단 여백
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// 이름 수정 다이얼로그 컴포넌트 - 달력 디자인 스타일 적용
@Composable
fun EditNameDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }

    // 포커스 요청을 위한 FocusRequester
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .width(240.dp), // 달력 팝업과 동일한 너비
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // 헤더 부분 - 달력 팝업의 년/월 선택 헤더와 유사한 스타일
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "이름 수정",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 구분선 추가 (달력 팝업처럼)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(LightGray5)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 이름 입력 영역
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("이름을 입력하세요", fontSize = 14.sp, color = Color.Gray) },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = MainPurple,
                        unfocusedIndicatorColor = LightGray60
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 확인/취소 버튼 - 달력 팝업과 동일한 스타일
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // 취소 버튼
                    FilterChip(
                        selected = false,
                        onClick = { onDismiss() },
                        label = {
                            Text(
                                text = "취소",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = chipGray,
                            labelColor = Color.Black
                        ),
                        border = null
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 확인 버튼
                    FilterChip(
                        selected = false,
                        onClick = { onConfirm(name) },
                        label = {
                            Text(
                                text = "확인",
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MainPurple,
                            labelColor = Color.White
                        ),
                        border = null
                    )
                }
            }
        }
    }

    // 다이얼로그가 표시되면 자동으로 텍스트 필드에 포커스 요청
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSelectionRow(myPageViewModel: MyPageViewModel) {
    var isCalenderExpanded by remember { mutableStateOf(false) }
    // 제목과 날짜 선택 박스를 가로로 배치
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 내 공부 기록 통계 제목
        Text(
            text = "내 공부 기록 통계",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        // 날짜 선택 박스
        CalendarSelectionBox(
            isExpanded = isCalenderExpanded,
            onToggle = { isCalenderExpanded = it },
            myPageViewModel = myPageViewModel
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarSelectionBox(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    myPageViewModel: MyPageViewModel
) {
    // 현재 날짜 가져오기
    val currentDate = LocalDate.now()
    val currentYear = currentDate.format(DateTimeFormatter.ofPattern("yyyy"))
    val currentMonth = currentDate.format(DateTimeFormatter.ofPattern("MM"))

    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }

    myPageViewModel.getMonthlyStatistics("${selectedYear}-${selectedMonth}")

    // 달력 표시 여부
    var showCalendar by remember { mutableStateOf(false) }

    // 상대적인 위치 계산을 위한 박스
    Box(
        modifier = Modifier.zIndex(if (showCalendar) 1f else 0f)
    ) {
        // 날짜 선택 박스
        Card(
            modifier = Modifier
                .width(100.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, color = LightGray60)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 선택된 날짜 표시
                Text(
                    text = "$selectedYear.$selectedMonth",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(4.dp))

                // 토글 버튼
                IconButton(
                    onClick = {
                        // 토글 버튼 클릭 시 달력 표시/숨김 토글
                        showCalendar = !showCalendar
                        onToggle(!isExpanded)
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                        contentDescription = if (isExpanded) "접기" else "펼치기",
                    )
                }
            }
        }

        // 달력 팝업 - Popup 컴포넌트를 사용하여 절대 위치에 표시
        if (showCalendar) {
            Popup(
                alignment = Alignment.TopEnd,
                properties = PopupProperties(focusable = true),
                onDismissRequest = { showCalendar = false }
            ) {
                CalendarPopup(
                    initialYear = selectedYear,
                    initialMonth = selectedMonth,
                    onDateSelected = { year, month ->
                        selectedYear = year
                        selectedMonth = month
                        showCalendar = false
                    },
                    onDismiss = { showCalendar = false }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPopup(
    initialYear: String,
    initialMonth: String,
    onDateSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    // 달력 상태 - 년도 범위 확장
    val currentYear = LocalDate.now().year
    val years = (currentYear - 5..currentYear + 5).map { it.toString() }
    val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

    var selectedYear by remember { mutableStateOf(initialYear) }
    var selectedMonth by remember { mutableStateOf(initialMonth) }

    Card(
        modifier = Modifier
            .width(240.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // 명시적으로 흰색 배경 설정
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .background(Color.White) // 내부 컴포넌트에도 흰색 배경 명시적 설정
                .padding(16.dp)
        ) {
            // 년/월 선택 헤더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 년도 선택
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val currentIndex = years.indexOf(selectedYear)
                            if (currentIndex > 0) {
                                selectedYear = years[currentIndex - 1]
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                            contentDescription = "이전 년도",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(180f) // 왼쪽 방향으로 회전
                        )
                    }

                    Text(
                        text = selectedYear,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            val currentIndex = years.indexOf(selectedYear)
                            if (currentIndex < years.size - 1) {
                                selectedYear = years[currentIndex + 1]
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                            contentDescription = "다음 년도",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 년/월 구분선
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(1.dp)
                        .background(LightGray5)
                )

                // 월 선택
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val currentIndex = months.indexOf(selectedMonth)
                            if (currentIndex > 0) {
                                selectedMonth = months[currentIndex - 1]
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                            contentDescription = "이전 월",
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(180f) // 왼쪽 방향으로 회전
                        )
                    }

                    Text(
                        text = selectedMonth,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            val currentIndex = months.indexOf(selectedMonth)
                            if (currentIndex < months.size - 1) {
                                selectedMonth = months[currentIndex + 1]
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.screen_profile_see_more_iv),
                            contentDescription = "다음 월",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 월 선택 그리드
            MonthGrid(
                selectedMonth = selectedMonth,
                onMonthSelected = { month ->
                    selectedMonth = month
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 확인/취소 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // 취소 버튼
                FilterChip(
                    selected = false,
                    onClick = { onDismiss() },
                    label = {
                        Text(
                            text = "취소",
                            fontSize = 14.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = chipGray,
                        labelColor = Color.Black
                    ),
                    border = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 확인 버튼
                FilterChip(
                    selected = false,
                    onClick = {
                        onDateSelected(selectedYear, selectedMonth)
                    },
                    label = {
                        Text(
                            text = "확인",
                            fontSize = 14.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MainPurple,
                        labelColor = Color.White
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun MonthGrid(
    selectedMonth: String,
    onMonthSelected: (String) -> Unit
) {
    val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // 명시적으로 흰색 배경 설정
    ) {
        for (i in 0 until 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (j in 0 until 4) {
                    val index = i * 4 + j
                    if (index < months.size) {
                        val month = months[index]
                        MonthItem(
                            month = month,
                            isSelected = month == selectedMonth,
                            onSelected = { onMonthSelected(month) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthItem(
    month: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (isSelected) LightPurple else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = month,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MainPurple else Color.Black
        )
    }
}

@Composable
fun CompletedLecturesToggle(
    lectures: List<CompletedPlanDto>,
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
fun LectureItem(lecture: CompletedPlanDto, isLastItem: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                // 강의 제목
                Text(
                    text = "${lecture.platform.label} · ${lecture.teacher}",
                    style = MaterialTheme.typography.bodySmall,
                    color = textGray
                )
                // 강의 제목
                Text(
                    text = lecture.lectureTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }

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
            textSize = 14.sp.toPx()
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
        SubjectData(Subject.SCI, 5, WarmPurple),
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
                fontWeight = FontWeight.Bold
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
                    fontSize = 14.sp,
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
                    fontWeight = FontWeight.Bold,
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
                color = Color.White,
                radius = indicatorOuterRadius * 0.5f,
                center = Offset(indicatorX, indicatorY)
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun WeeklyStudyStatisticsGraph(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit
) {
    // 주차별 학습 시간 데이터
    val weeklyData = listOf(
        WeeklyData("1주", 3),
        WeeklyData("2주", 5),
        WeeklyData("3주", 8),
        WeeklyData("4주", 6),
        WeeklyData("5주", 10)
    )

    // 최대 시간 값
    val maxHours = weeklyData.maxOf { it.hours }

    // 가장 많은 공부를 한 주 찾기
    val mostStudiedWeeks = weeklyData.filter { it.hours == maxHours }
    val mostStudiedWeeksText = mostStudiedWeeks.joinToString(", ") { it.week }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
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
                painter = painterResource(id = R.drawable.screen_profile_learning_status_iv),
                contentDescription = "학습 통계 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp)
            )

            // 제목
            Text(
                text = "학습 통계",
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

            // 평균 공부 시간 표시 (펼치기 상태에 관계없이 표시)
            val averageHours = weeklyData.map { it.hours }.average()
            Text(
                text = "평균 ${String.format("%.1f", averageHours)}시간/주",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
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
                        withStyle(style = SpanStyle(color = MainPurple)) { // 강조색을 MainPurple로 통일
                            append(mostStudiedWeeksText)
                        }
                        append("차에 가장 많은 강의를 수강했어요")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        // 확장된 경우에만 막대 그래프와 범례 표시
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 세로 막대 그래프
                ImprovedWeeklyBarChart(
                    weeklyData = weeklyData,
                    maxHoursValue = maxHours
                )
            }
        }
    }
}

@Composable
fun ImprovedWeeklyBarChart(
    weeklyData: List<WeeklyData>,
    maxHoursValue: Int
) {
    // 애니메이션 값들
    val animatedValues = List(weeklyData.size) {
        remember { Animatable(0f) }
    }

    // 애니메이션 적용
    LaunchedEffect(weeklyData) {
        animatedValues.forEachIndexed { index, animatable ->
            animatable.snapTo(0f)
            animatable.animateTo(
                targetValue = weeklyData[index].hours.toFloat(),
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearEasing,
                    delayMillis = index * 100
                )
            )
        }
    }

    // 차트 높이와 최대 시간
    val chartHeight = 200.dp
    val maxHours = 10f // 고정된 최대값 (0, 5, 10 표시 위해)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 60.dp)
            .padding(start = 8.dp, end = 16.dp)
    ) {
        // 왼쪽 시간 표시 (0시간, 5시간, 10시간)
        Column(
            modifier = Modifier
                .width(40.dp)
                .height(chartHeight),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 시간 표시는 위에서부터 아래로
            Text(
                text = "10시간",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "5시간",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.End
            )

            Text(
                text = "0시간",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // 그래프 영역
        Box(
            modifier = Modifier
                .weight(1f)
                .height(chartHeight)
                .background(Color.White)
        ) {
            // 수평 그리드 라인 (0시간, 5시간, 10시간)
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // 10시간 선 (맨 위)
                drawLine(
                    color = LightGray5,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )

                // 5시간 선 (중간)
                drawLine(
                    color = LightGray5,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 1.dp.toPx()
                )

                // 0시간 선 (맨 아래)
                drawLine(
                    color = LightGray5,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 막대 그래프
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEachIndexed { index, data ->
                    val animatedHeight = animatedValues[index].value
                    val barHeight = (animatedHeight / maxHours) * chartHeight.value

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 가장 높은 막대만 다른 색상
                        val barColor = if (data.hours == maxHoursValue) SubPurple else WarmPurple

                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(barHeight.dp)
                                .background(
                                    color = barColor,
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {}
                    }
                }
            }
        }
    }

    // 주차 레이블
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 40.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weeklyData.forEach { data ->
            Text(
                text = data.week,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

// 주차별 데이터 클래스
data class WeeklyData(
    val week: String,
    val hours: Int
)

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubjectAchievementGraph(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    mypageState: GetDistinctMyPageResponse
) {
    // 현재 날짜 가져오기
    val currentDate = LocalDate.now()
    val currentYear = currentDate.format(DateTimeFormatter.ofPattern("yyyy"))
    val currentMonth = currentDate.format(DateTimeFormatter.ofPattern("MM"))
    val previousMonth = currentDate.minusMonths(2)

    // 과목별 성취 데이터
    val subjectData = mypageState.subjectAchievementList

    // 평균 성취율 계산
    val averageProgress = subjectData
        .map { (it.completedLessons.toFloat() / it.totalLessons) * 100f }
        .average()
        .toFloat()

    // 가장 높은 성취율의 과목 찾기
    val highestSubject = subjectData.maxByOrNull {
        (it.completedLessons.toFloat() / it.totalLessons) * 100f
    }

    val highestPercent = highestSubject?.let {
        (it.completedLessons.toFloat() / it.totalLessons) * 100f
    } ?: 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
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
                contentDescription = "과목별 성취율 아이콘",
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp)
            )

            // 제목
            Text(
                text = "과목별 성취율",
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

            // 평균 성취율 표시 (펼치기 상태에 관계없이 표시)
            Text(
                text = "평균 ${String.format("%.1f", averageProgress)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
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
                // 정보 텍스트
                if (highestSubject != null) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MainPurple)) {
                                append(highestSubject.subject.label)
                            }
                            append("은 약 ${String.format("%.0f", (highestSubject.completedLessons.toFloat() / highestSubject.totalLessons) * 100f)}%로 ")
                            append("가장 높은 성취율을 보이고 있어요")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 확장된 경우에만 과목별 성취율 표시
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 과목별 성취율 진행 바
                SubjectProgressBars(
                    subjectData = subjectData,
                    highestPercent = highestPercent
                )
            }
        }
    }
}

@Composable
fun SubjectProgressBars(
    subjectData: List<SubjectAchievementDto>,
    highestPercent: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        // 각 과목별 진행 바
        subjectData.forEach { subject ->
            // 가장 높은 성취율 과목만 MainPurple 컬러로
            val subjectColor = if ((subject.completedLessons.toFloat() / subject.totalLessons) * 100f == highestPercent) SubPurple else WarmPurple

            SubjectProgressItem(
                subjectAchievement = subject,
                barColor = subjectColor,
                highestPercent = highestPercent
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SubjectProgressItem(
    subjectAchievement: SubjectAchievementDto,
    barColor: Color,
    highestPercent: Float
) {
    val animatedProgress = remember { Animatable(0f) }

    // 애니메이션 적용
    LaunchedEffect(subjectAchievement) {
        animatedProgress.animateTo(
            targetValue = (subjectAchievement.completedLessons.toFloat() / subjectAchievement.totalLessons) * 100f / 100f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 과목명과 수강 기간
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 과목명
            Text(
                text = subjectAchievement.subject.label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            // 수강 기간
            Text(
                text = "${subjectAchievement.startDate} ~ ${subjectAchievement.endDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        // 프로그레스 바 (상대적 위치 계산을 위한 Box)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        ) {
            // 배경 프로그레스 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(LightGray5, RoundedCornerShape(4.dp))
                    .align(Alignment.Center)
            )

            // 진행률 프로그레스 바
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .height(8.dp)
                    .background(barColor, RoundedCornerShape(4.dp))
                    .align(Alignment.CenterStart)
            )

            // 현재 위치 표시 (역삼각형 + 점선)
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val triangleWidth = 12.dp.toPx()
                val triangleHeight = 6.dp.toPx()
                val currentX = size.width * animatedProgress.value

                // 역삼각형 그리기
                val trianglePath = Path().apply {
                    moveTo(currentX, 0f) // 상단 중앙 지점
                    lineTo(currentX - triangleWidth / 2, -triangleHeight) // 좌측 상단
                    lineTo(currentX + triangleWidth / 2, -triangleHeight) // 우측 상단
                    close()
                }

                drawPath(
                    path = trianglePath,
                    color = barColor
                )

                // 점선 그리기
                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)

                drawLine(
                    color = barColor,
                    start = Offset(currentX, 0f),
                    end = Offset(currentX, size.height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = dashPathEffect
                )
            }
        }

        // 진행률 퍼센트 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = "${String.format("%.0f", (subjectAchievement.completedLessons.toFloat() / subjectAchievement.totalLessons) * 100f)}%",
                style = MaterialTheme.typography.bodySmall,
                color = if ((subjectAchievement.completedLessons.toFloat() / subjectAchievement.totalLessons) * 100f == highestPercent) Color.Black else LightGray60,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = "100%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}