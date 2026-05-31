package edu.watumull.presencify.feature.academics.semester_details

import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class SemesterDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val semesterId: String = "",
    val semester: Semester? = null,
    val isRemovingSemester: Boolean = false,
    val courses: List<Course> = emptyList(),
    val isLoadingCourses: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

