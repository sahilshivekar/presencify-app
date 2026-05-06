package edu.watumull.presencify.feature.schedule.add_edit_timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditTimetableScreen(
    state: AddEditTimetableState,
    onAction: (AddEditTimetableAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditTimetableAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Timetable" else "Add Timetable",
    ) { paddingValues ->
        when (state.viewState) {
            is AddEditTimetableState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AddEditTimetableState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is AddEditTimetableState.ViewState.Content -> {
                AddEditTimetableScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                onAction(AddEditTimetableAction.DismissDialog)
            },
            onDismiss = {
                onAction(AddEditTimetableAction.DismissDialog)
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddEditTimetableScreenContent(
    state: AddEditTimetableState,
    onAction: (AddEditTimetableAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(DesignToken.spacing.lg),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg),
            horizontalAlignment = Alignment.Start
        ) {
            // Instructions
            Text(
                text = if (state.isEditMode) {
                    "Update timetable information"
                } else {
                    "Select a branch, semester number, and academic year to find divisions and create a timetable"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Branch Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Branch",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                PresencifyDropDownMenuBox<Branch>(
                    value = state.selectedBranch?.name ?: "",
                    options = state.branchOptions,
                    onSelectItem = { onAction(AddEditTimetableAction.SelectBranch(it)) },
                    label = "Select Branch",
                    itemToString = { it.name },
                    expanded = state.isBranchDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AddEditTimetableAction.ChangeBranchDropDownVisibility(it)) },
                    supportingText = state.branchError,
                    enabled = !state.areBranchesLoading && !state.isEditMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Semester Number Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Semester Number",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                PresencifyDropDownMenuBox<SemesterNumber>(
                    value = state.selectedSemesterNumber?.toDisplayLabel() ?: "",
                    options = state.semesterNumberOptions,
                    onSelectItem = { onAction(AddEditTimetableAction.SelectSemesterNumber(it)) },
                    label = "Select Semester Number",
                    itemToString = { it.toDisplayLabel() },
                    expanded = state.isSemesterNumberDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AddEditTimetableAction.ChangeSemesterNumberDropDownVisibility(it)) },
                    supportingText = state.semesterNumberError,
                    enabled = !state.isEditMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Academic Year Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Academic Year",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    PresencifyTextField(
                        value = state.startYear,
                        onValueChange = { onAction(AddEditTimetableAction.UpdateStartYear(it)) },
                        label = "Start Year",
                        supportingText = state.startYearError,
                        isError = state.startYearError != null,
                        enabled = !state.isEditMode,
                        modifier = Modifier.weight(1f)
                    )

                    PresencifyTextField(
                        value = state.endYear,
                        onValueChange = { onAction(AddEditTimetableAction.UpdateEndYear(it)) },
                        label = "End Year",
                        supportingText = state.endYearError,
                        isError = state.endYearError != null,
                        enabled = !state.isEditMode,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Find Divisions and Batches Button
            if (!state.isEditMode) {
                PresencifyButton(
                    onClick = { onAction(AddEditTimetableAction.FindDivisionsAndBatchesClick) },
                    text = "Find Divisions & Batches",
                    isLoading = state.isLookingDivisions || state.isLookingBatches,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = DesignToken.spacing.sm)
                )
            }

            // Division Selection (shown only after finding divisions or in edit mode)
            if (state.areDivisionsVisible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                    modifier = Modifier.padding(top = DesignToken.spacing.sm)
                ) {
                    Text(
                        text = "Division",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    PresencifyDropDownMenuBox<Division>(
                        value = state.selectedDivision?.divisionCode ?: "",
                        options = state.divisionOptions,
                        onSelectItem = { onAction(AddEditTimetableAction.SelectDivision(it)) },
                        label = "Select Division",
                        itemToString = { it.divisionCode },
                        expanded = state.isDivisionDropdownOpen,
                        onDropDownVisibilityChanged = { onAction(AddEditTimetableAction.ChangeDivisionDropDownVisibility(it)) },
                        supportingText = state.divisionError,
                        enabled = !state.isEditMode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Batch Selection (shown only after finding batches)
                if (state.areBatchesVisible) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                    ) {
                        Text(
                            text = "Batches (Optional)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Select batches that will use this timetable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                        ) {
                            state.batchOptions.forEach { batch ->
                                FilterChip(
                                    selected = state.selectedBatches.contains(batch),
                                    onClick = {
                                        onAction(AddEditTimetableAction.ToggleBatchSelection(batch))
                                    },
                                    label = {
                                        Text(text = batch.batchCode)
                                    }
                                )
                            }
                        }
                    }
                }

                // Timetable Version
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
                ) {
                    Text(
                        text = "Timetable Version",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    PresencifyTextField(
                        value = state.timetableVersion,
                        onValueChange = { onAction(AddEditTimetableAction.UpdateTimetableVersion(it)) },
                        label = "Version (e.g., 1, 2, 3)",
                        supportingText = state.timetableVersionError,
                        isError = state.timetableVersionError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Save Button
                PresencifyButton(
                    onClick = { onAction(AddEditTimetableAction.SaveTimetableClick) },
                    text = if (state.isEditMode) "Update Timetable" else "Create Timetable",
                    isLoading = state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = DesignToken.spacing.lg)
                )
            }
        }
    }
}
