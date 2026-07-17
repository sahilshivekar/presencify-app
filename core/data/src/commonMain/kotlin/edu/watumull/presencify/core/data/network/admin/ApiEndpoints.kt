package edu.watumull.presencify.core.data.network.admin

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.ADMINS
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {
    // Path segments
    val ME = "me"

    // Admin endpoints
    val ADD_ADMIN = "$PRESENCIFY_BASE_URL/$API_V1/$ADMINS"
    val UPDATE_ADMIN_DETAILS = "$PRESENCIFY_BASE_URL/$API_V1/$ADMINS/$ME"
    val REMOVE_ADMIN = "$PRESENCIFY_BASE_URL/$API_V1/$ADMINS/$ME"
    val GET_ADMINS = "$PRESENCIFY_BASE_URL/$API_V1/$ADMINS"
    val GET_ADMIN_DETAILS = "$PRESENCIFY_BASE_URL/$API_V1/$ADMINS/$ME"
}