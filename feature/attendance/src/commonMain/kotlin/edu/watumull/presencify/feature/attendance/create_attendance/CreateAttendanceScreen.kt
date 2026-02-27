package edu.watumull.presencify.feature.attendance.create_attendance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAttendanceScreen(
    state: CreateAttendanceState,
    onAction: (CreateAttendanceAction) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    PresencifyScaffold(
        backPress = { onAction(CreateAttendanceAction.BackButtonClick) },
        topBarTitle = "Create Attendance"
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state.viewState) {
                CreateAttendanceState.ViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is CreateAttendanceState.ViewState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.viewState.message.asString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { onAction(CreateAttendanceAction.BackButtonClick) }) {
                                Text("Go Back")
                            }
                        }
                    }
                }

                CreateAttendanceState.ViewState.Content -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Class Details Section
                            state.classSession?.let { classSession ->
                                ClassDetailsSection(
                                    classSession = classSession
                                )
                            }

                            // Date Selection Section
                            DateSelectionSection(
                                selectedDate = state.selectedDate,
                                dateError = state.dateError,
                                onDateClick = { showDatePicker = true }
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            PresencifyButton(
                                onClick = { onAction(CreateAttendanceAction.CreateAttendance) },
                                isLoading = state.isCreatingAttendance,
                                text = "Create Attendance"
                            )
                        }
                    }
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onConfirm = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val epochDays = millis / (24 * 60 * 60 * 1000)
                        val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                        onAction(CreateAttendanceAction.UpdateDate(selectedDate))
                    }
                    showDatePicker = false
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Dialog for errors/success
        state.dialogState?.let { dialogState ->
            PresencifyAlertDialog(
                dialogType = dialogState.dialogType,
                title = dialogState.title,
                message = dialogState.message.asString(),
                onDismiss = { onAction(CreateAttendanceAction.DismissDialog) },
                onConfirm = { onAction(CreateAttendanceAction.DismissDialog) }
            )
        }
    }
}

@Composable
private fun ClassDetailsSection(
    classSession: ClassSession
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Class Details",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        val division = classSession.timetable?.division
        val batch = classSession.batch
        val semester = division?.semester
        val branch = semester?.branch

        val divisionBatchText = when {
            batch != null -> batch.batchCode
            division != null -> division.divisionCode
            else -> null
        }

        val semesterText = semester?.let { sem ->
            val semNum = sem.semesterNumber.value
            val academicYear = "${sem.academicStartYear}-${sem.academicEndYear}"
            "Sem: $semNum $academicYear"
        }

        ClassListItem(
            courseName = classSession.course?.name ?: "Unknown Course",
            teacherName = classSession.teacher?.let { "${it.firstName} ${it.lastName}" }
                ?: "Unknown Teacher",
            startTime = classSession.startTime.toReadableString(),
            endTime = classSession.endTime.toReadableString(),
            dayOfWeek = classSession.dayOfWeek.toDisplayLabel(),
            activeFrom = classSession.activeFrom.toReadableString(),
            activeTill = classSession.activeTill.toReadableString(),
            classType = classSession.classType.toDisplayLabel(),
            isExtraClass = classSession.isExtraClass,
            roomNumber = classSession.room?.roomNumber,
            divisionOrBatchText = divisionBatchText,
            branchAbbreviation = branch?.abbreviation,
            semesterText = semesterText,
            onClick = { /* No action for this view */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateSelectionSection(
    selectedDate: LocalDate?,
    dateError: String?,
    onDateClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        PresencifyTextField(
            value = selectedDate?.toReadableString() ?: "",
            onValueChange = { },
            label = "Date *",
            readOnly = true,
            supportingText = dateError,
            isError = dateError != null,
            leadingIcon = {
                IconButton(onClick = { onDateClick() }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
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
