package edu.watumull.presencify.feature.academics.search_course

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BranchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.SchemeRepository
import edu.watumull.presencify.core.domain.repository.teacher.TeacherRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.ListItemFeedback
import edu.watumull.presencify.core.presentation.pagination.Paginator
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import edu.watumull.presencify.feature.academics.navigation.SearchCourseIntention
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchCourseViewModel(
    private val courseRepository: CourseRepository,
    private val branchRepository: BranchRepository,
    private val schemeRepository: SchemeRepository,
    private val teacherRepository: TeacherRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SearchCourseState, SearchCourseEvent, SearchCourseAction>(
    initialState = run {
        val routeParams = savedStateHandle.toRoute<AcademicsRoutes.SearchCourse>()
        val intention = try {
            SearchCourseIntention.valueOf(routeParams.intention)
        } catch (_: IllegalArgumentException) {
            SearchCourseIntention.DEFAULT
        }

        when (intention) {
            SearchCourseIntention.LINK_UNLINK_COURSE_TO_SEMESTER_NUMBER_BRANCH -> {
                if (routeParams.branchId == null || routeParams.semesterNumber == null) {
                    throw IllegalArgumentException("Branch ID and Semester Number are required for linking/unlinking courses")
                }
            }
            SearchCourseIntention.ASSIGN_UNASSIGN_COURSE_TO_TEACHER -> {
                if (routeParams.teacherId == null) {
                    throw IllegalArgumentException("Teacher ID is required for assigning/unassigning courses")
                }
            }
            SearchCourseIntention.DEFAULT -> {
            }
        }

        SearchCourseState(
            intention = intention,
            branchId = routeParams.branchId,
            semesterNumber = routeParams.semesterNumber,
            teacherId = routeParams.teacherId
        )
    }
) {

    private val paginator = Paginator<Int, edu.watumull.presencify.core.domain.model.academics.CourseListWithTotalCount>(
        initialKey = 1,
        onLoadUpdated = { isLoading ->
            updateState { it.copy(isLoadingMore = isLoading) }
        },
        onRequest = { page ->
            val state = stateFlow.value

            courseRepository.getCourses(
                searchQuery = state.searchQuery.ifBlank { null },
                branchId = state.selectedBranch?.id,
                semesterNumber = state.selectedSemesterNumber,
                schemeId = state.selectedScheme?.id,
                teacherIds = state.selectedTeacherIds.takeIf { it.isNotEmpty() }?.toList(),
                page = page,
                limit = 20
            )
        },
        getNextKey = { currentPage, _ ->
            currentPage + 1
        },
        onError = { error ->
            updateState {
                it.copy(
                    viewState = SearchCourseState.ViewState.Error(error.toUiText()),
                    isLoadingCourses = false
                )
            }
        },
        onSuccess = { response, _ ->
            updateState {
                val newCourses = if (stateFlow.value.currentPage == 1) {
                    response.courses.toPersistentList()
                } else {
                    it.courses.addAll(response.courses).toPersistentList()
                }
                it.copy(
                    courses = newCourses,
                    currentPage = stateFlow.value.currentPage + 1,
                    isRefreshing = false,
                    isLoadingCourses = false
                )
            }
        },
        endReached = { currentPage, response ->
            val totalLoadedCourses = currentPage * 20
            totalLoadedCourses >= response.totalCount
        }
    )

    init {
        viewModelScope.launch {
            val task1 = async { loadBranches() }
            val task2 = async { loadSchemes() }
            val task3 = async { loadTeachers() }
            awaitAll(task1, task2, task3)
            setupDebouncedSearch()
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupDebouncedSearch() {
        viewModelScope.launch {
            stateFlow
                .map { it.searchQuery }
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { _ ->
                    refreshSearch()
                }
        }
    }

    private fun loadNextCourses() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun refreshSearch() {
        updateState { it.copy(courses = persistentListOf(), currentPage = 1) }
        paginator.reset()
        loadNextCourses()
    }

    private suspend fun loadBranches() {
        updateState { it.copy(areBranchesLoading = true) }
        branchRepository.getBranches(searchQuery = null)
            .onSuccess { branches ->
                updateState {
                    it.copy(
                        branchOptions = branches.toPersistentList(),
                        areBranchesLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areBranchesLoading = false,
                        viewState = SearchCourseState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadSchemes() {
        updateState { it.copy(areSchemesLoading = true) }
        schemeRepository.getSchemes(searchQuery = null)
            .onSuccess { schemes ->
                updateState {
                    it.copy(
                        schemeOptions = schemes.toPersistentList(),
                        areSchemesLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areSchemesLoading = false,
                        viewState = SearchCourseState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun loadTeachers() {
        updateState { it.copy(areTeachersLoading = true) }
        teacherRepository.getTeachers(searchQuery = null, getAll = true, isActive = true)
            .onSuccess { response ->
                updateState {
                    it.copy(
                        teacherOptions = response.teachers.toPersistentList(),
                        areTeachersLoading = false
                    )
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        areTeachersLoading = false,
                        viewState = SearchCourseState.ViewState.Error(error.toUiText())
                    )
                }
            }
    }

    private suspend fun handleCourseActionButton(courseId: String) {
        val state = stateFlow.value

        when (state.intention) {
            SearchCourseIntention.ASSIGN_UNASSIGN_COURSE_TO_TEACHER -> {
                handleAssignUnassignCourse(courseId)
            }
            SearchCourseIntention.LINK_UNLINK_COURSE_TO_SEMESTER_NUMBER_BRANCH -> {
                handleLinkUnlinkCourse(courseId)
            }
            SearchCourseIntention.DEFAULT -> {
            }
        }
    }

    private suspend fun handleLinkUnlinkCourse(courseId: String) {
        val state = stateFlow.value
        val branchId = state.branchId ?: return
        val semesterNumber = state.semesterNumber?.let {
            edu.watumull.presencify.core.domain.enums.SemesterNumber.fromValue(it)
        } ?: return

        if (state.loadingCourseIds.contains(courseId)) return

        val course = state.courses.find { it.id == courseId } ?: return

        val branchCourseSemester = course.branchCourseSemesters?.find {
            it.branchId == branchId && it.semesterNumber == semesterNumber
        }
        val isLinked = branchCourseSemester != null

        updateState { it.copy(
            loadingCourseIds = it.loadingCourseIds + courseId,
            courseFeedback = it.courseFeedback - courseId
        ) }

        if (isLinked) {
            val branchCourseSemesterId = branchCourseSemester.id

            courseRepository.removeCourseFromBranchWithSemesterNumber(
                branchCourseSemesterId = branchCourseSemesterId
            )
                .onSuccess {
                    val updatedCourses = state.courses.map { c ->
                        if (c.id == courseId) {
                            c.copy(
                                branchCourseSemesters = c.branchCourseSemesters?.filter {
                                    !(it.branchId == branchId && it.semesterNumber == semesterNumber)
                                }
                            )
                        } else {
                            c
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            courses = updatedCourses,
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Success(UiText.DynamicString("Course unlinked successfully")))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            courseRepository.addCourseToBranchWithSemesterNumber(
                courseId = courseId,
                branchId = branchId,
                semesterNumber = semesterNumber
            )
                .onSuccess {
                    viewModelScope.launch {
                        courseRepository.getCourseById(courseId)
                            .onSuccess { updatedCourse ->
                                val updatedCourses = state.courses.map { c ->
                                    if (c.id == courseId) {
                                        updatedCourse
                                    } else {
                                        c
                                    }
                                }.toPersistentList()

                                updateState {
                                    it.copy(
                                        courses = updatedCourses,
                                        loadingCourseIds = it.loadingCourseIds - courseId,
                                        courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Success(UiText.DynamicString("Course linked successfully")))
                                    )
                                }
                            }
                            .onError { error ->
                                updateState {
                                    it.copy(
                                        loadingCourseIds = it.loadingCourseIds - courseId,
                                        courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Error(error.toUiText()))
                                    )
                                }
                            }
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    private suspend fun handleAssignUnassignCourse(courseId: String) {
        val state = stateFlow.value
        val teacherId = state.teacherId ?: return

        if (state.loadingCourseIds.contains(courseId)) return

        val course = state.courses.find { it.id == courseId } ?: return

        val teacherTeachesCourse = course.teacherTeachesCourses?.find { it.teacherId == teacherId }
        val isAssigned = teacherTeachesCourse != null

        updateState { it.copy(
            loadingCourseIds = it.loadingCourseIds + courseId,
            courseFeedback = it.courseFeedback - courseId
        ) }

        if (isAssigned) {
            val teacherTeachesCourseId = teacherTeachesCourse.id

            teacherRepository.removeTeachingCourse(teacherTeachesCourseId)
                .onSuccess {
                    val updatedCourses = state.courses.map { c ->
                        if (c.id == courseId) {
                            c.copy(
                                teacherTeachesCourses = c.teacherTeachesCourses?.filter { it.teacherId != teacherId }
                            )
                        } else {
                            c
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            courses = updatedCourses,
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Success(UiText.DynamicString("Course unassigned successfully")))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        } else {
            teacherRepository.addTeachingCourse(teacherId, courseId)
                .onSuccess { teacherTeachesCourse ->
                    val updatedCourses = state.courses.map { c ->
                        if (c.id == courseId) {
                            val existingList = c.teacherTeachesCourses ?: emptyList()
                            c.copy(
                                teacherTeachesCourses = existingList + teacherTeachesCourse
                            )
                        } else {
                            c
                        }
                    }.toPersistentList()

                    updateState {
                        it.copy(
                            courses = updatedCourses,
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Success(UiText.DynamicString("Course assigned successfully")))
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            loadingCourseIds = it.loadingCourseIds - courseId,
                            courseFeedback = it.courseFeedback + (courseId to ListItemFeedback.Error(error.toUiText()))
                        )
                    }
                }
        }
    }

    override fun handleAction(action: SearchCourseAction) {
        when (action) {

            is SearchCourseAction.NavigateBack -> {
                sendEvent(SearchCourseEvent.NavigateBack)
            }


            is SearchCourseAction.UpdateSearchQuery -> {
                updateState { it.copy(searchQuery = action.query) }
            }

            is SearchCourseAction.Search -> {
                refreshSearch()
            }

            is SearchCourseAction.Refresh -> {
                updateState { it.copy(isRefreshing = true) }
                refreshSearch()
            }

            is SearchCourseAction.SelectSemesterNumber -> {
                updateState { it.copy(selectedSemesterNumber = action.semesterNumber) }
            }

            is SearchCourseAction.SelectBranch -> {
                updateState { it.copy(selectedBranch = action.branch) }
            }

            is SearchCourseAction.SelectScheme -> {
                updateState { it.copy(selectedScheme = action.scheme) }
            }

            is SearchCourseAction.ToggleTeacherSelection -> {
                val currentSelection = stateFlow.value.selectedTeacherIds
                val newSelection = if (currentSelection.contains(action.teacherId)) {
                    currentSelection - action.teacherId
                } else {
                    currentSelection + action.teacherId
                }
                updateState { it.copy(selectedTeacherIds = newSelection) }
            }

            is SearchCourseAction.ResetFilters -> {
                updateState {
                    it.copy(
                        selectedSemesterNumber = null,
                        selectedBranch = null,
                        selectedScheme = null,
                        selectedTeacherIds = emptySet()
                    )
                }
            }

            is SearchCourseAction.ApplyFilters -> {
                refreshSearch()
            }

            is SearchCourseAction.CourseCardClick -> {
                if (stateFlow.value.intention == SearchCourseIntention.DEFAULT) {
                    sendEvent(SearchCourseEvent.NavigateToCourseDetails(action.courseId))
                }
            }

            is SearchCourseAction.CourseActionButtonClick -> {
                viewModelScope.launch {
                    handleCourseActionButton(action.courseId)
                }
            }

            is SearchCourseAction.LoadMoreCourses -> {
                loadNextCourses()
            }

            SearchCourseAction.ClickFloatingActionButton -> {
                sendEvent(SearchCourseEvent.NavigateToAddEditCourse)
            }
        }
    }
}
