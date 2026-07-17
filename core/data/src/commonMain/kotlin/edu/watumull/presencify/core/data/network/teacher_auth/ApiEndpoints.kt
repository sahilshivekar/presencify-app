package edu.watumull.presencify.core.data.network.teacher_auth

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {
    // Path segments
    val TEACHER_AUTH = "auth/teachers"
    val LOGIN_TEACHER = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/login"
    val SEND_VERIFICATION_CODE = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/send-verification-code"
    val VERIFY_CODE = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/verify-code"
    val UPDATE_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/update-password"
    val REFRESH_TOKENS = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/access-token"
    val LOGOUT = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHER_AUTH/logout"
}
