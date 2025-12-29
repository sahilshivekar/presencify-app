package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.BranchCourseSemesterDto
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
