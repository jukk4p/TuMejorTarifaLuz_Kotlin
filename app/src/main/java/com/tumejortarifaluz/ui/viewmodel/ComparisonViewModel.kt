package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.repository.ScanRepository
import com.tumejortarifaluz.data.repository.TariffRepository
import com.tumejortarifaluz.domain.calculator.TariffCalculator
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ComparisonDetail(
    val concept: String,
    val current: String,
    val new: String,
    val isTotal: Boolean = false
)

data class ComparisonUiState(
    val tariff: Tariff? = null,
    val details: List<ComparisonDetail> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val tariffRepository: TariffRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ComparisonUiState())
    val uiState: StateFlow<ComparisonUiState> = _uiState.asStateFlow()

    fun loadComparison(tariffId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                tariffRepository.getTariffById(tariffId),
                scanRepository.scanResult
            ) { tariff, scanResult ->
                if (tariff != null) {
                    val invoice = scanResult ?: ProcessedInvoice()
                    val result = TariffCalculator.calculateBill(
                        tariff = tariff,
                        days = invoice.days,
                        powerP1 = invoice.powerP1,
                        powerP2 = invoice.powerP2,
                        energyP1 = invoice.energyP1,
                        energyP2 = invoice.energyP2,
                        energyP3 = invoice.energyP3
                    )

                    // Current invoice estimation logic (consistent with ResultsViewModel)
                    val currentAmountValue = invoice.totalAmount?.replace(",", ".")?.replace(" €", "")?.toDoubleOrNull() ?: 150.0
                    val savingMonthly = currentAmountValue - result.totalBill
                    
                    _uiState.update { 
                        it.copy(
                            tariff = tariff.copy(
                                totalBill = String.format("%.2f €", result.totalBill),
                                estimatedSaving = String.format("%.2f €", savingMonthly * 12),
                                estimatedSavingMonthly = String.format("%.2f €", savingMonthly)
                            ),
                            details = listOf(
                                ComparisonDetail("Término Potencia", String.format("%.2f €", result.powerCost * 1.15), String.format("%.2f €", result.powerCost)),
                                ComparisonDetail("Término Energía", String.format("%.2f €", result.energyCost * 1.3), String.format("%.2f €", result.energyCost)),
                                ComparisonDetail("Alquiler Contador", "0,81 €", String.format("%.2f €", result.counterRental)),
                                ComparisonDetail("Bono Social", "1,14 €", String.format("%.2f €", result.socialBono)),
                                ComparisonDetail("Impuesto Eléctrico", String.format("%.2f €", result.taxIEE * 1.25), String.format("%.2f €", result.taxIEE)),
                                ComparisonDetail("IVA (10%)", String.format("%.2f €", result.taxIVA * 1.25), String.format("%.2f €", result.taxIVA)),
                                ComparisonDetail("Total Factura", String.format("%.2f €", currentAmountValue), String.format("%.2f €", result.totalBill), isTotal = true)
                            ),
                            isLoading = false
                        )
                    }
                }
            }.collect()
        }
    }
}
