package edu.watumull.presencify.core.data.network.admin_auth

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.ADMINS
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.AUTH
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {
    val LOGIN = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/login"
    val SEND_VERIFICATION_CODE_FORGOT = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/forgot-password"
    val VERIFY_CODE = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/verify-code"
    val REFRESH_TOKENS = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/access-token"
    val VERIFY_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/verify-password"
    val UPDATE_ADMIN_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/update-password"
    val LOGOUT = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/logout"
    val SEND_VERIFICATION_CODE_EMAIL = "$PRESENCIFY_BASE_URL/$API_V1/$AUTH/$ADMINS/email-verification"
}