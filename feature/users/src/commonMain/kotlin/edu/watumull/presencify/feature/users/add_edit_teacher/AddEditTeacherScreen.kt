package edu.watumull.presencify.feature.users.add_edit_teacher

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
import androidx.compose.material3.Switch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.TeacherRole
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditTeacherScreen(
    state: AddEditTeacherState,
    onAction: (AddEditTeacherAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditTeacherAction.NavigateBack) },
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
                    .padding(DesignToken.spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddEditTeacherScreenContent(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onConfirm = {
                onAction(AddEditTeacherAction.ConfirmNavigateBack)
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
        Text(
            text = "Personal Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = DesignToken.spacing.lg)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
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

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.lastName,
            onValueChange = { onAction(AddEditTeacherAction.UpdateLastName(it)) },
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
            onSelectItem = { onAction(AddEditTeacherAction.UpdateGender(it)) },
            label = "Gender *",
            itemToString = { it.toDisplayLabel() },
            expanded = state.isGenderDropdownOpen,
            onDropDownVisibilityChanged = { onAction(AddEditTeacherAction.ChangeGenderDropDownVisibility(it)) },
            supportingText = state.genderError,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.highestQualification,
            onValueChange = { onAction(AddEditTeacherAction.UpdateHighestQualification(it)) },
            label = "Highest Qualification",
            supportingText = state.highestQualificationError,
            isError = state.highestQualificationError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

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

        Spacer(Modifier.height(DesignToken.spacing.xl))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (state.isActive) "Teacher is currently active" else "Teacher is currently inactive",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = state.isActive,
                onCheckedChange = { onAction(AddEditTeacherAction.UpdateIsActive(it)) },
                enabled = !state.isLoading && !state.isSubmitting
            )
        }

        Spacer(Modifier.height(DesignToken.spacing.xl))

        Text(
            text = "Contact Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = DesignToken.spacing.lg)
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

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextField(
            value = state.phoneNumber,
            onValueChange = { onAction(AddEditTeacherAction.UpdatePhoneNumber(it)) },
            label = "Phone Number *",
            supportingText = state.phoneNumberError,
            isError = state.phoneNumberError != null,
            enabled = !state.isLoading && !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(DesignToken.spacing.xl))

        PresencifyButton(
            onClick = { onAction(AddEditTeacherAction.SubmitClick) },
            text = if (state.isEditMode) "Update Teacher" else "Add Teacher",
            isLoading = state.isSubmitting,
            enabled = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

