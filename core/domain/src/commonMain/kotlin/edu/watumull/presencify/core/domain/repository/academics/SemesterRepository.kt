package edu.watumull.presencify.core.domain.repository.academics

import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Semester
import edu.watumull.presencify.core.domain.model.academics.SemesterCourses
import edu.watumull.presencify.core.domain.model.academics.SemesterListWithTotalCount
import kotlinx.datetime.LocalDate

interface SemesterRepository {
    suspend fun getSemesters(
        semesterNumber: SemesterNumber? = null,
        academicStartYear: Int? = null,
        academicEndYear: Int? = null,
        branchId: String? = null,
        schemeId: String? = null,
        page: Int? = null,
        limit: Int? = null,
        getAll: Boolean? = null,
        isEven: Boolean? = true,
        isOdd: Boolean? = true
    ): Result<SemesterListWithTotalCount, DataError.Remote>

    suspend fun addSemester(
        branchId: String,
        semesterNumber: SemesterNumber,
        academicStartYear: Int,
        academicEndYear: Int,
        startDate: LocalDate,
        endDate: LocalDate,
        schemeId: String
    ): Result<Semester, DataError.Remote>

    suspend fun getSemesterById(id: String): Result<Semester, DataError.Remote>

    suspend fun updateSemester(
        id: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Semester, DataError.Remote>

    suspend fun removeSemester(id: String): Result<Unit, DataError.Remote>

    suspend fun getCoursesOfSemester(semesterId: String): Result<SemesterCourses, DataError.Remote>

    suspend fun bulkCreateSemesters(semesters: List<Map<String, Any>>): Result<List<Semester>, DataError.Remote>

    suspend fun bulkDeleteSemesters(semesterIds: List<String>): Result<Unit, DataError.Remote>

    suspend fun getOngoingSemesters(): Result<SemesterListWithTotalCount, DataError.Remote>
}
