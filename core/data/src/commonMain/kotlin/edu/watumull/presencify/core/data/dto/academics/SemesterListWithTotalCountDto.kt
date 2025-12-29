package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.Serializable

@Serializable
data class SemesterListWithTotalCountDto(
    val semesters: List<SemesterDto>,
    val totalCount: Int
)
