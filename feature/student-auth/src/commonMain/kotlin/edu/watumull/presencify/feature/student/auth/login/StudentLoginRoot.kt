package edu.watumull.presencify.feature.student.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentLoginRoot(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val viewModel: StudentLoginViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is StudentLoginEvent.NavigateBack -> onNavigateBack()
            is StudentLoginEvent.NavigateToHome -> onNavigateToHome()
            is StudentLoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
        }
    }

    StudentLoginScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
