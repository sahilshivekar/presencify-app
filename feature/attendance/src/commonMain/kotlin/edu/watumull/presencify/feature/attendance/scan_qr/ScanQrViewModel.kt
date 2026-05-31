package edu.watumull.presencify.feature.attendance.scan_qr

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.domain.NtpClock
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.dynamic_qr.DynamicQRCipher
import kotlinx.coroutines.launch
import kotlin.math.abs

class ScanQrViewModel(
    private val ntpClock: NtpClock
) : BaseViewModel<ScanQrState, ScanQrEvent, ScanQrAction>(
    initialState = ScanQrState()
) {
    init {
        ntpClock.startPeriodicSync(viewModelScope)
    }

    override fun handleAction(action: ScanQrAction) {
        when (action) {
            is ScanQrAction.NavigateBack -> {
                viewModelScope.launch {
                    sendEvent(ScanQrEvent.NavigateBack)
                }
            }
            is ScanQrAction.Scanned -> {
                processQrContent(action.content)
            }
            ScanQrAction.ScanFailed -> {
                viewModelScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = "Failed to scan QR code"
                        )
                    )
                }
            }
        }
    }

    private fun processQrContent(content: String) {
        viewModelScope.launch {
            if (stateFlow.value.isLoading) return@launch

            if (!ntpClock.isSynced()) {
                updateState {
                    it.copy(
                        viewState = ScanQrState.ViewState.Error(
                            UiText.DynamicString("Unable to synchronize network time. Please check your internet connection and try again.")
                        )
                    )
                }
                return@launch
            }

            updateState { it.copy(isLoading = true) }

            // Decrypt content first
            val decryptedContent = try {
                DynamicQRCipher.decrypt(content)
            } catch (e: Exception) {
                // Decryption failed
                e.printStackTrace()
                updateState { it.copy(isLoading = false) }
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Invalid QR Code"
                    )
                )
                return@launch
            }

            // Format: attendanceId|timestamp
            val parts = decryptedContent.split("|")
            if (parts.size != 2) {
                updateState { it.copy(isLoading = false) }
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Invalid QR Format"
                    )
                )
                return@launch
            }

            val attendanceId = parts[0]
            val timestampStr = parts[1]
            val timestamp = timestampStr.toLongOrNull()

            if (timestamp == null) {
                updateState { it.copy(isLoading = false) }
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = "Invalid QR Timestamp"
                    )
                )
                return@launch
            }

            val currentNtpTime = ntpClock.now()

            val diff = abs(currentNtpTime - timestamp)

            if (diff <= 1000) { // 1 second
                updateState {
                    it.copy(
                        isLoading = false,
                        lastScannedContent = decryptedContent,
                        isQrScanSuccessful = true
                    )
                }
                // Success
                sendEvent(ScanQrEvent.NavigateToRecognizeStudent(attendanceId))
                // Keep loading state until navigation occurs
            }
        }
    }
}
