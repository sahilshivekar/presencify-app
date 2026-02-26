package edu.watumull.presencify.feature.schedule.add_edit_room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyButton
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextField
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.domain.enums.RoomType
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun AddEditRoomScreen(
    state: AddEditRoomState,
    onAction: (AddEditRoomAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(AddEditRoomAction.BackButtonClick) },
        topBarTitle = if (state.isEditMode) "Edit Room" else "Add Room",
    ) { paddingValues ->
        when (state.viewState) {
            is AddEditRoomState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is AddEditRoomState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is AddEditRoomState.ViewState.Content -> {
                AddEditRoomScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message.asString(),
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.GENERIC -> {
                        onAction(AddEditRoomAction.DismissDialog)
                    }
                }
            },
            onDismiss = {
                onAction(AddEditRoomAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun AddEditRoomScreenContent(
    state: AddEditRoomState,
    onAction: (AddEditRoomAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Section: Room Details
            Text(
                text = "Room Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            // Room Number (Required)
            PresencifyTextField(
                value = state.roomNumber,
                onValueChange = { onAction(AddEditRoomAction.UpdateRoomNumber(it)) },
                label = "Room Number *",
                supportingText = state.roomNumberError,
                isError = state.roomNumberError != null,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            // Room Name (Optional)
            PresencifyTextField(
                value = state.name,
                onValueChange = { onAction(AddEditRoomAction.UpdateName(it)) },
                label = "Room Name",
                supportingText = state.nameError,
                isError = state.nameError != null,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            // Sitting Capacity (Required)
            PresencifyTextField(
                value = state.sittingCapacity,
                onValueChange = { onAction(AddEditRoomAction.UpdateSittingCapacity(it)) },
                label = "Sitting Capacity *",
                supportingText = state.sittingCapacityError,
                isError = state.sittingCapacityError != null,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )

            PresencifyDropDownMenuBox<RoomType>(
                value = state.roomType?.toDisplayLabel() ?: "",
                options = state.roomTypeOptions,
                onSelectItem = { onAction(AddEditRoomAction.UpdateRoomType(it)) },
                label = "Select Room Type",
                itemToString = { it.toDisplayLabel() },
                expanded = state.isRoomTypeDropdownOpen,
                onDropDownVisibilityChanged = { onAction(AddEditRoomAction.ChangeRoomTypeDropDownVisibility(it)) },
                supportingText = state.roomTypeError,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Submit Button
            PresencifyButton(
                onClick = { onAction(AddEditRoomAction.SubmitClick) },
                text = if (state.isEditMode) "Update Room" else "Add Room",
                isLoading = state.isSubmitting,
                enabled = !state.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
