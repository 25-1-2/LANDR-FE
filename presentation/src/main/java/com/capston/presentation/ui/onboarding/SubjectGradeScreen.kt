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
fun SubjectGradeScreen(onSetupComplete: (List<SubjectGrade>) -> Unit) {
    var subjectGrades by remember { mutableStateOf(listOf(SubjectGrade())) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusedItemId by remember { mutableStateOf<String?>(null) }

    // 과목을 영역별로 정리 (기존과 동일)
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
        // 상단 LANDR 로고
        Image(
            painter = painterResource(R.drawable.landr_title_iv),
            contentDescription = "과목명",
            modifier = Modifier
                .padding(top = 80.dp, start = 35.dp)
                .size(80.dp)
                .align(Alignment.TopStart)
        )

        // 텍스트 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        onSchoolGradeChange = { newGrade ->
                            subjectGrades = subjectGrades.toMutableList().apply {
                                this[index] = this[index].copy(schoolGrade = newGrade)
                            }
                        },
                        onMockGradeChange = { newGrade ->
                            subjectGrades = subjectGrades.toMutableList().apply {
                                this[index] = this[index].copy(mockGrade = newGrade)
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
                                        scrollOffset = 0
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
                                                    index = newItemIndex,
                                                    scrollOffset = 0
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

            // 하단 다음 버튼 (수정된 조건)
            Button(
                onClick = {
                    // 유효한 과목 데이터만 필터링해서 전달 (내신과 모의고사 모두 입력 필요)
                    val validSubjectGrades = subjectGrades.filter {
                        it.subject.isNotEmpty() && it.schoolGrade > 0 && it.mockGrade > 0
                    }
                    onSetupComplete(validSubjectGrades)
                },
                enabled = subjectGrades.any {
                    it.subject.isNotEmpty() && it.schoolGrade > 0 && it.mockGrade > 0
                },
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
    onSchoolGradeChange: (Int) -> Unit,
    onMockGradeChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var showSubjectDropdown by remember { mutableStateOf(false) }

    // 애니메이션 (기존과 동일)
    val scale by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 1.03f
            isFocused -> 1.01f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 0.5f
            isFocused -> 0.3f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 600),
        label = "border_alpha"
    )

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

            // 과목 선택 커스텀 드롭다운 (기존과 동일)
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

                // 영역별 정리된 커스텀 드롭다운 메뉴 (기존과 동일)
                if (showSubjectDropdown) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.White)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.White)
                            ) {
                                subjectCategories.forEach { category ->
                                    item {
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
                                                text = "  $subject",
                                                fontSize = 13.sp,
                                                color = if (subject == subjectGrade.subject) MainPurple else Color.Black,
                                                fontWeight = if (subject == subjectGrade.subject) FontWeight.SemiBold else FontWeight.Normal
                                            )
                                        }
                                    }

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

            // 내신 등급과 모의고사 등급 모두 입력 (수정된 부분)
            if (subjectGrade.subject.isNotEmpty()) {
                // 내신 등급 선택
                Text(
                    text = "내신 등급",
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
                                .clickable { onSchoolGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.schoolGrade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.schoolGrade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.schoolGrade == grade) Color.White else Color.Gray,
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
                                .clickable { onSchoolGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.schoolGrade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.schoolGrade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.schoolGrade == grade) Color.White else Color.Gray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 모의고사 등급 선택
                Text(
                    text = "모의고사 등급",
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
                                .clickable { onMockGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.mockGrade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.mockGrade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.mockGrade == grade) Color.White else Color.Gray,
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
                                .clickable { onMockGradeChange(grade) }
                                .background(
                                    color = if (subjectGrade.mockGrade == grade) MainPurple else Color.Transparent,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (subjectGrade.mockGrade == grade) MainPurple else Color.LightGray,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = grade.toString(),
                                color = if (subjectGrade.mockGrade == grade) Color.White else Color.Gray,
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