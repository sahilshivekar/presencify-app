package edu.watumull.presencify.core.data.network.admin

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.admin.AdminDto
import edu.watumull.presencify.core.data.network.admin.ApiEndpoints.ADD_ADMIN
import edu.watumull.presencify.core.data.network.admin.ApiEndpoints.GET_ADMINS
import edu.watumull.presencify.core.data.network.admin.ApiEndpoints.GET_ADMIN_DETAILS
import edu.watumull.presencify.core.data.network.admin.ApiEndpoints.REMOVE_ADMIN
import edu.watumull.presencify.core.data.network.admin.ApiEndpoints.UPDATE_ADMIN_DETAILS
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.enums.admin.AdminSortBy
import edu.watumull.presencify.core.domain.enums.admin.AdminSortOrder
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteAdminDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteAdminDataSource {
    

    override suspend fun addAdmin(email: String, username: String, password: String): Result<AdminDto, DataError.Remote> {
        return safeCall<AdminDto> {
            clientProvider.getClient().post(ADD_ADMIN) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "username" to username, "password" to password))
            }
        }
    }

    override suspend fun updateAdminDetails(email: String, username: String): Result<AdminDto, DataError.Remote> {
        return safeCall<AdminDto> {
            clientProvider.getClient().put(UPDATE_ADMIN_DETAILS) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "username" to username))
            }
        }
    }

    override suspend fun removeAdmin(): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REMOVE_ADMIN)
        }
    }

    override suspend fun getAdmins(searchQuery: String?, sortBy: AdminSortBy, sortOrder: AdminSortOrder, page: Int, limit: Int): Result<List<AdminDto>, DataError.Remote> {
        return safeCall<List<AdminDto>> {
            clientProvider.getClient().get(GET_ADMINS) {
                parameter("searchQuery", searchQuery)
                parameter("sortBy", sortBy)
                parameter("sortOrder", sortOrder)
                parameter("page", page)
                parameter("limit", limit)
            }
        }
    }

    override suspend fun getAdminDetails(): Result<AdminDto, DataError.Remote> {
        return safeCall<AdminDto> {
            clientProvider.getClient().get(GET_ADMIN_DETAILS)
        }
    }
}