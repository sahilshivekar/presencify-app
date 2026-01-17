package edu.watumull.presencify.feature.academics.di

import edu.watumull.presencify.feature.academics.dashboard.AcademicsDashboardViewModel
import edu.watumull.presencify.feature.academics.search_batch.SearchBatchViewModel
import edu.watumull.presencify.feature.academics.search_branch.SearchBranchViewModel
import edu.watumull.presencify.feature.academics.search_course.SearchCourseViewModel
import edu.watumull.presencify.feature.academics.search_division.SearchDivisionViewModel
import edu.watumull.presencify.feature.academics.search_scheme.SearchSchemeViewModel
import edu.watumull.presencify.feature.academics.search_semester.SearchSemesterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val academicsModule = module {
    viewModel { SearchSemesterViewModel(get(), get(), get()) }
    viewModel { SearchBatchViewModel(get(), get(), get()) }
    viewModel { SearchDivisionViewModel(get(), get()) }
    viewModel { SearchCourseViewModel(get(), get(), get(), get()) }
    viewModel { SearchBranchViewModel(get()) }
    viewModel { SearchSchemeViewModel(get()) }
    viewModel { AcademicsDashboardViewModel() }
}

