package edu.watumull.presencify.core.data.network.student

import edu.watumull.presencify.core.data.HttpClientProvider
import edu.watumull.presencify.core.data.dto.student.StudentFCMTokenDto
import edu.watumull.presencify.core.data.dto.student.request.StudentFCMTokenRequest
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.ADD_STUDENT_FCM_TOKENS
import edu.watumull.presencify.core.data.network.student.ApiEndpoints.REMOVE_STUDENT_FCM_TOKENS
import edu.watumull.presencify.core.data.repository.safeCall
import edu.watumull.presencify.core.domain.DataError
import edu.watumull.presencify.core.domain.Result
import io.ktor.client.request.*
import io.ktor.http.*

class KtorRemoteStudentFCMTokenDataSource(
    private val clientProvider: HttpClientProvider
) : RemoteStudentFCMTokenDataSource {

    override suspend fun upsertStudentFCMTokens(
        studentId: String,
        fcmToken: String,
        deviceId: String,
        deviceModel: String?,
        osVersion: String?,
        appVersion: String?,
        deviceType: String
    ): Result<StudentFCMTokenDto, DataError.Remote> {
        return safeCall<StudentFCMTokenDto> {
            clientProvider.getClient().post(ADD_STUDENT_FCM_TOKENS) {
                contentType(ContentType.Application.Json)
                setBody(
                    StudentFCMTokenRequest(
                        studentId = studentId,
                        fcmToken = fcmToken,
                        deviceId = deviceId,
                        deviceModel = deviceModel,
                        osVersion = osVersion,
                        appVersion = appVersion,
                        deviceType = deviceType
                    )
                )
            }
        }
    }

    override suspend fun removeStudentFCMTokens(studentId: String, deviceId: String): Result<Unit, DataError.Remote> {
        return safeCall<Unit> {
            clientProvider.getClient().delete(REMOVE_STUDENT_FCM_TOKENS) {
                parameter("studentId", studentId)
                parameter("deviceId", deviceId)
            }
        }
    }
}