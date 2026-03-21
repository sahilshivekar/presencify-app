package edu.watumull.presencify.core.data.network.attendance

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.attendance.AggregatedAttendanceDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceStudentAggregatedAndDetailedAttendanceDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceStudentDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceSummaryDto
import edu.watumull.presencify.core.data.dto.attendance.AttendanceWithTotalCountDto
import edu.watumull.presencify.core.data.dto.attendance.CreateAttendanceRequestDto
import edu.watumull.presencify.core.data.dto.attendance.UpdateStudentAttendanceRequestDto
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.BULK_UPDATE_STUDENT_ATTENDANCE
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.CREATE_ATTENDANCE
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ACTIVE_ATTENDANCE_SHEET
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ATTENDANCES
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ATTENDANCE_BY_ID
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ATTENDANCE_OF_ALL
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ATTENDANCE_OF_SELF
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.GET_ATTENDANCE_OF_STUDENT
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.REMOVE_ATTENDANCE
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.SEND_ATTENDANCE_REPORT
import edu.watumull.presencify.core.data.network.attendance.ApiEndpoints.UPDATE_STUDENT_ATTENDANCE
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDate

class KtorRemoteAttendanceDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteAttendanceDataSource {
    

    override suspend fun createAttendance(classId: String, date: LocalDate): Result<AttendanceDto, DataError.Remote> {
        return safeCall<AttendanceDto> {
            clientProvider.getClient().post(CREATE_ATTENDANCE) {
                contentType(ContentType.Application.Json)
                setBody(CreateAttendanceRequestDto(classId = classId, date = date))
            }
        }
    }


    override suspend fun updateStudentAttendance(
        attendanceId: String,
        studentId: String,
        newAttendanceStatus: Boolean
    ): Result<AttendanceStudentDto, DataError.Remote> {
        return safeCall<AttendanceStudentDto> {
            clientProvider.getClient().put(UPDATE_STUDENT_ATTENDANCE) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateStudentAttendanceRequestDto(
                        attendanceId = attendanceId,
                        studentId = studentId,
                        newAttendanceStatus = newAttendanceStatus
                    )
                )
            }
        }
    }

    override suspend fun bulkUpdateStudentAttendance(attendanceUpdates: List<Map<String, Any>>): Result<Map<String, Any>, DataError.Remote> {
        return safeCall<Map<String, Any>> {
            clientProvider.getClient().put(BULK_UPDATE_STUDENT_ATTENDANCE) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("attendanceUpdates" to attendanceUpdates))
            }
        }
    }

    override suspend fun removeAttendance(attendanceId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REMOVE_ATTENDANCE) {
                parameter("attendanceId", attendanceId)
            }
        }
    }

    override suspend fun getAttendanceOfAnyStudentForSpecificCourseInSemester(
        studentId: String,
        courseId: String,
        semesterId: String?,
        divisionId: String?,
        batchId: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        semesterNumber: SemesterNumber?,
        academicStartYear: Int?,
        academicEndYear: Int?,
        branchId: String?,
        schemeId: String?
    ): Result<AttendanceStudentAggregatedAndDetailedAttendanceDto, DataError.Remote> {
        return safeCall<AttendanceStudentAggregatedAndDetailedAttendanceDto> {
            clientProvider.getClient().get(GET_ATTENDANCE_OF_STUDENT) {
                parameter("studentId", studentId)
                parameter("courseId", courseId)
                semesterId?.let { parameter("semesterId", it) }
                divisionId?.let { parameter("divisionId", it) }
                batchId?.let { parameter("batchId", it) }
                startDate?.let { parameter("startDate", it) }
                endDate?.let { parameter("endDate", it) }
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
                academicStartYear?.let { parameter("academicStartYear", it) }
                academicEndYear?.let { parameter("academicEndYear", it) }
                branchId?.let { parameter("branchId", it) }
                schemeId?.let { parameter("schemeId", it) }
            }
        }
    }

    override suspend fun getAttendanceOfSelfForSpecificCourseInSemester(
        courseId: String,
        semesterId: String?,
        divisionId: String?,
        batchId: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        semesterNumber: SemesterNumber?,
        academicStartYear: Int?,
        academicEndYear: Int?,
        branchId: String?,
        schemeId: String?
    ): Result<AttendanceStudentAggregatedAndDetailedAttendanceDto, DataError.Remote> {
        return safeCall<AttendanceStudentAggregatedAndDetailedAttendanceDto> {
            clientProvider.getClient().get(GET_ATTENDANCE_OF_SELF) {
                parameter("courseId", courseId)
                semesterId?.let { parameter("semesterId", it) }
                divisionId?.let { parameter("divisionId", it) }
                batchId?.let { parameter("batchId", it) }
                startDate?.let { parameter("startDate", it) }
                endDate?.let { parameter("endDate", it) }
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
                academicStartYear?.let { parameter("academicStartYear", it) }
                academicEndYear?.let { parameter("academicEndYear", it) }
                branchId?.let { parameter("branchId", it) }
                schemeId?.let { parameter("schemeId", it) }
            }
        }

//        return when (result) {
//            is Result.Success -> {
//                val aggregated = result.data.aggregatedAttendance.firstOrNull()
//                if (aggregated != null) {
//                    Result.Success(aggregated)
//                } else {
//                    // No attendance data for this course
//                    Result.Error(DataError.Remote.Unknown)
//                }
//            }
//            is Result.Error -> result
//        }
    }

    override suspend fun getAttendanceOfAllForSemesterDivisionBatchCourse(
        semesterId: String?,
        divisionId: String?,
        batchId: String?,
        courseId: String?,
        startDate: LocalDate?,
        endDate: LocalDate?,
        semesterNumber: SemesterNumber?,
        academicStartYear: Int?,
        academicEndYear: Int?,
        branchId: String?,
        schemeId: String?
    ): Result<List<AttendanceSummaryDto>, DataError.Remote> {
        return safeCall<List<AttendanceSummaryDto>> {
            clientProvider.getClient().get(GET_ATTENDANCE_OF_ALL) {
                semesterId?.let { parameter("semesterId", it) }
                divisionId?.let { parameter("divisionId", it) }
                batchId?.let { parameter("batchId", it) }
                courseId?.let { parameter("courseId", it) }
                startDate?.let { parameter("startDate", it) }
                endDate?.let { parameter("endDate", it) }
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
                academicStartYear?.let { parameter("academicStartYear", it) }
                academicEndYear?.let { parameter("academicEndYear", it) }
                branchId?.let { parameter("branchId", it) }
                schemeId?.let { parameter("schemeId", it) }
            }
        }
    }

    override suspend fun sendAttendanceReport(
        startDate: LocalDate?,
        endDate: LocalDate?,
        studentIds: List<String>?,
        courseIds: List<String>?,
        semesterId: String?
    ): Result<Map<String, Any>, DataError.Remote> {
        return safeCall<Map<String, Any>> {
            clientProvider.getClient().post(SEND_ATTENDANCE_REPORT) {
                contentType(ContentType.Application.Json)
                setBody(
                    mapOf(
                        "startDate" to startDate,
                        "endDate" to endDate,
                        "studentIds" to studentIds,
                        "courseIds" to courseIds,
                        "semesterId" to semesterId
                    )
                )
            }
        }
    }

    override suspend fun getAttendanceById(attendanceId: String): Result<AttendanceDto, DataError.Remote> {
        return safeCall<AttendanceDto> {
            clientProvider.getClient().get("$GET_ATTENDANCE_BY_ID/$attendanceId")
        }
    }

    override suspend fun getAttendances(
        date: LocalDate?,
        classId: String?,
        studentId: String?,
        courseId: String?,
        semesterId: String?,
        divisionId: String?,
        batchId: String?,
        semesterNumber: SemesterNumber?,
        academicStartYear: Int?,
        academicEndYear: Int?,
        branchId: String?,
        page: Int,
        limit: Int
    ): Result<AttendanceWithTotalCountDto, DataError.Remote> {
        return safeCall<AttendanceWithTotalCountDto> {
            clientProvider.getClient().get(GET_ATTENDANCES) {
                date?.let { parameter("date", it) }
                classId?.let { parameter("classId", it) }
                studentId?.let { parameter("studentId", it) }
                courseId?.let { parameter("courseId", it) }
                semesterId?.let { parameter("semesterId", it) }
                divisionId?.let { parameter("divisionId", it) }
                batchId?.let { parameter("batchId", it) }
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
                academicStartYear?.let { parameter("academicStartYear", it) }
                academicEndYear?.let { parameter("academicEndYear", it) }
                branchId?.let { parameter("branchId", it) }
                parameter("page", page)
                parameter("limit", limit)
            }
        }
    }

    override suspend fun getActiveAttendanceSheet(
        studentId: String,
        divisionId: String
    ): Result<List<AttendanceDto>, DataError.Remote> {
        return safeCall<List<AttendanceDto>> {
            clientProvider.getClient().get(GET_ACTIVE_ATTENDANCE_SHEET) {
                parameter("studentId", studentId)
                parameter("divisionId", divisionId)
            }
        }
    }
}