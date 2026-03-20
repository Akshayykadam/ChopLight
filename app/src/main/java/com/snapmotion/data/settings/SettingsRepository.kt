package com.snapmotion.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snapmotion_minimal_settings")

/**
 * Simplified repository solely for tracking if the background service is enabled by the user.
 */
class SettingsRepository(private val context: Context) {

    companion object {
        val KEY_MASTER_ENABLED = booleanPreferencesKey("master_enabled")
    }

    val masterEnabled: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_MASTER_ENABLED] ?: true
        }

    suspend fun setMasterEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_MASTER_ENABLED] = enabled
        }
    }
}
