package edu.watumull.presencify.core.data.dto.schedule.request

import kotlinx.serialization.Serializable

@Serializable
data class AddTimetableRequest(
    val divisionId: String,
    val timetableVersion: Int? = null
)

@Serializable
data class UpdateTimetableRequest(
    val timetableVersion: Int
)
