package com.capston.data.network

import android.content.Context
import android.content.SharedPreferences
import com.capston.domain.response.user.UserProfileResponse
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileInterceptor @Inject constructor(
    private val context: Context
) {
    private val PREFS_NAME = "user_profile_prefs"
    private val KEY_LAST_NAME = "last_updated_name"

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Property that reads/writes to SharedPreferences
    var lastUpdatedName: String?
        get() = prefs.getString(KEY_LAST_NAME, null)
        set(value) {
            prefs.edit().apply {
                if (value == null) {
                    remove(KEY_LAST_NAME)
                } else {
                    putString(KEY_LAST_NAME, value)
                }
                apply()
            }
        }

    fun createInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            // Only intercept user profile responses
            if (request.url.toString().contains("/api/users/me") &&
                response.isSuccessful &&
                lastUpdatedName != null) {

                // Extract the body
                val originalBody = response.body?.string()
                originalBody?.let {
                    try {
                        val gson = Gson()
                        val userProfile = gson.fromJson(it, UserProfileResponse::class.java)

                        // Override the name with our cached value
                        userProfile.name = lastUpdatedName!!

                        // Convert back to JSON
                        val modifiedBody = gson.toJson(userProfile)

                        // Create a new response with modified body
                        return@Interceptor response.newBuilder()
                            .body(modifiedBody.toResponseBody(response.body?.contentType()))
                            .build()
                    } catch (e: Exception) {
                        // In case of parsing issues, return original response
                    }
                }
            }

            response
        }
    }
}