package edu.watumull.presencify.core.data.dto.academics

import edu.watumull.presencify.core.data.dto.student.StudentDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchemeDto(
    val id: String,
    val name: String,
    val universityId: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("University")
    val university: UniversityDto? = null,
    @SerialName("Semesters")
    val semesters: List<SemesterDto>? = null,
    @SerialName("Students")
    val students: List<StudentDto>? = null,
    @SerialName("Courses")
    val courses: List<CourseDto>? = null
)
