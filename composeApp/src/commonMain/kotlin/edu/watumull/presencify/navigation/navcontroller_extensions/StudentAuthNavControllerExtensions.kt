package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.student.auth.navigation.StudentAuthRoutes


fun NavController.navigateToStudentLogin() {
    navigate(StudentAuthRoutes.StudentLogin)
}


fun NavController.navigateToStudentForgotPassword() {
    navigate(StudentAuthRoutes.StudentForgotPassword)
}


fun NavController.navigateToStudentVerifyCode(email: String) {
    navigate(StudentAuthRoutes.StudentVerifyCode(email = email))
}


