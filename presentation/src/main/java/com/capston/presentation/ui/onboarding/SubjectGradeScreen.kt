package com.capston.presentation.ui.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.theme.LightGray4
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray
import kotlinx.coroutines.delay

@Composable
fun SubjectGradeScreen(onSetupComplete: () -> Unit) {
    var subjectGrades by remember { mutableStateOf(listOf(SubjectGrade())) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusedItemId by remember { mutableStateOf<String?>(null) }

    // 과목을 영역별로 정리
    val subjectCategories = listOf(
        SubjectCategory(
            categoryName = "국어 영역",
            subjects = listOf("국어", "언어와매체", "화법과작문")
        ),
        SubjectCategory(
            categoryName = "수학 영역",
            subjects = listOf("수학", "수학Ⅰ", "수학Ⅱ", "미적분", "확률과통계", "기하")
        ),
        SubjectCategory(
            categoryName = "영어 영역",
            subjects = listOf("영어", "영어Ⅰ", "영어Ⅱ", "영어독해와작문")
        ),
        SubjectCategory(
            categoryName = "한국사",
            subjects = listOf("한국사")
        ),
        SubjectCategory(
            categoryName = "과학탐구 영역",
            subjects = listOf(
                "물리학Ⅰ", "물리학Ⅱ",
                "화학Ⅰ", "화학Ⅱ",
                "생명과학Ⅰ", "생명과학Ⅱ",
                "지구과학Ⅰ", "지구과학Ⅱ"
            )
        ),
        SubjectCategory(
            categoryName = "사회탐구 영역",
            subjects = listOf(
                "한국지리", "세계지리",
                "동아시아사", "세계사",
                "생활과윤리", "윤리와사상",
                "정치와법", "경제", "사회·문화"
            )
        ),
        SubjectCategory(
            categoryName = "직업탐구 영역",
            subjects = listOf("농업이해", "농업기초기술", "공업일반", "기초제도", "상업경제", "회계원리", "해양의이해", "수산·해운산업기초", "인간발달", "생활서비스산업의이해")
        )
    )

    // 모든 과목을 하나의 리스트로 통합 (드롭다운용)
    val allSubjects = subjectCategories.flatMap { it.subjects }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 LANDR 로고
            Image(
                painter = painterResource(R.drawable.landr_title_iv),
                contentDescription = "LANDR 로고",
                modifier = Modifier
                    .padding(top = 60.dp, start = 20.dp)
                    .size(80.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 메인 컨텐츠
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "조은채님!\n맞춤형 인강 학습을 시작해볼까요?",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "꼭 맞는 학습 계획을 LANDR가 제안해드릴게요:)",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = textGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 제목
                Text(
                    text = "2. 강의를 추천 받고 싶은 과목과 등급을 알려주세요",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainPurple,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 과목 목록
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(subjectGrades, key = { _, item -> item.id }) { index, subjectGrade ->
                    SubjectGradeCard(
                        subjectGrade = subjectGrade,
                        subjectCategories = subjectCategories,
                        allSubjects = allSubjects,
                        canDelete = subjectGrades.size > 1,
                        isFocused = focusedItemId == subjectGrade.id,
                        onSubjectChange = { newSubject ->
                            subjectGrades = subjectGrades.toMutableList().apply {
                                this[index] = this[index].copy(subject = newSubject)
                            }
                        },
                        onGradeChange = { newGrade ->
                            subjectGrades = subjectGrades.toMutableList().apply {
                                this[index] = this[index].copy(grade = newGrade)
                            }
                        },
                        onGradeTypeChange = { newType ->
                            subjectGrades = subjectGrades.toMutableList().apply {
                                this[index] = this[index].copy(gradeType = newType)
                            }
                        },
                        onDelete = {
                            val deletedIndex = index
                            subjectGrades = subjectGrades.toMutableList().apply {
                                removeAt(index)
                            }

                            coroutineScope.launch {
                                delay(100)

                                if (subjectGrades.isNotEmpty()) {
                                    val targetIndex = if (deletedIndex >= subjectGrades.size) {
                                        maxOf(0, subjectGrades.size - 1)
                                    } else {
                                        maxOf(0, deletedIndex - 1)
                                    }

                                    listState.animateScrollToItem(
                                        index = targetIndex,
                                        scrollOffset = 0  // -200에서 0으로 변경
                                    )
                                } else {
                                    listState.animateScrollToItem(0)
                                }
                            }
                        }
                    )
                }

                // + 버튼
                if (subjectGrades.size < 5) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clickable {
                                        val newSubject = SubjectGrade(
                                            isNew = true,
                                            id = System.currentTimeMillis().toString()
                                        )
                                        subjectGrades = subjectGrades + newSubject
                                        val newItemIndex = subjectGrades.size - 1

                                        coroutineScope.launch {
                                            delay(150)

                                            try {
                                                listState.animateScrollToItem(
                                                    index = newItemIndex, // 새로 추가된 아이템으로
                                                    scrollOffset = 0 // 잘리지 않게 0으로 설정
                                                )
                                            } catch (e: Exception) {
                                                listState.scrollToItem(newItemIndex, 0)
                                            }

                                            delay(800)

                                            subjectGrades = subjectGrades.mapIndexed { index, item ->
                                                if (index == newItemIndex) item.copy(isNew = false)
                                                else item
                                            }
                                        }
                                    }
                                    .background(
                                        color = MainPurple.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MainPurple,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "과목 추가",
                                    tint = MainPurple,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
                else {
                    // 최대 개수 도달 메시지
                    item {
                        MaxSubjectCard()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 시작하기 버튼
            Button(
                onClick = onSetupComplete,
                enabled = subjectGrades.any { it.subject.isNotEmpty() && it.grade > 0 },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "다음",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MaxSubjectCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FF)
        ),
        border = BorderStroke(1.dp, MainPurple.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "정보",
                tint = MainPurple,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "최대 5개 과목까지 추천받으실 수 있어요",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MainPurple,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubjectGradeCard(
    subjectGrade: SubjectGrade,
    subjectCategories: List<SubjectCategory>,
    allSubjects: List<String>,
    canDelete: Boolean,
    isFocused: Boolean = false,
    onSubjectChange: (String) -> Unit,
    onGradeChange: (Int) -> Unit,
    onGradeTypeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showSubjectDropdown by remember { mutableStateOf(false) }

    // 더 부드러운 애니메이션 스펙으로 개선
    val scale by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 1.03f // 스케일을 줄여서 더 자연스럽게
            isFocused -> 1.01f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, // 더 부드러운 스프링
            stiffness = Spring.StiffnessLow // 더 느린 애니메이션
        ),
        label = "card_scale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 0.5f
            isFocused -> 0.3f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 600), // 더 긴 지속시간
        label = "border_alpha"
    )

    // 배경색 애니메이션도 추가
    val backgroundAlpha by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 0.08f
            isFocused -> 0.04f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 500),
        label = "background_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                border = BorderStroke(1.dp, LightGray4),
                shape = RoundedCornerShape(12.dp)
            )
            .let { modifier ->
                if (borderAlpha > 0f) {
                    modifier.border(
                        width = 2.dp,
                        color = MainPurple.copy(alpha = borderAlpha),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else modifier
            },
        colors = CardDefaults.cardColors(
            containerColor = if (backgroundAlpha > 0f) {
                MainPurple.copy(alpha = backgroundAlpha)
            } else {
                Color.White
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 상단 헤더 (과목 선택 + 삭제 버튼)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "과목 선택",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 과목 선택 커스텀 드롭다운
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSubjectDropdown = !showSubjectDropdown }
                        .border(
                            width = 1.dp,
                            color = if (subjectGrade.subject.isEmpty()) Color.LightGray else MainPurple,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = if (subjectGrade.subject.isEmpty()) Color.Transparent else MainPurple.copy(
                                alpha = 0.05f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (subjectGrade.subject.isEmpty()) "과목을 선택하세요" else subjectGrade.subject,
                            color = if (subjectGrade.subject.isEmpty()) Color.Gray else MainPurple,
                            fontSize = 13.sp,
                            fontWeight = if (subjectGrade.subject.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (showSubjectDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "드롭다운",
                            tint = Color.Gray
                        )
                    }
                }

                // 영역별 정리된 커스텀 드롭다운 메뉴
                if (showSubjectDropdown) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(8.dp) // 선택 박스와 같은 모양
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.White) // 명시적으로 흰색 배경 설정
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.White) // LazyColumn도 흰색 배경
                            ) {
                                subjectCategories.forEach { category ->
                                    item {
                                        // 카테고리 헤더
                                        Text(
                                            text = category.categoryName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MainPurple,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp)
                                        )
                                    }

                                    // 해당 카테고리의 과목들
                                    items(category.subjects.size) { index ->
                                        val subject = category.subjects[index]
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onSubjectChange(subject)
                                                    showSubjectDropdown = false
                                                }
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "  $subject", // 들여쓰기
                                                fontSize = 13.sp,
                                                color = if (subject == subjectGrade.subject) MainPurple else Color.Black,
                                                fontWeight = if (subject == subjectGrade.subject) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        }
                                    }

                                    // 카테고리 구분선 (마지막 카테고리 제외)
                                    if (category != subjectCategories.last()) {
                                        item {
                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(Color.LightGray.copy(alpha = 0.3f))
                                                    .padding(vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 등급 타입 토글 (내신/모의고사)
            if (subjectGrade.subject.isNotEmpty()) {
                Text(
                    text = "등급 종류",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("내신", "모의고사").forEach { type ->
                        Box(
                            modifier = Modifier
                                .clickable { onGradeTypeChange(type) }
                                .border(
                                    width = 1.dp,
                                    color = if (subjectGrade.gradeType == type) MainPurple else Color.LightGray,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(
                                    color = if (subjectGrade.gradeType == type) MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = type,
                                color = if (subjectGrade.gradeType == type) MainPurple else Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = if (subjectGrade.gradeType == type) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                // 등급 선택 - 원형 버튼
                Text(
                    text = "등급",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (1..5).forEach { grade ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.grade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.grade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.grade == grade) Color.White else Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (6..9).forEach { grade ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { onGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.grade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.grade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.grade == grade) Color.White else Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}