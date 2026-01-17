package edu.watumull.presencify.feature.users.dashboard

sealed interface UsersDashboardEvent {
    data object NavigateToSearchStudents : UsersDashboardEvent
    data object NavigateToSearchTeachers : UsersDashboardEvent

    data object NavigateToAssignSemester : UsersDashboardEvent
    data object NavigateToRemoveSemester : UsersDashboardEvent

    data object NavigateToAssignDivision : UsersDashboardEvent
    data object NavigateToModifyDivision : UsersDashboardEvent
    data object NavigateToRemoveDivision : UsersDashboardEvent

    data object NavigateToAssignBatch : UsersDashboardEvent
    data object NavigateToModifyBatch : UsersDashboardEvent
    data object NavigateToRemoveBatch : UsersDashboardEvent

    data object NavigateToAddToDropout : UsersDashboardEvent
    data object NavigateToRemoveFromDropout : UsersDashboardEvent
}