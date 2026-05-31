package edu.watumull.presencify.feature.academics.university_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import edu.watumull.presencify.core.presentation.components.UniversityListItem
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole

@Composable
fun UniversityDetailsScreen(
    state: UniversityDetailsState,
    onAction: (UniversityDetailsAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(UniversityDetailsAction.NavigateBack) },
        topBarTitle = "Universities",
        floatingActionButton = {
            if (state.viewState is UniversityDetailsState.ViewState.Content) {
                FloatingActionButton(
                    onClick = { onAction(UniversityDetailsAction.AddUniversityClick) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add University"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.viewState) {
            is UniversityDetailsState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is UniversityDetailsState.ViewState.Error -> {
                PresencifyNoResultsIndicator(text = state.viewState.message.asString())
            }

            is UniversityDetailsState.ViewState.Content -> {
                if (state.universities.isEmpty()) {
                    PresencifyNoResultsIndicator(text = "No universities found")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues)
                            .padding(horizontal = DesignToken.spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.md),
                        contentPadding = PaddingValues(vertical = DesignToken.spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            items = state.universities,
                            key = { university -> university.id }
                        ) { university ->
                            Column(
                                modifier = Modifier
                                    .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                                    .fillMaxWidth()
                            ) {
                                UniversityListItem(
                                    name = university.name,
                                    abbreviation = university.abbreviation,
                                    onClick = null,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(DesignToken.spacing.sm))
                                if (LocalUserRole.current == UserRole.ADMIN) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        PresencifyTextButton(
                                            onClick = { onAction(UniversityDetailsAction.EditUniversityClick(university.id)) },
                                            enabled = state.removingUniversityId != university.id
                                        ) {
                                            Text(
                                                text = "Edit",
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        PresencifyTextButton(
                                            onClick = {
                                                onAction(
                                                    UniversityDetailsAction.RemoveUniversityClick(
                                                        university.id
                                                    )
                                                )
                                            },
                                            enabled = state.removingUniversityId != university.id
                                        ) {
                                            if (state.removingUniversityId == university.id) {
                                                CircularProgressIndicator(
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(DesignToken.components.progressMd),
                                                    strokeWidth = DesignToken.strokes.md
                                                )
                                            } else {
                                                Text(
                                                    text = "Delete",
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
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            dialogType = dialogState.dialogType,
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            onConfirm = { onAction(UniversityDetailsAction.ConfirmRemoveUniversity) },
            onDismiss = { onAction(UniversityDetailsAction.DismissDialog) }
        )
    }
}
