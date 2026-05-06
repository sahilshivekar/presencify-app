package edu.watumull.presencify.feature.schedule.add_edit_class

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDatePickerTextField
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTimePickerTextField
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.ClassType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.schedule.Room
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import edu.watumull.presencify.core.presentation.UiConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditClassScreen(
    state: AddEditClassState,
    onAction: (AddEditClassAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditClassAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Class" else "Add Class",
    ) { paddingValues ->
        when (state.viewState) {
            is AddEditClassState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AddEditClassState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is AddEditClassState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(DesignToken.spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg)
                    ) {
                        // Course Dropdown
                        PresencifyDropDownMenuBox<Course>(
                            value = state.selectedCourse?.name ?: "",
                            options = state.availableCourses,
                            onSelectItem = { onAction(AddEditClassAction.UpdateCourse(it?.id)) },
                            label = "Course *",
                            itemToString = { it.name },
                            expanded = state.isCourseDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditClassAction.ChangeCourseDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.selectedCourseError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Teacher Dropdown
                        PresencifyDropDownMenuBox<Teacher>(
                            value = state.selectedTeacher?.let { "${it.firstName} ${it.lastName}" } ?: "",
                            options = state.availableTeachers,
                            onSelectItem = { onAction(AddEditClassAction.UpdateTeacher(it?.id)) },
                            label = "Teacher *",
                            itemToString = { "${it.firstName} ${it.lastName}" },
                            expanded = state.isTeacherDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditClassAction.ChangeTeacherDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.selectedTeacherError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Room Dropdown
                        PresencifyDropDownMenuBox<Room>(
                            value = state.selectedRoom?.roomNumber ?: "",
                            options = state.availableRooms,
                            onSelectItem = { onAction(AddEditClassAction.UpdateRoom(it.id)) },
                            label = "Room *",
                            itemToString = { it.roomNumber },
                            expanded = state.isRoomDropdownOpen,
                            onDropDownVisibilityChanged = { onAction(AddEditClassAction.ChangeRoomDropDownVisibility(it)) },
                            supportingText = state.selectedRoomError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Class Type Dropdown
                        PresencifyDropDownMenuBox<ClassType>(
                            value = state.classType?.value ?: "",
                            options = state.classTypeOptions,
                            onSelectItem = { onAction(AddEditClassAction.UpdateClassType(it)) },
                            label = "Class Type *",
                            itemToString = { it.value },
                            expanded = state.isClassTypeDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditClassAction.ChangeClassTypeDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.classTypeError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Batch Dropdown (Only for Practical or Tutorial)
                        if (state.classType == ClassType.PRACTICAL ||
                            state.classType == ClassType.TUTORIAL
                        ) {
                            PresencifyDropDownMenuBox<Batch>(
                                value = state.selectedBatch?.batchCode ?: "",
                                options = state.availableBatches,
                                onSelectItem = { onAction(AddEditClassAction.UpdateBatch(it?.id)) },
                                label = "Batch (Optional)",
                                itemToString = { it.batchCode },
                                expanded = state.isBatchDropdownOpen,
                                onDropDownVisibilityChanged = {
                                    onAction(
                                        AddEditClassAction.ChangeBatchDropDownVisibility(
                                            it
                                        )
                                    )
                                },
                                enabled = !state.isSubmitting && !state.isEditMode,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Day of Week Dropdown
                        PresencifyDropDownMenuBox<DayOfWeek>(
                            value = state.dayOfWeek?.value ?: "",
                            options = state.dayOfWeekOptions,
                            onSelectItem = { onAction(AddEditClassAction.UpdateDayOfWeek(it)) },
                            label = "Day of Week *",
                            itemToString = { it.value },
                            expanded = state.isDayOfWeekDropdownOpen,
                            onDropDownVisibilityChanged = {
                                onAction(
                                    AddEditClassAction.ChangeDayOfWeekDropDownVisibility(
                                        it
                                    )
                                )
                            },
                            supportingText = state.dayOfWeekError,
                            enabled = !state.isSubmitting && !state.isEditMode,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Time Range (only show in add mode)
                        if (!state.isEditMode) {
                            Text(
                                text = "Time Range",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PresencifyTimePickerTextField(
                                    value = state.startTime,
                                    onValueChange = { onAction(AddEditClassAction.UpdateStartTime(it)) },
                                    label = "Start Time *",
                                    modifier = Modifier.weight(1f),
                                    supportingText = state.startTimeError,
                                    isError = state.startTimeError != null,
                                )
                                PresencifyTimePickerTextField(
                                    value = state.endTime,
                                    onValueChange = { onAction(AddEditClassAction.UpdateEndTime(it)) },
                                    label = "End Time *",
                                    modifier = Modifier.weight(1f),
                                    supportingText = state.endTimeError,
                                    isError = state.endTimeError != null,
                                )
                            }
                        }

                        // Active Date Range (always visible, editable in both add and edit mode)
                        Text(
                            text = "Active Date Range",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PresencifyDatePickerTextField(
                                value = state.activeFrom,
                                onValueChange = {
                                    onAction(AddEditClassAction.UpdateActiveFrom(it))
                                },
                                label = "Active From *",
                                modifier = Modifier.weight(1f),
                                supportingText = state.activeFromError,
                                isError = state.activeFromError != null,
                            )
                            PresencifyDatePickerTextField(
                                value = state.activeTill,
                                onValueChange = {
                                    onAction(AddEditClassAction.UpdateActiveTill(it))
                                },
                                label = "Active Till *",
                                modifier = Modifier.weight(1f),
                                supportingText = state.activeTillError,
                                isError = state.activeTillError != null,
                            )
                        }

                        // Extra Class Toggle (only show in add mode)
                        if (!state.isEditMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Extra Class",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Mark this as an extra class",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = state.isExtraClass,
                                    onCheckedChange = { onAction(AddEditClassAction.UpdateIsExtraClass(it)) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(DesignToken.spacing.sm))

                        // Submit Button
                        PresencifyButton(
                            onClick = { onAction(AddEditClassAction.SubmitClick) },
                            text = if (state.isEditMode) "Update Active Dates" else "Add Class",
                            enabled = !state.isSubmitting,
                            isLoading = state.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Dialog
    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> onAction(AddEditClassAction.DismissDialog)
                }
            },
            onDismiss = { onAction(AddEditClassAction.DismissDialog) }
        )
    }
}
