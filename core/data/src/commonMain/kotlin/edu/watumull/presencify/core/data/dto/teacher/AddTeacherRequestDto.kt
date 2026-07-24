package edu.watumull.presencify.core.data.dto.teacher

import edu.watumull.presencify.core.domain.enums.Gender
import edu.watumull.presencify.core.domain.enums.TeacherRole
import kotlinx.serialization.Serializable

@Serializable
data class AddTeacherRequestDto(
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val gender: Gender,
    val highestQualification: String? = null,
    val role: TeacherRole,
    val isActive: Boolean? = null,
)
