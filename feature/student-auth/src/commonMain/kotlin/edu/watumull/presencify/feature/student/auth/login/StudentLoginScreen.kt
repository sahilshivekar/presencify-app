package edu.watumull.presencify.feature.student.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.Res
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextButton
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.design.systems.presencify_logo_circle_svg
import edu.watumull.presencify.core.presentation.UiConstants
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentLoginScreen(
    state: StudentLoginState,
    onAction: (StudentLoginAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(StudentLoginAction.ClickBackButton) },
        topBarTitle = "Student Login",
    ) { paddingValues ->
        StudentLoginScreenContent(
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
            onDismiss = { onAction(StudentLoginAction.DismissDialog) }
        )
    }
}

@Composable
private fun StudentLoginScreenContent(
    state: StudentLoginState,
    onAction: (StudentLoginAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val passwordFocusRequester = remember { FocusRequester() }

        // Logo
        Image(
            modifier = Modifier
                .padding(top = 36.dp)
                .size(80.dp),
            painter = painterResource(Res.drawable.presencify_logo_circle_svg),
            contentDescription = "Presencify Logo",
        )

        Text(
            text = "Presencify",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(60.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
        ) {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )
            Text(
                text = "Log in to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Email/PRN Field
        PresencifyTextField(
            value = state.emailOrPRN,
            onValueChange = { onAction(StudentLoginAction.ChangeEmailOrPRN(it)) },
            label = "Email or PRN",
            enabled = !state.isLoading,
            isError = state.emailOrPRNError != null,
            supportingText = state.emailOrPRNError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
        )

        // Password Field
        PresencifyTextField(
            value = state.password,
            onValueChange = { onAction(StudentLoginAction.ChangePassword(it)) },
            label = "Password",
            visualTransformation = if (state.isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAction(StudentLoginAction.ClickLogin) }
            ),
            enabled = !state.isLoading,
            isError = state.passwordError != null,
            supportingText = state.passwordError,
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
                .widthIn(max = 800.dp)
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onAction(StudentLoginAction.TogglePasswordVisibility(!state.isPasswordVisible))
                    },
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = if (state.isPasswordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (state.isPasswordVisible) "Hide password" else "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
        )

        // Login Button
        PresencifyButton(
            onClick = { onAction(StudentLoginAction.ClickLogin) },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            text = "Log in"
        )

        Spacer(Modifier.height(16.dp))

        // Forgot Password Button
        PresencifyTextButton(
            onClick = { onAction(StudentLoginAction.ClickForgotPassword) },
            text = "Forgot password?",
            enabled = !state.isLoading
        )
    }
}
