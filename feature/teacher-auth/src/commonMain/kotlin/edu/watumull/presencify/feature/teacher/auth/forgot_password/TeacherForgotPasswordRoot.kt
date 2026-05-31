package edu.watumull.presencify.feature.teacher.auth.forgot_password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherForgotPasswordRoot(
    onNavigateBack: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
) {
    val viewModel: TeacherForgotPasswordViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is TeacherForgotPasswordEvent.NavigateBack -> onNavigateBack()
            is TeacherForgotPasswordEvent.NavigateToVerifyCode -> onNavigateToVerifyCode(event.email)
        }
    }

    TeacherForgotPasswordScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
