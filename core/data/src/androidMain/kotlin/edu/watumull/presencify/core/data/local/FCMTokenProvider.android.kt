package edu.watumull.presencify.core.data.local

import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class FCMTokenProvider actual constructor(private val platformContext: PlatformContext) {

    actual suspend fun getToken(): String? {
        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    continuation.resume(token)
                }
                .addOnFailureListener { exception ->
                    continuation.resume(null)
                }
        }
    }

    actual fun getDeviceModel(): String? = Build.MODEL

    actual fun getOsVersion(): String? = Build.VERSION.RELEASE

    actual fun getAppVersion(): String? {
        return try {
            val context = platformContext.androidContext
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            packageInfo.versionName
        } catch (e: Exception) {
            null
        }
    }

    actual val deviceType: String = "ANDROID"
}
