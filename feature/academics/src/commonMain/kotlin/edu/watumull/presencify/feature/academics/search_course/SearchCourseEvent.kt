package edu.watumull.presencify.feature.academics.search_course

sealed interface SearchCourseEvent {
    data object NavigateBack : SearchCourseEvent
    data class NavigateToCourseDetails(val courseId: String) : SearchCourseEvent
    data object NavigateBackWithSelection : SearchCourseEvent
    data object NavigateToAddEditCourse : SearchCourseEvent
}

