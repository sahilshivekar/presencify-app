package edu.watumull.presencify.feature.schedule.dashboard

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.utils.EventsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ScheduleDashboardRoot(
    onNavigateToSearchRoom: () -> Unit,
    onNavigateToSearchClass: () -> Unit,
    onNavigateToSearchTimetable: () -> Unit,
    onNavigateToClassDetails: (String) -> Unit,
    viewModel: ScheduleDashboardViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val userRole = LocalUserRole.current

    LaunchedEffect(userRole) {
        when (userRole) {
            UserRole.STUDENT, UserRole.TEACHER -> {
                viewModel.trySendAction(ScheduleDashboardAction.LoadUpcomingClasses(userRole))
            }
            else -> viewModel.trySendAction(ScheduleDashboardAction.ClearUpcomingClasses)
        }
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ScheduleDashboardEvent.NavigateToRoom -> onNavigateToSearchRoom()
            ScheduleDashboardEvent.NavigateToClasses -> onNavigateToSearchClass()
            ScheduleDashboardEvent.NavigateToTimetable -> onNavigateToSearchTimetable()
            is ScheduleDashboardEvent.NavigateToClassDetails -> onNavigateToClassDetails(event.classId)
        }
    }

    ScheduleDashboardScreen(
        state = state,
        currentUserRole = userRole,
        onAction = viewModel::trySendAction
    )
}

