package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import edu.watumull.presencify.feature.academics.navigation.SearchCourseIntention


fun NavController.navigateToAcademicsDashboard() {
    navigate(AcademicsRoutes.AcademicsDashboard)
}


fun NavController.navigateToAddEditBranch(branchId: String? = null) {
    navigate(AcademicsRoutes.AddEditBranch(branchId = branchId))
}


fun NavController.navigateToSearchBranch() {
    navigate(AcademicsRoutes.SearchBranch)
}


fun NavController.navigateToBranchDetails(branchId: String) {
    navigate(AcademicsRoutes.BranchDetails(branchId = branchId))
}


fun NavController.navigateToAddEditScheme(schemeId: String? = null) {
    navigate(AcademicsRoutes.AddEditScheme(schemeId = schemeId))
}


fun NavController.navigateToSearchScheme() {
    navigate(AcademicsRoutes.SearchScheme)
}


fun NavController.navigateToSchemeDetails(schemeId: String) {
    navigate(AcademicsRoutes.SchemeDetails(schemeId = schemeId))
}


fun NavController.navigateToAddEditUniversity(universityId: String? = null) {
    navigate(AcademicsRoutes.AddEditUniversity(universityId = universityId))
}


fun NavController.navigateToUniversityDetails() {
    navigate(AcademicsRoutes.UniversityDetails)
}


fun NavController.navigateToAddEditCourse(courseId: String? = null) {
    navigate(AcademicsRoutes.AddEditCourse(courseId = courseId))
}


fun NavController.navigateToSearchCourse(
    intention: String = SearchCourseIntention.DEFAULT.name,
    branchId: String? = null,
    semesterNumber: Int? = null,
    schemeId: String? = null,
    teacherId: String? = null,
) {
    navigate(
        AcademicsRoutes.SearchCourse(
            intention = intention,
            branchId = branchId,
            semesterNumber = semesterNumber,
            schemeId = schemeId,
            teacherId = teacherId
        )
    )
}


fun NavController.navigateToCourseDetails(courseId: String) {
    navigate(AcademicsRoutes.CourseDetails(courseId = courseId))
}


fun NavController.navigateToLinkUnlinkCourse() {
    navigate(AcademicsRoutes.LinkUnlinkCourse)
}


fun NavController.navigateToAddEditBatch(batchId: String? = null) {
    navigate(AcademicsRoutes.AddEditBatch(batchId = batchId))
}


fun NavController.navigateToSearchBatch() {
    navigate(AcademicsRoutes.SearchBatch)
}


fun NavController.navigateToBatchDetails(batchId: String) {
    navigate(AcademicsRoutes.BatchDetails(batchId = batchId))
}


fun NavController.navigateToAddEditDivision(divisionId: String? = null) {
    navigate(AcademicsRoutes.AddEditDivision(divisionId = divisionId))
}


fun NavController.navigateToSearchDivision() {
    navigate(AcademicsRoutes.SearchDivision)
}


fun NavController.navigateToDivisionDetails(divisionId: String) {
    navigate(AcademicsRoutes.DivisionDetails(divisionId = divisionId))
}


fun NavController.navigateToAddEditSemester(semesterId: String? = null) {
    navigate(AcademicsRoutes.AddEditSemester(semesterId = semesterId))
}


fun NavController.navigateToSearchSemester() {
    navigate(AcademicsRoutes.SearchSemester)
}


fun NavController.navigateToSemesterDetails(semesterId: String) {
    navigate(AcademicsRoutes.SemesterDetails(semesterId = semesterId))
}