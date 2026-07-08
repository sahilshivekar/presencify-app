package edu.watumull.presencify.core.data.network.schedule

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.schedule.CancelledClassDto
import edu.watumull.presencify.core.data.dto.schedule.CancelledClassListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.schedule.ClassDto
import edu.watumull.presencify.core.data.dto.schedule.ClassListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.schedule.request.AddClassRequest
import edu.watumull.presencify.core.data.dto.schedule.request.BulkDeleteClassesRequest
import edu.watumull.presencify.core.data.dto.schedule.request.CancelClassRequest
import edu.watumull.presencify.core.data.dto.schedule.request.EditActiveDatesRequest
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.ADD_CLASS
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.ADD_EXTRA_CLASS
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.BULK_CREATE_CLASSES_FROM_CSV
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.BULK_DELETE_CLASSES
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.CANCEL_CLASS
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.EDIT_ACTIVE_DATES
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_CANCELLED_CLASSES
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_CLASSES
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.GET_CLASS_BY_ID
import edu.watumull.presencify.core.data.network.schedule.ApiEndpoints.REMOVE_CLASS
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.data.util.toApiTimeString
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.CourseType
import edu.watumull.presencify.core.domain.enums.DayOfWeek
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class KtorRemoteClassSessionDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteClassSessionDataSource {

    override suspend fun getStudentUpcomingClasses(): Result<ClassListWithTotalCountDto, DataError.Remote> {
        return safeCall {
            clientProvider.getClient().get("$GET_CLASSES/students/upcoming").body()
        }
    }

    override suspend fun getTeacherUpcomingClasses(): Result<ClassListWithTotalCountDto, DataError.Remote> {
        return safeCall {
            clientProvider.getClient().get("$GET_CLASSES/teachers/upcoming").body()
        }
    }

    override suspend fun getClasses(
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
    ): Result<ClassListWithTotalCountDto, DataError.Remote> {
        return safeCall<ClassListWithTotalCountDto> {
            clientProvider.getClient().get(GET_CLASSES) {
                searchQuery?.let { parameter("searchQuery", it) }
                timetableId?.let { parameter("timetableId", it) }
                divisionId?.let { parameter("divisionId", it) }
                startTime?.let { parameter("startTime", it.toApiTimeString()) }
                endTime?.let { parameter("endTime", it.toApiTimeString()) }
                activeFrom?.let { parameter("activeFrom", it.toString()) }
                activeTill?.let { parameter("activeTill", it.toString()) }
                teacherId?.let { parameter("teacherId", it) }
                dayOfWeek?.value?.let { parameter("dayOfWeek", it) }
                roomId?.let { parameter("roomId", it) }
                batchId?.let { parameter("batchId", it) }
                courseType?.value?.let { parameter("courseType", it) }
                courseId?.let { parameter("courseId", it) }
                semesterId?.let { parameter("semesterId", it) }
                semesterNumber?.let { parameter("semesterNumber", it) }
                academicStartYearOfSemester?.let { parameter("academicStartYearOfSemester", it) }
                academicEndYearOfSemester?.let { parameter("academicEndYearOfSemester", it) }
                branchId?.let { parameter("branchId", it) }
                isExtraClass?.let { parameter("isExtraClass", it) }
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                getAll?.let { parameter("getAll", it) }
            }
        }
    }

    override suspend fun addClass(
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
    ): Result<ClassDto, DataError.Remote> {
        return safeCall<ClassDto> {
            clientProvider.getClient().post(ADD_CLASS) {
                contentType(ContentType.Application.Json)
                setBody(
                    AddClassRequest(
                        teacherId = teacherId,
                        startTime = startTime.toApiTimeString(),
                        endTime = endTime.toApiTimeString(),
                        dayOfWeek = dayOfWeek.value,
                        roomId = roomId,
                        batchId = batchId,
                        activeFrom = activeFrom.toString(),
                        activeTill = activeTill.toString(),
                        courseId = courseId,
                        timetableId = timetableId
                    )
                )
            }
        }
    }

    override suspend fun getClassById(classId: String): Result<ClassDto, DataError.Remote> {
        return safeCall<ClassDto> {
            clientProvider.getClient().get("$GET_CLASS_BY_ID/$classId")
        }
    }

    override suspend fun editActiveDatesOfClass(
        classId: String,
        newActiveFrom: LocalDate,
        newActiveTill: LocalDate
    ): Result<ClassDto, DataError.Remote> {
        return safeCall<ClassDto> {
            clientProvider.getClient().put("$EDIT_ACTIVE_DATES/$classId") {
                contentType(ContentType.Application.Json)
                setBody(
                    EditActiveDatesRequest(
                        newActiveFrom = newActiveFrom.toString(),
                        newActiveTill = newActiveTill.toString()
                    )
                )
            }
        }
    }

    override suspend fun removeClass(classId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete("$REMOVE_CLASS/$classId")
        }
    }

    override suspend fun addExtraClass(
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
    ): Result<ClassDto, DataError.Remote> {
        return safeCall<ClassDto> {
            clientProvider.getClient().post(ADD_EXTRA_CLASS) {
                contentType(ContentType.Application.Json)
                setBody(
                    AddClassRequest(
                        teacherId = teacherId,
                        startTime = startTime.toApiTimeString(),
                        endTime = endTime.toApiTimeString(),
                        dayOfWeek = dayOfWeek.value,
                        roomId = roomId,
                        batchId = batchId,
                        activeFrom = activeFrom.toString(),
                        activeTill = activeTill.toString(),
                        courseId = courseId,
                        timetableId = timetableId
                    )
                )
            }
        }
    }

    override suspend fun getCancelledClasses(
        divisionId: String?,
        batchId: String?,
        date: LocalDate?,
        page: Int?,
        limit: Int?,
        getAll: Boolean?
    ): Result<CancelledClassListWithTotalCountDto, DataError.Remote> {
        return safeCall<CancelledClassListWithTotalCountDto> {
            clientProvider.getClient().get(GET_CANCELLED_CLASSES) {
                divisionId?.let { parameter("divisionId", it) }
                batchId?.let { parameter("batchId", it) }
                date?.let { parameter("date", it.toString()) }
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                getAll?.let { parameter("getAll", it) }
            }
        }
    }

    override suspend fun cancelClass(
        classId: String,
        date: LocalDate,
        reason: String?
    ): Result<CancelledClassDto, DataError.Remote> {
        return safeCall<CancelledClassDto> {
            clientProvider.getClient().post(CANCEL_CLASS) {
                contentType(ContentType.Application.Json)
                setBody(
                    CancelClassRequest(
                        classId = classId,
                        date = date.toString(),
                        reason = reason
                    )
                )
            }
        }
    }

//    override suspend fun bulkCreateClasses(classes: List<Map<String, Any>>): Result<List<ClassDto>, DataError.Remote> {
//        return safeCall<List<ClassDto>> {
//            clientProvider.getClient().post(BULK_CREATE_CLASSES) {
//                contentType(ContentType.Application.Json)
//                setBody(BulkCreateClassesRequest(classes = classes))
//            }
//        }
//    }

    override suspend fun bulkDeleteClasses(classIds: List<String>): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(BULK_DELETE_CLASSES) {
                contentType(ContentType.Application.Json)
                setBody(BulkDeleteClassesRequest(classIds = classIds))
            }
        }
    }

    override suspend fun bulkCreateClassesFromCSV(): Result<List<ClassDto>, DataError.Remote> {
        return safeCall<List<ClassDto>> {
            clientProvider.getClient().post(BULK_CREATE_CLASSES_FROM_CSV) {
                // File upload handled by middleware
            }
        }
    }
}
