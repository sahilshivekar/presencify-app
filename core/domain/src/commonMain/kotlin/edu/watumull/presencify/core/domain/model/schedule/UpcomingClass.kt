package edu.watumull.presencify.core.domain.model.schedule

import edu.watumull.presencify.core.domain.enums.DayOfWeek
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.teacher.Teacher
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class UpcomingClass(
    val id: String,
    val nextClassDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: DayOfWeek,
    val course: Course,
    val room: Room,
    val teacher: Teacher
)