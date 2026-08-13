package edu.watumull.presencify.navigation.navcontroller_extensions

import androidx.navigation.NavController
import edu.watumull.presencify.feature.admin.mgt.navigation.AdminMgtRoutes


fun NavController.navigateToAddAdmin() {
    navigate(AdminMgtRoutes.AddAdmin)
}


fun NavController.navigateToUpdateAdminPassword() {
    navigate(AdminMgtRoutes.UpdateAdminPassword)
}


fun NavController.navigateToAdminDetails(adminId: String? = null) {
    navigate(AdminMgtRoutes.AdminDetails(adminId = adminId))
}


fun NavController.navigateToSearchAdmin() {
    navigate(AdminMgtRoutes.SearchAdmin)
}

