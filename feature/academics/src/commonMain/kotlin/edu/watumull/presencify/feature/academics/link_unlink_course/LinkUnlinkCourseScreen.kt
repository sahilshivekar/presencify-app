package edu.watumull.presencify.feature.academics.link_unlink_course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import edu.watumull.presencify.core.designsystem.components.PresencifyButton
import edu.watumull.presencify.core.designsystem.components.PresencifyDefaultLoadingScreen
import edu.watumull.presencify.core.designsystem.components.PresencifyDropDownMenuBox
import edu.watumull.presencify.core.designsystem.components.PresencifyNoResultsIndicator
import edu.watumull.presencify.core.designsystem.components.PresencifyScaffold
import edu.watumull.presencify.core.designsystem.components.dialog.PresencifyAlertDialog
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Branch
import edu.watumull.presencify.core.presentation.UiConstants

@Composable
fun LinkUnlinkCourseScreen(
    state: LinkUnlinkCourseState,
    onAction: (LinkUnlinkCourseAction) -> Unit,
) {
    PresencifyScaffold(
        backPress = { onAction(LinkUnlinkCourseAction.NavigateBack) },
        topBarTitle = "Link/Unlink Courses",
    ) { paddingValues ->
        when (state.viewState) {
            is LinkUnlinkCourseState.ViewState.Loading -> {
                PresencifyDefaultLoadingScreen()
            }

            is LinkUnlinkCourseState.ViewState.Error -> {
                PresencifyNoResultsIndicator(
                    text = state.viewState.message.asString()
                )
            }

            is LinkUnlinkCourseState.ViewState.Content -> {
                LinkUnlinkCourseScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    state.dialogState?.let { dialogState ->
        PresencifyAlertDialog(
            title = dialogState.title?.asString(),
            message = dialogState.message.asString(),
            dialogType = dialogState.dialogType,
            onDismiss = {
                onAction(LinkUnlinkCourseAction.DismissDialog)
            }
        )
    }
}

@Composable
private fun LinkUnlinkCourseScreenContent(
    state: LinkUnlinkCourseState,
    onAction: (LinkUnlinkCourseAction) -> Unit,
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
        Column(
            modifier = Modifier
                .widthIn(max = UiConstants.MAX_CONTENT_WIDTH)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.lg),
            horizontalAlignment = Alignment.Start
        ) {
            // Instructions
            Text(
                text = "Select a branch and semester to link or unlink courses",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Branch Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Branch",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                PresencifyDropDownMenuBox<Branch>(
                    value = state.selectedBranch?.name ?: "",
                    options = state.branchOptions,
                    onSelectItem = { onAction(LinkUnlinkCourseAction.SelectBranch(it)) },
                    label = "Select Branch",
                    itemToString = { it.name },
                    expanded = state.isBranchDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(LinkUnlinkCourseAction.ChangeBranchDropDownVisibility(it)) },
                    supportingText = state.branchError,
                    enabled = !state.areBranchesLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Semester Selection
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.sm)
            ) {
                Text(
                    text = "Semester",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                PresencifyDropDownMenuBox<SemesterNumber>(
                    value = state.selectedSemesterNumber?.toDisplayLabel() ?: "",
                    options = state.semesterNumberOptions,
                    onSelectItem = { onAction(LinkUnlinkCourseAction.SelectSemesterNumber(it)) },
                    label = "Select Semester",
                    itemToString = { it.toDisplayLabel() },
                    expanded = state.isSemesterDropdownOpen,
                    onDropDownVisibilityChanged = { onAction(LinkUnlinkCourseAction.ChangeSemesterDropDownVisibility(it)) },
                    supportingText = state.semesterError,
                    enabled = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Link Courses Button
            PresencifyButton(
                onClick = { onAction(LinkUnlinkCourseAction.LinkCoursesClick) },
                text = "Link Courses",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignToken.spacing.lg)
            )
        }
    }
}
