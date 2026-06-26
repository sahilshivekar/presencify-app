package edu.watumull.presencify.navigation.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
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
fun HomeBottomNavigationBar(
    currentDestination: NavDestination?,
    onItemSelected: (Any) -> Unit
) {

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
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

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onItemSelected(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        modifier = Modifier
                            .height(DesignToken.icons.md)
                            .width(DesignToken.icons.md)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 9.5.sp,
                        maxLines = 1
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIconColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal =DesignToken.spacing.sm)
            )
        }
    }

}


data class BottomNavBarItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val route: NavRoute
)

