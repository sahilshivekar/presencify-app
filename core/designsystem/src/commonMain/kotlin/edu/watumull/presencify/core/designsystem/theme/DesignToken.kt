package edu.watumull.presencify.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
    val huge: Dp = 60.dp
)

val LocalSpacing = staticCompositionLocalOf {
    AppSpacing()
}

@Immutable
data class AppElevation(
    val none: Dp = 0.dp,

    val xs: Dp = 1.dp,
    val sm: Dp = 2.dp,
    val md: Dp = 4.dp,
    val lg: Dp = 6.dp,
    val xl: Dp = 12.dp,
)

val LocalElevation = staticCompositionLocalOf {
    AppElevation()
}

@Immutable
data class AppIconSizes(
    val xs: Dp = 12.dp,
    val sm: Dp = 16.dp,
    val md: Dp = 24.dp,
    val lg: Dp = 32.dp,
    val xl: Dp = 40.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 96.dp,
)

val LocalIconSizes = staticCompositionLocalOf {
    AppIconSizes()
}

@Immutable
data class AppAvatarSizes(
    val sm: Dp = 32.dp,
    val md: Dp = 48.dp,
    val lg: Dp = 64.dp,
    val xl: Dp = 96.dp,
    val xxl: Dp = 128.dp,
)

val LocalAvatarSizes = staticCompositionLocalOf {
    AppAvatarSizes()
}

@Immutable
data class AppImageSizes(
    val thumbnail: Dp = 48.dp,

    val sm: Dp = 80.dp,
    val md: Dp = 128.dp,
    val lg: Dp = 200.dp,
)

val LocalImageSizes = staticCompositionLocalOf {
    AppImageSizes()
}

@Immutable
data class AppStrokes(
    val none: Dp = 0.dp,

    val hairline: Dp = 0.5.dp,
    val thin: Dp = 1.dp,
    val md: Dp = 2.dp,
    val thick: Dp = 4.dp,

    val extraThick: Dp = 8.dp,
    val huge: Dp = 12.dp,
)

val LocalStrokes = staticCompositionLocalOf {
    AppStrokes()
}

@Immutable
data class AppComponentSizes(
    val progressSm: Dp = 20.dp,
    val progressMd: Dp = 24.dp,
    val progressLg: Dp = 32.dp,
)

val LocalComponentSizes = staticCompositionLocalOf {
    AppComponentSizes()
}

object DesignToken {

    val spacing: AppSpacing
        @Composable
        get() = LocalSpacing.current

    val elevation: AppElevation
        @Composable
        get() = LocalElevation.current

    val icons: AppIconSizes
        @Composable
        get() = LocalIconSizes.current

    val avatars: AppAvatarSizes
        @Composable
        get() = LocalAvatarSizes.current

    val images: AppImageSizes
        @Composable
        get() = LocalImageSizes.current

    val strokes: AppStrokes
        @Composable
        get() = LocalStrokes.current

    val components: AppComponentSizes
        @Composable
        get() = LocalComponentSizes.current
}
