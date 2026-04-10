package com.tumejortarifaluz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.util.ConnectivityObserver
import com.tumejortarifaluz.util.NetworkStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    val isOffline: StateFlow<Boolean> = connectivityObserver.networkStatus
        .map { it == NetworkStatus.Unavailable }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
