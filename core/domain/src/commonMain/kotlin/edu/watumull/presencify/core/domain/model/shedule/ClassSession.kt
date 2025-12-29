package edu.watumull.presencify.core.domain.model.shedule

import edu.watumull.presencify.core.domain.model.academics.Batch
import edu.watumull.presencify.core.domain.model.academics.Course
import edu.watumull.presencify.core.domain.model.attendance.Attendance
import edu.watumull.presencify.core.domain.model.teacher.Teacher

data class ClassSession(
    val id: String,
    val teacherId: String,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: String,
    val roomId: String,
    val batchId: String?,
    val activeFrom: String,
    val activeTill: String,
    val classType: String,
    val courseId: String,
    val timetableId: String,
    val isExtraClass: Boolean,
    val teacher: Teacher? = null,
    val room: Room? = null,
    val batch: Batch? = null,
    val course: Course? = null,
    val timetable: Timetable? = null,
    val attendances: List<Attendance>? = null
)
