package com.tumejortarifaluz.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val BIOMETRICS_ENABLED = booleanPreferencesKey("biometrics_enabled")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val TARIFFS_VERSION = intPreferencesKey("tariffs_version")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
                biometricsEnabled = preferences[PreferencesKeys.BIOMETRICS_ENABLED] ?: false,
                language = preferences[PreferencesKeys.SELECTED_LANGUAGE] ?: "Español",
                isDarkTheme = preferences[PreferencesKeys.IS_DARK_THEME] ?: true,
                tariffsVersion = preferences[PreferencesKeys.TARIFFS_VERSION] ?: 0
            )
        }

    suspend fun updateNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateBiometrics(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BIOMETRICS_ENABLED] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language
        }
    }

    suspend fun updateDarkTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] = isDark
        }
    }

    suspend fun updateTariffsVersion(version: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TARIFFS_VERSION] = version
        }
    }

    val isDarkThemeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_THEME] ?: true
        }
}

data class UserPreferences(
    val notificationsEnabled: Boolean,
    val biometricsEnabled: Boolean,
    val language: String,
    val isDarkTheme: Boolean = true,
    val tariffsVersion: Int = 0
)
