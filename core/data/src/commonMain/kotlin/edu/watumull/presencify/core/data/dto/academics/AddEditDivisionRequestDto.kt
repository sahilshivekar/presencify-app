package edu.watumull.presencify.core.data.dto.academics

import kotlinx.serialization.Serializable

@Serializable
data class AddEditDivisionRequestDto(
    val divisionCode: String,
    val semesterId: String? = null,
    val optionalCourseIds: List<String>? = null
)
