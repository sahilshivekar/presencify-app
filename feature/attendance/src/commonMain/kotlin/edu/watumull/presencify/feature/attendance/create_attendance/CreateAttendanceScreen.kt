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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.schedule.ClassSession
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.ClassListItem
import edu.watumull.presencify.core.presentation.utils.DateTimeUtils.getCurrentDate
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAttendanceScreen(
    state: CreateAttendanceState,
    onAction: (CreateAttendanceAction) -> Unit
) {

    PresencifyScaffold(
        backPress = { onAction(CreateAttendanceAction.NavigateBack) },
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
                            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
                        ) {
                            Text(
                                text = state.viewState.message.asString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { onAction(CreateAttendanceAction.NavigateBack) }) {
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
                            .padding(horizontal = DesignToken.spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                .fillMaxWidth()
                                .padding(vertical = DesignToken.spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.xl)
                        ) {
                            state.classSession?.let { classSession ->
                                ClassDetailsSection(
                                    classSession = classSession
                                )
                            }

                            DateSelectionSection(
                                selectedDate = state.selectedDate,
                                dateError = state.dateError,
                                onAction = onAction
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

        state.dialogState?.let { dialogState ->
            PresencifyAlertDialog(
                title = dialogState.title?.asString(),
                message = dialogState.message.asString(),
                dialogType = dialogState.dialogType,
                onDismiss = { onAction(CreateAttendanceAction.DismissDialog) }
            )
        }
    }
}

@Composable
private fun ClassDetailsSection(
    classSession: ClassSession
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
            classType = classSession.course?.courseType?.toDisplayLabel() ?: "Unknown",
            isExtraClass = classSession.isExtraClass,
            roomNumber = classSession.room?.roomNumber,
            divisionOrBatchText = divisionBatchText,
            branchAbbreviation = branch?.abbreviation,
            semesterText = semesterText,
            onClick = {  },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DateSelectionSection(
    selectedDate: LocalDate?,
    dateError: String?,
    onAction: (CreateAttendanceAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
    ) {
        Text(
            text = "Select Date",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        PresencifyDatePickerTextField(
            value = selectedDate,
            onValueChange = {
                onAction(CreateAttendanceAction.UpdateDate(it))
            },
            label = "Date *",
            modifier = Modifier.fillMaxWidth(),
            supportingText = dateError,
            isError = dateError != null,
            isDateAllowed = { date ->
                date <= getCurrentDate()
            }
        )
    }
}

