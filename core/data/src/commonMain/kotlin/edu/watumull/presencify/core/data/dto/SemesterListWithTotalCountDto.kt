package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SemesterListWithTotalCountDto(
    val semesters: List<SemesterDto>,
    val totalCount: Int
)
