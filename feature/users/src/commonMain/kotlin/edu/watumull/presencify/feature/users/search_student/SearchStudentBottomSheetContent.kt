package edu.watumull.presencify.feature.users.search_student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.domain.enums.AdmissionType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchStudentBottomSheetContent(
    state: SearchStudentState,
    onAction: (SearchStudentAction) -> Unit,
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
                    onAction(SearchStudentAction.ResetFilters)
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
                            selected = state.selectedBranches.contains(branch),
                            onClick = { onAction(SearchStudentAction.ToggleBranch(branch)) },
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
                            onClick = { onAction(SearchStudentAction.ToggleSemester(semester)) },
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
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.academicYearOfSemesterOptions.forEach { year ->
                        FilterChip(
                            selected = state.selectedAcademicYearOfSemester == year,
                            onClick = {
                                val newYear = if (state.selectedAcademicYearOfSemester == year) null else year
                                onAction(SearchStudentAction.SelectAcademicYearOfSemester(newYear))
                            },
                            label = { Text(year) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Admission Year Filter
            FilterSection(title = "Admission Year") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.admissionYearOptions.forEach { year ->
                        FilterChip(
                            selected = state.selectedAdmissionYear == year,
                            onClick = {
                                val newYear = if (state.selectedAdmissionYear == year) null else year
                                onAction(SearchStudentAction.SelectAdmissionYear(newYear))
                            },
                            label = { Text(year) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Admission Type Filter
            FilterSection(title = "Admission Type") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.admissionTypeOptions.forEach { type ->
                        val displayText = when (type) {
                            AdmissionType.FE -> "First Year"
                            AdmissionType.DSE -> "Direct Second Year"
                        }
                        FilterChip(
                            selected = state.selectedAdmissionTypes.contains(type),
                            onClick = { onAction(SearchStudentAction.ToggleAdmissionType(type)) },
                            label = { Text(displayText) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Dropout Year Filter
            FilterSection(title = "Dropout Year") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.dropoutYearOptions.forEach { year ->
                        FilterChip(
                            selected = state.selectedDropoutYear == year,
                            onClick = {
                                val newYear = if (state.selectedDropoutYear == year) null else year
                                onAction(SearchStudentAction.SelectDropoutYear(newYear))
                            },
                            label = { Text(year) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Scheme Filter
            FilterSection(
                title = "Scheme",
                isLoading = state.areSchemesLoading
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.schemeOptions.forEach { scheme ->
                        FilterChip(
                            selected = state.selectedScheme == scheme,
                            onClick = {
                                val newScheme = if (state.selectedScheme == scheme) null else scheme
                                onAction(SearchStudentAction.SelectScheme(newScheme))
                            },
                            label = { Text(scheme.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Division Filter
            FilterSection(
                title = "Division",
                isLoading = state.areDivisionsLoading,
                emptyMessage = when {
                    state.selectedSemesters.isEmpty() ||
                            state.selectedBranches.isEmpty() ||
                            state.selectedAcademicYearOfSemester == null ->
                        "Select semester, branch, and academic year to load divisions"
                    state.divisionOptions.isEmpty() ->
                        "No divisions found for selected filters"
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
                                onAction(SearchStudentAction.SelectDivision(newDivision))
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
                    state.selectedSemesters.isEmpty() ||
                            state.selectedBranches.isEmpty() ||
                            state.selectedAcademicYearOfSemester == null ->
                        "Select semester, branch, and academic year to load batches"
                    state.batchOptions.isEmpty() ->
                        "No batches found for selected filters"
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
                                onAction(SearchStudentAction.SelectBatch(newBatch))
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
        }

        // Fixed Apply Filters Button
        PresencifyButton(
            onClick = {
                onDismiss()
                onAction(SearchStudentAction.ApplyFilters)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            text = "Apply Filters"
        )
    }
}

@Composable
private fun FilterSection(
    title: String,
    isLoading: Boolean = false,
    emptyMessage: String? = null,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp)
                )
            }
            emptyMessage != null -> {
                Text(
                    text = emptyMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            else -> {
                content()
            }
        }
    }
}

