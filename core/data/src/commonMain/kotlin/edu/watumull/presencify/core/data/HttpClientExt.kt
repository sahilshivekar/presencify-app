package edu.watumull.presencify.core.data

import edu.watumull.presencify.core.data.dto.ApiResponseDto
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.io.IOException

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse,
): Result<T?, DataError.Remote> {
    val response = try {
        execute()
    } catch (e: IOException) { // Covers SocketTimeoutException and UnresolvedAddressException
        return Result.Error(DataError.Remote.NO_INTERNET)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        return Result.Error(DataError.Remote.UNKNOWN)
    }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse,
): Result<T?, DataError.Remote> {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                val apiResponse = response.body<ApiResponseDto<T>>()
                Result.Success(apiResponse.data)
            } catch (e: NoTransformationFoundException) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
        400 -> Result.Error(DataError.Remote.VALIDATION_ERROR("Validation Error"))
        401 -> Result.Error(DataError.Remote.UNAUTHORIZED)
        408 -> Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        429 -> Result.Error(DataError.Remote.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Remote.SERVER_ERROR)
        else -> Result.Error(DataError.Remote.UNKNOWN)
    }
}
