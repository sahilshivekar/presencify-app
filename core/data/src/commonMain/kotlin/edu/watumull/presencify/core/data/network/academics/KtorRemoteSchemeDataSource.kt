package edu.watumull.presencify.core.data.network.academics

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.academics.SchemeDto
import edu.watumull.presencify.core.data.network.academics.ApiEndpoints.ADD_SCHEME
import edu.watumull.presencify.core.data.network.academics.ApiEndpoints.GET_SCHEMES
import edu.watumull.presencify.core.data.network.academics.ApiEndpoints.GET_SCHEME_BY_ID
import edu.watumull.presencify.core.data.network.academics.ApiEndpoints.REMOVE_SCHEME
import edu.watumull.presencify.core.data.network.academics.ApiEndpoints.UPDATE_SCHEME
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteSchemeDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteSchemeDataSource {
    

    override suspend fun getSchemes(searchQuery: String?): Result<List<SchemeDto>, DataError.Remote> {
        return safeCall<List<SchemeDto>> {
            clientProvider.getClient().get(GET_SCHEMES) {
                searchQuery?.let { parameter("searchQuery", it) }
            }
        }
    }

    override suspend fun addScheme(
        name: String,
        universityId: String
    ): Result<SchemeDto, DataError.Remote> {
        return safeCall<SchemeDto> {
            clientProvider.getClient().post(ADD_SCHEME) {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name" to name,
                    "universityId" to universityId
                ))
            }
        }
    }

    override suspend fun getSchemeById(id: String): Result<SchemeDto, DataError.Remote> {
        return safeCall<SchemeDto> {
            clientProvider.getClient().get("$GET_SCHEME_BY_ID/$id")
        }
    }

    override suspend fun updateScheme(
        id: String,
        name: String?
    ): Result<SchemeDto, DataError.Remote> {
        return safeCall<SchemeDto> {
            clientProvider.getClient().put("$UPDATE_SCHEME/$id") {
                contentType(ContentType.Application.Json)
                setBody(buildMap {
                    name?.let { put("name", it) }
                })
            }
        }
    }

    override suspend fun removeScheme(id: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete("$REMOVE_SCHEME/$id")
        }
    }
}