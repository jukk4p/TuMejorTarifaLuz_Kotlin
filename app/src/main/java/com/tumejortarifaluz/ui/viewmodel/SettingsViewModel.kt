package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.repository.UserPreferencesRepository
import com.tumejortarifaluz.data.repository.TariffRepository
import com.tumejortarifaluz.data.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val userName: String = "Usuario",
    val userEmail: String = "usuario@ejemplo.com",
    val notificationsEnabled: Boolean = true,
    val biometricsEnabled: Boolean = false,
    val language: String = "Español",
    val appVersion: String = "1.0.0 (build 24)"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val authRepository: AuthRepository,
    private val tariffRepository: TariffRepository
) : ViewModel() {
    
    val uiState: StateFlow<SettingsUiState> = userPreferencesRepository.userPreferencesFlow.map { prefs ->
        val currentUserEmail = authRepository.getCurrentUserEmail() ?: "usuario@ejemplo.com"
        val userName = currentUserEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Usuario"
        SettingsUiState(
            userName = userName,
            userEmail = currentUserEmail,
            notificationsEnabled = prefs.notificationsEnabled,
            biometricsEnabled = prefs.biometricsEnabled,
            language = prefs.language,
            appVersion = "1.1.7 (build 31)"
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateNotifications(enabled)
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateBiometrics(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguage(language)
        }
    }

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun seedTariffsToFirestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            _snackbarMessage.value = "⏳ Sincronizando tarifas con la nube..."
            val result = tariffRepository.seedFirestore()
            _isSyncing.value = false
            if (result.isSuccess) {
                _snackbarMessage.value = "✅ ¡Sincronización completada! ${result.getOrNull()} tarifas subidas."
            } else {
                _snackbarMessage.value = "❌ Error en sincronización: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun sendPasswordReset() {
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail()
            if (result.isSuccess) {
                _snackbarMessage.value = "✅ Email de restablecimiento enviado a ${authRepository.getCurrentUserEmail()}"
            } else {
                _snackbarMessage.value = "❌ Error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
