package com.tumejortarifaluz.data.repository

import com.tumejortarifaluz.data.local.dao.HistoryDao
import com.tumejortarifaluz.data.local.dao.TariffDao
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.tumejortarifaluz.data.auth.AuthRepository
import com.tumejortarifaluz.data.local.entity.TariffEntity
import com.tumejortarifaluz.ui.viewmodel.Tariff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.first
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TariffRepository @Inject constructor(
    private val tariffDao: TariffDao,
    private val firestore: FirebaseFirestore,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    fun getTariffs(): Flow<List<Tariff>> = tariffDao.getAllTariffs().map { entities ->
        entities.map { it.toUiModel() }
    }

    fun getFavoriteTariffs(): Flow<List<Tariff>> = tariffDao.getFavoriteTariffs().map { entities ->
        entities.map { it.toUiModel() }
    }

    fun getTariffById(id: String): Flow<Tariff?> = tariffDao.findTariffById(id).map { it?.toUiModel() }

    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        tariffDao.updateFavoriteStatus(id, isFavorite)
    }

    /**
     * Syncs tariffs from Firestore. Falls back to local mock data if unreachable.
     * Preserves existing favorite status across all syncs.
     */
    suspend fun syncTariffs() = withContext(Dispatchers.IO) {
        val favoriteIds = tariffDao.getFavoriteIds().toSet()
        val localVersion = userPreferencesRepository.userPreferencesFlow.first().tariffsVersion

        try {
            // Step 1: Check metadata version (1 read)
            val metadata = firestore.collection("metadata").document("tariffs_info").get().await()
            val cloudVersion = metadata.getLong("version")?.toInt() ?: 0

            Log.d("TariffSync", "Checking versions: Local=$localVersion, Cloud=$cloudVersion")

            if (cloudVersion <= localVersion && localVersion != 0) {
                Log.d("TariffSync", "Tariffs are up to date. Skipping full sync.")
                return@withContext
            }

            // Step 2: Fetch full collection only if version changed (N reads)
            Log.d("TariffSync", "Version mismatch or first sync. Fetching full collection...")
            val snapshot = firestore.collection("tariffs").get().await()
            if (!snapshot.isEmpty) {
                val entities = snapshot.documents.mapNotNull { doc ->
                    runCatching {
                        TariffEntity(
                            id = doc.id,
                            company = doc.getString("company") ?: "",
                            name = doc.getString("name") ?: "",
                            type = doc.getString("type") ?: "Fijo (1 Periodo)",
                            pricePowerP1 = doc.getDouble("pricePowerP1") ?: 0.0,
                            pricePowerP2 = doc.getDouble("pricePowerP2") ?: 0.0,
                            priceEnergyP1 = doc.getDouble("priceEnergyP1") ?: 0.0,
                            priceEnergyP2 = doc.getDouble("priceEnergyP2") ?: 0.0,
                            priceEnergyP3 = doc.getDouble("priceEnergyP3") ?: 0.0,
                            contractUrl = doc.getString("contractUrl") ?: "",
                            logoUrl = doc.getString("logoUrl"),
                            logoLightUrl = doc.getString("logoLightUrl"),
                            isFavorite = doc.id in favoriteIds,
                            permanence = doc.getBoolean("permanence") ?: false,
                            surplusPrice = doc.getDouble("surplusPrice") ?: 0.0,
                            updatedAt = doc.getString("updatedAt") ?: "",
                            pricePowerP1WithTaxes = doc.getDouble("pricePowerP1WithTaxes") ?: 0.0,
                            pricePowerP2WithTaxes = doc.getDouble("pricePowerP2WithTaxes") ?: 0.0,
                            priceEnergyP1WithTaxes = doc.getDouble("priceEnergyP1WithTaxes") ?: 0.0,
                            priceEnergyP2WithTaxes = doc.getDouble("priceEnergyP2WithTaxes") ?: 0.0,
                            priceEnergyP3WithTaxes = doc.getDouble("priceEnergyP3WithTaxes") ?: 0.0
                        )
                    }.getOrNull()
                }
                if (entities.isNotEmpty()) {
                    tariffDao.insertTariffs(entities)
                    userPreferencesRepository.updateTariffsVersion(cloudVersion)
                    Log.d("TariffSync", "Sync completed. Updated local version to $cloudVersion")
                    return@withContext
                }
            }
            // Firestore empty or returned no valid docs — fallback to local data
            loadLocalFallback(favoriteIds)
        } catch (e: Exception) {
            Log.e("TariffSync", "Sync failed: ${e.message}")
            e.printStackTrace()
            // No internet or Firestore error — use local mock data
            loadLocalFallback(favoriteIds)
        }
    }

    private suspend fun loadLocalFallback(favoriteIds: Set<String>) {
        val entities = com.tumejortarifaluz.data.local.TariffData.allTariffs.map { entity ->
            entity.copy(isFavorite = entity.id in favoriteIds)
        }
        tariffDao.insertTariffs(entities)
    }

    /**
     * One-time seed: uploads all local tariff data to Firestore.
     * Run this once from the app (e.g., from Settings) to populate the cloud database.
     */
    suspend fun seedFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        return@withContext try {
            val tariffs = com.tumejortarifaluz.data.local.TariffData.allTariffs
            val batch = firestore.batch()
            tariffs.forEach { tariff ->
                val ref = firestore.collection("tariffs").document(tariff.id)
                val data = hashMapOf(
                    "company" to tariff.company,
                    "name" to tariff.name,
                    "type" to tariff.type,
                    "pricePowerP1" to tariff.pricePowerP1,
                    "pricePowerP2" to tariff.pricePowerP2,
                    "priceEnergyP1" to tariff.priceEnergyP1,
                    "priceEnergyP2" to tariff.priceEnergyP2,
                    "priceEnergyP3" to tariff.priceEnergyP3,
                    "contractUrl" to tariff.contractUrl,
                    "logoUrl" to tariff.logoUrl,
                    "logoLightUrl" to tariff.logoLightUrl,
                    "permanence" to tariff.permanence,
                    "surplusPrice" to tariff.surplusPrice,
                    "updatedAt" to tariff.updatedAt,
                    "pricePowerP1WithTaxes" to tariff.pricePowerP1WithTaxes,
                    "pricePowerP2WithTaxes" to tariff.pricePowerP2WithTaxes,
                    "priceEnergyP1WithTaxes" to tariff.priceEnergyP1WithTaxes,
                    "priceEnergyP2WithTaxes" to tariff.priceEnergyP2WithTaxes,
                    "priceEnergyP3WithTaxes" to tariff.priceEnergyP3WithTaxes
                )
                batch.set(ref, data)
            }
            batch.commit().await()
            Result.success(tariffs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Legacy: inserts mock data locally, preserving favorites */
    suspend fun insertMockTariffs() = withContext(Dispatchers.IO) {
        val favoriteIds = tariffDao.getFavoriteIds().toSet()
        val entities = com.tumejortarifaluz.data.local.TariffData.allTariffs.map { entity ->
            entity.copy(isFavorite = entity.id in favoriteIds)
        }
        tariffDao.insertTariffs(entities)
    }
}


@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    fun getHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    /**
     * Saves a history item locally and pushes it to Firestore if logged in.
     * Returns the Firestore document ID so we can delete it later if needed.
     */
    suspend fun addHistoryItem(item: HistoryEntity) = withContext(Dispatchers.IO) {
        val generatedId = historyDao.insertHistoryItem(item)
        authRepository.getCurrentUserUid()?.let { uid ->
            try {
                val data = hashMapOf(
                    "date" to item.date,
                    "type" to item.type,
                    "status" to item.status,
                    "description" to item.description,
                    "company" to item.company,
                    "cups" to item.cups,
                    "powerP1" to item.powerP1,
                    "powerP2" to item.powerP2,
                    "energyP1" to item.energyP1,
                    "energyP2" to item.energyP2,
                    "energyP3" to item.energyP3,
                    "days" to item.days,
                    "totalAmount" to item.totalAmount,
                    "estimatedSaving" to item.estimatedSaving,
                    "localId" to generatedId.toInt()
                )
                firestore.collection("users")
                    .document(uid)
                    .collection("history")
                    .add(data)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Pulls history from Firestore ONLY if local DB is empty.
     * This avoids duplicates on the same device while still restoring
     * the full history on a fresh install or new device.
     */
    suspend fun loadFromCloud() = withContext(Dispatchers.IO) {
        val uid = authRepository.getCurrentUserUid() ?: return@withContext
        // Skip cloud load if local history already exists (prevents duplicates)
        val localCount = historyDao.getHistoryCount()
        if (localCount > 0) return@withContext
        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("history")
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            
            val entities = snapshot.documents.mapNotNull { doc ->
                runCatching {
                    HistoryEntity(
                        date = doc.getString("date") ?: "",
                        type = doc.getString("type") ?: "",
                        status = doc.getString("status") ?: "",
                        description = doc.getString("description") ?: "",
                        company = doc.getString("company"),
                        cups = doc.getString("cups"),
                        powerP1 = doc.getDouble("powerP1") ?: 3.5,
                        powerP2 = doc.getDouble("powerP2") ?: 3.5,
                        energyP1 = doc.getDouble("energyP1") ?: 0.0,
                        energyP2 = doc.getDouble("energyP2") ?: 0.0,
                        energyP3 = doc.getDouble("energyP3") ?: 0.0,
                        days = (doc.getLong("days") ?: 30).toInt(),
                        totalAmount = doc.getString("totalAmount"),
                        estimatedSaving = doc.getString("estimatedSaving")
                    )
                }.getOrNull()
            }
            if (entities.isNotEmpty()) {
                historyDao.insertAll(entities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteHistoryItem(item: HistoryEntity) = withContext(Dispatchers.IO) {
        historyDao.deleteHistoryItem(item)
        // Also delete from Firestore
        authRepository.getCurrentUserUid()?.let { uid ->
            try {
                // Primary strategy: Match exactly by localId
                val docs = firestore.collection("users")
                    .document(uid)
                    .collection("history")
                    .whereEqualTo("localId", item.id)
                    .get().await()
                
                if (docs.isEmpty) {
                    // Secondary strategy: Match by metadata (for old/sync-error items)
                    val fallbackDocs = firestore.collection("users")
                        .document(uid)
                        .collection("history")
                        .whereEqualTo("date", item.date)
                        .whereEqualTo("totalAmount", item.totalAmount)
                        .get().await()
                    fallbackDocs.documents.forEach { it.reference.delete() }
                } else {
                    docs.documents.forEach { it.reference.delete() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        historyDao.deleteAllHistory()
        authRepository.getCurrentUserUid()?.let { uid ->
            try {
                val docs = firestore.collection("users")
                    .document(uid)
                    .collection("history")
                    .get().await()
                val batch = firestore.batch()
                docs.documents.forEach { batch.delete(it.reference) }
                batch.commit().await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

/**
 * Syncs user favorite tariff IDs to Firestore under users/{uid}/favorites.
 * This allows favorited tariffs to persist across devices.
 */
@Singleton
class FavoritesRepository @Inject constructor(
    private val tariffDao: TariffDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {
    /**
     * Saves a favorite status change locally and pushes to Firestore.
     */
    suspend fun updateFavorite(tariffId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        tariffDao.updateFavoriteStatus(tariffId, isFavorite)
        authRepository.getCurrentUserUid()?.let { uid ->
            try {
                val ref = firestore.collection("users")
                    .document(uid)
                    .collection("favorites")
                    .document(tariffId)
                if (isFavorite) {
                    ref.set(mapOf("tariffId" to tariffId, "savedAt" to System.currentTimeMillis())).await()
                } else {
                    ref.delete().await()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Loads favorite tariff IDs from Firestore and applies them to local Room DB.
     * Called on login or app start.
     */
    suspend fun loadFavoritesFromCloud() = withContext(Dispatchers.IO) {
        val uid = authRepository.getCurrentUserUid() ?: return@withContext
        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .collection("favorites")
                .get().await()
            val cloudFavoriteIds = snapshot.documents.map { it.id }.toSet()
            // Get all local tariffs and update favorite status based on cloud data
            val localFavoriteIds = tariffDao.getFavoriteIds().toSet()
            val toAdd = cloudFavoriteIds - localFavoriteIds
            val toRemove = localFavoriteIds - cloudFavoriteIds
            toAdd.forEach { tariffDao.updateFavoriteStatus(it, true) }
            toRemove.forEach { tariffDao.updateFavoriteStatus(it, false) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


// Mapper extensions
fun TariffEntity.toUiModel() = Tariff(
    id = id,
    company = company,
    name = name,
    type = type,
    pricePowerP1 = pricePowerP1,
    pricePowerP2 = pricePowerP2,
    priceEnergyP1 = priceEnergyP1,
    priceEnergyP2 = priceEnergyP2,
    priceEnergyP3 = priceEnergyP3,
    contractUrl = contractUrl,
    logoUrl = logoUrl,
    logoLightUrl = logoLightUrl,
    isFavorite = isFavorite,
    permanence = permanence,
    surplusPrice = surplusPrice,
    updatedAt = updatedAt,
    pricePowerP1WithTaxes = pricePowerP1WithTaxes,
    pricePowerP2WithTaxes = pricePowerP2WithTaxes,
    priceEnergyP1WithTaxes = priceEnergyP1WithTaxes,
    priceEnergyP2WithTaxes = priceEnergyP2WithTaxes,
    priceEnergyP3WithTaxes = priceEnergyP3WithTaxes
)
