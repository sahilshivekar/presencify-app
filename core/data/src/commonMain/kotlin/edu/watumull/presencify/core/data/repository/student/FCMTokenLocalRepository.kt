package edu.watumull.presencify.core.data.repository.student

import com.russhwolf.settings.Settings
import edu.watumull.presencify.core.data.Constants
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FCMTokenLocalRepository(private val settings: Settings) {

    @OptIn(ExperimentalUuidApi::class)
    fun getOrGenerateDeviceId(): String {
        return settings.getStringOrNull(Constants.FCM_DEVICE_ID_KEY)
            ?: Uuid.random().toString().also { settings.putString(Constants.FCM_DEVICE_ID_KEY, it) }
    }

    fun getLastSentFcmToken(): String? = settings.getStringOrNull(Constants.FCM_LAST_TOKEN_KEY)

    fun saveLastSentFcmToken(token: String) = settings.putString(Constants.FCM_LAST_TOKEN_KEY, token)

    fun clearLastSentFcmToken() = settings.remove(Constants.FCM_LAST_TOKEN_KEY)
}
