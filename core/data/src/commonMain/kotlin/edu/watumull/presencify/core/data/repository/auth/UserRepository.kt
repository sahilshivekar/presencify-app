package edu.watumull.presencify.core.data.repository.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import edu.watumull.presencify.core.data.Constants
import edu.watumull.presencify.core.domain.model.auth.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val dataStore: DataStore<Preferences>
) {

    private val userRoleKey = stringPreferencesKey(Constants.USER_ROLE_KEY)
    private val userIdKey = stringPreferencesKey(Constants.USER_ID_KEY)

    suspend fun saveUserDetails(role: UserRole, userId: String) {
        dataStore.edit { preferences ->
            preferences[userRoleKey] = role.name
            preferences[userIdKey] = userId
        }
    }

    fun getUserRole(): Flow<UserRole?> {
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

    fun getUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[userIdKey]
        }
    }

    suspend fun clearUserDetails() {
        dataStore.edit { preferences ->
            preferences.remove(userRoleKey)
            preferences.remove(userIdKey)
        }
    }
}