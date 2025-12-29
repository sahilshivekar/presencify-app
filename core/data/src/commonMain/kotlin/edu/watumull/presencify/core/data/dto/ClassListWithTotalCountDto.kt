package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClassListWithTotalCountDto(
    val classes: List<ClassDto>,
    val totalCount: Int
)
