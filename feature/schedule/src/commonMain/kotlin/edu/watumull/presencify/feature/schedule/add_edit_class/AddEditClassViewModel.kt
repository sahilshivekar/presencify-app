package edu.watumull.presencify.feature.schedule.add_edit_class

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.enums.CourseType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.DivisionRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.domain.repository.schedule.TimetableRepository
import edu.watumull.presencify.core.domain.repository.teacher.TeacherRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.components.dialog.DialogState
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsClassActiveFrom
import edu.watumull.presencify.core.presentation.validation.validateAsClassActiveTill
import edu.watumull.presencify.core.presentation.validation.validateAsCourse
import edu.watumull.presencify.core.presentation.validation.validateAsDayOfWeek
import edu.watumull.presencify.core.presentation.validation.validateAsEndTime
import edu.watumull.presencify.core.presentation.validation.validateAsRoom
import edu.watumull.presencify.core.presentation.validation.validateAsStartTime
import edu.watumull.presencify.core.presentation.validation.validateAsTeacher
import edu.watumull.presencify.core.presentation.validation.validateAsBatch
import edu.watumull.presencify.core.presentation.validation.ValidationResult
import edu.watumull.presencify.feature.schedule.add_edit_class.AddEditClassEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

class AddEditClassViewModel(
    private val classSessionRepository: ClassSessionRepository,
    private val timetableRepository: TimetableRepository,
    private val courseRepository: CourseRepository,
    private val divisionRepository: DivisionRepository,
    private val semesterRepository: SemesterRepository,
    private val teacherRepository: TeacherRepository,
    private val roomRepository: RoomRepository,
    private val batchRepository: BatchRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditClassState, AddEditClassEvent, AddEditClassAction>(
    initialState = run {
        val routeParams = savedStateHandle.toRoute<ScheduleRoutes.AddEditClass>()
        AddEditClassState(
            isEditMode = routeParams.classId != null,
            classId = routeParams.classId,
            timetableId = routeParams.timetableId
        )
    }
) {

    init {
        viewModelScope.launch {
            loadDropdownData()
        }
        if (stateFlow.value.isEditMode && stateFlow.value.classId != null) {
            loadClassDetails(stateFlow.value.classId!!)
        }
    }

    private suspend fun loadDropdownData() {
        loadCourses()
        loadTeachers()
        loadRooms()
        loadBatches()
    }

    private suspend fun loadCourses() {
        updateState { it.copy(isLoadingCourses = true) }

        timetableRepository.getTimetableById(state.timetableId)
            .onSuccess { timetable ->
                val divisionId = timetable.division?.id
                val semesterId = timetable.division?.semester?.id

                if (divisionId != null && semesterId != null) {
                    viewModelScope.launch {
                        val coursesResult = divisionRepository.getCoursesOfDivision(divisionId)
                        val semesterResult = semesterRepository.getSemesterById(semesterId)

                        coursesResult.onSuccess { courses ->
                            semesterResult.onSuccess { semester ->
                                updateState {
                                    val combinedCourses = courses.compulsoryCourses + courses.optionalCourses.mapNotNull { divisionCourse ->
                                        divisionCourse.course }.distinctBy { course -> course.id }
                                    val newState = it.copy(
                                        availableCourses = combinedCourses.toImmutableList(),
                                        isLoadingCourses = false,
                                        activeFrom = it.activeFrom ?: semester.startDate,
                                        activeTill = it.activeTill ?: semester.endDate
                                    )
                                    if (it.isEditMode && it.selectedCourse != null) {
                                        val matchedCourse = combinedCourses.find { course -> course.id == it.selectedCourse?.id }
                                        if (matchedCourse != null) {
                                            newState.copy(selectedCourse = matchedCourse)
                                        } else {
                                            newState
                                        }
                                    } else {
                                        newState
                                    }
                                }
                            }.onError {
                                updateState { it.copy(isLoadingCourses = false) }
                            }
                        }.onError {
                            updateState { it.copy(isLoadingCourses = false) }
                        }
                    }
                } else {
                    val missingEntity = if (divisionId == null) "Division" else "Semester"
                    updateState {
                        it.copy(
                            isLoadingCourses = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = UiText.DynamicString("$missingEntity not found for this timetable"),
                            )
                        )
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoadingCourses = false,
                        dialogState = DialogState(
                            dialogType = DialogType.ERROR,
                            title = UiText.DynamicString("Error"),
                            message = error.toUiText(),
                        )
                    )
                }
            }
    }

    private suspend fun loadTeachers(courseId: String? = null) {
        updateState { it.copy(isLoadingTeachers = true) }
        teacherRepository.getTeachers(courseId = courseId, getAll = true, isActive = true)
            .onSuccess { teachersWithCount ->
                updateState {
                    val newState = it.copy(
                        availableTeachers = teachersWithCount.teachers.toImmutableList(),
                        isLoadingTeachers = false
                    )
                    if (it.isEditMode && it.selectedTeacher != null) {
                        val matchedTeacher =
                            teachersWithCount.teachers.find { teacher -> teacher.id == it.selectedTeacher?.id }
                        if (matchedTeacher != null) {
                            newState.copy(selectedTeacher = matchedTeacher)
                        } else {
                            newState
                        }
                    } else {
                        newState
                    }
                }
            }
            .onError {
                updateState { it.copy(isLoadingTeachers = false) }
            }
    }

    private suspend fun loadRooms(
        dayOfWeek: DayOfWeek? = null,
        startTime: LocalTime? = null,
        endTime: LocalTime? = null
    ) {
        updateState { it.copy(isLoadingRooms = true) }
        roomRepository.getRooms(
            getAll = true,
            dayOfWeek = dayOfWeek,
            freeBetweenStartTime = startTime,
            freeBetweenEndTime = endTime
        )
            .onSuccess { roomsWithCount ->
                updateState {
                    val newState = it.copy(
                        availableRooms = roomsWithCount.rooms.toImmutableList(),
                        isLoadingRooms = false
                    )
                    if (it.selectedRoom != null) {
                        val matchedRoom = roomsWithCount.rooms.find { room -> room.id == it.selectedRoom?.id }
                        when {
                            matchedRoom != null -> newState.copy(selectedRoom = matchedRoom)
                            it.isEditMode       -> newState
                            else                -> newState.copy(selectedRoom = null, selectedRoomError = null)
                        }
                    } else {
                        newState
                    }
                }
            }
            .onError {
                updateState { it.copy(isLoadingRooms = false) }
            }
    }

    private suspend fun loadBatches() {
        updateState { it.copy(isLoadingBatches = true) }

        timetableRepository.getTimetableById(state.timetableId)
            .onSuccess { timetable ->
                timetable.division?.id?.let { divisionId ->
                    batchRepository.getBatches(divisionId = divisionId, getAll = true)
                        .onSuccess { batchesWithCount ->
                            updateState {
                                val newState = it.copy(
                                    availableBatches = batchesWithCount.batches.toImmutableList(),
                                    isLoadingBatches = false
                                )
                                if (it.isEditMode && it.selectedBatch != null) {
                                    val matchedBatch =
                                        batchesWithCount.batches.find { batch -> batch.id == it.selectedBatch?.id }
                                    if (matchedBatch != null) {
                                        newState.copy(selectedBatch = matchedBatch)
                                    } else {
                                        newState
                                    }
                                } else {
                                    newState
                                }
                            }
                        }
                        .onError {
                            updateState { it.copy(isLoadingBatches = false) }
                        }
                } ?: run {
                    updateState { it.copy(isLoadingBatches = false) }
                }
            }
            .onError {
                updateState { it.copy(isLoadingBatches = false) }
            }
    }

    private fun loadClassDetails(classId: String) {
        viewModelScope.launch {
            updateState { it.copy(viewState = AddEditClassState.ViewState.Loading) }

            classSessionRepository.getClassById(classId)
                .onSuccess { classSession ->
                    updateState {
                        it.copy(
                            viewState = AddEditClassState.ViewState.Content,
                            selectedCourse = classSession.course,
                            selectedTeacher = classSession.teacher,
                            selectedRoom = classSession.room,
                            selectedBatch = classSession.batch,
                            dayOfWeek = classSession.dayOfWeek,
                            startTime = classSession.startTime,
                            endTime = classSession.endTime,
                            activeFrom = classSession.activeFrom,
                            activeTill = classSession.activeTill,
                            isExtraClass = classSession.isExtraClass,
                        )
                    }
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            viewState = AddEditClassState.ViewState.Error(error.toUiText())
                        )
                    }
                }
        }
    }

    private fun reloadRoomsIfNeeded() {
        if (state.isEditMode) return
        val s = stateFlow.value
        viewModelScope.launch {
            if (s.dayOfWeek != null && s.startTime != null && s.endTime != null) {
                loadRooms(dayOfWeek = s.dayOfWeek, startTime = s.startTime, endTime = s.endTime)
            } else {
                loadRooms()
            }
        }
    }

    private fun calculateEndTime(startTime: LocalTime, courseType: CourseType): LocalTime {
        val hoursToAdd = when (courseType) {
            CourseType.LECTURE -> 1
            CourseType.PRACTICAL -> 2
        }
        val totalMinutes = startTime.hour * 60 + startTime.minute + (hoursToAdd * 60)
        val endHour = (totalMinutes / 60) % 24
        val endMinute = totalMinutes % 60
        return LocalTime(endHour, endMinute)
    }

    private fun validateFields(): Boolean {
        val state = stateFlow.value

        if (state.isEditMode) {
            val activeFromValidation = state.activeFrom.validateAsClassActiveFrom(state.activeTill)
            val activeTillValidation = state.activeTill.validateAsClassActiveTill(state.activeFrom)

            updateState {
                it.copy(
                    activeFromError = activeFromValidation.errorMessage,
                    activeTillError = activeTillValidation.errorMessage
                )
            }

            val hasError = !activeFromValidation.successful || !activeTillValidation.successful

            if (hasError) {
                val errorMessage = listOfNotNull(
                    activeFromValidation.errorMessage,
                    activeTillValidation.errorMessage
                ).firstOrNull() ?: "Please fix the errors"
                return false
            }

            return true
        }

        val courseValidation = state.selectedCourse.validateAsCourse()
        val teacherValidation = state.selectedTeacher.validateAsTeacher()
        val roomValidation = state.selectedRoom.validateAsRoom()
        val dayOfWeekValidation = state.dayOfWeek.validateAsDayOfWeek()
        val activeFromValidation = state.activeFrom.validateAsClassActiveFrom(state.activeTill)
        val activeTillValidation = state.activeTill.validateAsClassActiveTill(state.activeFrom)
        val startTimeValidation = state.startTime.validateAsStartTime(state.endTime)
        val endTimeValidation = state.endTime.validateAsEndTime(state.startTime)

        val batchValidation = if (state.selectedCourse?.courseType == CourseType.PRACTICAL) {
            state.selectedBatch.validateAsBatch()
        } else {
            ValidationResult(successful = true)
        }

        updateState {
            it.copy(
                selectedCourseError = courseValidation.errorMessage,
                selectedTeacherError = teacherValidation.errorMessage,
                selectedRoomError = roomValidation.errorMessage,
                dayOfWeekError = dayOfWeekValidation.errorMessage,
                startTimeError = startTimeValidation.errorMessage,
                endTimeError = endTimeValidation.errorMessage,
                activeFromError = activeFromValidation.errorMessage,
                activeTillError = activeTillValidation.errorMessage,
                selectedBatchError = batchValidation.errorMessage
            )
        }

        val hasError = !courseValidation.successful ||
                !teacherValidation.successful ||
                !roomValidation.successful ||
                !dayOfWeekValidation.successful ||
                !activeFromValidation.successful ||
                !activeTillValidation.successful ||
                !startTimeValidation.successful ||
                !endTimeValidation.successful ||
                !batchValidation.successful

        if (hasError) {
            val errorMessage = listOfNotNull(
                courseValidation.errorMessage,
                teacherValidation.errorMessage,
                roomValidation.errorMessage,
                dayOfWeekValidation.errorMessage,
                startTimeValidation.errorMessage,
                endTimeValidation.errorMessage,
                activeFromValidation.errorMessage,
                activeTillValidation.errorMessage,
                batchValidation.errorMessage
            ).firstOrNull() ?: "Please fix the errors"
            return false
        }

        return true
    }

    private fun submitClass() {
        if (!validateFields()) return

        val state = stateFlow.value

        viewModelScope.launch {
            updateState { it.copy(isSubmitting = true) }

            val result = if (state.isEditMode) {
                classSessionRepository.editActiveDatesOfClass(
                    classId = state.classId!!,
                    newActiveFrom = state.activeFrom!!,
                    newActiveTill = state.activeTill!!
                )
            } else if (state.isExtraClass) {
                classSessionRepository.addExtraClass(
                    teacherId = state.selectedTeacher!!.id,
                    startTime = state.startTime!!,
                    endTime = state.endTime!!,
                    dayOfWeek = state.dayOfWeek!!,
                    roomId = state.selectedRoom!!.id,
                    batchId = state.selectedBatch?.id,
                    activeFrom = state.activeFrom!!,
                    activeTill = state.activeTill!!,
                    courseId = state.selectedCourse!!.id,
                    timetableId = state.timetableId
                )
            } else {
                classSessionRepository.addClass(
                    teacherId = state.selectedTeacher!!.id,
                    startTime = state.startTime!!,
                    endTime = state.endTime!!,
                    dayOfWeek = state.dayOfWeek!!,
                    roomId = state.selectedRoom!!.id,
                    batchId = state.selectedBatch?.id,
                    activeFrom = state.activeFrom!!,
                    activeTill = state.activeTill!!,
                    courseId = state.selectedCourse!!.id,
                    timetableId = state.timetableId
                )
            }

            result
                .onSuccess {
                    updateState { it.copy(isSubmitting = false) }

                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = if (state.isEditMode) {
                                "Class dates updated successfully"
                            } else {
                                "Class ${if (state.isExtraClass) "(Extra) " else ""}added successfully"
                            }
                        )
                    )

                    sendEvent(NavigateBack)
                }
                .onError { error ->
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            dialogState = DialogState(
                                dialogType = DialogType.ERROR,
                                title = UiText.DynamicString("Error"),
                                message = error.toUiText(),
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: AddEditClassAction) {
        when (action) {
            is AddEditClassAction.NavigateBack -> handleBackNavigation()
            is AddEditClassAction.ConfirmNavigateBack -> confirmNavigateBack()
            is AddEditClassAction.DismissDialog -> updateState { it.copy(dialogState = null) }

            is AddEditClassAction.UpdateCourse -> {
                val course = state.availableCourses.find { it.id == action.courseId }
                val newEndTime = if (course != null && state.startTime != null) {
                    calculateEndTime(state.startTime!!, course.courseType)
                } else {
                    state.endTime
                }
                updateState {
                    it.copy(
                        selectedCourse = course,
                        selectedCourseError = null,
                        endTime = newEndTime,
                        selectedTeacher = if (!it.isEditMode) null else it.selectedTeacher,
                        selectedTeacherError = if (!it.isEditMode) null else it.selectedTeacherError
                    )
                }
                if (!state.isEditMode) {
                    viewModelScope.launch { loadTeachers(courseId = course?.id) }
                }
            }

            is AddEditClassAction.ChangeCourseDropDownVisibility -> {
                updateState { it.copy(isCourseDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateTeacher -> {
                val teacher = state.availableTeachers.find { it.id == action.teacherId }
                updateState { it.copy(selectedTeacher = teacher, selectedTeacherError = null) }
            }

            is AddEditClassAction.ChangeTeacherDropDownVisibility -> {
                updateState { it.copy(isTeacherDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateRoom -> {
                val room = state.availableRooms.find { it.id == action.roomId }
                updateState { it.copy(selectedRoom = room, selectedRoomError = null) }
            }

            is AddEditClassAction.ChangeRoomDropDownVisibility -> {
                updateState { it.copy(isRoomDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateBatch -> {
                val batch = state.availableBatches.find { it.id == action.batchId }
                updateState { it.copy(selectedBatch = batch, selectedBatchError = null) }
            }

            is AddEditClassAction.ChangeBatchDropDownVisibility -> {
                updateState { it.copy(isBatchDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateDayOfWeek -> {
                updateState { it.copy(dayOfWeek = action.dayOfWeek, dayOfWeekError = null) }
                reloadRoomsIfNeeded()
            }

            is AddEditClassAction.ChangeDayOfWeekDropDownVisibility -> {
                updateState { it.copy(isDayOfWeekDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateStartTime -> {
                val newEndTime = if (state.selectedCourse != null && action.startTime != null) {
                    calculateEndTime(action.startTime, state.selectedCourse!!.courseType)
                } else {
                    state.endTime
                }
                updateState {
                    it.copy(
                        startTime = action.startTime,
                        startTimeError = null,
                        endTime = newEndTime
                    )
                }
                reloadRoomsIfNeeded()
            }

            is AddEditClassAction.UpdateEndTime -> {
                updateState { it.copy(endTime = action.endTime, endTimeError = null) }
                reloadRoomsIfNeeded()
            }

            is AddEditClassAction.UpdateActiveFrom -> {
                updateState { it.copy(activeFrom = action.activeFrom, activeFromError = null) }
            }

            is AddEditClassAction.UpdateActiveTill -> {
                updateState { it.copy(activeTill = action.activeTill, activeTillError = null) }
            }

            is AddEditClassAction.UpdateIsExtraClass -> {
                updateState { it.copy(isExtraClass = action.isExtraClass) }
            }

            is AddEditClassAction.SubmitClick -> submitClass()
        }
    }

    private fun handleBackNavigation() {
        if (hasUnsavedChanges()) {
            updateState {
                it.copy(
                    dialogState = DialogState(
                        dialogType = DialogType.CONFIRM_NORMAL_ACTION,
                        title = UiText.DynamicString("Unsaved Changes"),
                        message = UiText.DynamicString("You have unsaved changes. Are you sure you want to leave?")
                    )
                )
            }
        } else {
            sendEvent(AddEditClassEvent.NavigateBack)
        }
    }

    private fun confirmNavigateBack() {
        updateState { it.copy(dialogState = null) }
        sendEvent(AddEditClassEvent.NavigateBack)
    }

    private fun hasUnsavedChanges(): Boolean {
        return state.selectedCourse == null ||
                state.selectedTeacher == null ||
                state.selectedRoom == null ||
                state.dayOfWeek == null ||
                state.startTime == null ||
                state.endTime == null ||
                state.activeFrom == null ||
                state.activeTill == null
    }
}
