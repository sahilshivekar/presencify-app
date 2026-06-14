package edu.watumull.presencify.core.data.local

actual class FCMTokenProvider actual constructor(platformContext: PlatformContext) {
    actual suspend fun getToken(): String? = null

    actual fun getDeviceModel(): String? = null

    actual fun getOsVersion(): String? = null

    actual fun getAppVersion(): String? = null

    actual val deviceType: String = "IOS"
}
