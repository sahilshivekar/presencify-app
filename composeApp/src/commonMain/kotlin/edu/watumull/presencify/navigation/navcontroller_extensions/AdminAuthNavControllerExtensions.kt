package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.admin.auth.navigation.AdminAuthRoutes


fun NavController.navigateToAdminLogin() {
    navigate(AdminAuthRoutes.AdminLogin)
}


fun NavController.navigateToAdminForgotPassword() {
    navigate(AdminAuthRoutes.AdminForgotPassword)
}


fun NavController.navigateToAdminVerifyCode(email: String) {
    navigate(AdminAuthRoutes.AdminVerifyCode(email = email))
}

