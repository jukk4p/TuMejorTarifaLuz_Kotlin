package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.repository.ScanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ValidationField(
    val label: String,
    val value: String,
    val isEditable: Boolean = true
)

data class ValidationUiState(
    val fields: List<ValidationField> = emptyList(),
    val isLoading: Boolean = false,
    val editingFieldIndex: Int? = null
)

@HiltViewModel
class ValidationViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ValidationUiState())
    val uiState: StateFlow<ValidationUiState> = _uiState.asStateFlow()

    init {
        observeScanResult()
    }

    private fun observeScanResult() {
        scanRepository.scanResult.onEach { result ->
            if (result != null) {
                _uiState.update {
                    it.copy(
                        fields = listOf(
                            ValidationField("Compañía Actual", result.company ?: "No detectada"),
                            ValidationField("Importe Factura", result.totalAmount ?: "0,00 €"),
                            ValidationField("Días de Facturación", "${result.days} Días"),
                            ValidationField("Potencia P1 (Punta)", "${result.powerP1} kW"),
                            ValidationField("Potencia P2 (Valle)", "${result.powerP2} kW"),
                            ValidationField("Consumo Total", "${result.energyP1 + result.energyP2 + result.energyP3} kWh"),
                            ValidationField("CUPS", result.cups ?: "No detectado", isEditable = result.cups == null)
                        )
                    )
                }
            } else {
                loadExtractedData()
            }
        }.launchIn(viewModelScope)
    }

    private fun loadExtractedData() {
        _uiState.update { 
            it.copy(
                fields = listOf(
                    ValidationField("Compañía Actual", ""),
                    ValidationField("CUPS", "", isEditable = true),
                    ValidationField("Potencia Contratada", "4.6 kW"),
                    ValidationField("Consumo Último Mes", "0 kWh"),
                    ValidationField("Importe Factura", "0,00 €")
                )
            )
        }
    }

    fun updateField(index: Int, newValue: String) {
        _uiState.update { state ->
            val updatedFields = state.fields.toMutableList()
            updatedFields[index] = updatedFields[index].copy(value = newValue)
            state.copy(fields = updatedFields)
        }
    }

    fun setEditingField(index: Int?) {
        _uiState.update { it.copy(editingFieldIndex = index) }
    }

    fun saveAnalysisResult(onComplete: () -> Unit) {
        onComplete()
    }
}
