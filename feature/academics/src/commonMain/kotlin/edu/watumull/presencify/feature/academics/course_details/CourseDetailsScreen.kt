package edu.watumull.presencify.feature.academics.course_details

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.PresencifyTextButton
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.UiConstants
import edu.watumull.presencify.core.presentation.components.CourseListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

@Composable
fun CourseDetailsScreen(
    state: CourseDetailsState,
    onAction: (CourseDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(CourseDetailsAction.NavigateBack) },
        topBarTitle = "Course Details",
    ) { paddingValues ->
        when (state.viewState) {
            is CourseDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is CourseDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is CourseDetailsState.ViewState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues)
                        .padding(DesignToken.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Column(
                        modifier = Modifier
                            .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                    ) {
                        state.course?.let { course ->
                            CourseListItem(
                                name = course.name,
                                code = course.code,
                                schemeName = course.scheme?.name ?: "N/A",
                                optionalCourse = course.optionalCourse,
                                trailingContent = null,
                                onClick = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(DesignToken.spacing.lg))
                        if (LocalUserRole.current == UserRole.ADMIN) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PresencifyTextButton(
                                    onClick = { onAction(CourseDetailsAction.EditCourseClick) },
                                    enabled = !state.isRemovingCourse
                                ) {
                                    androidx.compose.material3.Text(
                                        text = "Edit course",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                PresencifyTextButton(
                                    onClick = { onAction(CourseDetailsAction.RemoveCourseClick) },
                                    enabled = !state.isRemovingCourse
                                ) {
                                    if (state.isRemovingCourse) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(DesignToken.components.progressMd),
                                            strokeWidth = DesignToken.strokes.md,
                                        )
                                    } else {
                                        androidx.compose.material3.Text(
                                            text = "Remove course",
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
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(CourseDetailsAction.ConfirmRemoveCourse) },
            onDismiss = { onAction(CourseDetailsAction.DismissDialog) }
        )
    }
}

