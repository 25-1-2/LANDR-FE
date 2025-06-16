package com.capston.presentation.ui.common

import android.content.Context
import com.capston.presentation.ui.home.PaymentPlan
import com.capston.presentation.ui.home.PaymentReceipt
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// 결제 관련 Util 함수들
object PaymentUtils {

    // 결제 검증 함수 (실제로는 서버에서 처리)
    suspend fun verifyPayment(orderId: String): Boolean {
        // 서버 API 호출로 결제 검증
        return try {
            // API 호출 시뮬레이션
            delay(1000)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 구독 상태 업데이트
    fun updateSubscriptionStatus(context: Context, plan: PaymentPlan) {
        val sharedPref = context.getSharedPreferences("user_subscription", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("is_premium", true)
            putString("subscription_type", plan.id)
            putLong("subscription_start", System.currentTimeMillis())
            putLong("subscription_end", calculateSubscriptionEnd(plan))
            apply()
        }
    }

    private fun calculateSubscriptionEnd(plan: PaymentPlan): Long {
        val calendar = Calendar.getInstance()
        when (plan.id) {
            "monthly" -> calendar.add(Calendar.MONTH, 1)
            "quarterly" -> calendar.add(Calendar.MONTH, 3)
            "yearly" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }

    // 결제 영수증 생성
    fun generateReceipt(plan: PaymentPlan): PaymentReceipt {
        return PaymentReceipt(
            orderId = "ORD${System.currentTimeMillis()}",
            planName = plan.title,
            amount = plan.discountPrice,
            paymentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            paymentMethod = "카드결제"
        )
    }
}