package edu.watumull.presencify.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerificationCodeDto(
    val id: String,
    val email: String,
    val code: String,
    val purpose: String,
    val expiresAt: String,
    val createdAt: String,
    val updatedAt: String
)
