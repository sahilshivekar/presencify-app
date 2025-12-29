package edu.watumull.presencify.core.domain.model.academics

data class CourseListWithTotalCount(
    val courses: List<Course>,
    val totalCount: Int
)