package edu.watumull.presencify.core.data.local

expect class FCMTokenProvider(platformContext: PlatformContext) {
    suspend fun getToken(): String?
    fun getDeviceModel(): String?
    fun getOsVersion(): String?
    fun getAppVersion(): String?
    val deviceType: String
}
