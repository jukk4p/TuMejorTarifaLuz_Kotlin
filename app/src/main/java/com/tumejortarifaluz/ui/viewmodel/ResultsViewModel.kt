package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.repository.FavoritesRepository
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.repository.TariffRepository
import com.tumejortarifaluz.domain.calculator.TariffCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TariffFilter(val label: String, val emoji: String) {
    ALL("Todas", "⚡"),
    TOP4("Top 4", "🏆"),
    FIXED("Precio Fijo", "🔒"),
    THREE_PERIOD("Discriminación Horaria", "🕐")
}

data class Tariff(
    val id: String,
    val company: String,
    val name: String,
    val type: String,
    val pricePowerP1: Double,
    val pricePowerP2: Double,
    val priceEnergyP1: Double,
    val priceEnergyP2: Double,
    val priceEnergyP3: Double,
    val totalBill: String = "0,00 €",
    val totalBillValue: Double = 0.0,
    val estimatedSaving: String = "0,00 €",
    val estimatedSavingMonthly: String = "0,00 €",
    val contractUrl: String = "",
    val logoUrl: String? = null,
    val logoLightUrl: String? = null,
    val isFavorite: Boolean = false,
    val permanence: Boolean = false,
    val surplusPrice: Double = 0.0,
    val updatedAt: String = "",
    val pricePowerP1WithTaxes: Double = 0.0,
    val pricePowerP2WithTaxes: Double = 0.0,
    val priceEnergyP1WithTaxes: Double = 0.0,
    val priceEnergyP2WithTaxes: Double = 0.0,
    val priceEnergyP3WithTaxes: Double = 0.0
)


data class ResultsUiState(
    val tariffs: List<Tariff> = emptyList(),
    val allCalculatedTariffs: List<Tariff> = emptyList(),
    val isLoading: Boolean = false,
    val totalPossibleSaving: String = "0,00 €",
    val currentInvoice: ProcessedInvoice? = null,
    val selectedFilter: TariffFilter = TariffFilter.ALL,
    val filterCounts: Map<TariffFilter, Int> = emptyMap()
)

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val tariffRepository: TariffRepository,
    private val favoritesRepository: FavoritesRepository,
    private val historyRepository: HistoryRepository,
    private val scanRepository: com.tumejortarifaluz.data.repository.ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ResultsUiState())
    val uiState: StateFlow<ResultsUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TariffFilter.ALL)

    init {
        observeTariffsAndScan()
        refreshTariffs()
    }

    private fun refreshTariffs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            tariffRepository.syncTariffs()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeTariffsAndScan() {
        combine(
            tariffRepository.getTariffs(),
            scanRepository.scanResult,
            _selectedFilter
        ) { tariffs, scanResult, filter ->
            val invoice = scanResult ?: com.tumejortarifaluz.domain.model.ProcessedInvoice()

            // Calculate bill for each tariff and sort cheapest first
            val allCalculated = tariffs.map { tariff ->
                val result = TariffCalculator.calculateBill(
                    tariff = tariff,
                    days = invoice.days,
                    powerP1 = invoice.powerP1,
                    powerP2 = invoice.powerP2,
                    energyP1 = invoice.energyP1,
                    energyP2 = invoice.energyP2,
                    energyP3 = invoice.energyP3
                )
                val currentBillValue = invoice.totalAmount?.replace(",", ".")?.replace(" €", "")?.toDoubleOrNull() ?: 150.0
                val savingValue = currentBillValue - result.totalBill
                tariff.copy(
                    totalBill = String.format("%.2f €", result.totalBill),
                    totalBillValue = result.totalBill,
                    estimatedSaving = String.format("%.2f €", savingValue * 12),
                    estimatedSavingMonthly = String.format("%.2f €", savingValue)
                )
            }.sortedBy { it.totalBillValue }

            // Pre-compute counts for each filter
            val counts = TariffFilter.entries.associateWith { f ->
                allCalculated.count { t -> matchesFilter(t, f) }
            }

            // Apply selected filter
            val filtered = if (filter == TariffFilter.TOP4) {
                allCalculated.take(4) // Top 4 cheapest
            } else {
                allCalculated.filter { matchesFilter(it, filter) }
            }

            _uiState.update {
                it.copy(
                    tariffs = filtered,
                    allCalculatedTariffs = allCalculated,
                    totalPossibleSaving = allCalculated.firstOrNull()?.estimatedSaving ?: "0,00 €",
                    currentInvoice = invoice,
                    selectedFilter = filter,
                    filterCounts = counts
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun matchesFilter(tariff: Tariff, filter: TariffFilter): Boolean = when (filter) {
        TariffFilter.ALL -> true
        TariffFilter.TOP4 -> true // handled via take(4) above
        TariffFilter.FIXED -> tariff.type.contains("Fijo", ignoreCase = true) &&
                !tariff.type.contains("3 Periodos", ignoreCase = true) &&
                !tariff.type.contains("Discriminación", ignoreCase = true)
        TariffFilter.THREE_PERIOD -> tariff.type.contains("3 Periodos", ignoreCase = true) ||
                tariff.type.contains("Discriminación", ignoreCase = true) ||
                tariff.type.contains("DH", ignoreCase = true)
    }

    fun updateFilter(filter: TariffFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(tariff: Tariff) {
        viewModelScope.launch {
            favoritesRepository.updateFavorite(tariff.id, !tariff.isFavorite)
        }
    }

    fun saveAnalysis() {
        val currentState = _uiState.value
        val invoice = currentState.currentInvoice ?: return
        val bestTariff = currentState.tariffs.firstOrNull() ?: return

        viewModelScope.launch {
            val historyItem = com.tumejortarifaluz.data.local.entity.HistoryEntity(
                date = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
                type = "Comparativa Completa",
                status = "Guardado",
                description = "Análisis guardado desde Resultados",
                company = bestTariff.company,
                powerP1 = invoice.powerP1,
                powerP2 = invoice.powerP2,
                energyP1 = invoice.energyP1,
                energyP2 = invoice.energyP2,
                energyP3 = invoice.energyP3,
                days = invoice.days,
                totalAmount = bestTariff.totalBill,
                estimatedSaving = bestTariff.estimatedSavingMonthly
            )
            historyRepository.addHistoryItem(historyItem)
        }
    }
}
