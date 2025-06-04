package com.capston.presentation.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearningPreferenceScreen(
    onSetupComplete: (String, String, List<String>) -> Unit
) {
    var selectedFocus by remember { mutableStateOf<String?>(null) }
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    var selectedStyles by remember { mutableStateOf(setOf<String>()) }

    val focusOptions = listOf(
        "수능 중심",
        "내신 중심",
        "균형 잡힌 학습"
    )

    val goalOptions = listOf(
        "개념 정리",
        "기출 분석",
        "실전 문제풀이",
        "빠른 요약 정리"
    )

    val styleOptions = listOf(
        "체계적 학습",
        "빠른 진도",
        "반복 학습",
        "실전 중심",
        "이론 중심",
        "문제풀이 중심"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단 LANDR 로고
        Image(
            painter = painterResource(R.drawable.landr_title_iv),
            contentDescription = "LANDR 로고",
            modifier = Modifier
                .padding(top = 80.dp, start = 35.dp)
                .size(80.dp)
                .align(Alignment.TopStart)
        )

        // 스크롤 가능한 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp, bottom = 80.dp)
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(32.dp))

            // 학습 방향 선택
            PreferenceSection(
                title = "3. 학습 방향을 선택해주세요",
                options = focusOptions,
                selectedOption = selectedFocus,
                onOptionSelected = { selectedFocus = it },
                singleSelect = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 학습 목표 선택
            PreferenceSection(
                title = "4. 학습 목표를 선택해주세요",
                options = goalOptions,
                selectedOption = selectedGoal,
                onOptionSelected = { selectedGoal = it },
                singleSelect = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 학습 스타일 선택 (복수 선택)
            Text(
                text = "5. 선호하는 학습 스타일을 선택해주세요 (복수선택 가능)",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainPurple,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                styleOptions.forEach { style ->
                    MultiSelectCard(
                        text = style,
                        isSelected = selectedStyles.contains(style),
                        onSelect = {
                            selectedStyles = if (selectedStyles.contains(style)) {
                                selectedStyles - style
                            } else {
                                selectedStyles + style
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // 하단 다음 버튼
        Button(
            onClick = {
                onSetupComplete(
                    selectedFocus ?: "",
                    selectedGoal ?: "",
                    selectedStyles.toList()
                )
            },
            enabled = selectedFocus != null && selectedGoal != null && selectedStyles.isNotEmpty(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainPurple,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
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

@Composable
private fun PreferenceSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    singleSelect: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MainPurple,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                SelectionCard(
                    text = option,
                    isSelected = selectedOption == option,
                    onSelect = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun SelectionCard(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MainPurple.copy(alpha = 0.1f) else Color.Transparent
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MainPurple else Color.LightGray
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MainPurple else Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun MultiSelectCard(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onSelect() }
            .background(
                color = if (isSelected) MainPurple.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MainPurple else Color.LightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MainPurple else Color.Gray
        )
    }
}