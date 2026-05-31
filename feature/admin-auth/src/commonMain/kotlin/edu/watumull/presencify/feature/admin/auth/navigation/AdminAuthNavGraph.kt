package edu.watumull.presencify.feature.admin.auth.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.designsystem.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.admin.auth.forgot_password.AdminForgotPasswordRoot
import edu.watumull.presencify.feature.admin.auth.login.AdminLoginRoot
import edu.watumull.presencify.feature.admin.auth.verify_code.AdminVerifyCodeRoot

fun NavGraphBuilder.adminAuthNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composableWithSlideTransitions<AdminAuthRoutes.AdminLogin> {
        AdminLoginRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToHome = onNavigateToHome,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }

    composableWithSlideTransitions<AdminAuthRoutes.AdminForgotPassword> {
        AdminForgotPasswordRoot(
            onNavigateBack = onNavigateBack,
            onNavigateToVerifyCode = { email -> onNavigateToVerifyCode(email) }
        )
    }

    composableWithSlideTransitions<AdminAuthRoutes.AdminVerifyCode> { backStackEntry ->
        AdminVerifyCodeRoot(
            onNavigateBack = onNavigateBack,
            onCodeVerified = onNavigateToHome
        )
    }
}

