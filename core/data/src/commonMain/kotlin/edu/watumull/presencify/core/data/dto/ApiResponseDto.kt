package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val statusCode: Int,
    val message: String,
    val data: T? = null,
    val success: Boolean = statusCode in 200..299
)
