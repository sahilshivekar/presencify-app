package edu.watumull.presencify.core.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun PresencifyActionBar(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: DrawableResource? = null,
    leadingImageVector: ImageVector? = null,
    trailingIcon: ImageVector = Icons.Default.ChevronRight,
    leadingIconTint: Color = MaterialTheme.colorScheme.primary,
    trailingIconTint: Color = MaterialTheme.colorScheme.outline,
    headlineContentColor: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true
) {
    PresencifyListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = headlineContentColor
            )
        },
        leadingContent = if (leadingImageVector != null || leadingIcon != null) {
            {
                when {
                    leadingImageVector != null -> Icon(
                        imageVector = leadingImageVector,
                        contentDescription = null,
                        tint = leadingIconTint,
                        modifier = Modifier.size(DesignToken.icons.md)
                    )
                    leadingIcon != null -> Icon(
                        painter = painterResource(leadingIcon),
                        contentDescription = null,
                        tint = leadingIconTint,
                        modifier = Modifier.size(DesignToken.icons.md)
                    )
                }
            }
        } else null,
        trailingContent = {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingIconTint
            )
        },
        onClick = if (enabled) onClick else null
    )
}
