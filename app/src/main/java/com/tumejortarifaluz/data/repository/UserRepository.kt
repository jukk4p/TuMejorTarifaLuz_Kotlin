package com.tumejortarifaluz.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tumejortarifaluz.domain.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val snapshot = userCollection.document(uid).get().await()
            snapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            userCollection.document(profile.uid).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCups(uid: String, cups: String) {
        try {
            userCollection.document(uid).update("cups", cups).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveFcmToken(uid: String, token: String) {
        try {
            userCollection.document(uid).update("fcmToken", token).await()
        } catch (e: Exception) {
            // If document doesn't exist yet, set it
            try {
                userCollection.document(uid).set(mapOf("fcmToken" to token)).await()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }
}
