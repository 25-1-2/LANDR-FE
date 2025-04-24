package example.capston

import android.app.Application
import com.capston.data.loading.LoadingManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    @Inject
    lateinit var loadingManager: LoadingManager
}
