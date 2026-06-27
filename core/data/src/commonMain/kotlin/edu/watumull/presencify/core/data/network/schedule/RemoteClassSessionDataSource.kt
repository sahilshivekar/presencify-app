package edu.watumull.presencify.core.data.network.schedule

import edu.watumull.presencify.core.data.dto.schedule.CancelledClassDto
import edu.watumull.presencify.core.data.dto.schedule.CancelledClassListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.schedule.ClassDto
import edu.watumull.presencify.core.data.dto.schedule.ClassListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.schedule.UpcomingClassesResponseDto
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.CourseType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface RemoteClassSessionDataSource {

    suspend fun getStudentUpcomingClasses(): Result<UpcomingClassesResponseDto, DataError.Remote>

    suspend fun getTeacherUpcomingClasses(): Result<UpcomingClassesResponseDto, DataError.Remote>

    suspend fun getClasses(
        searchQuery: String?,
        timetableId: String?,
        divisionId: String?,
        startTime: LocalTime?,
        endTime: LocalTime?,
        activeFrom: LocalDate?,
        activeTill: LocalDate?,
        teacherId: String?,
        dayOfWeek: DayOfWeek?,
        roomId: String?,
        batchId: String?,
        courseType: CourseType?,
        courseId: String?,
        semesterId: String?,
        semesterNumber: Int?,
        academicStartYearOfSemester: Int?,
        academicEndYearOfSemester: Int?,
        branchId: String?,
        isExtraClass: Boolean?,
        page: Int?,
        limit: Int?,
        getAll: Boolean?
    ): Result<ClassListWithTotalCountDto, DataError.Remote>

    suspend fun addClass(
        teacherId: String,
        startTime: LocalTime,
        endTime: LocalTime,
        dayOfWeek: DayOfWeek,
        roomId: String,
        batchId: String?,
        activeFrom: LocalDate,
        activeTill: LocalDate,
        courseId: String,
        timetableId: String
    ): Result<ClassDto, DataError.Remote>

    suspend fun getClassById(classId: String): Result<ClassDto, DataError.Remote>

    suspend fun editActiveDatesOfClass(
        classId: String,
        newActiveFrom: LocalDate,
        newActiveTill: LocalDate
    ): Result<ClassDto, DataError.Remote>

    suspend fun removeClass(classId: String): Result<Unit, DataError.Remote>

    suspend fun addExtraClass(
        teacherId: String,
        startTime: LocalTime,
        endTime: LocalTime,
        dayOfWeek: DayOfWeek,
        roomId: String,
        batchId: String?,
        activeFrom: LocalDate,
        activeTill: LocalDate,
        courseId: String,
        timetableId: String
    ): Result<ClassDto, DataError.Remote>

    suspend fun getCancelledClasses(
        divisionId: String?,
        batchId: String?,
        date: LocalDate?,
        page: Int?,
        limit: Int?,
        getAll: Boolean?
    ): Result<CancelledClassListWithTotalCountDto, DataError.Remote>

    suspend fun cancelClass(
        classId: String,
        date: LocalDate,
        reason: String?
    ): Result<CancelledClassDto, DataError.Remote>

//    suspend fun bulkCreateClasses(classes: List<Map<String, Any>>): Result<List<ClassDto>, DataError.Remote>

    suspend fun bulkDeleteClasses(classIds: List<String>): Result<Unit, DataError.Remote>

    suspend fun bulkCreateClassesFromCSV(): Result<List<ClassDto>, DataError.Remote>
}
