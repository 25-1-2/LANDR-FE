package com.capston.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.capston.domain.response.user.UserProfileResponse
import com.capston.presentation.R
import com.capston.presentation.theme.MainPurple
import com.capston.presentation.theme.textGray

@Composable
fun SchoolYearScreen(
    onSetupComplete: (String) -> Unit,
    userProfile: UserProfileResponse
) {
    var selectedGrade by remember { mutableStateOf<String?>(null) }

    val gradeOptions = listOf(
        "고등학교 1학년",
        "고등학교 2학년",
        "고등학교 3학년",
        "N수 / 그외"
    )

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

        // 텍스트 영역 (이미지 제거됨)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 140.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${userProfile.name}님!\n맞춤형 인강 학습을 시작해볼까요?",
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

            // 학년 선택 제목
            Text(
                text = "1. 현재 학년을 알려주세요",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MainPurple,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 학년 선택 옵션들
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                gradeOptions.forEach { grade ->
                    GradeSelectionCard(
                        grade = grade,
                        isSelected = selectedGrade == grade,
                        onSelect = { selectedGrade = grade }
                    )
                }
            }
        }

        // 하단 다음 버튼 부분 수정:
        Button(
            onClick = {
                selectedGrade?.let { grade ->
                    onSetupComplete(grade) // 선택된 학년을 콜백으로 전달
                }
            },
            enabled = selectedGrade != null,
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
                color = if (selectedGrade != null) Color.White else Color.Gray
            )
        }
    }
}

@Composable
private fun GradeSelectionCard(
    grade: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MainPurple else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isSelected) MainPurple.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = grade,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MainPurple else Color.Black,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}