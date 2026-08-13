package edu.watumull.presencify.core.data.network.teacher

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.COURSES
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.IMAGE
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PASSWORD
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.TEACHERS

object ApiEndpoints {

    val GET_TEACHERS = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS"
    val ADD_TEACHER = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS"
    val GET_TEACHER_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS"
    val UPDATE_TEACHER_DETAILS = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS"
    val REMOVE_TEACHER = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS"

    val UPDATE_TEACHER_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$PASSWORD"
    val UPDATE_TEACHER_IMAGE = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$IMAGE"
    val REMOVE_TEACHER_IMAGE = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$IMAGE"

    val GET_TEACHING_COURSES = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$COURSES"
    val ADD_TEACHING_SUBJECT = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$COURSES"
    val REMOVE_TEACHING_SUBJECT = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/$COURSES"

    val BULK_CREATE_TEACHERS = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/bulk/create"
    val BULK_DELETE_TEACHERS = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/bulk/delete"
    val BULK_CREATE_TEACHERS_FROM_CSV = "$PRESENCIFY_BASE_URL/$API_V1/$TEACHERS/bulk/csv"
}