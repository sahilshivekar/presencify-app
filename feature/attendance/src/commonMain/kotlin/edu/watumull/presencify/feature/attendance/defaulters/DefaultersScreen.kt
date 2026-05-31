package edu.watumull.presencify.feature.attendance.defaulters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.designsystem.components.DonutGraph
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.StudentListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultersScreen(
    state: DefaultersState,
    onAction: (DefaultersAction) -> Unit,
    onNavigateBack: () -> Unit,
) {

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
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DesignToken.spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
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
                                .padding(vertical = DesignToken.spacing.xxl),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (state.students.isEmpty() && !state.isLoadingStudents && state.selectedSemesterNumber != null) {
                    item {
                        PresencifyNoResultsIndicator(
                            text = "No students found.",
                            modifier = Modifier.padding(DesignToken.spacing.lg)
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
                            modifier = Modifier.padding(horizontal = DesignToken.spacing.lg),
                            trailingContent = {
                                val isLoading = state.isAttendanceLoadingMap[student.id] == true
                                val attendancePercentage = if (state.selectedCourse != null) {
                                    state.studentCourseAttendanceMap[student.id]?.get(state.selectedCourse.id)
                                } else {
                                    state.studentAttendanceMap[student.id]
                                }

                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(DesignToken.components.progressMd),
                                        strokeWidth = DesignToken.strokes.md
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
                                            strokeWidth = DesignToken.strokes.thick,
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
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onDismiss = { onAction(DefaultersAction.DismissDialog) }
        )
    }
}

@Composable
private fun FilterSection(
    state: DefaultersState,
    onAction: (DefaultersAction) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)
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
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
        ) {

            PresencifyDatePickerTextField(
                value = state.startDate,
                onValueChange = {
                    onAction(DefaultersAction.SelectStartDate(it))
                },
                label = "Start Date",
                modifier = Modifier.weight(1f),
                enabled = !state.isLoadingStudents
            )


            PresencifyDatePickerTextField(
                value = state.endDate,
                onValueChange = {
                    onAction(DefaultersAction.SelectEndDate(it))
                },
                label = "End Date",
                modifier = Modifier.weight(1f),
                enabled = !state.isLoadingStudents
            )
        }
    }
}
