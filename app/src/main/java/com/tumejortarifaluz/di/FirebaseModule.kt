package com.tumejortarifaluz.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            // Return a "safe" instance or just let it crash elsewhere if absolutely needed,
            // but for now, we want to avoid the crash on startup.
            // In a real app, we might check for initialization.
            FirebaseAuth.getInstance() // Re-calling as a placeholder, but in practice, 
                                       // if it throws here, we need a better recovery or mock.
        }
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            FirebaseFirestore.getInstance()
        }
    }
}
