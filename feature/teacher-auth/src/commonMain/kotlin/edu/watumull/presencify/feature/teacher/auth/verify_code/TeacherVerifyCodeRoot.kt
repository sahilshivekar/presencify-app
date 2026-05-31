package edu.watumull.presencify.feature.teacher.auth.verify_code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TeacherVerifyCodeRoot(
    onNavigateBack: () -> Unit,
    onCodeVerified: () -> Unit,
) {
    val viewModel: TeacherVerifyCodeViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is TeacherVerifyCodeEvent.NavigateBack -> onNavigateBack()
            is TeacherVerifyCodeEvent.NavigateToHome -> onCodeVerified()
        }
    }

    TeacherVerifyCodeScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
