package edu.watumull.presencify.core.data.network.student

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.student.StudentBatchDto
import edu.watumull.presencify.core.data.dto.student.StudentBiometricsDto
import edu.watumull.presencify.core.data.dto.student.StudentDivisionDto
import edu.watumull.presencify.core.data.dto.student.StudentDto
import edu.watumull.presencify.core.data.dto.student.StudentListWithTotalCountDto
import edu.watumull.presencify.core.data.dto.student.StudentSemesterDto
import edu.watumull.presencify.core.data.dto.student.request.AddStudentToDivisionRequest
import edu.watumull.presencify.core.data.dto.student.request.AddStudentToSemesterRequest
import edu.watumull.presencify.core.data.dto.student.request.ChangeStudentBatchRequest
import edu.watumull.presencify.core.data.dto.student.request.ChangeStudentDivisionRequest
import edu.watumull.presencify.core.data.dto.student.request.RevertAddStudentToDivisionRequest
import edu.watumull.presencify.core.data.dto.student.request.UpdateStudentDetailsRequest
import edu.watumull.presencify.core.data.dto.student.request.UpdateStudentPasswordRequest
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.ADD_STUDENT
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.ADD_STUDENT_TO_BATCH
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.ADD_STUDENT_TO_DIVISION
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.ADD_STUDENT_TO_SEMESTER
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.BULK_CREATE_STUDENTS_FROM_CSV
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.CHANGE_STUDENT_BATCH
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.CHANGE_STUDENT_DIVISION
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENTS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENT_BATCHES
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENT_BIOMETRICS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENT_BY_ID
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENT_DIVISIONS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.GET_STUDENT_SEMESTERS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REMOVE_STUDENT
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REMOVE_STUDENT_FROM_SEMESTER
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REMOVE_STUDENT_IMAGE
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REVERT_ADD_STUDENT_TO_BATCH
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REVERT_ADD_STUDENT_TO_DIVISION
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REVERT_CHANGE_STUDENT_BATCH
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REVERT_CHANGE_STUDENT_DIVISION
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.SUBMIT_BIOMETRICS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.UPDATE_STUDENT_DETAILS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.UPDATE_STUDENT_IMAGE
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.UPDATE_STUDENT_PASSWORD
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.VERIFY_STUDENT_BIOMETRICS
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.data.util.FileMimeUtils
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.AdmissionType
import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json

class KtorRemoteStudentDataSource(
    private val clientProvider: HttpClientProvider,
) : RemoteStudentDataSource {


    override suspend fun getStudents(
        searchQuery: String?,
        branchIds: List<String>?,
        semesterNumbers: List<Int>?,
        academicStartYearOfSemester: Int?,
        academicEndYearOfSemester: Int?,
        semesterId: String?,
        batchId: String?,
        schemeId: String?,
        divisionId: String?,
        dropoutAcademicStartYear: Int?,
        dropoutAcademicEndYear: Int?,
        admissionTypes: List<AdmissionType>?,
        admissionYear: Int?,
        currentBatch: Boolean?,
        currentDivision: Boolean?,
        currentSemester: Boolean?,
        divisionCode: String?,
        batchCode: String?,
        page: Int?,
        limit: Int?,
        getAll: Boolean?,
        intention: String?,
    ): Result<StudentListWithTotalCountDto, DataError.Remote> {
        return safeCall<StudentListWithTotalCountDto> {
            clientProvider.getClient().get(GET_STUDENTS) {
                searchQuery?.let { parameter("searchQuery", it) }
                // Send array parameters - each item individually with same parameter name
                branchIds?.forEach { parameter("branchIds", it) }
                semesterNumbers?.forEach { parameter("semesterNumbers", it) }
                academicStartYearOfSemester?.let { parameter("academicStartYearOfSemester", it) }
                academicEndYearOfSemester?.let { parameter("academicEndYearOfSemester", it) }
                semesterId?.let { parameter("semesterId", it) }
                batchId?.let { parameter("batchId", it) }
                schemeId?.let { parameter("schemeId", it) }
                divisionId?.let { parameter("divisionId", it) }
                dropoutAcademicStartYear?.let { parameter("dropoutAcademicStartYear", it) }
                dropoutAcademicEndYear?.let { parameter("dropoutAcademicEndYear", it) }
                // Send admissionTypes array - each item individually with same parameter name
                admissionTypes?.forEach { parameter("admissionTypes", it.value) }
                admissionYear?.let { parameter("admissionYear", it) }
                currentBatch?.let { parameter("currentBatch", it) }
                currentDivision?.let { parameter("currentDivision", it) }
                currentSemester?.let { parameter("currentSemester", it) }
                divisionCode?.let { parameter("divisionCode", it) }
                batchCode?.let { parameter("batchCode", it) }
                page?.let { parameter("page", it) }
                limit?.let { parameter("limit", it) }
                getAll?.let { parameter("getAll", it) }
                intention?.let { parameter("intention", it) }
            }
        }
    }

    override suspend fun addStudent(
        prn: String,
        firstName: String,
        middleName: String?,
        lastName: String,
        email: String,
        phoneNumber: String,
        gender: Gender,
        dob: String?,
        schemeId: String,
        admissionYear: Int,
        admissionType: AdmissionType,
        branchId: String,
        parentEmail: String?,
        studentImage: ByteArray?,
    ): Result<StudentDto, DataError.Remote> {


        return safeCall<StudentDto> {
            clientProvider.getClient().post(ADD_STUDENT) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("prn", prn)
                            append("firstName", firstName)
                            middleName?.let { append("middleName", it) }
                            append("lastName", lastName)
                            append("email", email)
                            append("phoneNumber", phoneNumber)
                            append("gender", gender.value)
                            dob?.let { append("dob", it) }
                            append("schemeId", schemeId)
                            append("admissionYear", admissionYear.toString())
                            append("admissionType", admissionType.value)
                            append("branchId", branchId)
                            parentEmail?.let { append("parentEmail", it) }

                            studentImage?.let {
                                val mimeType = FileMimeUtils.getMimeType(studentImage)
                                val extension = FileMimeUtils.getExtensionFromMime(mimeType)
                                append("studentImageFile", studentImage, Headers.build {
                                    append(HttpHeaders.ContentType, mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"student_new.$extension\""
                                    )
                                })
                            }
                        }
                    )
                )
            }
        }
    }

    override suspend fun getStudentById(id: String): Result<StudentDto, DataError.Remote> {
        return safeCall<StudentDto> {
            clientProvider.getClient().get("${GET_STUDENT_BY_ID}/$id")
        }
    }

    override suspend fun updateStudentDetails(
        id: String,
        firstName: String?,
        middleName: String?,
        lastName: String?,
        email: String?,
        gender: Gender?,
        phoneNumber: String?,
        dob: String?,
        schemeId: String?,
        branchId: String?,
        parentEmail: String?,
        prn: String?,
        admissionYear: Int?,
        admissionType: AdmissionType?,
    ): Result<StudentDto, DataError.Remote> {
        return safeCall<StudentDto> {
            clientProvider.getClient().put(UPDATE_STUDENT_DETAILS) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateStudentDetailsRequest(
                        id = id,
                        firstName = firstName,
                        middleName = middleName,
                        lastName = lastName,
                        email = email,
                        gender = gender?.value,
                        phoneNumber = phoneNumber,
                        dob = dob,
                        schemeId = schemeId,
                        branchId = branchId,
                        parentEmail = parentEmail,
                        prn = prn,
                        admissionYear = admissionYear?.toString(),
                        admissionType = admissionType?.value
                    )
                )
            }
        }
    }

    override suspend fun updateStudentPassword(
        id: String,
        password: String,
        confirmPassword: String,
    ): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().put(UPDATE_STUDENT_PASSWORD) {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateStudentPasswordRequest(
                        id = id,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                )
            }
        }
    }

    override suspend fun updateStudentImage(
        id: String,
        imageBytes: ByteArray?,
    ): Result<StudentDto, DataError.Remote> {

        return safeCall<StudentDto> {
            clientProvider.getClient().put(UPDATE_STUDENT_IMAGE) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("id", id)
                            imageBytes?.let {
                                val mimeType = FileMimeUtils.getMimeType(imageBytes)
                                val extension = FileMimeUtils.getExtensionFromMime(mimeType)
                                append("studentImageFile", imageBytes, Headers.build {
                                    append(HttpHeaders.ContentType, mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"student_$id.$extension\""
                                    )
                                })
                            }
                        }
                    )
                )
            }
        }
    }

    override suspend fun removeStudentImage(studentId: String): Result<StudentDto, DataError.Remote> {
        return safeCall<StudentDto> {
            clientProvider.getClient().delete(REMOVE_STUDENT_IMAGE) {
                parameter("studentId", studentId)
            }
        }
    }

    override suspend fun removeStudent(id: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete("${REMOVE_STUDENT}/$id")
        }
    }

    override suspend fun getStudentSemesters(id: String): Result<List<StudentSemesterDto>, DataError.Remote> {
        return safeCall<List<StudentSemesterDto>> {
            clientProvider.getClient().get("${GET_STUDENT_SEMESTERS}/$id/semesters")
        }
    }

    override suspend fun addStudentToSemester(
        studentId: String,
        semesterId: String,
    ): Result<StudentSemesterDto, DataError.Remote> {
        return safeCall<StudentSemesterDto> {
            clientProvider.getClient().post(ADD_STUDENT_TO_SEMESTER) {
                contentType(ContentType.Application.Json)
                setBody(AddStudentToSemesterRequest(studentId = studentId, semesterId = semesterId))
            }
        }
    }

    override suspend fun removeStudentFromSemester(studentSemesterId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REMOVE_STUDENT_FROM_SEMESTER) {
                parameter("studentSemesterId", studentSemesterId)
            }
        }
    }

    override suspend fun getStudentDivisions(
        id: String,
        semesterNumber: SemesterNumber?,
    ): Result<List<StudentDivisionDto>, DataError.Remote> {
        return safeCall<List<StudentDivisionDto>> {
            clientProvider.getClient().get("${GET_STUDENT_DIVISIONS}/$id/divisions") {
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
            }
        }
    }

    override suspend fun addStudentToDivision(
        studentId: String,
        divisionId: String,
    ): Result<StudentDivisionDto, DataError.Remote> {
        return safeCall<StudentDivisionDto> {
            clientProvider.getClient().post(ADD_STUDENT_TO_DIVISION) {
                contentType(ContentType.Application.Json)
                setBody(AddStudentToDivisionRequest(divisionId = divisionId, studentId = studentId))
            }
        }
    }

    override suspend fun changeStudentDivision(
        studentDivisionId: String,
        divisionId: String,
        newDivisionStartDate: LocalDate,
    ): Result<StudentDivisionDto, DataError.Remote> {
        return safeCall<StudentDivisionDto> {
            clientProvider.getClient().put(CHANGE_STUDENT_DIVISION) {
                contentType(ContentType.Application.Json)
                setBody(
                    ChangeStudentDivisionRequest(
                        studentDivisionId = studentDivisionId,
                        divisionId = divisionId,
                        newDivisionStartDate = newDivisionStartDate
                    )
                )
            }
        }
    }

    override suspend fun revertAddStudentToDivision(studentDivisionId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REVERT_ADD_STUDENT_TO_DIVISION) {
                contentType(ContentType.Application.Json)
                setBody(RevertAddStudentToDivisionRequest(studentDivisionId = studentDivisionId))
            }
        }
    }

    override suspend fun revertChangeStudentDivision(newStudentDivisionId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REVERT_CHANGE_STUDENT_DIVISION) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("newStudentDivisionId" to newStudentDivisionId))
            }
        }
    }

    override suspend fun getStudentBatches(
        id: String,
        semesterNumber: SemesterNumber?,
    ): Result<List<StudentBatchDto>, DataError.Remote> {
        return safeCall<List<StudentBatchDto>> {
            clientProvider.getClient().get("${GET_STUDENT_BATCHES}/$id/batches") {
                semesterNumber?.value?.let { parameter("semesterNumber", it) }
            }
        }
    }

    override suspend fun addStudentToBatch(
        studentId: String,
        batchId: String,
    ): Result<StudentBatchDto, DataError.Remote> {
        return safeCall<StudentBatchDto> {
            clientProvider.getClient().post(ADD_STUDENT_TO_BATCH) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("batchId" to batchId, "studentId" to studentId))
            }
        }
    }

    override suspend fun changeStudentBatch(
        studentBatchId: String,
        batchId: String,
        newBatchStartDate: LocalDate,
    ): Result<StudentBatchDto, DataError.Remote> {
        return safeCall<StudentBatchDto> {
            clientProvider.getClient().put(CHANGE_STUDENT_BATCH) {
                contentType(ContentType.Application.Json)
                setBody(
                    ChangeStudentBatchRequest(
                        studentBatchId = studentBatchId,
                        batchId = batchId,
                        newBatchStartDate = newBatchStartDate
                    )
                )
            }
        }
    }

    override suspend fun revertAddStudentToBatch(studentBatchId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REVERT_ADD_STUDENT_TO_BATCH) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("studentBatchId" to studentBatchId))
            }
        }
    }

    override suspend fun revertChangeStudentBatch(newStudentBatchId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REVERT_CHANGE_STUDENT_BATCH) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("newStudentBatchId" to newStudentBatchId))
            }
        }
    }

    override suspend fun bulkCreateStudents(students: List<Map<String, Any?>>): Result<List<StudentDto>, DataError.Remote> {
        return safeCall<List<StudentDto>> {
            clientProvider.getClient().post(ApiEndpoints.BULK_CREATE_STUDENTS) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("students" to students))
            }
        }
    }

    override suspend fun bulkDeleteStudents(studentIds: List<String>): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(ApiEndpoints.BULK_DELETE_STUDENTS) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("studentIds" to studentIds))
            }
        }
    }

    override suspend fun bulkAddStudentsToSemester(
        studentIds: List<String>,
        semesterId: String,
    ): Result<List<StudentSemesterDto>, DataError.Remote> {
        return safeCall<List<StudentSemesterDto>> {
            clientProvider.getClient().post(ApiEndpoints.BULK_ADD_STUDENTS_TO_SEMESTER) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("studentIds" to studentIds, "semesterId" to semesterId))
            }
        }
    }

    override suspend fun bulkAddStudentsToDivision(
        studentIds: List<String>,
        divisionId: String,
    ): Result<List<StudentDivisionDto>, DataError.Remote> {
        return safeCall<List<StudentDivisionDto>> {
            clientProvider.getClient().post(ApiEndpoints.BULK_ADD_STUDENTS_TO_DIVISION) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("studentIds" to studentIds, "divisionId" to divisionId))
            }
        }
    }

    override suspend fun bulkAddStudentsToBatch(
        studentIds: List<String>,
        batchId: String,
    ): Result<List<StudentBatchDto>, DataError.Remote> {
        return safeCall<List<StudentBatchDto>> {
            clientProvider.getClient().post(ApiEndpoints.BULK_ADD_STUDENTS_TO_BATCH) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("studentIds" to studentIds, "batchId" to batchId))
            }
        }
    }

    override suspend fun bulkCreateStudentsFromCSV(csvData: ByteArray): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().post(BULK_CREATE_STUDENTS_FROM_CSV) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("csvFile", csvData, Headers.build {
                                append(HttpHeaders.ContentType, "text/csv")
                                append(HttpHeaders.ContentDisposition, "filename=\"students.csv\"")
                            })
                        }
                    )
                )
            }
        }
    }

    override suspend fun submitBiometrics(
        images: List<ByteArray>,
        faceDescriptor: List<Float>,
    ): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().post(SUBMIT_BIOMETRICS) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            images.forEachIndexed { index, imageBytes ->
                                val mimeType = FileMimeUtils.getMimeType(imageBytes)
                                val extension = FileMimeUtils.getExtensionFromMime(mimeType) ?: "jpg"
                                append("biometricImages", imageBytes, Headers.build {
                                    append(HttpHeaders.ContentType, mimeType)
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"biometric_$index.$extension\""
                                    )
                                })
                            }
                            append("faceDescriptor", Json.encodeToString(faceDescriptor))
                        }
                    )
                )

            }
        }
    }

    override suspend fun getStudentBiometrics(studentId: String): Result<StudentBiometricsDto, DataError.Remote> {
        return safeCall<StudentBiometricsDto> {
            clientProvider.getClient().get("$GET_STUDENT_BIOMETRICS/$studentId/biometrics")
        }
    }

    override suspend fun verifyStudentBiometrics(studentId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().patch("$VERIFY_STUDENT_BIOMETRICS/$studentId/biometrics/verify") {
                contentType(ContentType.Application.Json)
                setBody("{}")
            }
        }
    }
}
