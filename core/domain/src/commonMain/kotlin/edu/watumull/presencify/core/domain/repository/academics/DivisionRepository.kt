package edu.watumull.presencify.core.domain.repository.academics

import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.SemesterNumber
import edu.watumull.presencify.core.domain.model.academics.Division
import edu.watumull.presencify.core.domain.model.academics.DivisionListWithTotalCount
import edu.watumull.presencify.core.domain.model.academics.DivisionCourses

interface DivisionRepository {
    suspend fun getDivisions(
        searchQuery: String? = null,
        semesterNumber: SemesterNumber? = null,
        branchId: String? = null,
        semesterId: String? = null,
        academicStartYear: Int? = null,
        academicEndYear: Int? = null,
        page: Int? = null,
        limit: Int? = null,
        getAll: Boolean? = null
    ): Result<DivisionListWithTotalCount, DataError.Remote>

    suspend fun addDivision(
        divisionCode: String,
        semesterId: String,
        optionalCourseIds: List<String>? = null
    ): Result<Division, DataError.Remote>

    suspend fun getDivisionById(id: String): Result<Division, DataError.Remote>

    suspend fun updateDivision(
        id: String,
        divisionCode: String? = null,
        optionalCourseIds: List<String>? = null
    ): Result<Division, DataError.Remote>

    suspend fun removeDivision(id: String): Result<Unit, DataError.Remote>

    suspend fun getCoursesOfDivision(divisionId: String): Result<DivisionCourses, DataError.Remote>
}
