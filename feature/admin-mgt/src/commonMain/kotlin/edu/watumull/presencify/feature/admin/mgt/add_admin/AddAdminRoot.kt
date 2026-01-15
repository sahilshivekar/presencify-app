package edu.watumull.presencify.feature.admin.mgt.add_admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddAdminRoot(
    onBackButtonClick: () -> Unit,
    onNavigateToAdminDetails: () -> Unit,
) {
    val viewModel: AddAdminViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is AddAdminEvent.NavigateBack -> onBackButtonClick()
            is AddAdminEvent.NavigateToAdminDetails -> onNavigateToAdminDetails()
        }
    }

    AddAdminScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
