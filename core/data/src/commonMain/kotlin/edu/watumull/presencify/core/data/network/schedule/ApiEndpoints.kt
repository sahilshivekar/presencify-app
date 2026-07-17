package edu.watumull.presencify.core.data.network.schedule

import edu.watumull.presencify.core.data.network.BaseApiEndpoints.API_V1
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.CANCELLED
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.CLASSES
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.PRESENCIFY_BASE_URL
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.ROOMS
import edu.watumull.presencify.core.data.network.BaseApiEndpoints.TIMETABLES

object ApiEndpoints {

    // Class endpoints
    val GET_CLASSES = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES"
    val ADD_CLASS = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES"
    val GET_CLASS_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES"
    val EDIT_ACTIVE_DATES = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES"
    val REMOVE_CLASS = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES"
    val ADD_EXTRA_CLASS = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/extra"
    val GET_CANCELLED_CLASSES = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/$CANCELLED"
    val CANCEL_CLASS = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/$CANCELLED"
    val BULK_CREATE_CLASSES = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/bulk"
    val BULK_DELETE_CLASSES = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/bulk"
    val BULK_CREATE_CLASSES_FROM_CSV = "$PRESENCIFY_BASE_URL/$API_V1/$CLASSES/bulk/csv"

    // Room endpoints
    val GET_ROOMS = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"
    val ADD_ROOM = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"
    val GET_ROOM_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"
    val GET_ROOM_SCHEDULE = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"
    val UPDATE_ROOM = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"
    val REMOVE_ROOM = "$PRESENCIFY_BASE_URL/$API_V1/$ROOMS"

    // Timetable endpoints
    val GET_TIMETABLES = "$PRESENCIFY_BASE_URL/$API_V1/$TIMETABLES"
    val GET_TIMETABLE_BY_ID = "$PRESENCIFY_BASE_URL/$API_V1/$TIMETABLES"
    val ADD_TIMETABLE = "$PRESENCIFY_BASE_URL/$API_V1/$TIMETABLES"
    val UPDATE_TIMETABLE = "$PRESENCIFY_BASE_URL/$API_V1/$TIMETABLES"
    val REMOVE_TIMETABLE = "$PRESENCIFY_BASE_URL/$API_V1/$TIMETABLES"
}
