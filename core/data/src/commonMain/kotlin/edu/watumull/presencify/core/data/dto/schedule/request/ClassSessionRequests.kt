package edu.watumull.presencify.core.data.dto.schedule.request

import kotlinx.serialization.Serializable

@Serializable
data class AddClassRequest(
    val teacherId: String,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: String,
    val roomId: String,
    val batchId: String?,
    val activeFrom: String,
    val activeTill: String,
    val courseId: String,
    val timetableId: String
)

@Serializable
data class EditActiveDatesRequest(
    val newActiveFrom: String,
    val newActiveTill: String
)

@Serializable
data class CancelClassRequest(
    val classId: String,
    val date: String,
    val reason: String?
)


@Serializable
data class BulkDeleteClassesRequest(
    val classIds: List<String>
)
