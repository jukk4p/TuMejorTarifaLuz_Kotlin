package com.tumejortarifaluz.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tumejortarifaluz.data.repository.ScanRepository
import com.tumejortarifaluz.data.repository.HistoryRepository
import com.tumejortarifaluz.data.local.entity.HistoryEntity
import com.tumejortarifaluz.domain.processor.InvoiceProcessor
import com.tumejortarifaluz.data.auth.AuthRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UploadUiState(
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val statusMessage: String = "",
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val scanResult: com.tumejortarifaluz.domain.model.ProcessedInvoice? = null
)

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val invoiceProcessor: InvoiceProcessor,
    private val scanRepository: ScanRepository,
    private val authRepository: AuthRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        _uiState.update {
            it.copy(
                isLoggedIn = authRepository.isUserLoggedIn(),
                userEmail = authRepository.getCurrentUserEmail()
            )
        }
    }

    fun startUpload(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, progress = 0f, statusMessage = "Subiendo archivo...") }
            
            // Simulate progress stages
            val stages = listOf(
                "Analizando factura..." to 0.4f,
                "Extrayendo datos..." to 0.7f,
                "Validando con el mercado..." to 0.9f
            )

            for ((msg, progress) in stages) {
                delay(800)
                _uiState.update { it.copy(statusMessage = msg, progress = progress) }
            }

            // Real OCR processing
            val result = invoiceProcessor.processImage(bitmap)
            scanRepository.setScanResult(result)
            
            delay(500)
            _uiState.update { it.copy(
                progress = 1f, 
                isUploading = false, 
                scanResult = result
            ) }
            
            // Allow user to REVIEW results for 4 seconds before auto-advancing
            delay(4000)
            _uiState.update { it.copy(isComplete = true) }
        }
    }
}
