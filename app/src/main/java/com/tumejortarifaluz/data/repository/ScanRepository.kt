package com.tumejortarifaluz.data.repository

import com.tumejortarifaluz.domain.model.ProcessedInvoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor() {
    private val _scanResult = MutableStateFlow<ProcessedInvoice?>(null)
    val scanResult: StateFlow<ProcessedInvoice?> = _scanResult.asStateFlow()

    fun setScanResult(result: ProcessedInvoice) {
        _scanResult.value = result
    }

    fun clearScanResult() {
        _scanResult.value = null
    }
}
