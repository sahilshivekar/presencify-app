package edu.watumull.presencify.core.data.network.attendance

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.ATTENDANCES
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {

    // Attendance endpoints
    const val CREATE_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // POST /
    const val REMOVE_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // DELETE /
    const val GET_ATTENDANCES = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES"    // GET / (paginated with filters)
    const val GET_ATTENDANCE_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // GET /:attendanceId

    // Student operations
    const val UPDATE_STUDENT_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/students" // PUT /students

    const val BULK_UPDATE_STUDENT_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/bulk/update"

    const val GET_ATTENDANCE_OF_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/student"
    const val GET_ATTENDANCE_OF_SELF = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/me"
    const val GET_ATTENDANCE_OF_ALL = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/all"
    const val SEND_ATTENDANCE_REPORT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/report"
    const val GET_ACTIVE_ATTENDANCE_SHEET = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/active"
    const val MARK_MY_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/me/mark"
}