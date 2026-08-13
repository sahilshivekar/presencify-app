package edu.watumull.presencify.feature.student.auth.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentForgotPasswordScreen(
    state: StudentForgotPasswordState,
    onAction: (StudentForgotPasswordAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(StudentForgotPasswordAction.ClickBackButton) },
        topBarTitle = "Forgot Password",
    ) { paddingValues ->
        StudentForgotPasswordScreenContent(
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
            onDismiss = { onAction(StudentForgotPasswordAction.DismissDialog) }
        )
    }
}

@Composable
private fun StudentForgotPasswordScreenContent(
    state: StudentForgotPasswordState,
    onAction: (StudentForgotPasswordAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .padding(DesignToken.spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Enter your email address and we will send you a verification code.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),

            )

            Spacer(modifier = Modifier.height(DesignToken.spacing.xxl))

            PresencifyTextField(
                value = state.email,
                onValueChange = { email ->
                    onAction(StudentForgotPasswordAction.ChangeEmail(email))
                },
                label = "Email",
                placeholder = {
                    Text(text = "Enter email")
                },
                enabled = !state.isLoading,
                isError = state.emailError != null,
                supportingText = state.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onAction(StudentForgotPasswordAction.ClickSendCode)
                    }
                )
            )
        }

        PresencifyButton(
            onClick = { onAction(StudentForgotPasswordAction.ClickSendCode) },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            text = "Send Code"
        )
    }
}
