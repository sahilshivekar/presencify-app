package edu.watumull.presencify.feature.teacher.auth.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.designsystem.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.teacher.auth.forgot_password.TeacherForgotPasswordRoot
import edu.watumull.presencify.feature.teacher.auth.login.TeacherLoginRoot
import edu.watumull.presencify.feature.teacher.auth.verify_code.TeacherVerifyCodeRoot

fun NavGraphBuilder.teacherAuthNavGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {

    composableWithSlideTransitions<TeacherAuthRoutes.TeacherLogin> {
        TeacherLoginRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToHome = onNavigateToHome,
            onNavigateToForgotPassword = onNavigateToForgotPassword
        )
    }
    composableWithSlideTransitions<TeacherAuthRoutes.TeacherForgotPassword> {
        TeacherForgotPasswordRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToVerifyCode = { email -> onNavigateToVerifyCode(email) }
        )
    }
    composableWithSlideTransitions<TeacherAuthRoutes.TeacherVerifyCode> {
        TeacherVerifyCodeRoot(
            onBackButtonClick = onNavigateBack,
            onCodeVerified = onNavigateToHome
        )
    }

}
