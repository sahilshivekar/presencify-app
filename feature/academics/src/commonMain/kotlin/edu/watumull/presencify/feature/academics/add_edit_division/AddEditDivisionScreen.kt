package edu.watumull.presencify.feature.academics.add_edit_division

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditDivisionScreen(
    state: AddEditDivisionState,
    onAction: (AddEditDivisionAction) -> Unit,
) {


    PresencifyScaffold(
        backPress = { onAction(AddEditDivisionAction.NavigateBack) },
        topBarTitle = if (state.isEditMode) "Edit Division" else "Add Division",
    ) { paddingValues ->
        if (state.isEditMode && state.isLoadingDivision) {
            PresencifyDefaultLoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                        .padding(DesignToken.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!state.isEditMode) {
                        // Step 1: Semester Selection
                        Text(
                            text = "Step 1: Select Semester",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = DesignToken.spacing.lg)
                        )

                        // Semester Number Dropdown
                        PresencifyDropDownMenuBox<SemesterNumber>(
                            value = state.semesterNumber?.toDisplayLabel() ?: "",
                            options = SemesterNumber.entries,
                            onSelectItem = { onAction(AddEditDivisionAction.UpdateSemesterNumber(it)) },
                            label = "Semester Number *",
                            itemToString = { it.toDisplayLabel() },
                            expanded = state.isSemesterNumberDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditDivisionAction.ChangeSemesterNumberDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.semesterNumberError,
                            enabled = !state.isLoading && !state.isSubmitting && !state.showDivisionInput,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(DesignToken.spacing.lg))

                        // Academic Start Year
                        PresencifyTextField(
                            value = state.academicStartYear,
                            onValueChange = { onAction(AddEditDivisionAction.UpdateAcademicStartYear(it)) },
                            label = "Academic Start Year *",
                            supportingText = state.academicStartYearError,
                            isError = state.academicStartYearError != null,
                            enabled = !state.isLoading && !state.isSubmitting && !state.showDivisionInput,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(DesignToken.spacing.lg))

                        // Academic End Year
                        PresencifyTextField(
                            value = state.academicEndYear,
                            onValueChange = { onAction(AddEditDivisionAction.UpdateAcademicEndYear(it)) },
                            label = "Academic End Year *",
                            supportingText = state.academicEndYearError,
                            isError = state.academicEndYearError != null,
                            enabled = !state.isLoading && !state.isSubmitting && !state.showDivisionInput,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(DesignToken.spacing.lg))

                        // Branch Dropdown
                        PresencifyDropDownMenuBox<Branch>(
                            value = state.branchOptions.find { it.id == state.selectedBranchId }?.abbreviation ?: "",
                            options = state.branchOptions,
                            onSelectItem = { onAction(AddEditDivisionAction.UpdateSelectedBranch(it.id)) },
                            label = "Branch *",
                            itemToString = { it.toDisplayLabel() },
                            expanded = state.isBranchDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditDivisionAction.ChangeBranchDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.branchError,
                            enabled = !state.isLoading && !state.isSubmitting && !state.showDivisionInput,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(DesignToken.spacing.xl))

                        // Find Semester Button
                        PresencifyButton(
                            onClick = { onAction(AddEditDivisionAction.FindSemesterClick) },
                            text = "Find Semester",
                            isLoading = state.isLoading,
                            enabled = !state.isLoading && !state.showDivisionInput,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (state.showDivisionInput && state.foundSemester != null) {
                        Spacer(Modifier.height(DesignToken.spacing.xxl))

                        if (!state.isEditMode) {
                            Text(
                                text = "Step 2: Add Division Code",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = DesignToken.spacing.sm)
                            )
                        }

                        Text(
                            text = "Semester: ${state.foundSemester.semesterNumber.value} | Branch: ${state.foundSemester.branch?.name} | Academic Year: ${state.foundSemester.academicStartYear} - ${state.foundSemester.academicEndYear}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = DesignToken.spacing.lg)
                        )

                        PresencifyTextField(
                            value = state.divisionCode,
                            onValueChange = { onAction(AddEditDivisionAction.UpdateDivisionCode(it)) },
                            label = "Division Code *",
                            supportingText = state.divisionCodeError,
                            isError = state.divisionCodeError != null,
                            enabled = !state.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (state.optionalCourseGroups.isNotEmpty()) {
                            Spacer(Modifier.height(DesignToken.spacing.xl))

                            Text(
                                text = "Optional Courses",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = DesignToken.spacing.md)
                            )

                            state.optionalCourseGroups.keys.sorted().forEach { optionalCourse ->
                                val courses = state.optionalCourseGroups[optionalCourse] ?: emptyList()
                                val selectedCourseId = state.selectedOptionalCourses[optionalCourse]
                                val selectedCourse = courses.find { it.id == selectedCourseId }
                                val isDropdownOpen = state.openOptionalDropdowns.contains(optionalCourse)

                                PresencifyDropDownMenuBox<Course>(
                                    value = selectedCourse?.toDisplayLabel() ?: "",
                                    options = courses,
                                    onSelectItem = { course ->
                                        onAction(AddEditDivisionAction.SelectOptionalCourse(optionalCourse, course.id))
                                    },
                                    label = "$optionalCourse *",
                                    itemToString = { it.toDisplayLabel() },
                                    expanded = isDropdownOpen,
                                    onDropDownVisibilityChanged = { isVisible ->
                                        onAction(
                                            AddEditDivisionAction.ChangeOptionalCourseDropdownVisibility(
                                                optionalCourse,
                                                isVisible
                                            )
                                        )
                                    },
                                    enabled = !state.isLoading && !state.isSubmitting && !state.isFetchingOptionalCourses,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(Modifier.height(DesignToken.spacing.lg))
                            }
                        }

                        Spacer(Modifier.height(DesignToken.spacing.xl))

                        PresencifyButton(
                            onClick = { onAction(AddEditDivisionAction.SubmitClick) },
                            text = if (state.isEditMode) "Edit Division" else "Add Division",
                            isLoading = state.isSubmitting,
                            enabled = !state.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(AddEditDivisionAction.ConfirmNavigateBack) },
            onDismiss = { onAction(AddEditDivisionAction.DismissDialog) }
        )
    }
}
