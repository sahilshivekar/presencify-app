package edu.watumull.presencify.feature.academics.add_edit_course

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
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.academics.Scheme
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditCourseScreen(
    state: AddEditCourseState,
    onAction: (AddEditCourseAction) -> Unit,
    onConfirmNavigateBack: () -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditCourseAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Course" else "Add Course",
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
                Text(
                    text = "Course Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = DesignToken.spacing.lg)
                )

                PresencifyTextField(
                    value = state.code,
                    onValueChange = { onAction(AddEditCourseAction.UpdateCode(it)) },
                    label = "Course code *",
                    supportingText = state.codeError,
                    isError = state.codeError != null,
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(DesignToken.spacing.lg))

                PresencifyTextField(
                    value = state.name,
                    onValueChange = { onAction(AddEditCourseAction.UpdateName(it)) },
                    label = "Course name *",
                    supportingText = state.nameError,
                    isError = state.nameError != null,
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(DesignToken.spacing.lg))

                PresencifyTextField(
                    value = state.optionalCourse,
                    onValueChange = { onAction(AddEditCourseAction.UpdateOptionalCourse(it)) },
                    label = "Optional course",
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(DesignToken.spacing.lg))

                PresencifyDropDownMenuBox<Scheme>(
                    value = state.schemeOptions.find { it.id == state.selectedSchemeId }?.name ?: "",
                    options = state.schemeOptions,
                    onSelectItem = { onAction(AddEditCourseAction.UpdateSelectedScheme(it.id)) },
                    label = "Scheme *",
                    itemToString = { it.name },
                    expanded = state.isSchemeDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(AddEditCourseAction.ChangeSchemeDropDownVisibility(it)) },
                    supportingText = state.schemeError,
                    enabled = !state.isLoading && !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(DesignToken.spacing.xl))

                PresencifyButton(
                    onClick = { onAction(AddEditCourseAction.SubmitClick) },
                    text = if (state.isEditMode) "Update Course" else "Add Course",
                    isLoading = state.isSubmitting,
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth()
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
                    DialogIntention.CONFIRM_NAVIGATION_WITH_UNSAVED_CHANGES -> onConfirmNavigateBack()
                    DialogIntention.GENERIC -> onAction(AddEditCourseAction.DismissDialog)
                }
            },
            onDismiss = { onAction(AddEditCourseAction.DismissDialog) }
        )
    }
}

