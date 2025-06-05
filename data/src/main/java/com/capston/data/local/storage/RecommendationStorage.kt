package com.capston.data.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.capston.domain.response.recommend.RecommendResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.recommendationDataStore: DataStore<Preferences> by preferencesDataStore(name = "recommendations")

@Singleton
class RecommendationStorage @Inject constructor(
    private val context: Context,
    private val gson: Gson
) {
    companion object {
        private val RECOMMENDATIONS_KEY = stringPreferencesKey("saved_recommendations")
    }

    // 추천 데이터 저장
    suspend fun saveRecommendations(recommendations: List<RecommendResponse>) {
        val jsonString = gson.toJson(recommendations)
        context.recommendationDataStore.edit { preferences ->
            preferences[RECOMMENDATIONS_KEY] = jsonString
        }
    }

    // 추천 데이터 불러오기
    fun getRecommendations(): Flow<List<RecommendResponse>> = context.recommendationDataStore.data
        .map { preferences ->
            val jsonString = preferences[RECOMMENDATIONS_KEY] ?: ""
            if (jsonString.isNotEmpty()) {
                try {
                    val type = object : TypeToken<List<RecommendResponse>>() {}.type
                    gson.fromJson<List<RecommendResponse>>(jsonString, type) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    // 추천 데이터 삭제
    suspend fun clearRecommendations() {
        context.recommendationDataStore.edit { preferences ->
            preferences.remove(RECOMMENDATIONS_KEY)
        }
    }
}