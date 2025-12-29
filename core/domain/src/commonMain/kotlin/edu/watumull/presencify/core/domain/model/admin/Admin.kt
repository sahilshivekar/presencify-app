package edu.watumull.presencify.core.domain.model.admin

data class Admin(
    val id: String,
    val email: String,
    val username: String,
    val password: String?,
    val refreshToken: String?,
    val isVerified: Boolean
)