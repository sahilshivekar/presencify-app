package edu.watumull.presencify.core.data.mapper.academics

import edu.watumull.presencify.core.data.dto.academics.BranchCourseSemesterDto
import edu.watumull.presencify.core.domain.model.academics.BranchCourseSemester

fun BranchCourseSemesterDto.toDomain(): BranchCourseSemester =
    BranchCourseSemester(
        id = id,
        branchId = branchId,
        courseId = courseId,
        semesterNumber = semesterNumber,
        branch = branch?.toDomain(),
        course = course?.toDomain()
    )
