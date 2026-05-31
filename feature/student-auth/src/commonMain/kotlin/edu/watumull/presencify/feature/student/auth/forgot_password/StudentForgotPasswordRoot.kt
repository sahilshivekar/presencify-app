package edu.watumull.presencify.feature.student.auth.forgot_password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentForgotPasswordRoot(
    onNavigateBack: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
) {
    val viewModel: StudentForgotPasswordViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is StudentForgotPasswordEvent.NavigateBack -> onNavigateBack()
            is StudentForgotPasswordEvent.NavigateToVerifyCode -> onNavigateToVerifyCode(event.email)
        }
    }

    StudentForgotPasswordScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
