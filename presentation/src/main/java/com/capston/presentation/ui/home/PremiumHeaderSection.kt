package com.capston.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.outlinedButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.capston.presentation.R
import com.capston.presentation.theme.*
import kotlinx.coroutines.delay

// 결제 플랜 데이터 클래스
data class PaymentPlan(
    val id: String,
    val title: String,
    val period: String,
    val originalPrice: Int,
    val discountPrice: Int,
    val discountPercent: Int,
    val isPopular: Boolean = false,
    val features: List<String>
)

// 결제 상태
enum class PaymentState {
    SELECTING, PROCESSING, SUCCESS, FAILED
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    var selectedPlan by remember { mutableStateOf<PaymentPlan?>(null) }
    var paymentState by remember { mutableStateOf(PaymentState.SELECTING) }
    var showPaymentDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // 결제 플랜들
    val paymentPlans = listOf(
        PaymentPlan(
            id = "monthly",
            title = "월간 구독",
            period = "1개월",
            originalPrice = 9900,
            discountPrice = 9900,
            discountPercent = 0,
            features = listOf(
                "무제한 통계 분석",
                "과목별 상세 리포트",
                "목표 달성 예측"
            )
        ),
        PaymentPlan(
            id = "quarterly",
            title = "3개월 구독",
            period = "3개월",
            originalPrice = 29700,
            discountPrice = 24900,
            discountPercent = 16,
            isPopular = true,
            features = listOf(
                "월간 구독의 모든 기능",
                "학습 히스토리 무제한 보관",
                "프리미엄 테마",
                "우선 고객지원"
            )
        ),
        PaymentPlan(
            id = "yearly",
            title = "연간 구독",
            period = "12개월",
            originalPrice = 118800,
            discountPrice = 79900,
            discountPercent = 33,
            features = listOf(
                "3개월 구독의 모든 기능",
                "개인 맞춤 학습 코칭",
                "오프라인 리포트 다운로드",
                "베타 기능 우선 체험"
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "프리미엄 구독",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 헤더 섹션
                PremiumHeaderSection()

                Spacer(modifier = Modifier.height(32.dp))

                // 플랜 선택 섹션
                Text(
                    text = "요금제를 선택하세요",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 결제 플랜 카드들
                paymentPlans.forEach { plan ->
                    PaymentPlanCard(
                        plan = plan,
                        isSelected = selectedPlan?.id == plan.id,
                        onSelect = { selectedPlan = plan }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 프리미엄 기능 소개
                PremiumFeaturesSection()

                Spacer(modifier = Modifier.height(100.dp))
            }

            // 하단 결제 버튼
            if (selectedPlan != null) {
                PaymentBottomBar(
                    selectedPlan = selectedPlan!!,
                    onPaymentClick = { showPaymentDialog = true },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // 결제 처리 다이얼로그
    if (showPaymentDialog && selectedPlan != null) {
        PaymentProcessDialog(
            plan = selectedPlan!!,
            paymentState = paymentState,
            onDismiss = {
                showPaymentDialog = false
                paymentState = PaymentState.SELECTING
            },
            onPaymentStateChange = { paymentState = it },
            onPaymentSuccess = {
                showPaymentDialog = false
                onPaymentSuccess()
            }
        )
    }
}

@Composable
fun PremiumHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.1f),
                        Color.White
                    )
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프리미엄 아이콘
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA000)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "프리미엄",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "학습의 새로운 차원을 경험하세요",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "개인 맞춤 코칭으로\n더 스마트하게 공부할 수 있어요",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PaymentPlanCard(
    plan: PaymentPlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .scale(animatedScale)
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF8E1) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFFFFD700) else LightGray60
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box {
            // 인기 배지
            if (plan.isPopular) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = Color(0xFFFF6B6B),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "인기",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // 플랜 제목
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 가격 정보
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 할인 가격
                    Text(
                        text = "${String.format("%,d", plan.discountPrice)}원",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B)
                    )

                    Text(
                        text = "/${plan.period}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // 원래 가격 (할인이 있는 경우)
                    if (plan.discountPercent > 0) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${String.format("%,d", plan.originalPrice)}원",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                // 할인 퍼센트
                if (plan.discountPercent > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${plan.discountPercent}% 할인",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 기능 목록
                plan.features.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.home_screen_check_off),
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }

                // 선택 표시
                if (isSelected) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.home_screen_check_on),
                            contentDescription = "선택됨",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "선택된 요금제",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFF8F00),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumFeaturesSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "프리미엄으로 더 똑똑하게 공부하세요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val features = listOf(
                Triple("📊", "상세 학습 분석", "과목별, 주차별 상세한 학습 통계"),
                Triple("🤖", "AI 학습 코칭", "개인 맞춤형 학습 방법 제안"),
                Triple("🎯", "목표 달성 예측", "현재 진도로 목표 달성 시기 예측"),
                Triple("📱", "프리미엄 테마", "다양한 컬러 테마와 UI 커스터마이징"),
                Triple("💾", "무제한 백업", "학습 데이터 클라우드 백업"),
                Triple("🏆", "성취 배지", "학습 목표 달성시 특별 배지 획득")
            )

            features.forEach { (emoji, title, description) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = emoji,
                        fontSize = 24.sp,
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentBottomBar(
    selectedPlan: PaymentPlan,
    onPaymentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 선택된 플랜 정보
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedPlan.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedPlan.discountPercent > 0) {
                        Text(
                            text = "${selectedPlan.discountPercent}% 할인 적용",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }

                Text(
                    text = "${String.format("%,d", selectedPlan.discountPrice)}원",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 결제 버튼
            Button(
                onClick = onPaymentClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "결제하기",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 약관 동의
            Text(
                text = "결제 시 이용약관 및 개인정보처리방침에 동의한 것으로 간주됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PaymentProcessDialog(
    plan: PaymentPlan,
    paymentState: PaymentState,
    onDismiss: () -> Unit,
    onPaymentStateChange: (PaymentState) -> Unit,
    onPaymentSuccess: () -> Unit
) {
    // 결제 처리 시뮬레이션
    LaunchedEffect(paymentState) {
        if (paymentState == PaymentState.PROCESSING) {
            delay(3000) // 3초 대기
            // 90% 확률로 성공, 10% 확률로 실패 (시뮬레이션)
            if (Math.random() > 0.1) {
                onPaymentStateChange(PaymentState.SUCCESS)
                delay(2000)
                onPaymentSuccess()
            } else {
                onPaymentStateChange(PaymentState.FAILED)
            }
        }
    }

    Dialog(
        onDismissRequest = { if (paymentState != PaymentState.PROCESSING) onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (paymentState) {
                    PaymentState.SELECTING -> {
                        PaymentConfirmationContent(
                            plan = plan,
                            onConfirm = { onPaymentStateChange(PaymentState.PROCESSING) },
                            onCancel = onDismiss
                        )
                    }
                    PaymentState.PROCESSING -> {
                        PaymentProcessingContent()
                    }
                    PaymentState.SUCCESS -> {
                        PaymentSuccessContent(plan = plan)
                    }
                    PaymentState.FAILED -> {
                        PaymentFailedContent(
                            onRetry = { onPaymentStateChange(PaymentState.PROCESSING) },
                            onCancel = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentConfirmationContent(
    plan: PaymentPlan,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "결제",
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "결제 확인",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 결제 정보
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("상품명", color = Color.Gray)
                    Text(plan.title, fontWeight = FontWeight.Bold)
                }

                if (plan.discountPercent > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("정가", color = Color.Gray)
                        Text(
                            "${String.format("%,d", plan.originalPrice)}원",
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("할인", color = Color.Gray)
                        Text(
                            "-${String.format("%,d", plan.originalPrice - plan.discountPrice)}원",
                            color = Color(0xFFFF6B6B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("최종 결제금액", fontWeight = FontWeight.Bold)
                    Text(
                        "${String.format("%,d", plan.discountPrice)}원",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B6B),
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("취소")
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                )
            ) {
                Text("결제", color = Color.White)
            }
        }
    }
}

@Composable
fun PaymentProcessingContent() {
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 패딩 추가
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // 세로 중앙 정렬 추가
    ) {
        Box(
            modifier = Modifier.size(80.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFFD700),
                strokeWidth = 6.dp
            )
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "결제 처리중",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "결제 처리중입니다",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, // 텍스트 중앙 정렬 추가
            modifier = Modifier.fillMaxWidth() // 전체 너비 사용
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "잠시만 기다려주세요...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center, // 텍스트 중앙 정렬 추가
            modifier = Modifier.fillMaxWidth() // 전체 너비 사용
        )
    }
}

@Composable
fun PaymentSuccessContent(plan: PaymentPlan) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 성공 애니메이션
        val scale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "successScale"
        )

        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .background(
                    color = Color(0xFF4CAF50),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.home_screen_check_on),
                contentDescription = "성공",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "결제가 완료되었습니다!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${plan.title} 구독이 시작되었어요\n프리미엄 기능을 마음껏 이용하세요!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 프리미엄 혜택 미리보기
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF8E1)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "프리미엄",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "프리미엄 활성화됨",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF8F00)
                    )
                    Text(
                        text = "모든 고급 기능을 이용할 수 있어요",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF8F00)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentFailedContent(
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFFFF5722),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "실패",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "결제에 실패했습니다",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "네트워크 연결을 확인하고\n다시 시도해주세요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 에러 정보 카드
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "오류 코드: PAY_001",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF5722),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "결제 처리 중 오류가 발생했습니다.\n카드 정보를 확인하거나 다른 결제 수단을 이용해주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = outlinedButtonColors(
                    contentColor = Color(0xFFFF5722)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF5722))
            ) {
                Text("닫기")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                )
            ) {
                Text("다시 시도", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 고객센터 연결
        TextButton(
            onClick = { /* 고객센터 연결 */ }
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "고객센터",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "고객센터 문의",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// 결제 영수증 데이터 클래스
data class PaymentReceipt(
    val orderId: String,
    val planName: String,
    val amount: Int,
    val paymentDate: String,
    val paymentMethod: String
)
