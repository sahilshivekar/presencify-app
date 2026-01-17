package edu.watumull.presencify.feature.admin.mgt.admin_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminDetailsRoot(
    onBackButtonClick: () -> Unit,
    onNavigateToUpdatePassword: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onNavigateToAddAdmin: () -> Unit,
) {
    val viewModel: AdminDetailsViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AdminDetailsEvent.NavigateBack -> onBackButtonClick()
            is AdminDetailsEvent.NavigateToUpdatePassword -> onNavigateToUpdatePassword()
            is AdminDetailsEvent.NavigateToVerifyCode -> onNavigateToVerifyCode(event.email)
            is AdminDetailsEvent.NavigateToAddAdmin -> onNavigateToAddAdmin()
        }
    }

    AdminDetailsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
