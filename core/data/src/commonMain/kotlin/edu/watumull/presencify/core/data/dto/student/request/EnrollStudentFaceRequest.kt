package edu.watumull.presencify.core.data.dto.student.request

import kotlinx.serialization.Serializable

@Serializable
data class EnrollStudentFaceRequest(
    val studentId: String,
    val faceDescriptor: List<Float>
)