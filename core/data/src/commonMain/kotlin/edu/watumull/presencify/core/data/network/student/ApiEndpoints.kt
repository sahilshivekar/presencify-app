package edu.watumull.presencify.core.data.network.student

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.IMAGE
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PASSWORD
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.STUDENTS

object ApiEndpoints {
    // Independent Routes (Mounted at root in app.js)
    val DROPOUTS_ROOT = "dropouts"
    val STUDENT_FCM_TOKENS_ROOT = "student-fcm-tokens"

    // Student endpoints
    val GET_STUDENTS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS"
    val ADD_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS"
    val GET_STUDENT_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id
    val UPDATE_STUDENT_DETAILS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id
    val UPDATE_STUDENT_PASSWORD = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/$PASSWORD"
    val UPDATE_STUDENT_IMAGE = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/$IMAGE"
    val REMOVE_STUDENT_IMAGE = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/$IMAGE"
    val REMOVE_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id

    // Student relationship endpoints
    // Note: Backend uses /students/:id/semesters for GET, but /students/semester for POST/DELETE
    val GET_STUDENT_SEMESTERS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id/semesters
    val ADD_STUDENT_TO_SEMESTER = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/semester"
    val REMOVE_STUDENT_FROM_SEMESTER = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/semester"

    val GET_STUDENT_DIVISIONS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id/divisions
    val ADD_STUDENT_TO_DIVISION = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/division"
    val CHANGE_STUDENT_DIVISION = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/division"
    val REVERT_ADD_STUDENT_TO_DIVISION = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/division/revert-add"
    val REVERT_CHANGE_STUDENT_DIVISION = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/division/revert-change"

    val GET_STUDENT_BATCHES = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS" // Usage: /$id/batches
    val ADD_STUDENT_TO_BATCH = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/batch"
    val CHANGE_STUDENT_BATCH = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/batch"
    val REVERT_ADD_STUDENT_TO_BATCH = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/batch/revert-add"
    val REVERT_CHANGE_STUDENT_BATCH = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/batch/revert-change"

    // Bulk operations
    val BULK_CREATE_STUDENTS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/create"
    val BULK_DELETE_STUDENTS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/delete"
    val BULK_ADD_STUDENTS_TO_SEMESTER = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/semester"
    val BULK_ADD_STUDENTS_TO_DIVISION = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/division"
    val BULK_ADD_STUDENTS_TO_BATCH = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/batch"
    val BULK_CREATE_STUDENTS_FROM_CSV = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/bulk/csv"

    // Dropout endpoints (Mounted at /api/v1/dropouts in app.js)
    val ADD_STUDENT_TO_DROPOUT = "$PRESENCIFY_BASE_URL/$API_V1/$DROPOUTS_ROOT"
    val REMOVE_STUDENT_FROM_DROPOUT = "$PRESENCIFY_BASE_URL/$API_V1/$DROPOUTS_ROOT"
    val GET_DROPOUT_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$DROPOUTS_ROOT" // Usage: /$id
    val GET_DROPOUT_DETAILS_OF_STUDENT = "$PRESENCIFY_BASE_URL/$API_V1/$DROPOUTS_ROOT/student"

    // FCM Token endpoints (Mounted at /api/v1/student-fcm-tokens in app.js)
    val ADD_STUDENT_FCM_TOKENS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_FCM_TOKENS_ROOT"
    val REMOVE_STUDENT_FCM_TOKENS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENT_FCM_TOKENS_ROOT"

    // Biometric endpoints
    val SUBMIT_BIOMETRICS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS/biometrics/submit"
    val GET_STUDENT_BIOMETRICS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS"
    val VERIFY_STUDENT_BIOMETRICS = "$PRESENCIFY_BASE_URL/$API_V1/$STUDENTS"
}