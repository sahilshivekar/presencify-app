package edu.watumull.presencify.feature.teacher.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherLoginRoot(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val viewModel: TeacherLoginViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is TeacherLoginEvent.NavigateBack -> onNavigateBack()
            is TeacherLoginEvent.NavigateToHome -> onNavigateToHome()
            is TeacherLoginEvent.NavigateToForgotPassword -> onNavigateToForgotPassword()
        }
    }

    TeacherLoginScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
