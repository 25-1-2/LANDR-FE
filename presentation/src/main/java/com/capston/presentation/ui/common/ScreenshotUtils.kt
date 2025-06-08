package com.capston.presentation.ui.common

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.delay

class ScreenshotManager(private val context: Context) {

    /**
     * 현재 화면을 캡처합니다
     */
    /**
     * 개선된 화면 캡처 (전체 컨텐츠 포함)
     */
    fun captureFullScreen(activity: Activity): Bitmap? {
        return try {
            // 메인 컨텐츠 뷰 찾기
            val contentView = activity.findViewById<View>(android.R.id.content)
            val rootView = contentView ?: activity.window.decorView.rootView

            // 전체 크기 계산
            val totalHeight = rootView.height
            val totalWidth = rootView.width

            // 비트맵 생성
            val bitmap = createBitmap(totalWidth, totalHeight)
            val canvas = Canvas(bitmap)

            // 배경을 흰색으로 설정
            canvas.drawColor(android.graphics.Color.WHITE)

            // 뷰 그리기
            rootView.draw(canvas)

            bitmap
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "전체 화면 캡처 실패: ${e.message}")
            // 실패 시 기본 캡처 방법 사용
            captureScreen(activity)
        }
    }

    fun captureScreen(activity: Activity): Bitmap? {
        return try {
            val view = activity.window.decorView.rootView
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "화면 캡처 실패: ${e.message}")
            null
        }
    }

    /**
     * 특정 View를 캡처합니다
     */
    fun captureView(view: View): Bitmap? {
        return try {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "뷰 캡처 실패: ${e.message}")
            null
        }
    }

    /**
     * 스크롤 가능한 뷰의 전체 내용을 캡처합니다
     */
    fun captureScrollableView(scrollView: android.view.View): Bitmap? {
        return try {
            // 스크롤뷰의 전체 높이 계산
            val totalHeight = scrollView.height
            val totalWidth = scrollView.width

            // 전체 크기의 비트맵 생성
            val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // 배경색을 흰색으로 설정
            canvas.drawColor(android.graphics.Color.WHITE)

            // 스크롤뷰의 전체 내용 그리기
            scrollView.draw(canvas)

            bitmap
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "스크롤 뷰 캡처 실패: ${e.message}")
            null
        }
    }

    /**
     * 비트맵을 갤러리에 저장합니다
     */
    suspend fun saveImageToGallery(bitmap: Bitmap): Uri? = withContext(Dispatchers.IO) {
        try {
            val filename = "screenshot_${System.currentTimeMillis()}.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let { imageUri ->
                context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(imageUri, contentValues, null, null)
                }
            }

            uri
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "이미지 저장 실패: ${e.message}")
            null
        }
    }

    /**
     * 카카오톡으로 이미지를 공유합니다
     */
    fun shareToKakaoTalk(imageUri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Landr 마이페이지를 공유합니다!")
                setPackage("com.kakao.talk")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // 카카오톡이 설치되지 않은 경우 일반 공유
                shareToGeneral(imageUri)
            }
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "카카오톡 공유 실패: ${e.message}")
            shareToGeneral(imageUri)
        }
    }

    /**
     * 일반 공유 (카카오톡이 없는 경우)
     */
    private fun shareToGeneral(imageUri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Landr 마이페이지를 공유합니다!")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "공유하기")
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("ScreenshotManager", "일반 공유 실패: ${e.message}")
            Toast.makeText(context, "공유에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 캡처만 하고 갤러리에 저장 (공유하지 않음)
     */
    suspend fun captureOnly(activity: Activity) {
        val bitmap = captureFullScreen(activity)
        if (bitmap != null) {
            val uri = saveImageToGallery(bitmap)
            if (uri != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ 캡처 완료! 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ 이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ 화면 캡처에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 전체 캡처 및 공유 프로세스를 실행합니다
     */
    suspend fun captureAndShare(activity: Activity) {
        val bitmap = captureFullScreen(activity)
        if (bitmap != null) {
            val uri = saveImageToGallery(bitmap)
            if (uri != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ 캡처 완료! 카카오톡으로 공유합니다.", Toast.LENGTH_SHORT).show()
                    shareToKakaoTalk(uri)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ 이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "❌ 화면 캡처에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/**
 * Compose에서 사용할 수 있는 스크린샷 매니저 훅
 */
@Composable
fun rememberScreenshotManager(): ScreenshotManager {
    val context = LocalContext.current
    return remember { ScreenshotManager(context) }
}

/**
 * 스크린샷 캡처 및 공유를 위한 Compose 확장 함수
 */
@Composable
fun useScreenshotCapture(
    onCaptureStart: () -> Unit = {},
    onCaptureSuccess: () -> Unit = {},
    onCaptureError: (String) -> Unit = {}
): (Activity) -> Unit {
    val screenshotManager = rememberScreenshotManager()
    val scope = rememberCoroutineScope()

    return { activity ->
        scope.launch {
            try {
                onCaptureStart()
                screenshotManager.captureAndShare(activity)
                onCaptureSuccess()
            } catch (e: Exception) {
                onCaptureError(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}