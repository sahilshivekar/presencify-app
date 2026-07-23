package edu.watumull.presencify.feature.schedule.di

import edu.watumull.presencify.feature.schedule.add_edit_class.AddEditClassViewModel
import edu.watumull.presencify.feature.schedule.add_edit_room.AddEditRoomViewModel
import edu.watumull.presencify.feature.schedule.add_edit_timetable.AddEditTimetableViewModel
import edu.watumull.presencify.feature.schedule.class_details.ClassDetailsViewModel
import edu.watumull.presencify.feature.schedule.dashboard.ScheduleDashboardViewModel
import edu.watumull.presencify.feature.schedule.room_details.RoomDetailsViewModel
import edu.watumull.presencify.feature.schedule.search_class.SearchClassViewModel
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomViewModel
import edu.watumull.presencify.feature.schedule.search_timetable.SearchTimetableViewModel
import edu.watumull.presencify.feature.schedule.timetable_details.TimetableDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scheduleModule = module {
    viewModel { ScheduleDashboardViewModel(classSessionRepository = get()) }
    viewModel { SearchRoomViewModel(roomRepository = get()) }
    viewModel { AddEditRoomViewModel(roomRepository = get(), savedStateHandle = get()) }
    viewModel { RoomDetailsViewModel(roomRepository = get(), savedStateHandle = get()) }
    viewModel { SearchClassViewModel(classSessionRepository = get(), roomRepository = get(), teacherRepository = get(), branchRepository = get(), divisionRepository = get(), batchRepository = get(), savedStateHandle = get()) }
    viewModel { AddEditClassViewModel(classSessionRepository = get(), timetableRepository = get(), courseRepository = get(), semesterRepository = get(), teacherRepository = get(), roomRepository = get(), batchRepository = get(), divisionRepository = get(), savedStateHandle = get()) }
    viewModel { ClassDetailsViewModel(classSessionRepository = get(), savedStateHandle = get()) }
    viewModel { SearchTimetableViewModel(timetableRepository = get(), branchRepository = get()) }
    viewModel { AddEditTimetableViewModel(branchRepository = get(), divisionRepository = get(), batchRepository = get(), timetableRepository = get(), savedStateHandle = get()) }
    viewModel { TimetableDetailsViewModel(timetableRepository = get(), classSessionRepository = get(), savedStateHandle = get()) }
}

