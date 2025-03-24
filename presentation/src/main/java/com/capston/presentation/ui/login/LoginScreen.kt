package com.capston.presentation.ui

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.capston.presentation.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Composable 로그인 화면.
 * - [onLoginSuccess]: 로그인 성공 시 호출되는 콜백
 * - [credentialManager]: CredentialManager 객체 (Activity 등에서 생성 후 주입)
 * - [activityContext]: 현재 Activity Context
 * - [externalCoroutineScope]: 코루틴을 실행할 Scope (ViewModelScope 등)
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    credentialManager: CredentialManager,
    activityContext: Activity,
    externalCoroutineScope: CoroutineScope,
    auth: FirebaseAuth
) {
    // Compose 환경에서 context 얻기
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("로그인 화면", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                // 구글 로그인 프로세스
                googleLogin(
                    context = context,
                    credentialManager = credentialManager,
                    activityContext = activityContext,
                    coroutineScope = externalCoroutineScope,
                    auth = auth,

                    // Add a callback or invoke a lambda on success
                    onSuccess = {
                        onLoginSuccess()
                    },
                    onFailure = {
                        // Show error to user or do nothing
                    }
                )

                // 로그인 성공 시
                onLoginSuccess()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("구글 로그인")
        }
    }
}

/**
 * 구글 로그인 처리 함수
 */
fun googleLogin(
    context: Context,
    credentialManager: CredentialManager,
    activityContext: Activity,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main),
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
    auth: FirebaseAuth
) {
    // 1) GoogleIdOption 설정
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
//        .setServerClientId(context.getString(R.string.google_client_id))
        .setServerClientId("441389992846-sqijfp1cvh0i94b0ubuk8vut8n0tn5eh.apps.googleusercontent.com")
        .setAutoSelectEnabled(false)
        .build()

    // 2) CredentialRequest 생성
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    // 3) CredentialManager를 통해 비동기 호출
    coroutineScope.launch {
        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            handleSignIn(result, auth, onSuccess)
            onSuccess()
        } catch (e: GetCredentialException) {
            // 여기서 예외 처리
            Log.e("LoginScreen", "Credential get failure", e)
            // 필요하다면 handleFailure(e) 같은 함수로 분리 가능
            onFailure(e)
        }
    }
}

/**
 * Credential 반환 성공 시 처리
 */
fun handleSignIn(
    result: GetCredentialResponse,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,

    ) {
    val credential = result.credential
    when (credential) {

        // 패스키 (Passkey) 사용 시
        is PublicKeyCredential -> {
            val responseJson = credential.authenticationResponseJson
            Log.d("LoginScreen", "Passkey responseJson: $responseJson")

            // TODO: 서버에 responseJson 전송 후 검증
        }

        // ID/비밀번호 사용 시
        is PasswordCredential -> {
            val username = credential.id
            val password = credential.password
            Log.d("LoginScreen", "Password login => username: $username, pw: $password")

            // TODO: 서버에 username/password 전송 후 검증
        }

        // GoogleIdTokenCredential (CustomCredential)
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    // ID 토큰 문자열
                    val idTokenString = googleIdTokenCredential.idToken
                    Log.d("LoginScreen", "Google ID token: $idTokenString")

                    // Sign in to Firebase with using the token
                    firebaseAuthWithGoogle(idTokenString, auth, onSuccess)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e("LoginScreen", "Invalid google id token response", e)
                }
            } else {
                // 기타 CustomCredential
                Log.e("LoginScreen", "Unexpected type of custom credential: ${credential.type}")
            }
        }

        else -> {
            // 그 외 처리 불가능한 Credential
            Log.e("LoginScreen", "Unexpected type of credential: ${credential.javaClass}")
        }
    }
}

private fun firebaseAuthWithGoogle(
    idToken: String,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,

    ) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("firebaseAuthWithGoogle", "signInWithCredential:success")
                val user = auth.currentUser
                onSuccess(/*user*/)
            } else {
                // If sign in fails, display a message to the user
                Log.w("firebaseAuthWithGoogle", "signInWithCredential:failure", task.exception)
//                updateUI(null)
            }
        }
}
