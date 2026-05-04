package edu.watumull.presencify.feature.users.modify_student_division

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.utils.toReadableString
import kotlinx.datetime.LocalDate

@Composable
fun ModifyStudentDivisionScreen(
    state: ModifyStudentDivisionState,
    onAction: (ModifyStudentDivisionAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(ModifyStudentDivisionAction.BackButtonClick) },
        topBarTitle = "Modify Student Division",
    ) { paddingValues ->
        when (state.viewState) {
            is ModifyStudentDivisionState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is ModifyStudentDivisionState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is ModifyStudentDivisionState.ViewState.Content -> {
                ModifyStudentDivisionScreenContent(
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
                        onAction(ModifyStudentDivisionAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(ModifyStudentDivisionAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun ModifyStudentDivisionScreenContent(
    state: ModifyStudentDivisionState,
    onAction: (ModifyStudentDivisionAction) -> Unit,
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
                text = "Select a branch, semester number, and academic year to find divisions. Then select the new division and start date.",
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
                    onSelectItem = { onAction(ModifyStudentDivisionAction.SelectBranch(it)) },
                    label = "Select Branch",
                    itemToString = { it.name },
                    expanded = state.isBranchDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(ModifyStudentDivisionAction.ChangeBranchDropDownVisibility(it)) },
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
                    onSelectItem = { onAction(ModifyStudentDivisionAction.SelectSemesterNumber(it)) },
                    label = "Select Semester Number",
                    itemToString = { it.toDisplayLabel() },
                    expanded = state.isSemesterNumberDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(ModifyStudentDivisionAction.ChangeSemesterNumberDropDownVisibility(it)) },
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
                        onValueChange = { onAction(ModifyStudentDivisionAction.UpdateStartYear(it)) },
                        label = "Start Year",
                        supportingText = state.startYearError,
                        isError = state.startYearError != null,
                        modifier = Modifier.weight(1f)
                    )

                    PresencifyTextField(
                        value = state.endYear,
                        onValueChange = { onAction(ModifyStudentDivisionAction.UpdateEndYear(it)) },
                        label = "End Year",
                        supportingText = state.endYearError,
                        isError = state.endYearError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Find Divisions Button
            PresencifyButton(
                onClick = { onAction(ModifyStudentDivisionAction.FindDivisionsClick) },
                text = "Find Divisions",
                isLoading = state.isLookingDivisions,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Division Selection (shown only after finding divisions)
            if (state.areDivisionsVisible) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "New Division",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    PresencifyDropDownMenuBox<Division>(
                        value = state.selectedDivision?.divisionCode ?: "",
                        options = state.divisionOptions,
                        onSelectItem = { onAction(ModifyStudentDivisionAction.SelectDivision(it)) },
                        label = "Select Division",
                        itemToString = { it.divisionCode },
                        expanded = state.isDivisionDropdownOpen,
                        onDropDownVisibilityChanged = { onAction(ModifyStudentDivisionAction.ChangeDivisionDropDownVisibility(it)) },
                        supportingText = state.divisionError,
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // New Division Start Date Selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "New Division Start Date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    PresencifyDatePickerTextField(
                        value = state.newDivisionStartDate,
                        onValueChange = {
                            onAction(ModifyStudentDivisionAction.UpdateNewDivisionStartDate(it))
                        },
                        label = "Active From",
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = state.newDivisionStartDateError,
                        isError = state.newDivisionStartDateError != null,
                    )
                }

                // Navigate to Search Student Button
                PresencifyButton(
                    onClick = { onAction(ModifyStudentDivisionAction.NavigateToSearchStudentClick) },
                    text = "Continue to Search Students",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

