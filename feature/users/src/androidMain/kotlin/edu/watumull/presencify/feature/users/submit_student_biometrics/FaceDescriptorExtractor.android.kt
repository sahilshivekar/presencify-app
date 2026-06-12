package edu.watumull.presencify.feature.users.submit_student_biometrics

import android.content.Context
import android.util.Log
import edu.watumull.presencify.feature.attendance.recognize_student.FaceEmbeddingExtractor
import edu.watumull.presencify.feature.attendance.recognize_student.toPlatformImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private const val TAG = "FaceDescriptorExtractor"

actual suspend fun extractFaceDescriptors(images: List<ByteArray>): List<FloatArray>? {
    return withContext(Dispatchers.Default) {
        try {
            val context: Context? = try {
                // Try to get context from Koin
                val koinComponent = object : KoinComponent {}
                koinComponent.get<Context>()
            } catch (e: Exception) {
                Log.w(TAG, "Could not get context from Koin: ${e.message}")
                null
            }

            if (context == null) {
                Log.e(TAG, "Cannot extract face descriptors: Android context not available")
                return@withContext null
            }

            val extractor = FaceEmbeddingExtractor()
            extractor.initialize(context)

            val descriptors = mutableListOf<FloatArray>()
            for (imageBytes in images) {
                val platformImage = imageBytes.toPlatformImage()
                if (platformImage != null) {
                    val descriptor = extractor.generateSingleDescriptor(platformImage)
                    if (descriptor != null) {
                        descriptors.add(descriptor)
                    }
                }
            }

            extractor.close()

            if (descriptors.isEmpty()) null else descriptors
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting face descriptors: ${e.message}", e)
            null
        }
    }
}
