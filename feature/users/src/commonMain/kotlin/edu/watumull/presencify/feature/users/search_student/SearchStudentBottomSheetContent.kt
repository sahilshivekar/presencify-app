package edu.watumull.presencify.feature.users.search_student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.BiometricVerificationStatus

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
            .padding(DesignToken.spacing.lg)
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

        HorizontalDivider(modifier = Modifier.padding(vertical = DesignToken.spacing.lg))

        // Scrollable Filter Options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
        ) {
            // Branch Filter
            FilterSection(
                title = "Branch",
                isLoading = state.areBranchesLoading
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    PresencifyTextField(
                        value = state.academicStartYear,
                        onValueChange = { onAction(SearchStudentAction.UpdateAcademicStartYear(it)) },
                        label = "Start Year *",
                        modifier = Modifier.weight(1f)
                    )
                    PresencifyTextField(
                        value = state.academicEndYear,
                        onValueChange = { onAction(SearchStudentAction.UpdateAcademicEndYear(it)) },
                        label = "End Year *",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Admission Year Filter
            FilterSection(title = "Admission Year") {
                PresencifyTextField(
                    value = state.admissionYear ?: "",
                    onValueChange = { input ->
                        val cleaned = input.filter { it.isDigit() }.take(4)
                        onAction(SearchStudentAction.SelectAdmissionYear(cleaned.ifBlank { null }))
                    },
                    label = "Admission Year",
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Admission Type Filter
            FilterSection(title = "Admission Type") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    PresencifyTextField(
                        value = state.dropoutStartYear,
                        onValueChange = { onAction(SearchStudentAction.UpdateDropoutStartYear(it)) },
                        label = "Start Year",
                        modifier = Modifier.weight(1f)
                    )
                    PresencifyTextField(
                        value = state.dropoutEndYear,
                        onValueChange = { onAction(SearchStudentAction.UpdateDropoutEndYear(it)) },
                        label = "End Year",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Biometric Verification Status Filter
            FilterSection(title = "Biometric Verification Status") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    state.biometricVerificationStatusOptions.forEach { status ->
                        FilterChip(
                            selected = state.selectedBiometricVerificationStatus == status,
                            onClick = {
                                val newStatus = if (state.selectedBiometricVerificationStatus == status) null else status
                                onAction(SearchStudentAction.SelectBiometricVerificationStatus(newStatus))
                            },
                            label = { Text(status.value) },
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
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                            state.academicStartYear.isEmpty() ||
                            state.academicEndYear.isEmpty() ->
                        "Select semester, branch, and academic year to load divisions"
                    state.divisionOptions.isEmpty() ->
                        "No divisions found for selected filters"
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                            state.academicStartYear.isEmpty() ||
                            state.academicEndYear.isEmpty() ->
                        "Select semester, branch, and academic year to load batches"
                    state.batchOptions.isEmpty() ->
                        "No batches found for selected filters"
                    else -> null
                }
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                .padding(vertical = DesignToken.spacing.lg),
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
        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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
                    strokeWidth = DesignToken.strokes.md,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(DesignToken.components.progressSm)
                        .padding(top = DesignToken.spacing.xs)
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
