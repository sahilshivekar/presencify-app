package edu.watumull.presencify.feature.users.add_edit_teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.TeacherRole
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditTeacherScreen(
    state: AddEditTeacherState,
    onAction: (AddEditTeacherAction) -> Unit,
    onConfirmNavigateBack: () -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditTeacherAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Teacher" else "Add Teacher",
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddEditTeacherScreenContent(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }

    // Dialogs
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.CONFIRM_NAVIGATION_WITH_UNSAVED_CHANGES -> {
                        onConfirmNavigateBack()
                    }
                    DialogIntention.GENERIC -> {
                        onAction(AddEditTeacherAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(AddEditTeacherAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun AddEditTeacherScreenContent(
    state: AddEditTeacherState,
    onAction: (AddEditTeacherAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Personal Details Section
        Text(
            text = "Personal Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresencifyTextField(
                value = state.firstName,
                onValueChange = { onAction(AddEditTeacherAction.UpdateFirstName(it)) },
                label = "First Name *",
                supportingText = state.firstNameError,
                isError = state.firstNameError != null,
                enabled = !state.isLoading && !state.isSubmitting,
                modifier = Modifier.weight(1f)
            )

            PresencifyTextField(
                value = state.middleName,
                onValueChange = { onAction(AddEditTeacherAction.UpdateMiddleName(it)) },
                label = "Middle Name",
                supportingText = state.middleNameError,
                isError = state.middleNameError != null,
                enabled = !state.isLoading && !state.isSubmitting,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        PresencifyTextField(
            value = state.lastName,
            onValueChange = { onAction(AddEditTeacherAction.UpdateLastName(it)) },
            label = "Last Name *",
            supportingText = state.lastNameError,
            isError = state.lastNameError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PresencifyDropDownMenuBox<Gender>(
            value = state.gender?.toDisplayLabel() ?: "",
            options = Gender.entries,
            onSelectItem = { onAction(AddEditTeacherAction.UpdateGender(it)) },
            label = "Gender *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isGenderDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditTeacherAction.ChangeGenderDropDownVisibility(it)) },
            supportingText = state.genderError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PresencifyTextField(
            value = state.highestQualification,
            onValueChange = { onAction(AddEditTeacherAction.UpdateHighestQualification(it)) },
            label = "Highest Qualification",
            supportingText = state.highestQualificationError,
            isError = state.highestQualificationError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PresencifyDropDownMenuBox<TeacherRole>(
            value = state.role?.toDisplayLabel() ?: "",
            options = TeacherRole.entries,
            onSelectItem = { onAction(AddEditTeacherAction.UpdateRole(it)) },
            label = "Role *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isRoleDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditTeacherAction.ChangeRoleDropDownVisibility(it)) },
            supportingText = state.roleError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Contact Details Section
        Text(
            text = "Contact Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        PresencifyTextField(
            value = state.email,
            onValueChange = { onAction(AddEditTeacherAction.UpdateEmail(it)) },
            label = "Email *",
            supportingText = state.emailError,
            isError = state.emailError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        PresencifyTextField(
            value = state.phoneNumber,
            onValueChange = { onAction(AddEditTeacherAction.UpdatePhoneNumber(it)) },
            label = "Phone Number *",
            supportingText = state.phoneNumberError,
            isError = state.phoneNumberError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Submit Button
        PresencifyButton(
            onClick = { onAction(AddEditTeacherAction.SubmitClick) },
            text = if (state.isEditMode) "Update Teacher" else "Add Teacher",
            isLoading = state.isSubmitting,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

