package edu.watumull.presencify.core.data.dto.teacher

import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.TeacherRole
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTeacherRequestDto(
    val id: String,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val role: TeacherRole? = null,
    val gender: Gender? = null,
    val highestQualification: String? = null,
    val phoneNumber: String? = null,
    val isActive: Boolean? = null,
)
