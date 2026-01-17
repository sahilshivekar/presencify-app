package edu.watumull.presencify.feature.academics.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.academics.dashboard.AcademicsDashboardRoot
import edu.watumull.presencify.feature.academics.search_batch.SearchBatchRoot
import edu.watumull.presencify.feature.academics.search_branch.SearchBranchRoot
import edu.watumull.presencify.feature.academics.search_course.SearchCourseRoot
import edu.watumull.presencify.feature.academics.search_division.SearchDivisionRoot
import edu.watumull.presencify.feature.academics.search_scheme.SearchSchemeRoot
import edu.watumull.presencify.feature.academics.search_semester.SearchSemesterRoot

fun NavGraphBuilder.academicsDashboard(
    onNavigateToSearchBranch: () -> Unit,
    onNavigateToSearchScheme: () -> Unit,
    onNavigateToSearchCourse: () -> Unit,
    onNavigateToSearchUniversity: () -> Unit,
    onNavigateToSearchSemester: () -> Unit,
    onNavigateToSearchDivision: () -> Unit,
    onNavigateToSearchBatch: () -> Unit,
) {
    composableWithSlideTransitions<AcademicsRoutes.AcademicsDashboard> {
        AcademicsDashboardRoot(
            onNavigateToSearchBranch = onNavigateToSearchBranch,
            onNavigateToSearchScheme = onNavigateToSearchScheme,
            onNavigateToSearchCourse = onNavigateToSearchCourse,
            onNavigateToSearchUniversity = onNavigateToSearchUniversity,
            onNavigateToSearchSemester = onNavigateToSearchSemester,
            onNavigateToSearchDivision = onNavigateToSearchDivision,
            onNavigateToSearchBatch = onNavigateToSearchBatch
        )
    }
}

fun NavGraphBuilder.academicsNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToBranchDetails: (String) -> Unit,
    onNavigateToAddEditBranch: () -> Unit,
    onNavigateToSchemeDetails: (String) -> Unit,
    onNavigateToAddEditScheme: () -> Unit,
    onNavigateToCourseDetails: (String) -> Unit,
    onNavigateToAddEditCourse: () -> Unit,
    onNavigateToSemesterDetails: (String) -> Unit,
    onNavigateToAddEditSemester: () -> Unit,
    onNavigateToDivisionDetails: (String) -> Unit,
    onNavigateToAddEditDivision: () -> Unit,
    onNavigateToBatchDetails: (String) -> Unit,
    onNavigateToAddEditBatch: () -> Unit,
) {

    composableWithSlideTransitions<AcademicsRoutes.AddEditBranch> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchBranch> {
        SearchBranchRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToBranchDetails = onNavigateToBranchDetails,
            onNavigateToAddEditBranch = onNavigateToAddEditBranch
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.BranchDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditScheme> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchScheme> {
        SearchSchemeRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSchemeDetails = onNavigateToSchemeDetails,
            onNavigateToAddEditScheme = onNavigateToAddEditScheme
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.SchemeDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditUniversity> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchUniversity> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.UniversityDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditCourse> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchCourse> {
        SearchCourseRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToCourseDetails = onNavigateToCourseDetails,
            onNavigateToAddEditCourse = onNavigateToAddEditCourse
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.CourseDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.LinkCourseToSemester> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.UnlinkCourseToSemester> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditBatch> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchBatch> {
        SearchBatchRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToBatchDetails = onNavigateToBatchDetails,
            onNavigateToAddEditBatch = onNavigateToAddEditBatch
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.BatchDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditDivision> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchDivision> {
        SearchDivisionRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToDivisionDetails = onNavigateToDivisionDetails,
            onNavigateToAddEditDivision = onNavigateToAddEditDivision
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.DivisionDetails> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.AddEditSemester> {
        // TODO: Add screen content
    }

    composableWithSlideTransitions<AcademicsRoutes.SearchSemester> {
        SearchSemesterRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToSemesterDetails = onNavigateToSemesterDetails,
            onNavigateToAddEditSemester = onNavigateToAddEditSemester
        )
    }

    composableWithSlideTransitions<AcademicsRoutes.SemesterDetails> {
        // TODO: Add screen content
    }

}
