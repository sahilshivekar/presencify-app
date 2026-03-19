package edu.watumull.presencify.feature.users.di

import edu.watumull.presencify.feature.users.add_edit_student.AddEditStudentViewModel
import edu.watumull.presencify.feature.users.add_edit_teacher.AddEditTeacherViewModel
import edu.watumull.presencify.feature.users.assign_unassign_student_to_batch.AssignUnassignStudentToBatchViewModel
import edu.watumull.presencify.feature.users.assign_unassign_student_to_division.AssignUnassignStudentToDivisionViewModel
import edu.watumull.presencify.feature.users.assign_unassign_student_to_semester.AssignUnassignStudentToSemesterViewModel
import edu.watumull.presencify.feature.users.dashboard.UsersDashboardViewModel
import edu.watumull.presencify.feature.users.mark_unmark_student_dropout.MarkUnmarkStudentAsDropoutViewModel
import edu.watumull.presencify.feature.users.modify_student_batch.ModifyStudentBatchViewModel
import edu.watumull.presencify.feature.users.modify_student_division.ModifyStudentDivisionViewModel
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
        semesterRepository = get(),
        divisionRepository = get(),
        batchRepository = get(),
        studentDropoutRepository = get(),
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
            studentAuthRepository = get(),
            savedStateHandle = get()
        )
    }
    viewModel {
        TeacherDetailsViewModel(
            teacherRepository = get(),
            teacherAuthRepository = get(),
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
    viewModel {
        AssignUnassignStudentToSemesterViewModel(
            branchRepository = get(),
            semesterRepository = get()
        )
    }
    viewModel {
        AssignUnassignStudentToDivisionViewModel(
            branchRepository = get(),
            divisionRepository = get()
        )
    }
    viewModel {
        ModifyStudentDivisionViewModel(
            branchRepository = get(),
            divisionRepository = get()
        )
    }
    viewModel {
        ModifyStudentBatchViewModel(
            branchRepository = get(),
            divisionRepository = get(),
            batchRepository = get()
        )
    }
    viewModel {
        AssignUnassignStudentToBatchViewModel(
            branchRepository = get(),
            divisionRepository = get(),
            batchRepository = get()
        )
    }
    viewModel {
        MarkUnmarkStudentAsDropoutViewModel()
    }
}
