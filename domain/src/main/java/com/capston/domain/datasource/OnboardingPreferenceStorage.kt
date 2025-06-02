package com.capston.domain.datasource

interface OnboardingPreferenceStorage {
    fun hasCompletedOnboarding(userEmail: String?): Boolean
    fun setOnboardingCompleted(userEmail: String?)
    fun clearOnboardingStatus(userEmail: String?)
}

