package edu.watumull.presencify.core.data.network.student

import edu.watumull.presencify.core.data.dto.student.StudentFCMTokenDto
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result

interface RemoteStudentFCMTokenDataSource {
    suspend fun upsertStudentFCMTokens(
        studentId: String,
        fcmToken: String,
        deviceId: String,
        deviceModel: String?,
        osVersion: String?,
        appVersion: String?,
        deviceType: String
    ): Result<StudentFCMTokenDto, DataError.Remote>

    suspend fun removeStudentFCMTokens(studentId: String, deviceId: String): Result<Unit, DataError.Remote>
}