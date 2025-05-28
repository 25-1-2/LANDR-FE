package com.capston.presentation.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LoginDto
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.MainActivity
import com.capston.presentation.ui.onboarding.OnboardingActivity
import com.capston.presentation.viewmodel.LoginViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Inject
    lateinit var loadingStateManager: LoadingStateManager

    @Inject
    lateinit var onboardingPreferenceStorage: OnboardingPreferenceStorage

    private lateinit var auth: FirebaseAuth

    val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 로그인 상태 확인
        auth = Firebase.auth
        val currentUser = auth.currentUser
        Log.d("LoginActivity", "currentUser: ${currentUser?.uid}")

        // 이미 로그인되어 있는지 확인
        if (currentUser != null) {
            // 로그인 상태이면 MainActivity로 이동
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            CapstonTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(
                        onLoginButtonClick = {
                            loadingStateManager.show() // 로그인 시작 시 로딩 표시
                            googleLogin(
                                credentialManager = CredentialManager.create(this@LoginActivity),
                                activityContext = this@LoginActivity,
                            )
                        }
                    )

                    // 전역 로딩 인디케이터
                    LoadingIndicator(loadingStateManager)
                }
            }
        }
    }

    fun googleLogin(
        credentialManager: CredentialManager,
        activityContext: Activity,
    ) {
        // 1) GoogleIdOption 설정 - 더 관대한 설정으로 변경
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.google_client_id))
            .setFilterByAuthorizedAccounts(false) // 이미 false로 되어 있음
            .setAutoSelectEnabled(true) // true로 변경해서 자동 선택 활성화
            .build()

        // 2) CredentialRequest 생성
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // 3) CredentialManager를 통해 비동기 호출
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activityContext,
                )
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "googleLogin error: ${e.javaClass.simpleName}: ${e.message}")
                loadingStateManager.hide() // 로딩 숨기기

                // 다른 방법으로 시도하거나 사용자에게 알림
                handleCredentialError(e)
            }
        }
    }

    private fun handleCredentialError(exception: GetCredentialException) {
        when (exception) {
            is androidx.credentials.exceptions.NoCredentialException -> {
                Log.e("LoginActivity", "No Google accounts found on device")
                // 사용자에게 Google 계정 추가를 안내
                showErrorDialog("Google 계정이 필요합니다", "기기에 Google 계정을 추가해주세요.")
            }
            is androidx.credentials.exceptions.GetCredentialCancellationException -> {
                Log.e("LoginActivity", "User cancelled the credential request")
                // 사용자가 취소함
            }
            else -> {
                Log.e("LoginActivity", "Credential error: ${exception.message}")
                showErrorDialog("로그인 오류", "로그인 중 오류가 발생했습니다.")
            }
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        // AlertDialog로 사용자에게 알림
        // 또는 Toast 메시지
        runOnUiThread {
            android.widget.Toast.makeText(this, "$title: $message", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // Create Google ID Token
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            // Sign in to Firebase with using the token
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            loadingStateManager.hide() // 로딩 숨기기
        } else {
            // Catch any unrecognized custom credential type here.
            Log.w("LoginActivity", "handleSignIn: Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginActivity", "signInWithCredential:success")
                    // FCM 토큰까지 불러와 백엔드에 회원가입 정보 전달
                    getFcmToken { token ->
                        loginViewModel.postLogin(
                            LoginDto(
                                auth.currentUser?.email,
                                auth.currentUser?.displayName,
                                token
                            )
                        )
                        lifecycleScope.launchWhenStarted {
                            loginViewModel.isTokenSaved.collect { isSaved ->
//                                if (isSaved) {
//                                    // 첫 로그인인지 확인
//                                    if (onboardingPreferenceStorage.isFirstLogin()) {
//                                        // 첫 로그인이면 온보딩으로 이동
//                                        startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
//                                    } else {
//                                        // 기존 사용자면 메인으로 이동
//                                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//                                    }
//                                    finish()
//                                }
                                if (isSaved) {
                                    // 저장이 완료된 경우에만 다음 화면으로 이동
                                    startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun getFcmToken(onTokenReceived: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("LoginActivity", "token: $token")
                onTokenReceived(token)
            } else {
                Log.w("LoginActivity", "Fetching FCM token failed", task.exception)
                onTokenReceived(null)
            }
        }
    }
}