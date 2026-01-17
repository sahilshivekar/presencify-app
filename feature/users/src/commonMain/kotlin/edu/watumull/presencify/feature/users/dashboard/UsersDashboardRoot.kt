package edu.watumull.presencify.feature.users.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsersDashboardRoot(
    onNavigateToSearchStudents: () -> Unit,
    onNavigateToSearchTeachers: () -> Unit,
    onNavigateToAssignSemester: () -> Unit,
    onNavigateToRemoveSemester: () -> Unit,
    onNavigateToAssignDivision: () -> Unit,
    onNavigateToModifyDivision: () -> Unit,
    onNavigateToRemoveDivision: () -> Unit,
    onNavigateToAssignBatch: () -> Unit,
    onNavigateToModifyBatch: () -> Unit,
    onNavigateToRemoveBatch: () -> Unit,
    onNavigateToAddToDropout: () -> Unit,
    onNavigateToRemoveFromDropout: () -> Unit,
    viewModel: UsersDashboardViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            UsersDashboardEvent.NavigateToSearchStudents -> onNavigateToSearchStudents()
            UsersDashboardEvent.NavigateToSearchTeachers -> onNavigateToSearchTeachers()

            UsersDashboardEvent.NavigateToAssignSemester -> onNavigateToAssignSemester()
            UsersDashboardEvent.NavigateToRemoveSemester -> onNavigateToRemoveSemester()

            UsersDashboardEvent.NavigateToAssignDivision -> onNavigateToAssignDivision()
            UsersDashboardEvent.NavigateToModifyDivision -> onNavigateToModifyDivision()
            UsersDashboardEvent.NavigateToRemoveDivision -> onNavigateToRemoveDivision()

            UsersDashboardEvent.NavigateToAssignBatch -> onNavigateToAssignBatch()
            UsersDashboardEvent.NavigateToModifyBatch -> onNavigateToModifyBatch()
            UsersDashboardEvent.NavigateToRemoveBatch -> onNavigateToRemoveBatch()

            UsersDashboardEvent.NavigateToAddToDropout -> onNavigateToAddToDropout()
            UsersDashboardEvent.NavigateToRemoveFromDropout -> onNavigateToRemoveFromDropout()
        }
    }

    UsersDashboardScreen(
        state = state,
        onAction = viewModel::trySendAction
    )
}