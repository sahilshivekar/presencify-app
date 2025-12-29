package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentBatchDto(
    val id: String,
    val studentId: String,
    val batchId: String,
    val fromDate: String,
    val toDate: String? = null,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("Student")
    val student: StudentDto? = null,
    @SerialName("Batch")
    val batch: BatchDto? = null
)
