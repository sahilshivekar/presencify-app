package edu.watumull.presencify.core.domain.model.shedule

data class TimetableListWithTotalCount(
    val timetables: List<Timetable>,
    val totalCount: Int
)