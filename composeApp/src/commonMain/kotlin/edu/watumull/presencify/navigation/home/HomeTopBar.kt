package edu.watumull.presencify.navigation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.watumull.presencify.core.designsystem.Res
import edu.watumull.presencify.core.designsystem.presencify_logo_circle_svg
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onProfileIconButtonClick: () -> Unit,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(Res.drawable.presencify_logo_circle_svg),
                    contentDescription = null,
                    modifier = Modifier.size(DesignToken.icons.lg)
                )
                Spacer(Modifier.width(DesignToken.spacing.sm))
                Text("Presencify", style = MaterialTheme.typography.titleMedium)
            }
        },
        windowInsets = windowInsets,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        actions = {
            IconButton(
                onClick = onProfileIconButtonClick
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account Details",
                    modifier = Modifier.size(DesignToken.icons.md)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
            canScroll = {
                false
            }
        ),
    )
}