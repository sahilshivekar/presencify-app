package edu.watumull.presencify.feature.users.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.users.add_edit_student.AddEditStudentRoot
import edu.watumull.presencify.feature.users.add_edit_teacher.AddEditTeacherRoot
import edu.watumull.presencify.feature.users.dashboard.UsersDashboardRoot
import edu.watumull.presencify.feature.users.search_student.SearchStudentRoot
import edu.watumull.presencify.feature.users.search_teacher.SearchTeacherRoot
import edu.watumull.presencify.feature.users.student_details.StudentDetailsRoot
import edu.watumull.presencify.feature.users.teacher_details.TeacherDetailsRoot

fun NavGraphBuilder.usersDashboard(
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
) {
    composableWithSlideTransitions<UsersRoutes.UsersDashboard> {
        UsersDashboardRoot(
            onNavigateToSearchStudents = onNavigateToSearchStudents,
            onNavigateToSearchTeachers = onNavigateToSearchTeachers,
            onNavigateToAssignSemester = onNavigateToAssignSemester,
            onNavigateToRemoveSemester = onNavigateToRemoveSemester,
            onNavigateToAssignDivision = onNavigateToAssignDivision,
            onNavigateToModifyDivision = onNavigateToModifyDivision,
            onNavigateToRemoveDivision = onNavigateToRemoveDivision,
            onNavigateToAssignBatch = onNavigateToAssignBatch,
            onNavigateToModifyBatch = onNavigateToModifyBatch,
            onNavigateToRemoveBatch = onNavigateToRemoveBatch,
            onNavigateToAddToDropout = onNavigateToAddToDropout,
            onNavigateToRemoveFromDropout = onNavigateToRemoveFromDropout
        )
    }
}

fun NavGraphBuilder.usersNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToStudentDetails: (String) -> Unit,
    onNavigateToAddEditStudent: (studentId: String?) -> Unit,
    onNavigateToTeacherDetails: (String) -> Unit,
    onNavigateToAddEditTeacher: (String?) -> Unit
) {

    composableWithSlideTransitions<UsersRoutes.AddEditStudent> {
        AddEditStudentRoot(
            onNavigateBack = onNavigateBack
        )
    }
    composableWithSlideTransitions<UsersRoutes.SearchStudent> {
        SearchStudentRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToStudentDetails = onNavigateToStudentDetails,
            onNavigateToAddEditStudent = onNavigateToAddEditStudent
        )
    }
    composableWithSlideTransitions<UsersRoutes.AddStudentToSemester> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.RemoveStudentFromSemester> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.AddStudentToDivision> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.ModifyStudentDivision> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.RemoveStudentFromDivision> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.AddStudentToBatch> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.ModifyStudentBatch> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.RemoveStudentFromBatch> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.AddStudentToDropout> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.RemoveStudentFromDropout> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.StudentDetails> {
        StudentDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditStudent = onNavigateToAddEditStudent
        )
    }
    composableWithSlideTransitions<UsersRoutes.AddEditTeacher> {
        AddEditTeacherRoot(
            onNavigateBack = onNavigateBack
        )
    }
    composableWithSlideTransitions<UsersRoutes.SearchTeacher> {
        SearchTeacherRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToTeacherDetails = onNavigateToTeacherDetails,
            onNavigateToAddEditTeacher = onNavigateToAddEditTeacher
        )
    }
    composableWithSlideTransitions<UsersRoutes.AssignSubjectToTeacher> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.UnassignSubjectToTeacher> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.TeacherDetails> {
        TeacherDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditTeacher = onNavigateToAddEditTeacher
        )
    }
}
