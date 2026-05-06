package edu.watumull.presencify.feature.schedule.add_edit_class

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import edu.watumull.presencify.core.designsystem.components.dialog.DialogType
import edu.watumull.presencify.core.domain.onError
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.academics.BatchRepository
import edu.watumull.presencify.core.domain.repository.academics.CourseRepository
import edu.watumull.presencify.core.domain.repository.academics.SemesterRepository
import edu.watumull.presencify.core.domain.repository.schedule.ClassSessionRepository
import edu.watumull.presencify.core.domain.repository.schedule.RoomRepository
import edu.watumull.presencify.core.domain.repository.schedule.TimetableRepository
import edu.watumull.presencify.core.domain.repository.teacher.TeacherRepository
import edu.watumull.presencify.core.presentation.UiText
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarController
import edu.watumull.presencify.core.presentation.global_snackbar.SnackbarEvent
import edu.watumull.presencify.core.presentation.toUiText
import edu.watumull.presencify.core.presentation.utils.BaseViewModel
import edu.watumull.presencify.core.presentation.validation.validateAsClassActiveFrom
import edu.watumull.presencify.core.presentation.validation.validateAsClassActiveTill
import edu.watumull.presencify.core.presentation.validation.validateAsClassType
import edu.watumull.presencify.core.presentation.validation.validateAsCourse
import edu.watumull.presencify.core.presentation.validation.validateAsDayOfWeek
import edu.watumull.presencify.core.presentation.validation.validateAsEndTime
import edu.watumull.presencify.core.presentation.validation.validateAsRoom
import edu.watumull.presencify.core.presentation.validation.validateAsStartTime
import edu.watumull.presencify.core.presentation.validation.validateAsTeacher
import edu.watumull.presencify.feature.schedule.add_edit_class.AddEditClassEvent.NavigateBack
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class AddEditClassViewModel(
    private val classSessionRepository: ClassSessionRepository,
    private val timetableRepository: TimetableRepository,
    private val courseRepository: CourseRepository,
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

        // First get the timetable to find the division and semester
        timetableRepository.getTimetableById(state.timetableId)
            .onSuccess { timetable ->
                timetable.division?.semester?.id?.let { semesterId ->
                    // Load courses for this semester
                    semesterRepository.getCoursesOfSemester(semesterId)
                        .onSuccess { courses ->
                            updateState {
                                val newState = it.copy(
                                    availableCourses = courses.toImmutableList(),
                                    isLoadingCourses = false
                                )
                                // If in edit mode and a course is already selected, ensure we use the instance from availableCourses
                                if (it.isEditMode && it.selectedCourse != null) {
                                    val matchedCourse = courses.find { course -> course.id == it.selectedCourse?.id }
                                    if (matchedCourse != null) {
                                        newState.copy(selectedCourse = matchedCourse)
                                    } else {
                                        newState
                                    }
                                } else {
                                    newState
                                }
                            }
                        }
                        .onError {
                            updateState { it.copy(isLoadingCourses = false) }
                        }
                } ?: run {
                    updateState {
                        it.copy(
                            isLoadingCourses = false,
                            dialogState = AddEditClassState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = UiText.DynamicString("Semester not found for this timetable"),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
            }
            .onError { error ->
                updateState {
                    it.copy(
                        isLoadingCourses = false,
                        dialogState = AddEditClassState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Error",
                            message = error.toUiText(),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
            }
    }

    private suspend fun loadTeachers() {
        updateState { it.copy(isLoadingTeachers = true) }
        teacherRepository.getTeachers(getAll = true)
            .onSuccess { teachersWithCount ->
                updateState {
                    val newState = it.copy(
                        availableTeachers = teachersWithCount.teachers.toImmutableList(),
                        isLoadingTeachers = false
                    )
                    // If in edit mode and a teacher is already selected, ensure we use the instance from availableTeachers
                    if (it.isEditMode && it.selectedTeacher != null) {
                        val matchedTeacher = teachersWithCount.teachers.find { teacher -> teacher.id == it.selectedTeacher?.id }
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

    private suspend fun loadRooms() {
        updateState { it.copy(isLoadingRooms = true) }
        roomRepository.getRooms(getAll = true)
            .onSuccess { roomsWithCount ->
                updateState {
                    val newState = it.copy(
                        availableRooms = roomsWithCount.rooms.toImmutableList(),
                        isLoadingRooms = false
                    )
                    // If in edit mode and a room is already selected, ensure we use the instance from availableRooms
                    if (it.isEditMode && it.selectedRoom != null) {
                        val matchedRoom = roomsWithCount.rooms.find { room -> room.id == it.selectedRoom?.id }
                        if (matchedRoom != null) {
                            newState.copy(selectedRoom = matchedRoom)
                        } else {
                            newState
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

        // First get the timetable to find the division
        timetableRepository.getTimetableById(state.timetableId)
            .onSuccess { timetable ->
                timetable.division?.id?.let { divisionId ->
                    // Load batches for this division
                    batchRepository.getBatches(divisionId = divisionId, getAll = true)
                        .onSuccess { batchesWithCount ->
                            updateState {
                                val newState = it.copy(
                                    availableBatches = batchesWithCount.batches.toImmutableList(),
                                    isLoadingBatches = false
                                )
                                // If in edit mode and a batch is already selected, ensure we use the instance from availableBatches
                                if (it.isEditMode && it.selectedBatch != null) {
                                    val matchedBatch = batchesWithCount.batches.find { batch -> batch.id == it.selectedBatch?.id }
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
                            classType = classSession.classType,
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

    private fun validateFields(): Boolean {
        val state = stateFlow.value

        // In edit mode, only validate active dates
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

                updateState {
                    it.copy(
                        dialogState = AddEditClassState.DialogState(
                            dialogType = DialogType.ERROR,
                            title = "Validation Error",
                            message = UiText.DynamicString(errorMessage),
                            dialogIntention = DialogIntention.GENERIC
                        )
                    )
                }
                return false
            }

            return true
        }

        // In add mode, validate all fields
        val courseValidation = state.selectedCourse.validateAsCourse()
        val teacherValidation = state.selectedTeacher.validateAsTeacher()
        val roomValidation = state.selectedRoom.validateAsRoom()
        val dayOfWeekValidation = state.dayOfWeek.validateAsDayOfWeek()
        val activeFromValidation = state.activeFrom.validateAsClassActiveFrom(state.activeTill)
        val activeTillValidation = state.activeTill.validateAsClassActiveTill(state.activeFrom)
        val classTypeValidation = state.classType.validateAsClassType()
        val startTimeValidation = state.startTime.validateAsStartTime(state.endTime)
        val endTimeValidation = state.endTime.validateAsEndTime(state.startTime)

        updateState {
            it.copy(
                selectedCourseError = courseValidation.errorMessage,
                selectedTeacherError = teacherValidation.errorMessage,
                selectedRoomError = roomValidation.errorMessage,
                classTypeError = classTypeValidation.errorMessage,
                dayOfWeekError = dayOfWeekValidation.errorMessage,
                startTimeError = startTimeValidation.errorMessage,
                endTimeError = endTimeValidation.errorMessage,
                activeFromError = activeFromValidation.errorMessage,
                activeTillError = activeTillValidation.errorMessage
            )
        }

        val hasError = !courseValidation.successful ||
                !teacherValidation.successful ||
                !roomValidation.successful ||
                !dayOfWeekValidation.successful ||
                !activeFromValidation.successful ||
                !activeTillValidation.successful ||
                !classTypeValidation.successful ||
                !startTimeValidation.successful ||
                !endTimeValidation.successful

        if (hasError) {
            val errorMessage = listOfNotNull(
                courseValidation.errorMessage,
                teacherValidation.errorMessage,
                roomValidation.errorMessage,
                classTypeValidation.errorMessage,
                dayOfWeekValidation.errorMessage,
                startTimeValidation.errorMessage,
                endTimeValidation.errorMessage,
                activeFromValidation.errorMessage,
                activeTillValidation.errorMessage
            ).firstOrNull() ?: "Please fix the errors"

            updateState {
                it.copy(
                    dialogState = AddEditClassState.DialogState(
                        dialogType = DialogType.ERROR,
                        title = "Validation Error",
                        message = UiText.DynamicString(errorMessage),
                        dialogIntention = DialogIntention.GENERIC
                    )
                )
            }
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
                // In edit mode, only update active dates
                classSessionRepository.editActiveDatesOfClass(
                    classId = state.classId!!,
                    newActiveFrom = state.activeFrom!!,
                    newActiveTill = state.activeTill!!
                )
            } else if (state.isExtraClass) {
                // Add extra class
                classSessionRepository.addExtraClass(
                    teacherId = state.selectedTeacher!!.id,
                    startTime = state.startTime!!,
                    endTime = state.endTime!!,
                    dayOfWeek = state.dayOfWeek!!,
                    roomId = state.selectedRoom!!.id,
                    batchId = state.selectedBatch?.id,
                    activeFrom = state.activeFrom!!,
                    activeTill = state.activeTill!!,
                    classType = state.classType!!,
                    courseId = state.selectedCourse!!.id,
                    timetableId = state.timetableId
                )
            } else {
                // Add regular class
                classSessionRepository.addClass(
                    teacherId = state.selectedTeacher!!.id,
                    startTime = state.startTime!!,
                    endTime = state.endTime!!,
                    dayOfWeek = state.dayOfWeek!!,
                    roomId = state.selectedRoom!!.id,
                    batchId = state.selectedBatch?.id,
                    activeFrom = state.activeFrom!!,
                    activeTill = state.activeTill!!,
                    classType = state.classType!!,
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
                            dialogState = AddEditClassState.DialogState(
                                dialogType = DialogType.ERROR,
                                title = "Error",
                                message = error.toUiText(),
                                dialogIntention = DialogIntention.GENERIC
                            )
                        )
                    }
                }
        }
    }

    override fun handleAction(action: AddEditClassAction) {
        when (action) {
            is AddEditClassAction.BackButtonClick -> sendEvent(NavigateBack)
            is AddEditClassAction.DismissDialog -> updateState { it.copy(dialogState = null) }

            is AddEditClassAction.UpdateCourse -> {
                val course = state.availableCourses.find { it.id == action.courseId }
                updateState { it.copy(selectedCourse = course, selectedCourseError = null) }
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
                updateState { it.copy(selectedBatch = batch) }
            }
            is AddEditClassAction.ChangeBatchDropDownVisibility -> {
                updateState { it.copy(isBatchDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateClassType -> {
                updateState { it.copy(classType = action.classType, classTypeError = null) }
            }
            is AddEditClassAction.ChangeClassTypeDropDownVisibility -> {
                updateState { it.copy(isClassTypeDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateDayOfWeek -> {
                updateState { it.copy(dayOfWeek = action.dayOfWeek, dayOfWeekError = null) }
            }
            is AddEditClassAction.ChangeDayOfWeekDropDownVisibility -> {
                updateState { it.copy(isDayOfWeekDropdownOpen = action.isVisible) }
            }

            is AddEditClassAction.UpdateStartTime -> {
                updateState { it.copy(startTime = action.startTime, startTimeError = null) }
            }

            is AddEditClassAction.UpdateEndTime -> {
                updateState { it.copy(endTime = action.endTime, endTimeError = null) }
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
}
