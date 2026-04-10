package com.tumejortarifaluz.data.local.dao

import androidx.room.*
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.data.local.entity.TariffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TariffDao {
    @Query("SELECT * FROM tariffs")
    fun getAllTariffs(): Flow<List<TariffEntity>>

    @Query("SELECT * FROM tariffs WHERE isFavorite = 1")
    fun getFavoriteTariffs(): Flow<List<TariffEntity>>

    @Query("SELECT id FROM tariffs WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<String>

    @Query("SELECT * FROM tariffs WHERE id = :id")
    fun findTariffById(id: String): Flow<TariffEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTariffs(tariffs: List<TariffEntity>)

    @Query("UPDATE tariffs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("DELETE FROM tariffs")
    suspend fun deleteAllTariffs()
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: HistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<HistoryEntity>)

    @Delete
    suspend fun deleteHistoryItem(item: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()
}
