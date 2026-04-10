package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.repository.ScanRepository
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ManualEntryUiState(
    val company: String = "",
    val cups: String = "",
    val powerP1: String = "3.5",
    val powerP2: String = "3.5",
    val energyP1: String = "50",
    val energyP2: String = "50",
    val energyP3: String = "100",
    val days: String = "30",
    val totalAmount: String = "70",
    val error: String? = null,
    val cupsError: String? = null,
    val powerError: String? = null,
    val energyError: String? = null
)

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val scanRepository: ScanRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManualEntryUiState())
    val uiState: StateFlow<ManualEntryUiState> = _uiState.asStateFlow()

    init {
        loadExistingData()
    }

    private fun loadExistingData() {
        scanRepository.scanResult.value?.let { result ->
            _uiState.update { 
                it.copy(
                    company = result.company ?: "",
                    cups = result.cups ?: "",
                    powerP1 = result.powerP1.toString(),
                    powerP2 = result.powerP2.toString(),
                    energyP1 = result.energyP1.toString(),
                    energyP2 = result.energyP2.toString(),
                    energyP3 = result.energyP3.toString(),
                    days = result.days.toString(),
                    totalAmount = result.totalAmount?.replace(" €", "") ?: ""
                )
            }
        }
    }

    fun updateCompany(value: String) = _uiState.update { it.copy(company = value) }
    fun updateCups(value: String) = _uiState.update { it.copy(cups = value) }
    fun updatePowerP1(value: String) = _uiState.update { it.copy(powerP1 = value) }
    fun updatePowerP2(value: String) = _uiState.update { it.copy(powerP2 = value) }
    fun updateEnergyP1(value: String) = _uiState.update { it.copy(energyP1 = value) }
    fun updateEnergyP2(value: String) = _uiState.update { it.copy(energyP2 = value) }
    fun updateEnergyP3(value: String) = _uiState.update { it.copy(energyP3 = value) }
    fun updateDays(value: String) = _uiState.update { it.copy(days = value) }
    fun updateTotalAmount(value: String) = _uiState.update { it.copy(totalAmount = value) }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validation logic
        val cupsErr = if (state.cups.isNotBlank()) {
            val cupsRegex = "^ES[0-9]{20,22}$".toRegex()
            if (!state.cups.uppercase().matches(cupsRegex)) "El CUPS debe empezar por ES y tener 20 o 22 números" else null
        } else null

        val p1 = state.powerP1.toDoubleOrNull()
        val p2 = state.powerP2.toDoubleOrNull()
        val powerErr = if (p1 == null || p1 <= 0 || p2 == null || p2 <= 0) "Las potencias deben ser números positivos" else null

        val e1 = state.energyP1.toDoubleOrNull()
        val e2 = state.energyP2.toDoubleOrNull()
        val e3 = state.energyP3.toDoubleOrNull()
        val energyErr = if (e1 == null || e1 < 0 || e2 == null || e2 < 0 || e3 == null || e3 < 0) "Los consumos deben ser números positivos" else null

        if (cupsErr != null || powerErr != null || energyErr != null) {
            _uiState.update { it.copy(cupsError = cupsErr, powerError = powerErr, energyError = energyErr) }
            return
        }

        val result = ProcessedInvoice(
            company = state.company,
            cups = state.cups.takeIf { it.isNotBlank() }?.uppercase(),
            powerP1 = p1 ?: 3.5,
            powerP2 = p2 ?: 3.5,
            energyP1 = e1 ?: 50.0,
            energyP2 = e2 ?: 50.0,
            energyP3 = e3 ?: 100.0,
            days = state.days.toIntOrNull() ?: 30,
            totalAmount = state.totalAmount.takeIf { it.isNotBlank() }?.let { if (it.contains("€")) it else "$it €" } ?: "70,00 €"
        )
        scanRepository.setScanResult(result)
        
        onSuccess()
    }
}
