package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.auth.AuthRepository
import com.tumejortarifaluz.data.repository.UserRepository
import com.tumejortarifaluz.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val nameError: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        val emailErr = validateEmail(email)
        val passwordErr = validatePassword(password)
        
        if (emailErr != null || passwordErr != null) {
            _uiState.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, emailError = null, passwordError = null) }
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Login failed") }
            }
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        val nameErr = if (name.isBlank()) "El nombre no puede estar vacío" else null
        val emailErr = validateEmail(email)
        val passwordErr = validatePassword(password)
        val confirmErr = when {
            confirmPassword.isBlank() -> "Debes confirmar la contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
        
        if (nameErr != null || emailErr != null || passwordErr != null || confirmErr != null) {
            _uiState.update { it.copy(nameError = nameErr, emailError = emailErr, passwordError = passwordErr, error = confirmErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, nameError = null, emailError = null, passwordError = null) }
            val result = authRepository.register(email, password)
            if (result.isSuccess) {
                // Save profile to Firestore
                val uid = authRepository.getCurrentUserUid() ?: email.replace(".", "_")
                val profile = UserProfile(
                    uid = uid,
                    email = email,
                    name = name
                )
                userRepository.saveUserProfile(profile)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Registration failed") }
            }
        }
    }

    private fun validateEmail(email: String): String? {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}$".toRegex()
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            !email.matches(emailRegex) -> "Formato de correo inválido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }
}
