package edu.watumull.presencify.feature.academics.course_details

import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState

data class CourseDetailsState(
    val viewState: ViewState = ViewState.Loading,
    val dialogState: DialogState? = null,
    val courseId: String = "",
    val course: Course? = null,
    val isRemovingCourse: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

