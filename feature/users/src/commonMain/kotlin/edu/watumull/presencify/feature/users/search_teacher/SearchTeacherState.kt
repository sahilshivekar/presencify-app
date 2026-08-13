package edu.watumull.presencify.feature.users.search_teacher

import androidx.compose.runtime.Stable
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.feature.users.navigation.SearchTeacherIntention
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class SearchTeacherState(
    val viewState: ViewState = ViewState.Content,

    val searchQuery: String = "",
    val isRefreshing: Boolean = false,

    val teachers: PersistentList<Teacher> = persistentListOf(),
    val isLoadingTeachers: Boolean = true,

    val intention: SearchTeacherIntention = SearchTeacherIntention.DEFAULT,
    val selectedTeacherIds: Set<String> = emptySet(),
    val isSelectable: Boolean = false,

    val currentPage: Int = 1,
    val isLoadingMore: Boolean = false,

    val courseId: String? = null,
    val getAll: Boolean? = null,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: UiText) : ViewState
        data object Content : ViewState
    }
}

