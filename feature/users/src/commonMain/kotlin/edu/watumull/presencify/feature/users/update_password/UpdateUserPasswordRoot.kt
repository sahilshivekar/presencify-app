package edu.watumull.presencify.feature.users.update_password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UpdateUserPasswordRoot(
    onNavigateBack: () -> Unit,
    onNavigateToMyDetails: () -> Unit,
) {
    val viewModel: UpdateUserPasswordViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is UpdateUserPasswordEvent.NavigateBack -> onNavigateBack()
            is UpdateUserPasswordEvent.NavigateToMyDetails -> onNavigateToMyDetails()
        }
    }

    UpdateUserPasswordScreen(
        state = state,
        onAction = viewModel::trySendAction,
    )
}
