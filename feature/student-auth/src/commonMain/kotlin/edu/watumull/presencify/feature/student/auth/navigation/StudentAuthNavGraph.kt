package edu.watumull.presencify.feature.student.auth.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.student.auth.forgot_password.StudentForgotPasswordRoot
import edu.watumull.presencify.feature.student.auth.login.StudentLoginRoot
import edu.watumull.presencify.feature.student.auth.verify_code.StudentVerifyCodeRoot

fun NavGraphBuilder.studentAuthNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {

    composableWithSlideTransitions<StudentAuthRoutes.StudentLogin> {
        StudentLoginRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToHome = onNavigateToHome,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }
    composableWithSlideTransitions<StudentAuthRoutes.StudentForgotPassword> {
        StudentForgotPasswordRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToVerifyCode = { email -> onNavigateToVerifyCode(email) }
        )
    }
    composableWithSlideTransitions<StudentAuthRoutes.StudentVerifyCode> {
        StudentVerifyCodeRoot(
            onBackButtonClick = onNavigateBack,
            onCodeVerified = onNavigateToHome
        )
    }

}
