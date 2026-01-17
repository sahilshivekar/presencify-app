package edu.watumull.presencify.feature.users.dashboard

sealed interface UsersDashboardAction {
    data object ClickStudents : UsersDashboardAction
    data object ClickTeachers : UsersDashboardAction

    // Semester
    data object ClickAssignToSemester : UsersDashboardAction
    data object ClickRemoveFromSemester : UsersDashboardAction

    // Division
    data object ClickAssignToDivision : UsersDashboardAction
    data object ClickModifyDivision : UsersDashboardAction
    data object ClickRemoveFromDivision : UsersDashboardAction

    // Batch
    data object ClickAssignToBatch : UsersDashboardAction
    data object ClickModifyBatch : UsersDashboardAction
    data object ClickRemoveFromBatch : UsersDashboardAction

    // Dropout
    data object ClickAddToDropout : UsersDashboardAction
    data object ClickRemoveFromDropout : UsersDashboardAction
}