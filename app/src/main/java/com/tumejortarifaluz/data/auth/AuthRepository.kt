package com.tumejortarifaluz.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val _authState = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(firebaseAuth.currentUser)
    val authState: StateFlow<com.google.firebase.auth.FirebaseUser?> = _authState.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _authState.value = auth.currentUser
        }
    }

    fun isUserLoggedIn(): Boolean {
        return try {
            firebaseAuth.currentUser != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val cleanedEmail = email.trim().lowercase()
            firebaseAuth.signInWithEmailAndPassword(cleanedEmail, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            val cleanedEmail = email.trim().lowercase()
            firebaseAuth.createUserWithEmailAndPassword(cleanedEmail, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentUserEmail(): String? {
        return try {
            firebaseAuth.currentUser?.email
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUserUid(): String? {
        return try {
            firebaseAuth.currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendPasswordResetEmail(): Result<Unit> {
        return try {
            val email = getCurrentUserEmail()
                ?: return Result.failure(Exception("No hay usuario logueado"))
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
