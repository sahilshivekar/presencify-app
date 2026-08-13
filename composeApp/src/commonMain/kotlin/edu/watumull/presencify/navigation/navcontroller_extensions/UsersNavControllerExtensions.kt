package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.users.navigation.SearchStudentIntention
import edu.watumull.presencify.feature.users.navigation.SearchTeacherIntention
import edu.watumull.presencify.feature.users.navigation.UsersRoutes


fun NavController.navigateToUsersDashboard() {
    navigate(UsersRoutes.UsersDashboard)
}


fun NavController.navigateToAddEditStudent(studentId: String? = null) {
    navigate(UsersRoutes.AddEditStudent(studentId = studentId))
}


fun NavController.navigateToSearchStudent(
    searchQuery: String? = null,
    branchIds: List<String>? = null,
    semesterNumbers: List<Int>? = null,
    academicStartYearOfSemester: Int? = null,
    academicEndYearOfSemester: Int? = null,
    semesterId: String? = null,
    batchId: String? = null,
    schemeId: String? = null,
    divisionId: String? = null,
    dropoutAcademicStartYear: Int? = null,
    dropoutAcademicEndYear: Int? = null,
    admissionTypes: List<String>? = null,
    admissionYear: Int? = null,
    currentBatch: Boolean? = null,
    currentDivision: Boolean? = null,
    currentSemester: Boolean? = null,
    divisionCode: String? = null,
    batchCode: String? = null,
    getAll: Boolean? = null,
    intention: String = SearchStudentIntention.DEFAULT.name,
    branchId: String? = null,
    academicStartYear: Int? = null,
    academicEndYear: Int? = null,
    semesterNumber: Int? = null,
    newStartDate: String? = null,
) {
    navigate(
        UsersRoutes.SearchStudent(
            searchQuery = searchQuery,
            branchIds = branchIds,
            semesterNumbers = semesterNumbers,
            academicStartYearOfSemester = academicStartYearOfSemester,
            academicEndYearOfSemester = academicEndYearOfSemester,
            semesterId = semesterId,
            batchId = batchId,
            schemeId = schemeId,
            divisionId = divisionId,
            dropoutAcademicStartYear = dropoutAcademicStartYear,
            dropoutAcademicEndYear = dropoutAcademicEndYear,
            admissionTypes = admissionTypes,
            admissionYear = admissionYear,
            currentBatch = currentBatch,
            currentDivision = currentDivision,
            currentSemester = currentSemester,
            divisionCode = divisionCode,
            batchCode = batchCode,
            getAll = getAll,
            intention = intention,
            branchId = branchId,
            academicStartYear = academicStartYear,
            academicEndYear = academicEndYear,
            semesterNumber = semesterNumber,
            newStartDate = newStartDate,
        )
    )
}


fun NavController.navigateToAssignUnassignStudentToSemester() {
    navigate(UsersRoutes.AssignUnassignStudentToSemester)
}


fun NavController.navigateToAssignUnassignStudentToDivision() {
    navigate(UsersRoutes.AssignUnassignStudentToDivision)
}


fun NavController.navigateToModifyStudentDivision() {
    navigate(UsersRoutes.ModifyStudentDivision)
}


fun NavController.navigateToAssignUnassignStudentToBatch() {
    navigate(UsersRoutes.AssignUnassignStudentToBatch)
}


fun NavController.navigateToModifyStudentBatch() {
    navigate(UsersRoutes.ModifyStudentBatch)
}


fun NavController.navigateToMarkUnmarkStudentAsDropout() {
    navigate(UsersRoutes.MarkUnmarkStudentAsDropout)
}


fun NavController.navigateToStudentDetails(studentId: String, showSelfActions: Boolean = false) {
    navigate(UsersRoutes.StudentDetails(studentId = studentId, showSelfActions = showSelfActions))
}


fun NavController.navigateToAddEditTeacher(teacherId: String? = null) {
    navigate(UsersRoutes.AddEditTeacher(teacherId = teacherId))
}


fun NavController.navigateToSearchTeacher(
    searchQuery: String? = null,
    courseId: String? = null,
    getAll: Boolean? = null,
    intention: String = SearchTeacherIntention.DEFAULT.name,
) {
    navigate(
        UsersRoutes.SearchTeacher(
            searchQuery = searchQuery,
            courseId = courseId,
            getAll = getAll,
            intention = intention
        )
    )
}


fun NavController.navigateToAssignCourseToTeacher() {
    navigate(UsersRoutes.AssignCourseToTeacher)
}


fun NavController.navigateToUnassignCourseToTeacher() {
    navigate(UsersRoutes.UnassignCourseToTeacher)
}


fun NavController.navigateToTeacherDetails(teacherId: String, showSelfActions: Boolean = false) {
    navigate(UsersRoutes.TeacherDetails(teacherId = teacherId, showSelfActions = showSelfActions))
}


fun NavController.navigateToImportStudents() {
    navigate(UsersRoutes.ImportStudents)
}


fun NavController.navigateToImportTeachers() {
    navigate(UsersRoutes.ImportTeachers)
}


fun NavController.navigateToUpdateUserPassword() {
    navigate(UsersRoutes.UpdateUserPassword)
}


fun NavController.navigateToSubmitStudentBiometrics() {
    navigate(UsersRoutes.SubmitStudentBiometrics)
}


fun NavController.navigateToReviewStudentBiometrics(studentId: String) {
    navigate(UsersRoutes.ReviewStudentBiometrics(studentId))
}
