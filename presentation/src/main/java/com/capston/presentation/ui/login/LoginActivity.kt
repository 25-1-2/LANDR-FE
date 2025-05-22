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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LoginDto
import com.capston.presentation.R
import com.capston.presentation.theme.CapstonTheme
import com.capston.presentation.ui.common.LoadingIndicator
import com.capston.presentation.ui.MainActivity
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    @Inject
    lateinit var loadingStateManager: LoadingStateManager
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
        // 1) GoogleIdOption 설정
        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(getString(R.string.google_client_id))
            // true: Only show accounts previously used to sign in.
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
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
                Log.e("LoginActivity", "googleLogin: $e")
                // handleFailure(e)
            }
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
                        lifecycleScope.launch {
                            delay(1000) // Wait for token to be saved and processed
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
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

