package edu.watumull.presencify.feature.schedule.dashboard

import androidx.lifecycle.viewModelScope
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ScheduleDashboardViewModel(
    private val classSessionRepository: ClassSessionRepository
) : BaseViewModel<ScheduleDashboardState, ScheduleDashboardEvent, ScheduleDashboardAction>(
    initialState = ScheduleDashboardState()
) {
    private var loadUpcomingClassesJob: Job? = null
    private var loadedUserRole: UserRole? = null

    override fun handleAction(action: ScheduleDashboardAction) {
        when (action) {
            ScheduleDashboardAction.ClickRoom -> sendEvent(ScheduleDashboardEvent.NavigateToRoom)
            ScheduleDashboardAction.ClickClasses -> sendEvent(ScheduleDashboardEvent.NavigateToClasses)
            ScheduleDashboardAction.ClickTimetable -> sendEvent(ScheduleDashboardEvent.NavigateToTimetable)
            is ScheduleDashboardAction.ClickUpcomingClass -> {
                sendEvent(ScheduleDashboardEvent.NavigateToClassDetails(action.classId))
            }
            is ScheduleDashboardAction.LoadUpcomingClasses -> loadUpcomingClasses(action.userRole)
            ScheduleDashboardAction.ClearUpcomingClasses -> {
                loadedUserRole = null
                loadUpcomingClassesJob?.cancel()
                updateState {
                    it.copy(
                        upcomingClasses = emptyList(),
                        isLoadingUpcomingClasses = false,
                        upcomingClassesError = null
                    )
                }
            }
        }
    }

    private fun loadUpcomingClasses(userRole: UserRole) {
        if (userRole == loadedUserRole && stateFlow.value.upcomingClasses.isNotEmpty()) return

        loadedUserRole = userRole
        loadUpcomingClassesJob?.cancel()
        loadUpcomingClassesJob = viewModelScope.launch {
            updateState {
                it.copy(
                    isLoadingUpcomingClasses = true,
                    upcomingClassesError = null
                )
            }

            classSessionRepository.getUpcomingClasses(userRole)
                .onSuccess { classes ->
                    updateState {
                        it.copy(
                            upcomingClasses = classes,
                            isLoadingUpcomingClasses = false,
                            upcomingClassesError = null
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isLoadingUpcomingClasses = false,
                            upcomingClassesError = error.toUiText()
                        )
                    }
                }
        }
    }
}

