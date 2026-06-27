package edu.watumull.presencify.core.data.dto.schedule

import edu.watumull.presencify.core.data.dto.academics.CourseDto
import edu.watumull.presencify.core.data.dto.teacher.TeacherDto
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class UpcomingClassesResponseDto(
    val classes: List<UpcomingClassDto>,
    val totalCount: Int
)

@Serializable
data class UpcomingClassDto(
    val id: String,
    val nextClassDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val dayOfWeek: DayOfWeek,
    val course: CourseDto,
    val room: RoomDto,
    val teacher: TeacherDto
)