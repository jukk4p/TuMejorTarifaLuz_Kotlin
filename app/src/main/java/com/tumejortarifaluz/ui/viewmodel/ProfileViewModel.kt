package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.tumejortarifaluz.data.auth.AuthRepository
import com.tumejortarifaluz.data.repository.UserRepository
import com.tumejortarifaluz.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val cups: String = "",
    val currentCompany: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val notificationsEnabled: Boolean = false,
    val fcmToken: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        loadFcmToken()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val uid = authRepository.getCurrentUserUid()
            val email = authRepository.getCurrentUserEmail() ?: ""
            if (uid != null) {
                val profile = userRepository.getUserProfile(uid)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = profile?.name ?: email.split("@").firstOrNull()?.replaceFirstChar { c -> c.uppercase() } ?: "",
                        email = email,
                        cups = profile?.cups ?: "",
                        currentCompany = profile?.currentCompany ?: ""
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, email = email) }
            }
        }
    }

    private fun loadFcmToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                _uiState.update { it.copy(fcmToken = token) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onCupsChange(value: String) = _uiState.update { it.copy(cups = value) }
    fun onCompanyChange(value: String) = _uiState.update { it.copy(currentCompany = value) }

    fun saveProfile() {
        viewModelScope.launch {
            val uid = authRepository.getCurrentUserUid() ?: return@launch
            _uiState.update { it.copy(isSaving = true, error = null) }
            val profile = UserProfile(
                uid = uid,
                email = _uiState.value.email,
                name = _uiState.value.name,
                cups = _uiState.value.cups,
                currentCompany = _uiState.value.currentCompany
            )
            val result = userRepository.saveUserProfile(profile)
            if (result.isSuccess) {
                // Also save FCM token to Firestore for server-side push
                if (_uiState.value.fcmToken.isNotBlank()) {
                    userRepository.saveFcmToken(uid, _uiState.value.fcmToken)
                }
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = result.exceptionOrNull()?.message ?: "Error al guardar"
                    )
                }
            }
        }
    }

    fun clearSuccess() = _uiState.update { it.copy(saveSuccess = false) }
}
