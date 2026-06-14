package edu.watumull.presencify.core.data.repository.student

import edu.watumull.presencify.core.data.mapper.student.toDomain
import edu.watumull.presencify.core.data.network.student.RemoteStudentFCMTokenDataSource
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.map
import edu.watumull.presencify.core.domain.model.student.StudentFCMToken
import edu.watumull.presencify.core.domain.repository.student.StudentFCMRepository

class StudentFCMRepositoryImpl(
    private val remoteDataSource: RemoteStudentFCMTokenDataSource
) : StudentFCMRepository {

    override suspend fun upsertStudentFCMTokens(
        studentId: String,
        fcmToken: String,
        deviceId: String,
        deviceModel: String?,
        osVersion: String?,
        appVersion: String?,
        deviceType: String
    ): Result<StudentFCMToken, DataError.Remote> {
        return remoteDataSource.upsertStudentFCMTokens(
            studentId = studentId,
            fcmToken = fcmToken,
            deviceId = deviceId,
            deviceModel = deviceModel,
            osVersion = osVersion,
            appVersion = appVersion,
            deviceType = deviceType
        ).map { it.toDomain() }
    }

    override suspend fun removeStudentFCMTokens(studentId: String, deviceId: String): Result<Unit, DataError.Remote> {
        return remoteDataSource.removeStudentFCMTokens(studentId, deviceId)
    }
}