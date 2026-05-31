package edu.watumull.presencify.feature.users.update_password

import androidx.compose.foundation.background
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
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateUserPasswordScreen(
    state: UpdateUserPasswordState,
    onAction: (UpdateUserPasswordAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(UpdateUserPasswordAction.ClickBackButton) },
        topBarTitle = "Update Password",
    ) { paddingValues ->
        UpdateUserPasswordScreenContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues)
        )
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onDismiss = { onAction(UpdateUserPasswordAction.DismissDialog) }
        )
    }
}

@Composable
private fun UpdateUserPasswordScreenContent(
    state: UpdateUserPasswordState,
    onAction: (UpdateUserPasswordAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(DesignToken.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Enter your new password and confirm it.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

                PresencifyTextField(
                    value = state.password,
                    onValueChange = { onAction(UpdateUserPasswordAction.ChangePassword(it)) },
                    isError = state.passwordError != null,
                    supportingText = state.passwordError,
                    label = "Password",
                    enabled = !state.isUpdating,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { onAction(UpdateUserPasswordAction.TogglePasswordVisibility) },
                            enabled = !state.isUpdating
                        ) {
                            Icon(
                                imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
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

                Spacer(modifier = Modifier.height(DesignToken.spacing.sm))

                PresencifyTextField(
                    value = state.confirmPassword,
                    onValueChange = { onAction(UpdateUserPasswordAction.ChangeConfirmPassword(it)) },
                    isError = state.confirmPasswordError != null,
                    supportingText = state.confirmPasswordError,
                    label = "Confirm password",
                    enabled = !state.isUpdating,
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onAction(UpdateUserPasswordAction.ClickUpdatePassword) }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(confirmPasswordFocusRequester)
                )
            }

            Spacer(modifier = Modifier.height(DesignToken.spacing.xxl))

            PresencifyButton(
                onClick = { onAction(UpdateUserPasswordAction.ClickUpdatePassword) },
                enabled = !state.isUpdating,
                isLoading = state.isUpdating,
                text = "Update password",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
