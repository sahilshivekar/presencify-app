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
    val activeFrom: String, // LocalDate as String
    val activeTill: String, // LocalDate as String
    val courseId: String,
    val timetableId: String
)

@Serializable
data class EditActiveDatesRequest(
    val newActiveFrom: String, // LocalDate as String
    val newActiveTill: String // LocalDate as String
)

@Serializable
data class CancelClassRequest(
    val classId: String,
    val date: String, // LocalDate as String
    val reason: String?
)

//@Serializable
//data class BulkCreateClassesRequest(
//    val classes: List<Map<String, Any>>
//)

@Serializable
data class BulkDeleteClassesRequest(
    val classIds: List<String>
)
