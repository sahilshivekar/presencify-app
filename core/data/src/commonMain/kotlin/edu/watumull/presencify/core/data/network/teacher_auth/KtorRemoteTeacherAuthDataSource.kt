package edu.watumull.presencify.core.data.network.teacher_auth

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.auth.LoginTeacherDto
import edu.watumull.presencify.core.data.dto.auth.TokenDto
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.LOGIN_TEACHER
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.LOGOUT
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.REFRESH_TOKENS
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.SEND_VERIFICATION_CODE
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.UPDATE_PASSWORD
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints.VERIFY_CODE
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.model.auth.LoginTeacher
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteTeacherAuthDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteTeacherAuthDataSource {
    

    override suspend fun loginTeacher(email: String, password: String): Result<LoginTeacherDto, DataError.Remote> {
        return safeCall<LoginTeacherDto> {
            clientProvider.getClient().post(LOGIN_TEACHER) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }
        }
    }

    override suspend fun sendVerificationCodeToEmail(email: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().post(SEND_VERIFICATION_CODE) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }
        }
    }

    override suspend fun verifyCode(email: String, code: String): Result<LoginTeacherDto, DataError.Remote> {
        return safeCall<LoginTeacherDto> {
            clientProvider.getClient().post(VERIFY_CODE) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "code" to code))
            }
        }
    }

    override suspend fun updatePassword(password: String, confirmPassword: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().put(UPDATE_PASSWORD) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("password" to password, "confirmPassword" to confirmPassword))
            }
        }
    }

    override suspend fun refreshTokens(refreshToken: String): Result<TokenDto, DataError.Remote> {
        return safeCall<TokenDto> {
            clientProvider.getClient().post(REFRESH_TOKENS) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }
        }
    }

    override suspend fun logout(): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().post(LOGOUT)
        }
    }
}
