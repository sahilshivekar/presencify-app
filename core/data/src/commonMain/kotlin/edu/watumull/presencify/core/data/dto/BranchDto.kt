package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BranchDto(
    val id: String,
    val name: String,
    val abbreviation: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Semesters")
    val semesters: List<SemesterDto>? = null,
    @SerialName("Students")
    val students: List<StudentDto>? = null,
    @SerialName("BranchCourseSemesters")
    val branchCourseSemesters: List<BranchCourseSemesterDto>? = null
)
