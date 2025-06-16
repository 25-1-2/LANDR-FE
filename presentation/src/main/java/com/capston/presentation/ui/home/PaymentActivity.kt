package com.capston.presentation.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import com.capston.presentation.theme.CapstonTheme

class PaymentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CapstonTheme { // 앱의 테마 적용
                PaymentScreen(
                    onBackClick = { finish() },
                    onPaymentSuccess = {
                        // 결제 성공 시 결과 전달하고 액티비티 종료
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PAYMENT = 1001

        fun startForResult(context: Context, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(context, PaymentActivity::class.java)
            launcher.launch(intent)
        }
    }
}