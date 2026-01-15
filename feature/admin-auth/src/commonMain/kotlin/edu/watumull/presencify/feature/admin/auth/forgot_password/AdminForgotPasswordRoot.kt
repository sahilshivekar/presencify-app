package edu.watumull.presencify.feature.admin.auth.forgot_password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminForgotPasswordRoot(
    onBackButtonClick: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
) {
    val viewModel: AdminForgotPasswordViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AdminForgotPasswordEvent.NavigateBack -> onBackButtonClick()
            is AdminForgotPasswordEvent.NavigateToVerifyCode -> onNavigateToVerifyCode(event.email)
        }
    }

    AdminForgotPasswordScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
