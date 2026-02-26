package edu.watumull.presencify.feature.schedule.add_edit_class

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassScreen(
    state: AddEditClassState,
    onAction: (AddEditClassAction) -> Unit,
) {
    // Time Pickers
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val startTimePickerState = rememberTimePickerState(
        initialHour = state.startTime?.hour ?: 9,
        initialMinute = state.startTime?.minute ?: 0
    )
    val endTimePickerState = rememberTimePickerState(
        initialHour = state.endTime?.hour ?: 10,
        initialMinute = state.endTime?.minute ?: 0
    )

    // Date Pickers
    var showActiveFromPicker by remember { mutableStateOf(false) }
    var showActiveTillPicker by remember { mutableStateOf(false) }
    val activeFromDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.activeFrom?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
    )
    val activeTillDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.activeTill?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
    )

    PresencifyScaffold(
        backPress = { onAction(AddEditClassAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Class" else "Add Class",
    ) { paddingValues ->
        when (state.viewState) {
            is AddEditClassState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AddEditClassState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is AddEditClassState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Course Dropdown
                        PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.model.academics.Course>(
                            value = state.selectedCourse?.name ?: "",
                            options = state.availableCourses,
                            onSelectItem = { onAction(AddEditClassAction.UpdateCourse(it?.id)) },
                            label = "Course *",
                            itemToString = { it.name },
                            expanded = state.isCourseDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeCourseDropDownVisibility(it)) },
                            supportingText = state.selectedCourseError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Teacher Dropdown
                        PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.model.teacher.Teacher>(
                            value = state.selectedTeacher?.let { "${it.firstName} ${it.lastName}" } ?: "",
                            options = state.availableTeachers,
                            onSelectItem = { onAction(AddEditClassAction.UpdateTeacher(it?.id)) },
                            label = "Teacher *",
                            itemToString = { "${it.firstName} ${it.lastName}" },
                            expanded = state.isTeacherDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeTeacherDropDownVisibility(it)) },
                            supportingText = state.selectedTeacherError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Room Dropdown
                        PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.model.schedule.Room>(
                            value = state.selectedRoom?.roomNumber ?: "",
                            options = state.availableRooms,
                            onSelectItem = { onAction(AddEditClassAction.UpdateRoom(it.id)) },
                            label = "Room *",
                            itemToString = { it.roomNumber },
                            expanded = state.isRoomDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeRoomDropDownVisibility(it)) },
                            supportingText = state.selectedRoomError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Class Type Dropdown
                        PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.enums.ClassType>(
                            value = state.classType?.value ?: "",
                            options = state.classTypeOptions,
                            onSelectItem = { onAction(AddEditClassAction.UpdateClassType(it)) },
                            label = "Class Type *",
                            itemToString = { it.value },
                            expanded = state.isClassTypeDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeClassTypeDropDownVisibility(it)) },
                            supportingText = state.classTypeError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Batch Dropdown (Only for Practical or Tutorial)
                        if (state.classType == edu.watumull.presencify.core.domain.enums.ClassType.PRACTICAL ||
                            state.classType == edu.watumull.presencify.core.domain.enums.ClassType.TUTORIAL) {
                            PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.model.academics.Batch>(
                                value = state.selectedBatch?.batchCode ?: "",
                                options = state.availableBatches,
                                onSelectItem = { onAction(AddEditClassAction.UpdateBatch(it?.id)) },
                                label = "Batch (Optional)",
                                itemToString = { it.batchCode },
                                expanded = state.isBatchDropdownOpen,
                                onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeBatchDropDownVisibility(it)) },
                                enabled = !state.isSubmitting && !state.isEditMode,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Day of Week Dropdown
                        PresencifyDropDownMenuBox<edu.watumull.presencify.core.domain.enums.DayOfWeek>(
                            value = state.dayOfWeek?.value ?: "",
                            options = state.dayOfWeekOptions,
                            onSelectItem = { onAction(AddEditClassAction.UpdateDayOfWeek(it)) },
                            label = "Day of Week *",
                            itemToString = { it.value },
                            expanded = state.isDayOfWeekDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeDayOfWeekDropDownVisibility(it)) },
                            supportingText = state.dayOfWeekError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Time Range (only show in add mode)
                        if (!state.isEditMode) {
                            Text(
                                text = "Time Range",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Start Time Picker
                                PresencifyTextField(
                                    value = state.startTime?.toReadableString() ?: "",
                                    onValueChange = { },
                                    label = "Start Time *",
                                    readOnly = true,
                                    supportingText = state.startTimeError,
                                    isError = state.startTimeError != null,
                                    leadingIcon = {
                                        IconButton(onClick = { showStartTimePicker = true }) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = "Select start time"
                                            )
                                        }
                                    },
                                    trailingIcon = if (state.startTime != null) {
                                        {
                                            IconButton(onClick = { onAction(AddEditClassAction.UpdateStartTime(null)) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Clear,
                                                    contentDescription = "Clear start time"
                                                )
                                            }
                                        }
                                    } else null,
                                    modifier = Modifier.weight(1f)
                                )

                                // End Time Picker
                                PresencifyTextField(
                                    value = state.endTime?.toReadableString() ?: "",
                                    onValueChange = { },
                                    label = "End Time *",
                                    readOnly = true,
                                    supportingText = state.endTimeError,
                                    isError = state.endTimeError != null,
                                    leadingIcon = {
                                        IconButton(onClick = { showEndTimePicker = true }) {
                                            Icon(
                                                imageVector = Icons.Default.AccessTime,
                                                contentDescription = "Select end time"
                                            )
                                        }
                                    },
                                trailingIcon = if (state.endTime != null) {
                                    {
                                        IconButton(onClick = { onAction(AddEditClassAction.UpdateEndTime(null)) }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear end time"
                                            )
                                        }
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        } // End of time range if block

                        // Active Date Range (always visible, editable in both add and edit mode)
                        Text(
                            text = "Active Date Range",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Active From Date Picker
                            PresencifyTextField(
                                value = state.activeFrom?.toReadableString() ?: "",
                                onValueChange = { },
                                label = "Active From *",
                                readOnly = true,
                                supportingText = state.activeFromError,
                                isError = state.activeFromError != null,
                                leadingIcon = {
                                    IconButton(onClick = { showActiveFromPicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select active from date"
                                        )
                                    }
                                },
                                trailingIcon = if (state.activeFrom != null) {
                                    {
                                        IconButton(onClick = { onAction(AddEditClassAction.UpdateActiveFrom(null)) }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear active from date"
                                            )
                                        }
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )

                            // Active Till Date Picker
                            PresencifyTextField(
                                value = state.activeTill?.toReadableString() ?: "",
                                onValueChange = { },
                                label = "Active Till *",
                                readOnly = true,
                                supportingText = state.activeTillError,
                                isError = state.activeTillError != null,
                                leadingIcon = {
                                    IconButton(onClick = { showActiveTillPicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Select active till date"
                                        )
                                    }
                                },
                                trailingIcon = if (state.activeTill != null) {
                                    {
                                        IconButton(onClick = { onAction(AddEditClassAction.UpdateActiveTill(null)) }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Clear active till date"
                                            )
                                        }
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Extra Class Toggle (only show in add mode)
                        if (!state.isEditMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Extra Class",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Mark this as an extra class",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.isExtraClass,
                                    onCheckedChange = { onAction(AddEditClassAction.UpdateIsExtraClass(it)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit Button
                        PresencifyButton(
                            onClick = { onAction(AddEditClassAction.SubmitClick) },
                            text = if (state.isEditMode) "Update Active Dates" else "Add Class",
                            enabled = !state.isSubmitting,
                            isLoading = state.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Time Pickers Dialogs
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            onConfirm = {
                val time = LocalTime(startTimePickerState.hour, startTimePickerState.minute)
                onAction(AddEditClassAction.UpdateStartTime(time))
                showStartTimePicker = false
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            onConfirm = {
                val time = LocalTime(endTimePickerState.hour, endTimePickerState.minute)
                onAction(AddEditClassAction.UpdateEndTime(time))
                showEndTimePicker = false
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }

    // Date Pickers Dialogs
    if (showActiveFromPicker) {
        DatePickerDialog(
            onDismissRequest = { showActiveFromPicker = false },
            onConfirm = {
                activeFromDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(AddEditClassAction.UpdateActiveFrom(selectedDate))
                }
                showActiveFromPicker = false
            }
        ) {
            DatePicker(state = activeFromDatePickerState)
        }
    }

    if (showActiveTillPicker) {
        DatePickerDialog(
            onDismissRequest = { showActiveTillPicker = false },
            onConfirm = {
                activeTillDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(AddEditClassAction.UpdateActiveTill(selectedDate))
                }
                showActiveTillPicker = false
            }
        ) {
            DatePicker(state = activeTillDatePickerState)
        }
    }

    // Dialog
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> onAction(AddEditClassAction.DismissDialog)
                }
            },
            onDismiss = { onAction(AddEditClassAction.DismissDialog) }
        )
    }
}

@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    )
}

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        text = {
            content()
        }
    )
}
