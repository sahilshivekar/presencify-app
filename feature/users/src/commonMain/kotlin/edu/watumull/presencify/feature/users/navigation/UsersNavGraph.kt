package edu.watumull.presencify.feature.users.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.users.add_edit_student.AddEditStudentRoot
import edu.watumull.presencify.feature.users.add_edit_teacher.AddEditTeacherRoot
import edu.watumull.presencify.feature.users.assign_unassign_student_to_batch.AssignUnassignStudentToBatchRoot
import edu.watumull.presencify.feature.users.assign_unassign_student_to_division.AssignUnassignStudentToDivisionRoot
import edu.watumull.presencify.feature.users.assign_unassign_student_to_semester.AssignUnassignStudentToSemesterRoot
import edu.watumull.presencify.feature.users.dashboard.UsersDashboardRoot
import edu.watumull.presencify.feature.users.import_students.ImportStudentsRoot
import edu.watumull.presencify.feature.users.import_teachers.ImportTeachersRoot
import edu.watumull.presencify.feature.users.mark_unmark_student_dropout.MarkUnmarkStudentAsDropoutRoot
import edu.watumull.presencify.feature.users.modify_student_batch.ModifyStudentBatchRoot
import edu.watumull.presencify.feature.users.modify_student_division.ModifyStudentDivisionRoot
import edu.watumull.presencify.feature.users.search_student.SearchStudentRoot
import edu.watumull.presencify.feature.users.search_teacher.SearchTeacherRoot
import edu.watumull.presencify.feature.users.student_details.StudentDetailsRoot
import edu.watumull.presencify.feature.users.teacher_details.TeacherDetailsRoot
import edu.watumull.presencify.feature.users.update_password.UpdateUserPasswordRoot

fun NavGraphBuilder.usersDashboard(
    onNavigateToSearchStudents: () -> Unit,
    onNavigateToSearchTeachers: () -> Unit,
    onNavigateToAssignUnassignSemester: () -> Unit,
    onNavigateToAssignUnassignDivision: () -> Unit,
    onNavigateToAssignUnassignBatch: () -> Unit,
    onNavigateToModifyDivision: () -> Unit,
    onNavigateToModifyBatch: () -> Unit,
    onNavigateToMarkUnmarkStudentAsDropout: () -> Unit,
    onNavigateToImportStudents: () -> Unit,
    onNavigateToImportTeachers: () -> Unit,
) {
    composableWithSlideTransitions<UsersRoutes.UsersDashboard> {
        UsersDashboardRoot(
            onNavigateToSearchStudents = onNavigateToSearchStudents,
            onNavigateToSearchTeachers = onNavigateToSearchTeachers,
            onNavigateToAssignUnassignSemester = onNavigateToAssignUnassignSemester,
            onNavigateToAssignUnassignDivision = onNavigateToAssignUnassignDivision,
            onNavigateToAssignUnassignBatch = onNavigateToAssignUnassignBatch,
            onNavigateToModifyDivision = onNavigateToModifyDivision,
            onNavigateToModifyBatch = onNavigateToModifyBatch,
            onNavigateToMarkUnmarkStudentAsDropout = onNavigateToMarkUnmarkStudentAsDropout,
            onNavigateToImportStudents = onNavigateToImportStudents,
            onNavigateToImportTeachers = onNavigateToImportTeachers,
        )
    }
}

fun NavGraphBuilder.usersNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToStudentDetails: (String) -> Unit,
    onNavigateToAddEditStudent: (studentId: String?) -> Unit,
    onNavigateToStudentAttendanceAnalytics: (studentId: String) -> Unit,
    onNavigateToSearchStudentForAssignUnassignSemester: (semesterId: String, branchId: String) -> Unit,
    onNavigateToSearchStudentForAssignUnassignDivision: (
        divisionId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int
    ) -> Unit,
    onNavigateToSearchStudentForAssignUnassignBatch: (
        batchId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int
    ) -> Unit,
    onNavigateToSearchStudentForModifyDivision: (
        divisionId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int,
        newStartDate: String
    ) -> Unit,
    onNavigateToSearchStudentForModifyBatch: (
        batchId: String,
        branchId: String,
        academicStartYear: Int,
        academicEndYear: Int,
        semesterNumber: Int,
        newStartDate: String
    ) -> Unit,
    onNavigateToSearchStudentForMarkUnmarkDropout: (
        dropoutAcademicStartYear: Int,
        dropoutAcademicEndYear: Int
    ) -> Unit,
    onNavigateToTeacherDetails: (String) -> Unit,
    onNavigateToAddEditTeacher: (String?) -> Unit,
    onNavigateToAssignUnassignCourses: (String) -> Unit,
    onNavigateToAddStudentBiometrics: (String) -> Unit,
    onNavigateToUpdateUserPassword: () -> Unit,
) {

    composableWithSlideTransitions<UsersRoutes.ImportStudents> {
        ImportStudentsRoot(
            onNavigateBack = onNavigateBack
        )
    }

    composableWithSlideTransitions<UsersRoutes.ImportTeachers> {
        ImportTeachersRoot(
            onNavigateBack = onNavigateBack
        )
    }

    composableWithSlideTransitions<UsersRoutes.AddEditStudent> {
        AddEditStudentRoot(
            onNavigateBack = onNavigateBack
        )
    }
    composableWithSlideTransitions<UsersRoutes.SearchStudent> {
        SearchStudentRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToStudentDetails = onNavigateToStudentDetails,
            onNavigateToAddEditStudent = onNavigateToAddEditStudent,
            onNavigateToStudentAttendanceAnalytics = onNavigateToStudentAttendanceAnalytics,
            onNavigateToAddStudentBiometrics = onNavigateToAddStudentBiometrics
        )
    }
    composableWithSlideTransitions<UsersRoutes.AssignUnassignStudentToSemester> {
        AssignUnassignStudentToSemesterRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = onNavigateToSearchStudentForAssignUnassignSemester
        )
    }
    composableWithSlideTransitions<UsersRoutes.AssignUnassignStudentToDivision> {
        AssignUnassignStudentToDivisionRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = onNavigateToSearchStudentForAssignUnassignDivision
        )
    }
    composableWithSlideTransitions<UsersRoutes.ModifyStudentDivision> {
        ModifyStudentDivisionRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = { divisionId, branchId, academicStartYear, academicEndYear, semesterNumber, newStartDate ->
                onNavigateToSearchStudentForModifyDivision(
                    divisionId,
                    branchId,
                    academicStartYear,
                    academicEndYear,
                    semesterNumber,
                    newStartDate,
                )
            }
        )
    }
    composableWithSlideTransitions<UsersRoutes.AssignUnassignStudentToBatch> {
        AssignUnassignStudentToBatchRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = onNavigateToSearchStudentForAssignUnassignBatch
        )
    }
    composableWithSlideTransitions<UsersRoutes.ModifyStudentBatch> {
        ModifyStudentBatchRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = { batchId, branchId, academicStartYear, academicEndYear, semesterNumber, newStartDate ->
                onNavigateToSearchStudentForModifyBatch(
                    batchId,
                    branchId,
                    academicStartYear,
                    academicEndYear,
                    semesterNumber,
                    newStartDate,
                )
            }
        )
    }
    composableWithSlideTransitions<UsersRoutes.MarkUnmarkStudentAsDropout> {
        MarkUnmarkStudentAsDropoutRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSearchStudent = onNavigateToSearchStudentForMarkUnmarkDropout
        )
    }
    composableWithSlideTransitions<UsersRoutes.StudentDetails> {
        StudentDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditStudent = onNavigateToAddEditStudent,
            onNavigateToUpdatePassword = onNavigateToUpdateUserPassword
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
    composableWithSlideTransitions<UsersRoutes.AssignCourseToTeacher> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.UnassignCourseToTeacher> {
        // TODO: Add screen content
    }
    composableWithSlideTransitions<UsersRoutes.TeacherDetails> {
        TeacherDetailsRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToEditTeacher = onNavigateToAddEditTeacher,
            onNavigateToAssignUnassignCourses = onNavigateToAssignUnassignCourses,
            onNavigateToUpdatePassword = onNavigateToUpdateUserPassword
        )
    }

    composableWithSlideTransitions<UsersRoutes.UpdateUserPassword> {
        UpdateUserPasswordRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToMyDetails = onNavigateBack,
        )
    }
}
