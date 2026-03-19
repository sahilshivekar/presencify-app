package edu.watumull.presencify.core.domain.repository.auth

import edu.watumull.presencify.core.domain.model.auth.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUserDetails(role: UserRole, userId: String)
    fun getUserRole(): Flow<UserRole?>
    fun getUserId(): Flow<String?>
    suspend fun clearUserDetails()
}
