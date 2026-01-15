package edu.watumull.presencify.feature.admin.auth.forgot_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.Res
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.design.systems.presencify_logo_circle_svg
import edu.watumull.presencify.core.presentation.UiConstants
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminForgotPasswordScreen(
    state: AdminForgotPasswordState,
    onAction: (AdminForgotPasswordAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AdminForgotPasswordAction.ClickBackButton) },
        topBarTitle = "Forgot Password",
    ) { paddingValues ->
        AdminForgotPasswordScreenContent(
            state = state,
            onAction = onAction,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Dialog handling
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message!!.asString(),
            onDismiss = { onAction(AdminForgotPasswordAction.DismissDialog) }
        )
    }
}

@Composable
private fun AdminForgotPasswordScreenContent(
    state: AdminForgotPasswordState,
    onAction: (AdminForgotPasswordAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .size(80.dp),
                painter = painterResource(Res.drawable.presencify_logo_circle_svg),
                contentDescription = "Presencify Logo",
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = "Enter your email address and we will send you a verification code.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email TextField
            PresencifyTextField(
                value = state.email,
                onValueChange = { email ->
                    onAction(AdminForgotPasswordAction.ChangeEmail(email))
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
                        onAction(AdminForgotPasswordAction.ClickSendCode)
                    }
                )
            )
        }

        // Send Code Button
        PresencifyButton(
            onClick = { onAction(AdminForgotPasswordAction.ClickSendCode) },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            text = "Send Code"
        )
    }
}
