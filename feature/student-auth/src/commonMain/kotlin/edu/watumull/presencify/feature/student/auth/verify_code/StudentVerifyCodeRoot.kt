package edu.watumull.presencify.feature.student.auth.verify_code

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StudentVerifyCodeRoot(
    onNavigateBack: () -> Unit,
    onCodeVerified: () -> Unit,
) {
    val viewModel: StudentVerifyCodeViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is StudentVerifyCodeEvent.NavigateBack -> onNavigateBack()
            is StudentVerifyCodeEvent.NavigateToHome -> onCodeVerified()
        }
    }

    StudentVerifyCodeScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
