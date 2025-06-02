package com.capston.data.local.storage

import android.content.Context
import android.content.SharedPreferences
import com.capston.domain.datasource.OnboardingPreferenceStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OnboardingPreferenceStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OnboardingPreferenceStorage {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "onboarding_preferences",
        Context.MODE_PRIVATE
    )

    override fun hasCompletedOnboarding(userEmail: String?): Boolean {
        return if (userEmail != null) {
            prefs.getBoolean("onboarding_completed_$userEmail", false)
        } else {
            false
        }
    }

    override fun setOnboardingCompleted(userEmail: String?) {
        userEmail?.let {
            prefs.edit().putBoolean("onboarding_completed_$it", true).apply()
        }
    }

    override fun clearOnboardingStatus(userEmail: String?) {
        userEmail?.let {
            prefs.edit().remove("onboarding_completed_$it").apply()
        }
    }
}