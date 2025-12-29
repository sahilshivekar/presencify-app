package edu.watumull.presencify.core.domain.model.academics

data class BranchCourseSemester(
    val id: String,
    val branchId: String,
    val courseId: String,
    val semesterNumber: Int,
    val branch: Branch? = null,
    val course: Course? = null
)
