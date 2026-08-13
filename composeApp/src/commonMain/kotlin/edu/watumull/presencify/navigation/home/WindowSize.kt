package edu.watumull.presencify.navigation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


enum class WindowWidthSizeClass {
    Compact,
    Medium,
    Expanded
}


@Composable
expect fun calculateWindowWidth(): Dp

@Composable
fun calculateWindowWidthSizeClass(): WindowWidthSizeClass {
    val windowWidth = calculateWindowWidth()
    return when {
        windowWidth < 600.dp -> WindowWidthSizeClass.Compact
        windowWidth < 840.dp -> WindowWidthSizeClass.Medium
        else -> WindowWidthSizeClass.Expanded
    }
}

