package edu.watumull.presencify.core.data.network.attendance

import edu.watumull.presencify.core.data.network.BaseApiEndpoints
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.ATTENDANCES
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL

object ApiEndpoints {

    // Attendance endpoints
    val CREATE_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // POST /
    val REMOVE_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // DELETE /
    val GET_ATTENDANCES = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES"    // GET / (paginated with filters)
    val GET_ATTENDANCE_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES" // GET /:attendanceId

    // Student operations
    val UPDATE_STUDENT_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/students" // PUT /students

    val BULK_UPDATE_STUDENT_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/bulk/update"

    val GET_ATTENDANCE_OF_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/student"
    val GET_ATTENDANCE_OF_EVERY_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/students/bulk"
    val GET_ATTENDANCE_OF_SELF = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/me"
    val GET_ATTENDANCE_OF_ALL = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/all"
    val SEND_ATTENDANCE_REPORT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/report"
    val GET_ACTIVE_ATTENDANCE_SHEET = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/active"
    val MARK_MY_ATTENDANCE = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/me/mark"
    val MARK_ALL_PRESENT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/bulk/mark-all-present"
    val MARK_ALL_ABSENT = "$PRESENCIFY_BASE_URL/$API_V1/$ATTENDANCES/bulk/mark-all-absent"
}
