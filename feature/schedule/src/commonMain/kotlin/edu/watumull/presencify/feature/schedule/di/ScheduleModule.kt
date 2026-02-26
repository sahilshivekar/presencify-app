package edu.watumull.presencify.feature.schedule.di

import edu.watumull.presencify.feature.schedule.add_edit_room.AddEditRoomViewModel
import edu.watumull.presencify.feature.schedule.dashboard.ScheduleDashboardViewModel
import edu.watumull.presencify.feature.schedule.room_details.RoomDetailsViewModel
import edu.watumull.presencify.feature.schedule.search_class.SearchClassViewModel
import edu.watumull.presencify.feature.schedule.search_room.SearchRoomViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val scheduleModule = module {
    viewModel { ScheduleDashboardViewModel() }
    viewModel { SearchRoomViewModel(roomRepository = get()) }
    viewModel { AddEditRoomViewModel(roomRepository = get(), savedStateHandle = get()) }
    viewModel { RoomDetailsViewModel(roomRepository = get(), savedStateHandle = get()) }
    viewModel { SearchClassViewModel(classSessionRepository = get(), roomRepository = get(), teacherRepository = get(), branchRepository = get(), divisionRepository = get(), batchRepository = get()) }
}

