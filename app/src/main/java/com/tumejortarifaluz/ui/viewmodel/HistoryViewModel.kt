package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.repository.ScanRepository
import com.tumejortarifaluz.domain.model.ProcessedInvoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val items: List<HistoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val scanRepository: ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        historyRepository.getHistory().onEach { items ->
            _uiState.update { it.copy(items = items) }
        }.launchIn(viewModelScope)
    }

    fun openHistoryItem(item: HistoryEntity, onNavigate: () -> Unit) {
        val processedInvoice = ProcessedInvoice(
            company = item.company,
            cups = item.cups,
            powerP1 = item.powerP1,
            powerP2 = item.powerP2,
            energyP1 = item.energyP1,
            energyP2 = item.energyP2,
            energyP3 = item.energyP3,
            days = item.days,
            totalAmount = item.totalAmount
        )
        scanRepository.setScanResult(processedInvoice)
        onNavigate()
    }

    fun deleteItem(item: HistoryEntity) {
        viewModelScope.launch {
            historyRepository.deleteHistoryItem(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearAllHistory()
        }
    }
}
