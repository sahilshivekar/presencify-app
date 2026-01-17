package edu.watumull.presencify.feature.users.di

import edu.watumull.presencify.feature.users.add_edit_student.AddEditStudentViewModel
import edu.watumull.presencify.feature.users.add_edit_teacher.AddEditTeacherViewModel
import edu.watumull.presencify.feature.users.dashboard.UsersDashboardViewModel
import edu.watumull.presencify.feature.users.search_student.SearchStudentViewModel
import edu.watumull.presencify.feature.users.search_teacher.SearchTeacherViewModel
import edu.watumull.presencify.feature.users.student_details.StudentDetailsViewModel
import edu.watumull.presencify.feature.users.teacher_details.TeacherDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val usersModule = module {
    viewModel { SearchStudentViewModel(
        studentRepository = get(),
        branchRepository = get(),
        schemeRepository = get(),
        divisionRepository = get(),
        batchRepository = get(),
        savedStateHandle = get()
    ) }
    viewModel {
        SearchTeacherViewModel(get(), get())
    }
    viewModel { UsersDashboardViewModel() }
    viewModel {
        StudentDetailsViewModel(
            studentRepository = get(),
            studentDropoutRepository = get(),
            savedStateHandle = get()
        )
    }
    viewModel {
        TeacherDetailsViewModel(
            teacherRepository = get(),
            savedStateHandle = get()
        )
    }
    viewModel {
        AddEditStudentViewModel(
            studentRepository = get(),
            branchRepository = get(),
            schemeRepository = get(),
            savedStateHandle = get()
        )
    }
    viewModel {
        AddEditTeacherViewModel(
            teacherRepository = get(),
            savedStateHandle = get()
        )
    }
}

