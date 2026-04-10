package com.tumejortarifaluz.di

import android.content.Context
import androidx.room.Room
import com.tumejortarifaluz.data.local.AhorroLuzDatabase
import com.tumejortarifaluz.data.local.dao.HistoryDao
import com.tumejortarifaluz.data.local.dao.TariffDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AhorroLuzDatabase {
        return Room.databaseBuilder(
            context,
            AhorroLuzDatabase::class.java,
            "ahorroluz_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTariffDao(db: AhorroLuzDatabase): TariffDao = db.tariffDao()

    @Provides
    fun provideHistoryDao(db: AhorroLuzDatabase): HistoryDao = db.historyDao()
}
