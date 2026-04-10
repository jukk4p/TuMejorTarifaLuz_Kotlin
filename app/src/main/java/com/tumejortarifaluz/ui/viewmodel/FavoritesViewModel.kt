package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.repository.FavoritesRepository
import com.tumejortarifaluz.data.repository.ScanRepository
import com.tumejortarifaluz.data.repository.TariffRepository
import com.tumejortarifaluz.domain.calculator.TariffCalculator
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val favoriteTariffs: List<Tariff> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val tariffRepository: TariffRepository,
    private val favoritesRepository: FavoritesRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        combine(
            tariffRepository.getFavoriteTariffs(),
            scanRepository.scanResult
        ) { favorites, scanResult ->
            val invoice = scanResult ?: ProcessedInvoice()
            
            val updatedFavorites = favorites.map { tariff ->
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
                    estimatedSaving = String.format("%.2f €", savingValue * 12)
                )
            }
            
            _uiState.update { it.copy(favoriteTariffs = updatedFavorites) }
        }.launchIn(viewModelScope)
    }

    fun toggleFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            favoritesRepository.updateFavorite(id, isFavorite)
        }
    }
}
