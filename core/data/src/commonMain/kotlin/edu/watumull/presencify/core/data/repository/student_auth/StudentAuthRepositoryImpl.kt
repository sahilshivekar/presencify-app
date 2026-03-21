package edu.watumull.presencify.core.data.repository.student_auth

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.network.student_auth.RemoteStudentAuthDataSource
import edu.watumull.presencify.core.data.repository.auth.UserRepositoryImpl
import edu.watumull.presencify.core.data.repository.auth.TokenRepository
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.map
import edu.watumull.presencify.core.domain.model.auth.UserRole
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student_auth.StudentAuthRepository

class StudentAuthRepositoryImpl(
    private val remoteDataSource: RemoteStudentAuthDataSource,
    private val tokenRepository: TokenRepository,
    private val userRepositoryImpl: UserRepositoryImpl,
    private val httpClientProvider: HttpClientProvider
) : StudentAuthRepository {

    override suspend fun loginStudent(emailOrPRN: String, password: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.loginStudent(emailOrPRN, password).onSuccess { loginStudentDto ->
            tokenRepository.saveAccessToken(loginStudentDto.accessToken)
            tokenRepository.saveRefreshToken(loginStudentDto.refreshToken)
            httpClientProvider.recreateClient()
            userRepositoryImpl.saveUserDetails(UserRole.STUDENT, loginStudentDto.student.id)
        }.map {}
    }

    override suspend fun sendVerificationCodeToEmail(email: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.sendVerificationCodeToEmail(email)
    }

    override suspend fun verifyCode(email: String, code: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.verifyCode(email, code).onSuccess { loginStudentDto ->
            tokenRepository.saveAccessToken(loginStudentDto.accessToken)
            tokenRepository.saveRefreshToken(loginStudentDto.refreshToken)
            httpClientProvider.recreateClient()
            userRepositoryImpl.saveUserDetails(UserRole.STUDENT, loginStudentDto.student.id)
        }.map {}
    }

    override suspend fun updatePassword(password: String, confirmPassword: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.updatePassword(password, confirmPassword)
    }

    override suspend fun refreshTokens(refreshToken: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.refreshTokens(refreshToken).onSuccess { tokenDto ->
            tokenRepository.saveAccessToken(tokenDto.accessToken)
            tokenRepository.saveRefreshToken(tokenDto.refreshToken)
            httpClientProvider.recreateClient()
        }.map {}
    }

    override suspend fun logout(): Result<Unit, DataError.Remote> {
        return remoteDataSource.logout().onSuccess {
            tokenRepository.clearTokens()
            userRepositoryImpl.clearUserDetails()
            httpClientProvider.recreateClient()
        }
    }
}
