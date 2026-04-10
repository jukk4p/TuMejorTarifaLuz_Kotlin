package com.tumejortarifaluz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tumejortarifaluz.data.local.dao.HistoryDao
import com.tumejortarifaluz.data.local.dao.TariffDao
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.data.local.entity.TariffEntity

@Database(entities = [TariffEntity::class, HistoryEntity::class], version = 5, exportSchema = false)
abstract class AhorroLuzDatabase : RoomDatabase() {
    abstract fun tariffDao(): TariffDao
    abstract fun historyDao(): HistoryDao
}
