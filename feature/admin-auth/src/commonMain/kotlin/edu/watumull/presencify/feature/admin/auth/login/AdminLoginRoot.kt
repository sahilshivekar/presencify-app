package edu.watumull.presencify.feature.admin.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminLoginRoot(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val viewModel: AdminLoginViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AdminLoginEvent.NavigateBack -> onNavigateBack()
            is AdminLoginEvent.NavigateToHome -> onNavigateToHome()
            is AdminLoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
        }
    }

    AdminLoginScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
