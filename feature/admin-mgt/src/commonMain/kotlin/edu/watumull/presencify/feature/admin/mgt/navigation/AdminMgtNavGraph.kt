package edu.watumull.presencify.feature.admin.mgt.navigation

import androidx.navigation.NavGraphBuilder
import edu.watumull.presencify.core.design.systems.components.composableWithSlideTransitions
import edu.watumull.presencify.feature.admin.mgt.add_admin.AddAdminRoot
import edu.watumull.presencify.feature.admin.mgt.admin_details.AdminDetailsRoot
import edu.watumull.presencify.feature.admin.mgt.update_password.UpdatePasswordRoot

fun NavGraphBuilder.adminMgtNavGraph(
    onNavigateBack: () -> Unit,
    onNavigateToUpdatePassword: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onNavigateToAddAdmin: () -> Unit,
    onNavigateToAdminDetails: () -> Unit,
) {

    composableWithSlideTransitions<AdminMgtRoutes.AddAdmin> {
        AddAdminRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToAdminDetails = onNavigateToAdminDetails
        )
    }

    composableWithSlideTransitions<AdminMgtRoutes.UpdateAdminPassword> {
        UpdatePasswordRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToAdminDetails = onNavigateToAdminDetails
        )
    }

    composableWithSlideTransitions<AdminMgtRoutes.AdminDetails> {
        AdminDetailsRoot(
            onBackButtonClick = onNavigateBack,
            onNavigateToUpdatePassword = onNavigateToUpdatePassword,
            onNavigateToVerifyCode = onNavigateToVerifyCode,
            onNavigateToAddAdmin = onNavigateToAddAdmin,
        )
    }

    composableWithSlideTransitions<AdminMgtRoutes.SearchAdmin> {
        // TODO: Add screen content
    }

}

