package edu.watumull.presencify.feature.academics.search_division

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchDivisionBottomSheetContent(
    state: SearchDivisionState,
    onAction: (SearchDivisionAction) -> Unit,
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
                    onAction(SearchDivisionAction.ResetFilters)
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
            // Semester Number Filter
            FilterSection(title = "Semester") {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    state.semesterNumberOptions.forEach { semesterNumber ->
                        FilterChip(
                            selected = state.selectedSemesterNumber == semesterNumber,
                            onClick = {
                                val newSemester = if (state.selectedSemesterNumber == semesterNumber) null else semesterNumber
                                onAction(SearchDivisionAction.SelectSemesterNumber(newSemester))
                            },
                            label = { Text("Sem ${semesterNumber.value}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Academic Year Filter
            FilterSection(title = "Academic Year") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    PresencifyTextField(
                        value = state.academicStartYear,
                        onValueChange = { onAction(SearchDivisionAction.UpdateAcademicStartYear(it)) },
                        label = "Start Year *",
                        modifier = Modifier.weight(1f)
                    )
                    PresencifyTextField(
                        value = state.academicEndYear,
                        onValueChange = { onAction(SearchDivisionAction.UpdateAcademicEndYear(it)) },
                        label = "End Year *",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

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
                            selected = state.selectedBranch == branch,
                            onClick = {
                                val newBranch = if (state.selectedBranch == branch) null else branch
                                onAction(SearchDivisionAction.SelectBranch(newBranch))
                            },
                            label = { Text(branch.abbreviation) },
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
                onAction(SearchDivisionAction.ApplyFilters)
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

