package edu.watumull.presencify.feature.users.dashboard

sealed interface UsersDashboardAction {
    data object ClickStudents : UsersDashboardAction
    data object ClickTeachers : UsersDashboardAction

    data object ClickAssignUnassignSemester : UsersDashboardAction
    data object ClickAssignUnassignDivision : UsersDashboardAction
    data object ClickAssignUnassignBatch : UsersDashboardAction

    data object ClickModifyDivision : UsersDashboardAction
    data object ClickModifyBatch : UsersDashboardAction

    data object ClickMarkUnmarkStudentAsDropout : UsersDashboardAction

    data object ClickImportStudents : UsersDashboardAction
    data object ClickImportTeachers : UsersDashboardAction
}