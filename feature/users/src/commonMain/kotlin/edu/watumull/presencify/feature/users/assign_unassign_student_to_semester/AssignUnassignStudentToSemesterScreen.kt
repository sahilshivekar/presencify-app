package edu.watumull.presencify.feature.users.assign_unassign_student_to_semester

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
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AssignUnassignStudentToSemesterScreen(
    state: AssignUnassignStudentToSemesterState,
    onAction: (AssignUnassignStudentToSemesterAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AssignUnassignStudentToSemesterAction.BackButtonClick) },
        topBarTitle = "Assign/Unassign Student to Semester",
    ) { paddingValues ->
        when (state.viewState) {
            is AssignUnassignStudentToSemesterState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AssignUnassignStudentToSemesterState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is AssignUnassignStudentToSemesterState.ViewState.Content -> {
                AssignUnassignStudentToSemesterScreenContent(
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
                        onAction(AssignUnassignStudentToSemesterAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(AssignUnassignStudentToSemesterAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun AssignUnassignStudentToSemesterScreenContent(
    state: AssignUnassignStudentToSemesterState,
    onAction: (AssignUnassignStudentToSemesterAction) -> Unit,
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
                text = "Select a branch, semester number, and academic year to find the semester",
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
                    onSelectItem = { onAction(AssignUnassignStudentToSemesterAction.SelectBranch(it)) },
                    label = "Select Branch",
                    itemToString = { it.name },
                    expanded = state.isBranchDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AssignUnassignStudentToSemesterAction.ChangeBranchDropDownVisibility(it)) },
                    supportingText = state.branchError,
                    enabled = !state.areBranchesLoading,
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
                    onSelectItem = { onAction(AssignUnassignStudentToSemesterAction.SelectSemesterNumber(it)) },
                    label = "Select Semester Number",
                    itemToString = { it.toDisplayLabel() },
                    expanded = state.isSemesterNumberDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AssignUnassignStudentToSemesterAction.ChangeSemesterNumberDropDownVisibility(it)) },
                    supportingText = state.semesterNumberError,
                    enabled = true,
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
                        onValueChange = { onAction(AssignUnassignStudentToSemesterAction.UpdateStartYear(it)) },
                        label = "Start Year",
                        supportingText = state.startYearError,
                        isError = state.startYearError != null,
                        modifier = Modifier.weight(1f)
                    )

                    PresencifyTextField(
                        value = state.endYear,
                        onValueChange = { onAction(AssignUnassignStudentToSemesterAction.UpdateEndYear(it)) },
                        label = "End Year",
                        supportingText = state.endYearError,
                        isError = state.endYearError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Find and Navigate Button
            PresencifyButton(
                onClick = { onAction(AssignUnassignStudentToSemesterAction.FindAndNavigateClick) },
                text = "Find Semester & Continue",
                isLoading = state.isLookingSemester,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignToken.spacing.lg)
            )
        }
    }
}
