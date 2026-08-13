package edu.watumull.presencify.core.data.network

import edu.watumull.presencify.core.data.dto.auth.TokenDto
import edu.watumull.presencify.core.data.repository.auth.TokenRepository
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.repository.auth.UserRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import edu.watumull.presencify.core.data.network.admin_auth.ApiEndpoints as AdminApiEndpoints
import edu.watumull.presencify.core.data.network.student_auth.ApiEndpoints as StudentApiEndpoints
import edu.watumull.presencify.core.data.network.teacher_auth.ApiEndpoints as TeacherApiEndpoints


class HttpClientFactory(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {
    private fun createBasicClient(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 60_000L
                requestTimeoutMillis = 60_000L
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = tokenRepository.readAccessToken() ?: ""
                        val refreshToken = tokenRepository.readRefreshToken() ?: ""

                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = refreshToken
                        )
                    }

                    refreshTokens {
                        val refreshToken = tokenRepository.readRefreshToken()

                        val userRole = userRepository.getUserRole().firstOrNull()

                        println("👤 User role: $userRole")

                        val basicClient = createBasicClient(engine)

                        return@refreshTokens try {
                            val tokenDto = when (userRole) {
                                UserRole.STUDENT -> {
                                    basicClient.post(StudentApiEndpoints.REFRESH_TOKENS) {
                                        contentType(ContentType.Application.Json)
                                        setBody(mapOf("refreshToken" to refreshToken))
                                    }.body<TokenDto>()
                                }

                                UserRole.TEACHER -> {
                                    basicClient.post(TeacherApiEndpoints.REFRESH_TOKENS) {
                                        contentType(ContentType.Application.Json)
                                        setBody(mapOf("refreshToken" to refreshToken))
                                    }.body<TokenDto>()
                                }

                                UserRole.ADMIN -> {
                                    basicClient.post(AdminApiEndpoints.REFRESH_TOKENS) {
                                        contentType(ContentType.Application.Json)
                                        setBody(mapOf("refreshToken" to refreshToken))
                                    }.body<TokenDto>()
                                }

                                else -> {
                                    throw IllegalStateException("Unknown user role: $userRole")
                                }
                            }

                            tokenRepository.saveAccessToken(tokenDto.accessToken)
                            tokenRepository.saveRefreshToken(tokenDto.refreshToken)

                            BearerTokens(tokenDto.accessToken, tokenDto.refreshToken)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            tokenRepository.clearTokens()
                            userRepository.clearUserDetails()
                            null
                        } finally {
                            basicClient.close()
                        }
                    }
                    sendWithoutRequest { request ->
                        val isAuthEndpoint = request.url.pathSegments.contains("login") ||
                                request.url.pathSegments.contains("register")

                        !isAuthEndpoint
                    }
                }
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
                level = LogLevel.ALL
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}