package edu.watumull.presencify.feature.teacher.auth.login

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
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.components.PresencifyTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.presencify_logo_circle_svg
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.presentation.UiConstants
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherLoginScreen(
    state: TeacherLoginState,
    onAction: (TeacherLoginAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(TeacherLoginAction.ClickBackButton) },
        topBarTitle = "Teacher Login",
    ) { paddingValues ->
        TeacherLoginScreenContent(
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
            onDismiss = { onAction(TeacherLoginAction.DismissDialog) }
        )
    }
}

@Composable
private fun TeacherLoginScreenContent(
    state: TeacherLoginState,
    onAction: (TeacherLoginAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .padding(DesignToken.spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val passwordFocusRequester = remember { FocusRequester() }

        Image(
            modifier = Modifier
                .padding(top = DesignToken.spacing.xxl)
                .size(DesignToken.images.sm),
            painter = painterResource(Res.drawable.presencify_logo_circle_svg),
            contentDescription = "Presencify Logo",
        )

        Text(
            text = "Presencify",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(DesignToken.spacing.huge))

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

        Spacer(Modifier.height(DesignToken.spacing.sm))

        PresencifyTextField(
            value = state.email,
            onValueChange = { onAction(TeacherLoginAction.ChangeEmail(it)) },
            label = "Email",
            enabled = !state.isLoading,
            isError = state.emailError != null,
            supportingText = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
        )

        PresencifyTextField(
            value = state.password,
            onValueChange = { onAction(TeacherLoginAction.ChangePassword(it)) },
            label = "Password",
            visualTransformation = if (state.isPasswordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onAction(TeacherLoginAction.ClickLogin) }
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
                        onAction(TeacherLoginAction.TogglePasswordVisibility(!state.isPasswordVisible))
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

        PresencifyButton(
            onClick = { onAction(TeacherLoginAction.ClickLogin) },
            enabled = !state.isLoading,
            isLoading = state.isLoading,
            text = "Log in"
        )

        Spacer(Modifier.height(DesignToken.spacing.lg))

        PresencifyTextButton(
            onClick = { onAction(TeacherLoginAction.ClickForgotPassword) },
            text = "Forgot password?",
            enabled = !state.isLoading
        )
    }
}
