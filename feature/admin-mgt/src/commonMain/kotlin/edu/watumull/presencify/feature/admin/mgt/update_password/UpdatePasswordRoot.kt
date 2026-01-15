package edu.watumull.presencify.feature.admin.mgt.update_password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UpdatePasswordRoot(
    onBackButtonClick: () -> Unit,
    onNavigateToAdminDetails: () -> Unit,
) {
    val viewModel: UpdatePasswordViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is UpdatePasswordEvent.NavigateBack -> onBackButtonClick()
            is UpdatePasswordEvent.NavigateToAdminDetails -> onNavigateToAdminDetails()
        }
    }

    UpdatePasswordScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
