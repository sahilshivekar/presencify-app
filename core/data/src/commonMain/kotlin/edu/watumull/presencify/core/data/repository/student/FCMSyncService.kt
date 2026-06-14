package edu.watumull.presencify.core.data.repository.student

import edu.watumull.presencify.core.data.local.FCMTokenProvider
import edu.watumull.presencify.core.domain.onSuccess
import edu.watumull.presencify.core.domain.repository.student.StudentFCMRepository

class FCMSyncService(
    private val fcmTokenProvider: FCMTokenProvider,
    private val fcmRepository: StudentFCMRepository,
    private val fcmLocalRepository: FCMTokenLocalRepository,
) {

    suspend fun syncIfNeeded(studentId: String) {
        val currentToken = fcmTokenProvider.getToken() ?: return
        if (currentToken == fcmLocalRepository.getLastSentFcmToken()) return

        fcmRepository.upsertStudentFCMTokens(
            studentId = studentId,
            fcmToken = currentToken,
            deviceId = fcmLocalRepository.getOrGenerateDeviceId(),
            deviceModel = fcmTokenProvider.getDeviceModel(),
            osVersion = fcmTokenProvider.getOsVersion(),
            appVersion = fcmTokenProvider.getAppVersion(),
            deviceType = fcmTokenProvider.deviceType
        ).onSuccess {
            fcmLocalRepository.saveLastSentFcmToken(currentToken)
        }
    }

    suspend fun remove(studentId: String) {
        val deviceId = fcmLocalRepository.getOrGenerateDeviceId()
        fcmRepository.removeStudentFCMTokens(studentId, deviceId)
        fcmLocalRepository.clearLastSentFcmToken()
    }
}
