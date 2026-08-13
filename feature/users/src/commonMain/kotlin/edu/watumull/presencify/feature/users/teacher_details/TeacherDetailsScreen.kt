package edu.watumull.presencify.feature.users.teacher_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.baseline_account_circle_24
import edu.watumull.presencify.core.designsystem.components.PresencifyActionBar
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyCard
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserId
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import org.jetbrains.compose.resources.painterResource

@Composable
fun TeacherDetailsScreen(
    state: TeacherDetailsState,
    onAction: (TeacherDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(TeacherDetailsAction.NavigateBack) },
        topBarTitle = "Teacher Details",
    ) { paddingValues ->
        when (state.viewState) {
            is TeacherDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is TeacherDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is TeacherDetailsState.ViewState.Content -> {
                TeacherDetailsScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(TeacherDetailsAction.ConfirmRemoveTeacher) },
            onDismiss = { onAction(TeacherDetailsAction.DismissDialog) }
        )
    }
}

@Composable
private fun TeacherDetailsScreenContent(
    state: TeacherDetailsState,
    onAction: (TeacherDetailsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(DesignToken.spacing.lg),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeacherImageContainer(state = state, onAction = onAction)

        Spacer(modifier = Modifier.height(DesignToken.spacing.lg))

        TeacherDetailsContainer(state = state)

        Spacer(modifier = Modifier.height(DesignToken.spacing.lg))


        Column(
            modifier = Modifier
                .padding(top = DesignToken.spacing.lg)
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = DesignToken.spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (LocalUserRole.current == UserRole.ADMIN) {
                    PresencifyTextButton(
                        onClick = { onAction(TeacherDetailsAction.EditTeacherDetailsClick) },
                        enabled = !state.isRemovingTeacher
                    ) {
                        Text(text = "Edit details", color = MaterialTheme.colorScheme.primary)
                    }
                    PresencifyTextButton(
                        onClick = { onAction(TeacherDetailsAction.RemoveTeacherClick) },
                        enabled = !state.isRemovingTeacher
                    ) {
                        if (state.isRemovingTeacher) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(DesignToken.components.progressMd),
                                strokeWidth = DesignToken.strokes.md,
                            )
                        } else {
                            Text(
                                text = "Remove teacher",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else if (
                    state.showSelfActions &&
                    LocalUserRole.current == UserRole.TEACHER &&
                    state.teacherId == LocalUserId.current
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.md)) {
                        PresencifyTextButton(
                            onClick = { onAction(TeacherDetailsAction.ClickUpdatePassword) },
                            enabled = !state.isLoggingOut
                        ) {
                            Text(
                                "Update password",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        PresencifyTextButton(
                            onClick = { onAction(TeacherDetailsAction.LogoutClick) },
                            isLoading = state.isLoggingOut,
                            enabled = !state.isLoggingOut
                        ) {
                            Text(
                                "Logout",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            if (LocalUserRole.current == UserRole.ADMIN) {
                PresencifyActionBar(
                    text = "Assign / Unassign Courses",
                    onClick = { onAction(TeacherDetailsAction.AssignUnassignCoursesClick) },
                    leadingImageVector = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TeacherImageContainer(
    state: TeacherDetailsState,
    onAction: (TeacherDetailsAction) -> Unit,
) {
    if (state.isImageDialogVisible) {
        Dialog(
            onDismissRequest = { onAction(TeacherDetailsAction.ToggleImageDialog) },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = DesignToken.spacing.lg)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = state.teacher?.teacherImageUrl,
                    placeholder = painterResource(Res.drawable.baseline_account_circle_24),
                    fallback = painterResource(Res.drawable.baseline_account_circle_24),
                    error = painterResource(Res.drawable.baseline_account_circle_24),
                    contentDescription = "Teacher Image",
                    modifier = Modifier
                        .padding(DesignToken.spacing.lg)
                        .size(DesignToken.avatars.xxl)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DesignToken.spacing.lg)
                        .padding(bottom = DesignToken.spacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PresencifyTextButton(
                        onClick = {
                            onAction(TeacherDetailsAction.ToggleImageDialog)
                        },
                    ) {
                        Text("Dismiss", color = MaterialTheme.colorScheme.onSurface)
                    }
                    if (state.teacher?.teacherImageUrl != null) {
                        PresencifyTextButton(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                                onAction(TeacherDetailsAction.RemoveImageClick)
                            },
                            enabled = !state.isUpdatingImage && !state.isRemovingImage
                        ) {
                            if (state.isRemovingImage) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(DesignToken.components.progressMd),
                                    strokeWidth = DesignToken.strokes.md,
                                )
                            } else {
                                Text(text = "Remove Image", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else if (state.newUploadedImageBytes == null) {
                        PresencifyButton(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                            },
                            enabled = !state.isUpdatingImage && !state.isRemovingImage,
                            isLoading = false
                        ) {
                            Text(text = "Add Image")
                        }
                    } else {
                        PresencifyButton(
                            modifier = Modifier.wrapContentSize(),
                            onClick = {
                                onAction(TeacherDetailsAction.UpdateTeacherImageClick)
                            },
                            enabled = !state.isUpdatingImage && !state.isRemovingImage,
                            isLoading = state.isUpdatingImage
                        ) {
                            Text(text = "Upload Image")
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.widthIn(max = UiConstants.MAX_CONTENT_WIDTH),
    ) {
        AsyncImage(
            model = state.teacher?.teacherImageUrl,
            contentDescription = null,
            modifier = Modifier
                .padding(DesignToken.spacing.sm)
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            colorFilter = if (state.teacher?.teacherImageUrl != null) null else ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface.copy(alpha = .5f)
            ),
            placeholder = painterResource(Res.drawable.baseline_account_circle_24),
            error = painterResource(Res.drawable.baseline_account_circle_24),
            fallback = painterResource(Res.drawable.baseline_account_circle_24)
        )

        if (LocalUserRole.current == UserRole.ADMIN) {
            IconButton(
                onClick = {
                    onAction(TeacherDetailsAction.ToggleImageDialog)
                },
                modifier = Modifier.align(alignment = Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun TeacherDetailsContainer(state: TeacherDetailsState) {
    PresencifyCard(
        modifier = Modifier
            .wrapContentHeight()
            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
            .padding(top = DesignToken.spacing.lg),
    ) {
        DetailRow(
            label = "Full Name",
            value = buildString {
                append(state.teacher?.firstName ?: "")
                state.teacher?.middleName?.let {
                    if (it.isNotBlank()) append(" $it")
                }
                append(" ${state.teacher?.lastName ?: ""}")
            }.trim()
        )

        DetailRow(
            label = "Gender",
            value = state.teacher?.gender?.toDisplayLabel() ?: ""
        )

        DetailRow(
            label = "Email",
            value = state.teacher?.email ?: ""
        )

        DetailRow(
            label = "Phone",
            value = state.teacher?.phoneNumber ?: ""
        )

        DetailRow(
            label = "Qualification",
            value = state.teacher?.highestQualification ?: "Not added"
        )

        DetailRow(
            label = "Role",
            value = state.teacher?.role?.toDisplayLabel() ?: "",
        )

        DetailRow(
            label = "Status",
            value = if (state.teacher?.isActive == true) "Active" else "Inactive",
            isLast = true
        )
    }
}


@Composable
private fun DetailRow(
    label: String,
    value: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DesignToken.spacing.lg)
            .padding(top = DesignToken.spacing.lg, bottom = if (isLast) DesignToken.spacing.lg else DesignToken.spacing.none),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(.5f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = ":",
            modifier = Modifier.weight(.05f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            modifier = Modifier.weight(.5f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
