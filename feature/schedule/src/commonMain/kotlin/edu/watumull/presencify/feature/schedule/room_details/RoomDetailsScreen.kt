package edu.watumull.presencify.feature.schedule.room_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.watumull.presencify.core.design.systems.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.design.systems.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.design.systems.components.PresencifyScaffold
import edu.watumull.presencify.core.design.systems.components.PresencifyTextButton
import edu.watumull.presencify.core.design.systems.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.RoomListItem

@Composable
fun RoomDetailsScreen(
    state: RoomDetailsState,
    onAction: (RoomDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(RoomDetailsAction.BackButtonClick) },
        topBarTitle = "Room Details",
    ) { paddingValues ->
        when (state.viewState) {
            is RoomDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is RoomDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is RoomDetailsState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    ) {
                        state.room?.let { room ->
                            RoomListItem(
                                roomNumber = room.roomNumber,
                                sittingCapacity = room.sittingCapacity,
                                type = room.type,
                                name = room.name,
                                trailingIcon = null,
                                onClick = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PresencifyTextButton(
                                onClick = { onAction(RoomDetailsAction.EditRoomClick) },
                                enabled = !state.isRemovingRoom
                            ) {
                                Text(
                                    text = "Edit room",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            PresencifyTextButton(
                                onClick = { onAction(RoomDetailsAction.RemoveRoomClick) },
                                enabled = !state.isRemovingRoom
                            ) {
                                if (state.isRemovingRoom) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    Text(
                                        text = "Remove room",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            isVisible = dialogState.isVisible,
            dialogType = dialogState.dialogType,
            title = dialogState.title,
            message = dialogState.message?.asString() ?: "",
            onConfirm = {
                when (dialogState.dialogIntention) {
                    DialogIntention.CONFIRM_REMOVE_ROOM -> onAction(RoomDetailsAction.ConfirmRemoveRoom)
                    DialogIntention.GENERIC -> onAction(RoomDetailsAction.DismissDialog)
                }
            },
            onDismiss = { onAction(RoomDetailsAction.DismissDialog) }
        )
    }
}
