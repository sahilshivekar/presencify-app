package edu.watumull.presencify.feature.users.import_students

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ImportStudentsRoot(
    onNavigateBack: () -> Unit,
) {
    val viewModel: ImportStudentsViewModel = koinViewModel()
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is ImportStudentsEvent.NavigateBack -> onNavigateBack()
        }
    }

    ImportStudentsScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}
