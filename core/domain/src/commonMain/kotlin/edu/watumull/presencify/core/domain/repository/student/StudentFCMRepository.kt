package edu.watumull.presencify.core.domain.repository.student

import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import edu.watumull.presencify.core.domain.model.student.StudentFCMToken

interface StudentFCMRepository {
    suspend fun upsertStudentFCMTokens(
        studentId: String,
        fcmToken: String,
        deviceId: String,
        deviceModel: String?,
        osVersion: String?,
        appVersion: String?,
        deviceType: String
    ): Result<StudentFCMToken, DataError.Remote>

    suspend fun removeStudentFCMTokens(studentId: String, deviceId: String): Result<Unit, DataError.Remote>
}