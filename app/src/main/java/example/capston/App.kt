package example.capston

import android.app.Application
import com.capston.data.loading.LoadingManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    @Inject
    lateinit var loadingManager: LoadingManager

    override fun onCreate() {
        super.onCreate()
        // Firebase 초기화
        FirebaseApp.initializeApp(this)
    }
}
