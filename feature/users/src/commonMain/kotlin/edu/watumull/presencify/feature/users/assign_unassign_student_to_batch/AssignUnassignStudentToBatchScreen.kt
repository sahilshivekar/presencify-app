package edu.watumull.presencify.feature.users.assign_unassign_student_to_batch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AssignUnassignStudentToBatchScreen(
    state: AssignUnassignStudentToBatchState,
    onAction: (AssignUnassignStudentToBatchAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AssignUnassignStudentToBatchAction.BackButtonClick) },
        topBarTitle = "Assign/Unassign Student to Batch",
    ) { paddingValues ->
        when (state.viewState) {
            is AssignUnassignStudentToBatchState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AssignUnassignStudentToBatchState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is AssignUnassignStudentToBatchState.ViewState.Content -> {
                AssignUnassignStudentToBatchScreenContent(
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
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> {
                        onAction(AssignUnassignStudentToBatchAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(AssignUnassignStudentToBatchAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun AssignUnassignStudentToBatchScreenContent(
    state: AssignUnassignStudentToBatchState,
    onAction: (AssignUnassignStudentToBatchAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Instructions
            Text(
                text = "Select a branch, semester number, and academic year to find all batches",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Branch Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    onSelectItem = { onAction(AssignUnassignStudentToBatchAction.SelectBranch(it)) },
                    label = "Select Branch",
                    itemToString = { it.name },
                    expanded = state.isBranchDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AssignUnassignStudentToBatchAction.ChangeBranchDropDownVisibility(it)) },
                    supportingText = state.branchError,
                    enabled = !state.areBranchesLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Semester Number Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    onSelectItem = { onAction(AssignUnassignStudentToBatchAction.SelectSemesterNumber(it)) },
                    label = "Select Semester Number",
                    itemToString = { it.toDisplayLabel() },
                    expanded = state.isSemesterNumberDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AssignUnassignStudentToBatchAction.ChangeSemesterNumberDropDownVisibility(it)) },
                    supportingText = state.semesterNumberError,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Academic Year Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Academic Year",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresencifyTextField(
                        value = state.startYear,
                        onValueChange = { onAction(AssignUnassignStudentToBatchAction.UpdateStartYear(it)) },
                        label = "Start Year",
                        supportingText = state.startYearError,
                        isError = state.startYearError != null,
                        modifier = Modifier.weight(1f)
                    )

                    PresencifyTextField(
                        value = state.endYear,
                        onValueChange = { onAction(AssignUnassignStudentToBatchAction.UpdateEndYear(it)) },
                        label = "End Year",
                        supportingText = state.endYearError,
                        isError = state.endYearError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Find Batches Button
            PresencifyButton(
                onClick = { onAction(AssignUnassignStudentToBatchAction.FindBatchesClick) },
                text = "Find Batches",
                isLoading = state.isLookingDivisions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Batch Selection (shown only after finding batches)
            if (state.areBatchesVisible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Batch",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    PresencifyDropDownMenuBox<Batch>(
                        value = state.selectedBatch?.batchCode ?: "",
                        options = state.batchOptions,
                        onSelectItem = { onAction(AssignUnassignStudentToBatchAction.SelectBatch(it)) },
                        label = "Select Batch",
                        itemToString = { it.batchCode },
                        expanded = state.isBatchDropdownOpen,
                        onDropDownVisibilityChanged = { onAction(AssignUnassignStudentToBatchAction.ChangeBatchDropDownVisibility(it)) },
                        supportingText = state.batchError,
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Navigate to Search Student Button
                PresencifyButton(
                    onClick = { onAction(AssignUnassignStudentToBatchAction.NavigateToSearchStudentClick) },
                    text = "Continue to Search Students",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}
