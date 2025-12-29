package edu.watumull.presencify.core.domain.model.auth

data class VerificationCode(
    val id: String,
    val email: String,
    val code: String,
    val purpose: String,
    val expiresAt: String
)