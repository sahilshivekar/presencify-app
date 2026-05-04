package edu.watumull.presencify.feature.attendance.search_attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchAttendanceBottomSheetContent(
    state: SearchAttendanceState,
    onAction: (SearchAttendanceAction) -> Unit,
    onDismiss: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Fixed Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Filter", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Reset",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onAction(SearchAttendanceAction.ResetFilters)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Scrollable Filter Options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Date Filter
            FilterSection(title = "Date") {
                PresencifyDatePickerTextField(
                    value = state.selectedDate,
                    onValueChange = {
                        onAction(SearchAttendanceAction.SelectDate(it))
                    },
                    label = "Select Date",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Branch Filter
            FilterSection(
                title = "Branch",
                isLoading = state.areBranchesLoading
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.branchOptions.forEach { branch ->
                        FilterChip(
                            selected = state.selectedBranch == branch,
                            onClick = { onAction(SearchAttendanceAction.ToggleBranch(branch)) },
                            label = { Text(branch.abbreviation) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Semester Filter
            FilterSection(title = "Semester") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.semesterOptions.forEach { semester ->
                        FilterChip(
                            selected = state.selectedSemesters.contains(semester),
                            onClick = { onAction(SearchAttendanceAction.ToggleSemester(semester)) },
                            label = { Text("Sem ${semester.value}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Academic Year of Semester Filter
            FilterSection(title = "Academic Year of Semester") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresencifyTextField(
                        value = state.academicStartYear,
                        onValueChange = { onAction(SearchAttendanceAction.UpdateAcademicStartYear(it)) },
                        label = "Start Year *",
                        modifier = Modifier.weight(1f)
                    )
                    PresencifyTextField(
                        value = state.academicEndYear,
                        onValueChange = { onAction(SearchAttendanceAction.UpdateAcademicEndYear(it)) },
                        label = "End Year *",
                        modifier = Modifier.weight(1f)
                    )
                }
            }


            // Division Filter
            FilterSection(
                title = "Division",
                isLoading = state.areDivisionsLoading,
                emptyMessage = when {
                    state.divisionOptions.isEmpty() ->
                        "No divisions found for selected semester details"
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.divisionOptions.forEach { division ->
                        FilterChip(
                            selected = state.selectedDivision == division,
                            onClick = {
                                val newDivision = if (state.selectedDivision == division) null else division
                                onAction(SearchAttendanceAction.SelectDivision(newDivision))
                            },
                            label = { Text(division.divisionCode) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Batch Filter
            FilterSection(
                title = "Batch",
                isLoading = state.areBatchesLoading,
                emptyMessage = when {
                    state.batchOptions.isEmpty() ->
                        "No divisions found for selected semester details"
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.batchOptions.forEach { batch ->
                        FilterChip(
                            selected = state.selectedBatch == batch,
                            onClick = {
                                val newBatch = if (state.selectedBatch == batch) null else batch
                                onAction(SearchAttendanceAction.SelectBatch(newBatch))
                            },
                            label = { Text(batch.batchCode) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Course Filter
            FilterSection(
                title = "Course",
                isLoading = state.areCoursesLoading,
                emptyMessage = when {
                    state.selectedSemesters.isEmpty() || state.academicStartYear.isEmpty() || state.academicEndYear.isEmpty() ->
                        "Select semester and academic year to load courses"
                    state.courseOptions.isEmpty() ->
                        "No courses found for selected semester"
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.courseOptions.forEach { course ->
                        FilterChip(
                            selected = state.selectedCourse == course,
                            onClick = {
                                val newCourse = if (state.selectedCourse == course) null else course
                                onAction(SearchAttendanceAction.SelectCourse(newCourse))
                            },
                            label = { Text(course.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Fixed Footer with Apply Button
        Column {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            PresencifyButton(
                text = "Apply",
                onClick = {
                    onAction(SearchAttendanceAction.ApplyFilters)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    isLoading: Boolean = false,
    emptyMessage: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
            emptyMessage != null -> {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> content()
        }
    }
}
