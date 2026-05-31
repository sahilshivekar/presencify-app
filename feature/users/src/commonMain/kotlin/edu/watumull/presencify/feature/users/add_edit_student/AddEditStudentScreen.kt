package edu.watumull.presencify.feature.users.add_edit_student

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import edu.watumull.presencify.core.designsystem.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyOutlinedButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.feature.users.add_edit_student.StudentFormStep.ACADEMIC_DETAILS
import edu.watumull.presencify.feature.users.add_edit_student.StudentFormStep.CONTACT_DETAILS
import edu.watumull.presencify.feature.users.add_edit_student.StudentFormStep.PERSONAL_DETAILS

@Composable
fun AddEditStudentScreen(
    state: AddEditStudentState,
    onAction: (AddEditStudentAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditStudentAction.NavigateBack) },
        topBarTitle = if (state.isEditMode) "Edit Student" else "Add Student",
    ) { paddingValues ->
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
                AnimatedContent(
                    modifier = Modifier.fillMaxWidth(),
                    targetState = state.currentStep,
                    transitionSpec = {
                        if (targetState.ordinal > initialState.ordinal) {
                            ContentTransform(
                                targetContentEnter = slideInHorizontally { fullWidth -> fullWidth } + fadeIn(),
                                initialContentExit = slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                            )
                        } else {
                            ContentTransform(
                                targetContentEnter = slideInHorizontally { fullWidth -> -fullWidth } + fadeIn(),
                                initialContentExit = slideOutHorizontally { fullWidth -> fullWidth } + fadeOut()
                            )
                        }
                    }
                ) { targetState ->
                    when (targetState) {
                        PERSONAL_DETAILS -> PersonalDetailsStep(state, onAction)
                        CONTACT_DETAILS -> ContactDetailsStep(state, onAction)
                        ACADEMIC_DETAILS -> AcademicDetailsStep(state, onAction)
                    }
                }

                Spacer(Modifier.height(DesignToken.spacing.lg))

                FormNavigationButtons(state, onAction)
            }
        }
    }

    // Dialogs
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onConfirm = {
                onAction(AddEditStudentAction.ConfirmNavigateBack)
            },
            onDismiss = {
                onAction(AddEditStudentAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun PersonalDetailsStep(
    state: AddEditStudentState,
    onAction: (AddEditStudentAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Personal Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = DesignToken.spacing.xl)
        )

        PresencifyTextField(
            value = state.firstName,
            onValueChange = { onAction(AddEditStudentAction.UpdateFirstName(it)) },
            label = "First Name *",
            supportingText = state.firstNameError,
            isError = state.firstNameError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.middleName,
            onValueChange = { onAction(AddEditStudentAction.UpdateMiddleName(it)) },
            label = "Middle Name",
            supportingText = state.middleNameError,
            isError = state.middleNameError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.lastName,
            onValueChange = { onAction(AddEditStudentAction.UpdateLastName(it)) },
            label = "Last Name *",
            supportingText = state.lastNameError,
            isError = state.lastNameError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyDropDownMenuBox<Gender>(
            value = state.gender?.toDisplayLabel() ?: "",
            options = Gender.entries,
            onSelectItem = { onAction(AddEditStudentAction.UpdateGender(it)) },
            label = "Gender *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isGenderDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditStudentAction.ChangeGenderDropDownVisibility(it)) },
            supportingText = state.genderError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyDatePickerTextField(
            value = state.dob,
            onValueChange = {
                onAction(AddEditStudentAction.UpdateDob(it))
            },
            label = "Date of Birth",
            modifier = Modifier.fillMaxWidth(),
            supportingText = state.dobError,
            isError = state.dobError != null,
            enabled = !state.isLoading && !state.isSubmitting,
        )
    }
}

@Composable
private fun ContactDetailsStep(
    state: AddEditStudentState,
    onAction: (AddEditStudentAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contact Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = DesignToken.spacing.xl)
        )

        PresencifyTextField(
            value = state.email,
            onValueChange = { onAction(AddEditStudentAction.UpdateEmail(it)) },
            label = "Email *",
            supportingText = state.emailError,
            isError = state.emailError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.phoneNumber,
            onValueChange = { onAction(AddEditStudentAction.UpdatePhoneNumber(it)) },
            label = "Phone Number *",
            supportingText = state.phoneNumberError,
            isError = state.phoneNumberError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.parentEmail,
            onValueChange = { onAction(AddEditStudentAction.UpdateParentEmail(it)) },
            label = "Parent's Email",
            supportingText = state.parentEmailError,
            isError = state.parentEmailError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AcademicDetailsStep(
    state: AddEditStudentState,
    onAction: (AddEditStudentAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Academic Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = DesignToken.spacing.xl)
        )

        PresencifyTextField(
            value = state.prn,
            onValueChange = { onAction(AddEditStudentAction.UpdatePrn(it)) },
            label = "PRN *",
            supportingText = state.prnError,
            isError = state.prnError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.admissionYear,
            onValueChange = { input ->
                val cleaned = input.filter { it.isDigit() }.take(4)
                onAction(AddEditStudentAction.UpdateAdmissionYear(cleaned))
            },
            label = "Admission Year *",
            supportingText = state.admissionYearError,
            isError = state.admissionYearError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyDropDownMenuBox<AdmissionType>(
            value = state.admissionType?.toDisplayLabel() ?: "",
            options = AdmissionType.entries,
            onSelectItem = { onAction(AddEditStudentAction.UpdateAdmissionType(it)) },
            label = "Admission Type *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isAdmissionTypeDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditStudentAction.ChangeAdmissionTypeDropDownVisibility(it)) },
            supportingText = state.admissionTypeError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyDropDownMenuBox<Branch>(
            value = state.branchOptions.find { it.id == state.selectedBranchId }?.abbreviation ?: "",
            options = state.branchOptions,
            onSelectItem = { onAction(AddEditStudentAction.UpdateBranch(it.id)) },
            label = "Branch *",
            itemToString = { it.abbreviation },
            expanded = state.isBranchDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditStudentAction.ChangeBranchDropDownVisibility(it)) },
            supportingText = state.branchError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyDropDownMenuBox<Scheme>(
            value = state.schemeOptions.find { it.id == state.selectedSchemeId }?.name ?: "",
            options = state.schemeOptions,
            onSelectItem = { onAction(AddEditStudentAction.UpdateScheme(it.id)) },
            label = "Scheme *",
            itemToString = { it.name },
            expanded = state.isSchemeDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditStudentAction.ChangeSchemeDropDownVisibility(it)) },
            supportingText = state.schemeError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FormNavigationButtons(
    state: AddEditStudentState,
    onAction: (AddEditStudentAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = DesignToken.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (state.currentStep != StudentFormStep.PERSONAL_DETAILS) {
            PresencifyOutlinedButton(
                onClick = { onAction(AddEditStudentAction.BackStep) },
                enabled = !state.isSubmitting,
                modifier = Modifier
                    .weight(.5f)
                    .padding(end = DesignToken.spacing.xs),
                text = "Back"
            )
        }

        val buttonText = when (state.currentStep) {
            StudentFormStep.ACADEMIC_DETAILS -> if (state.isEditMode) "Update" else "Submit"
            else -> "Next"
        }

        PresencifyButton(
            onClick = { onAction(AddEditStudentAction.ValidateAndNext) },
            text = buttonText,
            isLoading = state.isSubmitting,
            enabled = !state.isSubmitting,
            modifier = Modifier
                .weight(.5f)
                .padding(start = if (state.currentStep != StudentFormStep.PERSONAL_DETAILS) DesignToken.spacing.xs else DesignToken.spacing.none)
        )
    }
}