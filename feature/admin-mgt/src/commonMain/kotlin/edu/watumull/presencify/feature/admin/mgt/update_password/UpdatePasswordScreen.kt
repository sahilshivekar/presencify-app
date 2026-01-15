package edu.watumull.presencify.feature.admin.mgt.update_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePasswordScreen(
    state: UpdatePasswordState,
    onAction: (UpdatePasswordAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(UpdatePasswordAction.ClickBackButton) },
        topBarTitle = "Update Password",
    ) { paddingValues ->
        UpdatePasswordScreenContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Dialog handling
    state.dialogState?.let { dialogState ->
        if (dialogState.message != null) {
            PresencifyAlertDialog(
                isVisible = dialogState.isVisible,
                dialogType = dialogState.dialogType,
                title = dialogState.title,
                message = dialogState.message.asString(),
                onDismiss = { onAction(UpdatePasswordAction.DismissDialog) }
            )
        }
    }
}

@Composable
private fun UpdatePasswordScreenContent(
    state: UpdatePasswordState,
    onAction: (UpdatePasswordAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Enter your new password and confirm it.",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            PresencifyTextField(
                value = state.password,
                onValueChange = { onAction(UpdatePasswordAction.ChangePassword(it)) },
                isError = state.passwordError != null,
                supportingText = state.passwordError,
                label = "Password",
                enabled = !state.isUpdating,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = { onAction(UpdatePasswordAction.TogglePasswordVisibility) },
                        enabled = !state.isUpdating
                    ) {
                        Icon(
                            imageVector = if (state.isPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (state.isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { confirmPasswordFocusRequester.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            PresencifyTextField(
                value = state.confirmPassword,
                onValueChange = { onAction(UpdatePasswordAction.ChangeConfirmPassword(it)) },
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError,
                label = "Confirm password",
                enabled = !state.isUpdating,
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onAction(UpdatePasswordAction.ClickUpdatePassword) }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester)
            )
        }

        PresencifyButton(
            onClick = { onAction(UpdatePasswordAction.ClickUpdatePassword) },
            enabled = !state.isUpdating,
            isLoading = state.isUpdating,
            text = "Update password"
        )
    }
}

@Preview
@Composable
private fun UpdatePasswordScreenPreview() {
    UpdatePasswordScreen(
        state = UpdatePasswordState(
            password = "newpassword123",
            confirmPassword = "newpassword123",
            passwordError = null,
            confirmPasswordError = null,
            isPasswordVisible = false,
            isUpdating = false
        ),
        onAction = {}
    )
}
