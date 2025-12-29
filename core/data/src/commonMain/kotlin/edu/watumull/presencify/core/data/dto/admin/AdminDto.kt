package edu.watumull.presencify.core.data.dto.admin

import kotlinx.serialization.Serializable

@Serializable
data class AdminDto(
    val id: String,
    val email: String,
    val username: String,
    val isVerified: Boolean = false,
    val password: String? = null,
    val refreshToken: String? = null,
    val createdAt: String,
    val updatedAt: String
)
