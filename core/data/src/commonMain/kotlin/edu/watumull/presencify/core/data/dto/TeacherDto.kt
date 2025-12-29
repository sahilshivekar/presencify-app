package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherDto(
    val id: String,
    val firstName: String,
    val middleName: String? = null,
    val lastName: String,
    val teacherImageUrl: String? = null,
    val teacherImagePublicId: String? = null,
    val email: String,
    val phoneNumber: String,
    val gender: String,
    val highestQualification: String? = null,
    val role: String,
    val password: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val isActive: Boolean = true,
    val refreshToken: String? = null,
    @SerialName("Classes")
    val classes: List<ClassDto>? = null,
    @SerialName("TeacherTeachesCourses")
    val teacherTeachesCourses: List<TeacherTeachesCourseDto>? = null
)
