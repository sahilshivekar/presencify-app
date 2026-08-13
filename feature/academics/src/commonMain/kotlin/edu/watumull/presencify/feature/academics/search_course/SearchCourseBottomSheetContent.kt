package edu.watumull.presencify.feature.academics.search_course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import edu.watumull.presencify.core.designsystem.theme.DesignToken

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchCourseBottomSheetContent(
    state: SearchCourseState,
    onAction: (SearchCourseAction) -> Unit,
    onDismiss: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DesignToken.spacing.lg)
    ) {
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
                    onAction(SearchCourseAction.ResetFilters)
                }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = DesignToken.spacing.lg))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
        ) {
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
                                onAction(SearchCourseAction.SelectSemesterNumber(newSemester))
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
                                onAction(SearchCourseAction.SelectBranch(newBranch))
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
                                onAction(SearchCourseAction.SelectScheme(newScheme))
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

            FilterSection(
                title = "Teachers",
                isLoading = state.areTeachersLoading
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    state.teacherOptions.forEach { teacher ->
                        val teacherName = buildString {
                            append(teacher.firstName)
                            teacher.middleName?.let {
                                if (it.isNotBlank()) append(" $it")
                            }
                            append(" ${teacher.lastName}")
                        }.trim()

                        FilterChip(
                            selected = state.selectedTeacherIds.contains(teacher.id),
                            onClick = {
                                onAction(SearchCourseAction.ToggleTeacherSelection(teacher.id))
                            },
                            label = { Text(teacherName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        PresencifyButton(
            onClick = {
                onDismiss()
                onAction(SearchCourseAction.ApplyFilters)
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

