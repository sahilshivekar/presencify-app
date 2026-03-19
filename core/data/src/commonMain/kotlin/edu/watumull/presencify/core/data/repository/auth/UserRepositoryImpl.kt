package edu.watumull.presencify.core.data.repository.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import edu.watumull.presencify.core.data.Constants
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserRepository {

    private val userRoleKey = stringPreferencesKey(Constants.USER_ROLE_KEY)
    private val userIdKey = stringPreferencesKey(Constants.USER_ID_KEY)

    override suspend fun saveUserDetails(role: UserRole, userId: String) {
        dataStore.edit { preferences ->
            preferences[userRoleKey] = role.name
            preferences[userIdKey] = userId
        }
    }

    override fun getUserRole(): Flow<UserRole?> {
        return dataStore.data.map { preferences ->
            preferences[userRoleKey]?.let { roleString ->
                try {
                    UserRole.valueOf(roleString)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    override fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[userIdKey]
        }
    }

    override suspend fun clearUserDetails() {
        dataStore.edit { preferences ->
            preferences.remove(userRoleKey)
            preferences.remove(userIdKey)
        }
    }
}