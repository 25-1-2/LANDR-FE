package com.capston.data.local.storage

import android.content.Context
import com.capston.domain.datasource.OnboardingPreferenceStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnboardingPreferenceStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OnboardingPreferenceStorage {

    private val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    override fun isFirstLogin(): Boolean {
        return prefs.getBoolean("is_first_login", true)
    }

    override fun setFirstLoginDone() {
        prefs.edit().putBoolean("is_first_login", false).apply()
    }
}

