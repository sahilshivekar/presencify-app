package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimetableListWithTotalCountDto(
    val timetables: List<TimetableDto>,
    val totalCount: Int
)
