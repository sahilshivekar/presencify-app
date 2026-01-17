package edu.watumull.presencify.feature.users.dashboard

import edu.watumull.presencify.core.presentation.utils.BaseViewModel

class UsersDashboardViewModel : BaseViewModel<UsersDashboardState, UsersDashboardEvent, UsersDashboardAction>(
    initialState = UsersDashboardState()
) {
    override fun handleAction(action: UsersDashboardAction) {
        when (action) {
            UsersDashboardAction.ClickStudents -> sendEvent(UsersDashboardEvent.NavigateToSearchStudents)
            UsersDashboardAction.ClickTeachers -> sendEvent(UsersDashboardEvent.NavigateToSearchTeachers)

            UsersDashboardAction.ClickAssignToSemester -> sendEvent(UsersDashboardEvent.NavigateToAssignSemester)
            UsersDashboardAction.ClickRemoveFromSemester -> sendEvent(UsersDashboardEvent.NavigateToRemoveSemester)

            UsersDashboardAction.ClickAssignToDivision -> sendEvent(UsersDashboardEvent.NavigateToAssignDivision)
            UsersDashboardAction.ClickModifyDivision -> sendEvent(UsersDashboardEvent.NavigateToModifyDivision)
            UsersDashboardAction.ClickRemoveFromDivision -> sendEvent(UsersDashboardEvent.NavigateToRemoveDivision)

            UsersDashboardAction.ClickAssignToBatch -> sendEvent(UsersDashboardEvent.NavigateToAssignBatch)
            UsersDashboardAction.ClickModifyBatch -> sendEvent(UsersDashboardEvent.NavigateToModifyBatch)
            UsersDashboardAction.ClickRemoveFromBatch -> sendEvent(UsersDashboardEvent.NavigateToRemoveBatch)

            UsersDashboardAction.ClickAddToDropout -> sendEvent(UsersDashboardEvent.NavigateToAddToDropout)
            UsersDashboardAction.ClickRemoveFromDropout -> sendEvent(UsersDashboardEvent.NavigateToRemoveFromDropout)
        }
    }
}
