package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.auth.AuthRepository
import com.tumejortarifaluz.data.repository.FavoritesRepository
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.repository.TariffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val totalSaving: String = "0,00 €",
    val monthlyConsumption: String = "0 kWh",
    val lastAnalysisDate: String = "No analizado",
    val currentPrice: Double = 0.1759,
    val priceZone: String = "Precio normal",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tariffRepository: TariffRepository,
    private val historyRepository: HistoryRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _internalState = MutableStateFlow(HomeUiState())
    
    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.authState,
        _internalState
    ) { user, internal ->
        internal.copy(isLoggedIn = user != null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoggedIn = authRepository.isUserLoggedIn())
    )

    init {
        // Initial sync
        syncData()
        // Re-sync on auth changes
        observeAuth()
        // Observe local history for dynamic updates
        observeHistory()
    }

    private fun observeHistory() {
        historyRepository.getHistory().onEach { history ->
            val lastItem = history.firstOrNull { it.estimatedSaving != null }
            if (lastItem != null) {
                _internalState.update {
                    it.copy(
                        totalSaving = lastItem.estimatedSaving ?: "0,00 €",
                        lastAnalysisDate = lastItem.date
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observeAuth() {
        viewModelScope.launch {
            authRepository.authState.collectLatest { user ->
                if (user != null) {
                    syncData()
                }
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            _internalState.update { it.copy(isLoading = true) }
            // Sync tariffs from Firestore (with local fallback)
            tariffRepository.syncTariffs()
            // If logged in, restore cloud data in parallel
            if (authRepository.isUserLoggedIn()) {
                launch { historyRepository.loadFromCloud() }
                launch { favoritesRepository.loadFavoritesFromCloud() }
            }
            _internalState.update {
                it.copy(
                    isLoading = false,
                    totalSaving = "124,50 €",
                    monthlyConsumption = "245 kWh",
                    lastAnalysisDate = "02 Mar 2026"
                )
            }
        }
    }
}
