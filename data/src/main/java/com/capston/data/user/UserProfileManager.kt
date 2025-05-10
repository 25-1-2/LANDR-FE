package com.capston.data.user

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.UserProfileResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileManager @Inject constructor(
    private val context: Context
) {
    private val PREFS_NAME = "user_profile_prefs"
    private val KEY_USER_NAME = "user_name"
    private val KEY_USE_CACHED_NAME = "use_cached_name"

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Gets the user's display name from all possible sources
    fun getUserName(): String? {
        // First, check if we should use cached name
        if (shouldUseCachedName()) {
            val cachedName = prefs.getString(KEY_USER_NAME, null)
            if (!cachedName.isNullOrBlank()) {
                Log.d("UserProfileManager", "Using cached name: $cachedName")
                return cachedName
            }
        }

        // Try to get from Firebase
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val firebaseName = firebaseUser?.displayName
        if (!firebaseName.isNullOrBlank()) {
            Log.d("UserProfileManager", "Using Firebase name: $firebaseName")
            return firebaseName
        }

        // If we reach here, no valid name found
        return null
    }

    // Update name in all places
    suspend fun updateUserName(name: String): Boolean {
        var success = true

        // 1. Save to local preferences
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_USE_CACHED_NAME, true)
            apply()
        }
        Log.d("UserProfileManager", "Saved name to preferences: $name")

        // 2. Update Firebase profile
        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates).await()
                Log.d("UserProfileManager", "Firebase profile updated successfully: $name")
            } else {
                Log.e("UserProfileManager", "Firebase user is null, can't update profile")
                success = false
            }
        } catch (e: Exception) {
            Log.e("UserProfileManager", "Failed to update Firebase profile: ${e.message}")
            success = false
        }

        return success
    }

    // Should we use the cached name instead of API response?
    fun shouldUseCachedName(): Boolean {
        return prefs.getBoolean(KEY_USE_CACHED_NAME, false)
    }

    // Override API response with our cached name if needed
    fun applyNameOverride(profile: UserProfileResponse): UserProfileResponse {
        if (shouldUseCachedName()) {
            val cachedName = prefs.getString(KEY_USER_NAME, null)
            if (!cachedName.isNullOrBlank() && profile.name != cachedName) {
                return profile.copy(name = cachedName)
            }
        }
        return profile
    }

    // Reset cache - call on logout
    fun clearCache() {
        prefs.edit().apply {
            remove(KEY_USER_NAME)
            putBoolean(KEY_USE_CACHED_NAME, false)
            apply()
        }
        Log.d("UserProfileManager", "User profile cache cleared")
    }
}