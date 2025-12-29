package edu.watumull.presencify.core.data.mapper

import edu.watumull.presencify.core.data.dto.BranchDto
import edu.watumull.presencify.core.domain.model.academics.Branch

fun BranchDto.toDomain(): Branch = Branch(
    id = id,
    name = name,
    abbreviation = abbreviation,
    semesters = semesters?.map { it.toDomain() },
    students = students?.map { it.toDomain() },
    branchCourseSemesters = branchCourseSemesters?.map { it.toDomain() }
)
