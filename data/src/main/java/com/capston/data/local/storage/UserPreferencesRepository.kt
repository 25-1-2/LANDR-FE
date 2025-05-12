package com.capston.data.local.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    suspend fun saveUserName(name: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    fun getUserName(): Flow<String?> = context.userDataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY]
        }

    suspend fun hasUserName(): Boolean {
        var hasName = false
        getUserName().collect {
            hasName = !it.isNullOrEmpty()
        }
        return hasName
    }
}