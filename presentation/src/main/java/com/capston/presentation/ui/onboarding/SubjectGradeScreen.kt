package com.capston.presentation.ui.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray
import kotlinx.coroutines.delay

data class SubjectGrade(
    val subject: String = "",
    val grade: Int = 0,
    val gradeType: String = "내신", // "내신" 또는 "모의고사"
    val isNew: Boolean = false, // 새로 추가된 아이템인지 확인
    val id: String = System.currentTimeMillis().toString() // 고유 ID 추가
)

@Composable
fun SubjectGradeScreen(onSetupComplete: () -> Unit) {
    var subjectGrades by remember { mutableStateOf(listOf(SubjectGrade())) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusedItemId by remember { mutableStateOf<String?>(null) }

    val subjects = listOf(
        "국어", "수학", "영어", "한국사",
        "물리학Ⅰ", "물리학Ⅱ", "화학Ⅰ", "화학Ⅱ",
        "생명과학Ⅰ", "생명과학Ⅱ", "지구과학Ⅰ", "지구과학Ⅱ",
        "한국지리", "세계지리", "동아시아사", "세계사",
        "생활과윤리", "윤리와사상", "정치와법", "경제", "사회·문화"
    )

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
                // 인사 메시지
                Text(
                    text = "조은채님!\n맞춤형 인강 학습을 시작해볼까요?",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )

                Text(
                    text = "꼭 맞는 학습 계획을 LANDR가 제안해드릴게요 :)",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = textGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 제목
                Text(
                    text = "2. 가장 자신없는 과목과 등급을 알려주세요",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
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
                    .height(400.dp), // 높이 제한 추가 - 카드 하나 정도만 보이게
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(subjectGrades, key = { _, item -> item.id }) { index, subjectGrade ->
                    SubjectGradeCard(
                        subjectGrade = subjectGrade,
                        subjects = subjects,
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
                            subjectGrades = subjectGrades.toMutableList().apply {
                                removeAt(index)
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
                                .height(300.dp), // + 버튼 영역도 충분한 높이 확보
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

                                        //focusedItemId = newSubject.id

                                        coroutineScope.launch {
                                            delay(50)

                                            // 이제 높이가 제한되어 있으므로 새 아이템으로 스크롤하면
                                            // 기존 아이템들이 자연스럽게 화면 밖으로 나감
                                            listState.animateScrollToItem(newItemIndex)

                                            delay(200)

                                            subjectGrades = subjectGrades.mapIndexed { index, item ->
                                                if (index == newItemIndex) item.copy(isNew = false)
                                                else item
                                            }
                                        }
                                    }
                                    .background(
                                        color = MainPurple.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = MainPurple.copy(alpha = 0.3f),
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 시작하기 버튼
            Button(
                onClick = onSetupComplete,
                enabled = subjectGrades.any { it.subject.isNotEmpty() && it.grade > 0 },
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainPurple,
                    disabledContainerColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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
}

@Composable
private fun SubjectGradeCard(
    subjectGrade: SubjectGrade,
    subjects: List<String>,
    canDelete: Boolean,
    isFocused: Boolean = false,
    onSubjectChange: (String) -> Unit,
    onGradeChange: (Int) -> Unit,
    onGradeTypeChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showSubjectDropdown by remember { mutableStateOf(false) }

    // 토스 스타일의 부드러운 애니메이션 효과
    val scale by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 1.05f
            isFocused -> 1.02f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = when {
            subjectGrade.isNew -> 0.6f
            isFocused -> 0.4f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 400),
        label = "border_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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
            containerColor = when {
                subjectGrade.isNew -> MainPurple.copy(alpha = 0.08f)
                isFocused -> MainPurple.copy(alpha = 0.05f)
                else -> Color.White
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
                            color = if (subjectGrade.subject.isEmpty()) Color.Transparent else MainPurple.copy(alpha = 0.05f),
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

                // 커스텀 드롭다운 메뉴
                if (showSubjectDropdown) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .zIndex(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp) // 높이 줄임
                        ) {
                            LazyColumn {
                                itemsIndexed(subjects) { _, subject ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSubjectChange(subject)
                                                showSubjectDropdown = false
                                            }
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = subject,
                                            fontSize = 13.sp,
                                            color = if (subject == subjectGrade.subject) MainPurple else Color.Black,
                                            fontWeight = if (subject == subjectGrade.subject) FontWeight.SemiBold else FontWeight.Normal
                                        )
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

                // 등급 선택 - 원형 버튼으로 복원
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
                                .size(36.dp) // 크기 약간 줄임
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
                                .size(36.dp) // 크기 약간 줄임
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

@Preview(showBackground = true)
@Composable
fun SubjectGradeScreenPreview() {
    CapstonTheme {
        SubjectGradeScreen(onSetupComplete = {})
    }
}