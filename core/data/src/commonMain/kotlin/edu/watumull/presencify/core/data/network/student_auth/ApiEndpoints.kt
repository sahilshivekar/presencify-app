package edu.watumull.presencify.core.data.network.student_auth

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {
    val STUDENT_AUTH = "auth/students"
    
    val LOGIN_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/login"
    val SEND_VERIFICATION_CODE = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/send-verification-code"
    val VERIFY_CODE = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/verify-code"
    val UPDATE_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/update-password"
    val REFRESH_TOKENS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/access-token"
    val LOGOUT = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_AUTH/logout"
}
