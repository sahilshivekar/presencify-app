package edu.watumull.presencify.feature.attendance.defaulters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.design.systems.components.DonutGraph
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.presentation.components.StudentListItem
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultersScreen(
    state: DefaultersState,
    onAction: (DefaultersAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.startDate?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
    )
    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.endDate?.toEpochDays()?.let { it * 24 * 60 * 60 * 1000L }
    )

    PresencifyScaffold(
        topBarTitle = "Defaulters List",
        backPress = onNavigateBack,
        actions = {
            if (state.students.isNotEmpty()) {
                IconButton(onClick = { onAction(DefaultersAction.ExportCsv) }) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export to CSV"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilterSection(state = state, onAction = onAction)

                        PresencifyButton(
                            text = "Get Attendance",
                            onClick = { onAction(DefaultersAction.GetDefaulters) },
                            isLoading = state.isLoadingStudents,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (state.isLoadingStudents) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.students.isEmpty() && !state.isLoadingStudents && state.selectedSemesterNumber != null) {
                    item {
                        PresencifyNoResultsIndicator(
                            text = "No students found.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(state.students, key = { it.id }) { student ->
                        StudentListItem(
                            studentName = "${student.firstName} ${student.lastName}",
                            prn = student.prn,
                            studentImageUrl = student.studentImageUrl,
                            studentBranch = student.branch?.abbreviation,
                            onClick = { /* Do nothing */ },
                            modifier = Modifier.padding(horizontal = 16.dp),
                            trailingContent = {
                                val isLoading = state.isAttendanceLoadingMap[student.id] == true
                                val attendancePercentage = if (state.selectedCourse != null) {
                                    state.studentCourseAttendanceMap[student.id]?.get(state.selectedCourse.id)
                                } else {
                                    state.studentAttendanceMap[student.id]
                                }

                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        strokeWidth = 3.dp
                                    )
                                } else if (attendancePercentage != null) {
                                    val (present, total) = if (state.selectedCourse != null) {
                                        val courseMap = state.studentCourseAttendanceNumbersMap[student.id]?.get(state.selectedCourse.id)
                                        Pair(courseMap?.first, courseMap?.second)
                                    } else {
                                        val numbers = state.studentAttendanceNumbersMap[student.id]
                                        Pair(numbers?.first, numbers?.second)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        DonutGraph(
                                            percentage = attendancePercentage,
                                            size = 40.dp,
                                            strokeWidth = 4.dp,
                                            animate = true,
                                            centerText = "${attendancePercentage.toInt()}%",
                                            centerSubtext = "$present / $total"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = { onAction(DefaultersAction.DismissDialog) },
            onDismiss = { onAction(DefaultersAction.DismissDialog) }
        )
    }

    if (state.showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(DefaultersAction.ChangeStartDatePickerVisibility(false)) },
            onConfirm = {
                startDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(DefaultersAction.SelectStartDate(selectedDate))
                }
                onAction(DefaultersAction.ChangeStartDatePickerVisibility(false))
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    if (state.showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { onAction(DefaultersAction.ChangeEndDatePickerVisibility(false)) },
            onConfirm = {
                endDatePickerState.selectedDateMillis?.let { millis ->
                    val epochDays = millis / (24 * 60 * 60 * 1000)
                    val selectedDate = LocalDate.fromEpochDays(epochDays.toInt())
                    onAction(DefaultersAction.SelectEndDate(selectedDate))
                }
                onAction(DefaultersAction.ChangeEndDatePickerVisibility(false))
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
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
private fun FilterSection(
    state: DefaultersState,
    onAction: (DefaultersAction) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select Semester",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        PresencifyDropDownMenuBox(
            value = state.selectedSemesterNumber?.toDisplayLabel() ?: "",
            options = SemesterNumber.entries,
            onSelectItem = { onAction(DefaultersAction.SelectSemesterNumber(it)) },
            label = "Semester Number *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isSemesterNumberDropdownOpen,
            onDropDownVisibilityChanged = {
                onAction(DefaultersAction.ChangeSemesterNumberDropDownVisibility(it))
            },
            enabled = !state.isLoadingStudents,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresencifyTextField(
                value = state.academicStartYear,
                onValueChange = { onAction(DefaultersAction.UpdateAcademicStartYear(it)) },
                label = "Academic Start Year *",
                enabled = !state.isLoadingStudents,
                modifier = Modifier.weight(1f)
            )
            PresencifyTextField(
                value = state.academicEndYear,
                onValueChange = { onAction(DefaultersAction.UpdateAcademicEndYear(it)) },
                label = "Academic End Year *",
                enabled = !state.isLoadingStudents,
                modifier = Modifier.weight(1f)
            )
        }

        PresencifyDropDownMenuBox(
            value = state.selectedBranch?.name ?: "",
            options = state.branchOptions,
            onSelectItem = { onAction(DefaultersAction.SelectBranch(it)) },
            label = "Branch *",
            itemToString = { it.name },
            expanded = state.isBranchDropdownOpen,
            onDropDownVisibilityChanged = {
                onAction(DefaultersAction.ChangeBranchDropDownVisibility(it))
            },
            enabled = !state.isLoadingStudents,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.courseOptions.isNotEmpty()) {
            PresencifyDropDownMenuBox(
                value = state.selectedCourse?.name ?: "All Courses",
                options = listOf(null) + state.courseOptions,
                onSelectItem = { onAction(DefaultersAction.SelectCourse(it)) },
                label = "Course Filter",
                itemToString = { it?.name ?: "All Courses" },
                expanded = state.isCourseDropdownOpen,
                onDropDownVisibilityChanged = {
                    onAction(DefaultersAction.ChangeCourseDropDownVisibility(it))
                },
                enabled = !state.isLoadingStudents,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresencifyTextField(
                value = state.startDate?.toReadableString() ?: "",
                onValueChange = { },
                label = "Start Date",
                readOnly = true,
                leadingIcon = {
                    IconButton(onClick = { onAction(DefaultersAction.ChangeStartDatePickerVisibility(true)) }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select start date"
                        )
                    }
                },
                trailingIcon = if (state.startDate != null) {
                    {
                        IconButton(onClick = { onAction(DefaultersAction.SelectStartDate(null)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear start date"
                            )
                        }
                    }
                } else null,
                modifier = Modifier.weight(1f),
                enabled = !state.isLoadingStudents
            )

            PresencifyTextField(
                value = state.endDate?.toReadableString() ?: "",
                onValueChange = { },
                label = "End Date",
                readOnly = true,
                leadingIcon = {
                    IconButton(onClick = { onAction(DefaultersAction.ChangeEndDatePickerVisibility(true)) }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select end date"
                        )
                    }
                },
                trailingIcon = if (state.endDate != null) {
                    {
                        IconButton(onClick = { onAction(DefaultersAction.SelectEndDate(null)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear end date"
                            )
                        }
                    }
                } else null,
                modifier = Modifier.weight(1f),
                enabled = !state.isLoadingStudents
            )
        }
    }
}
