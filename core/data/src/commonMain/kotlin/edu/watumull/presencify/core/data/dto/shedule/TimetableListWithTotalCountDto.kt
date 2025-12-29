package edu.watumull.presencify.core.data.dto.shedule

import kotlinx.serialization.Serializable

@Serializable
data class TimetableListWithTotalCountDto(
    val timetables: List<TimetableDto>,
    val totalCount: Int
)
