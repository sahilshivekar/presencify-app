package edu.watumull.presencify.navigation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import edu.watumull.presencify.core.designsystem.theme.DesignToken
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.presentation.composition_locals.LocalUserRole
import edu.watumull.presencify.core.presentation.navigation.NavRoute
import edu.watumull.presencify.feature.academics.navigation.AcademicsRoutes
import edu.watumull.presencify.feature.attendance.navigation.AttendanceRoutes
import edu.watumull.presencify.feature.schedule.navigation.ScheduleRoutes
import edu.watumull.presencify.feature.users.navigation.UsersRoutes

@Composable
fun HomeNavigationRail(
    currentDestination: NavDestination?,
    onItemSelected: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Spacer(Modifier.height(DesignToken.spacing.lg))

        val attendanceTabRoute: NavRoute = if (LocalUserRole.current == UserRole.STUDENT) {
            AttendanceRoutes.StudentAttendanceDashboard
        } else {
            AttendanceRoutes.AttendanceDashboard
        }

        val bottomNavBarItems = listOf(
            BottomNavBarItem(
                selectedIcon = Icons.Filled.HowToReg,
                unselectedIcon = Icons.Outlined.HowToReg,
                label = "Attendance",
                route = attendanceTabRoute
            ),
            BottomNavBarItem(
                selectedIcon = Icons.Filled.Event,
                unselectedIcon = Icons.Outlined.Event,
                label = "Schedule",
                route = ScheduleRoutes.ScheduleDashboard
            ),
            BottomNavBarItem(
                selectedIcon = Icons.Filled.Group,
                unselectedIcon = Icons.Outlined.Group,
                label = "Users",
                route = UsersRoutes.UsersDashboard
            ),
            BottomNavBarItem(
                selectedIcon = Icons.Default.AccountTree,
                unselectedIcon = Icons.Outlined.AccountTree,
                label = "Academics",
                route = AcademicsRoutes.AcademicsDashboard
            ),
        )

        bottomNavBarItems.forEach { item ->
            val isSelected = when (item.route) {
                attendanceTabRoute -> {
                    currentDestination?.hasRoute<AttendanceRoutes.AttendanceDashboard>() == true ||
                            currentDestination?.hasRoute<AttendanceRoutes.StudentAttendanceDashboard>() == true ||
                            currentDestination?.hasRoute<AttendanceRoutes.StudentAttendanceAnalytics>() == true
                }

                ScheduleRoutes.ScheduleDashboard -> currentDestination?.hasRoute<ScheduleRoutes.ScheduleDashboard>()
                    ?: false

                UsersRoutes.UsersDashboard -> currentDestination?.hasRoute<UsersRoutes.UsersDashboard>() ?: false
                AcademicsRoutes.AcademicsDashboard -> currentDestination?.hasRoute<AcademicsRoutes.AcademicsDashboard>()
                    ?: false

                else -> false
            }

            NavigationRailItem(
                selected = isSelected,
                onClick = {
                    onItemSelected(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier.size(DesignToken.icons.md)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = DesignToken.spacing.xs)
            )
        }
    }
}

