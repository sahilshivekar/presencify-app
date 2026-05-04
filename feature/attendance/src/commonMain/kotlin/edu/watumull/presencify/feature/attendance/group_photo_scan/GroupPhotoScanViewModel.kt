package edu.watumull.presencify.feature.attendance.group_photo_scan

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.design.systems.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.attendance.AttendanceRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import kotlinx.coroutines.launch

class GroupPhotoScanViewModel(
    private val attendanceRepository: AttendanceRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<GroupPhotoScanState, GroupPhotoScanEvent, GroupPhotoScanAction>(
    initialState = GroupPhotoScanState()
) {

    private val attendanceId: String =
        savedStateHandle.toRoute<AttendanceRoutes.MarkStudentAttendance>().attendanceId

    init {
        updateState { it.copy(attendanceId = attendanceId) }
    }

    override fun handleAction(action: GroupPhotoScanAction) {
        when (action) {
            GroupPhotoScanAction.NavigateBack -> sendEvent(GroupPhotoScanEvent.NavigateBack)
            is GroupPhotoScanAction.AddImages -> {
                val combined = (state.images + action.newImages).take(10)
                updateState { it.copy(images = combined) }
            }
            is GroupPhotoScanAction.RemoveImage -> {
                val newList = state.images.toMutableList().also { list ->
                    if (action.index in list.indices) list.removeAt(action.index)
                }
                updateState { it.copy(images = newList) }
            }
            GroupPhotoScanAction.SubmitGroupPhotoScan -> submitGroupPhotoScan()
            GroupPhotoScanAction.DismissDialog ->
                updateState { it.copy(dialogState = null) }
            GroupPhotoScanAction.OpenCamera ->
                updateState { it.copy(isCameraOpen = true) }
            GroupPhotoScanAction.CloseCamera ->
                updateState { it.copy(isCameraOpen = false) }
        }
    }

    private fun submitGroupPhotoScan() {
        val currentImages = state.images
        if (currentImages.isEmpty() || state.isLoading) return

        updateState { it.copy(isLoading = true) }

        viewModelScope.launch {
            attendanceRepository.groupPhotoScan(attendanceId, currentImages)
                .onSuccess { result ->

                    // 🔥 Build detailed message
                    val message = buildString {
                        append("Attendance Scan Completed\n\n")

                        append("👥 Total Faces Detected: ${result.totalFacesDetected}\n")
                        append("✅ Present Students: ${result.presentCount}\n")
                        append("❓ Unknown Faces: ${result.unknownFacesCount}\n")

                        // Optional insight
                        if (result.totalFacesDetected == 0) {
                            append("\n⚠️ No faces detected. Please try again.")
                        } else if (result.presentCount == 0) {
                            append("\n⚠️ No registered students recognized.")
                        }
                    }

                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = GroupPhotoScanState.DialogState(
                                dialogType = DialogType.SUCCESS, // 🔥 always show dialog
                                title = "Scan Result",
                                message = UiText.DynamicString(message)
                            )
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            dialogState = GroupPhotoScanState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Unable to submit",
                                message = error.toUiText(),
                            )
                        )
                    }
                }
        }
    }
}
