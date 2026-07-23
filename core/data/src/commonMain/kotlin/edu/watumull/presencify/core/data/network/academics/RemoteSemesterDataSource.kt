package edu.watumull.presencify.core.data.network.academics

import edu.watumull.presencify.core.data.dto.academics.SemesterCoursesDto
import edu.watumull.presencify.core.data.dto.academics.SemesterDto
import edu.watumull.presencify.core.data.dto.academics.SemesterListWithTotalCountDto
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import kotlinx.datetime.LocalDate

interface RemoteSemesterDataSource {
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
    ): Result<SemesterListWithTotalCountDto, DataError.Remote>

    suspend fun addSemester(
        branchId: String,
        semesterNumber: SemesterNumber,
        academicStartYear: Int,
        academicEndYear: Int,
        startDate: LocalDate,
        endDate: LocalDate,
        schemeId: String
    ): Result<SemesterDto, DataError.Remote>

    suspend fun getSemesterById(id: String): Result<SemesterDto, DataError.Remote>

    suspend fun updateSemester(
        id: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<SemesterDto, DataError.Remote>

    suspend fun removeSemester(id: String): Result<Unit, DataError.Remote>

    suspend fun getCoursesOfSemester(semesterId: String): Result<SemesterCoursesDto, DataError.Remote>

    suspend fun bulkCreateSemesters(semesters: List<Map<String, Any>>): Result<List<SemesterDto>, DataError.Remote>

    suspend fun bulkDeleteSemesters(semesterIds: List<String>): Result<Unit, DataError.Remote>
}
