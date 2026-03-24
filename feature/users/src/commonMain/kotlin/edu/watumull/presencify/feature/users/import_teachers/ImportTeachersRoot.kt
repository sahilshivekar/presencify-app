package edu.watumull.presencify.feature.users.import_teachers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportTeachersRoot(
    onNavigateBack: () -> Unit,
) {
    val viewModel: ImportTeachersViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ImportTeachersEvent.NavigateBack -> onNavigateBack()
        }
    }

    ImportTeachersScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
