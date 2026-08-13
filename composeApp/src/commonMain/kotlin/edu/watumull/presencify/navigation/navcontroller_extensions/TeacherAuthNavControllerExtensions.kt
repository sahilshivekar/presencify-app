package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.teacher.auth.navigation.TeacherAuthRoutes


fun NavController.navigateToTeacherLogin() {
    navigate(TeacherAuthRoutes.TeacherLogin)
}


fun NavController.navigateToTeacherForgotPassword() {
    navigate(TeacherAuthRoutes.TeacherForgotPassword)
}


fun NavController.navigateToTeacherVerifyCode(email: String) {
    navigate(TeacherAuthRoutes.TeacherVerifyCode(email = email))
}

