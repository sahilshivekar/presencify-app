package edu.watumull.presencify.feature.users.submit_student_biometrics

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import edu.watumull.presencify.feature.attendance.recognize_student.FaceEmbeddingExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

private const val TAG = "FaceDescriptorExtractor"

actual suspend fun extractFaceDescriptors(
    images: List<ByteArray>
): List<FloatArray>? = withContext(Dispatchers.Default) {

    try {
        val context = try {
            object : KoinComponent {}.get<Context>()
        } catch (e: Exception) {
            Log.e(TAG, "Android Context unavailable.", e)
            return@withContext null
        }

        val extractor = FaceEmbeddingExtractor()

        try {
            extractor.initialize(context)

            val descriptors: List<FloatArray> = images.mapNotNull { imageBytes ->
                val originalBitmap = BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size
                )
                if (originalBitmap == null) {
                    Log.e(
                        TAG,
                        "CRITICAL ERROR: BitmapFactory returned null! Your ByteArray is either corrupt, or it is raw YUV data instead of a JPEG. You must convert it to JPEG first."
                    )
                    return@mapNotNull null
                }

                Log.d(TAG, "Bitmap decoded successfully. Size: ${originalBitmap.width}x${originalBitmap.height}")
                val maxDim = 800f
                val scale = maxDim / maxOf(originalBitmap.width, originalBitmap.height)
                val scaledBitmap = if (scale < 1.0f) {
                    Bitmap.createScaledBitmap(
                        originalBitmap,
                        (originalBitmap.width * scale).toInt(),
                        (originalBitmap.height * scale).toInt(),
                        true
                    )
                } else {
                    originalBitmap
                }

                if (scaledBitmap != originalBitmap) originalBitmap.recycle()

                var foundDescriptor: FloatArray? = null
                val rotationsToTry = listOf(0f, 90f, 270f, 180f)

                for (angle in rotationsToTry) {
                    val matrix = Matrix().apply { postRotate(angle) }

                    val rotatedBitmap = if (angle != 0f) {
                        Bitmap.createBitmap(
                            scaledBitmap, 0, 0,
                            scaledBitmap.width, scaledBitmap.height,
                            matrix, true
                        )
                    } else {
                        scaledBitmap
                    }

                    val finalBitmap = if (rotatedBitmap.config != Bitmap.Config.ARGB_8888) {
                        val converted = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)
                        if (rotatedBitmap != scaledBitmap) rotatedBitmap.recycle()
                        converted
                    } else {
                        rotatedBitmap
                    }

                    foundDescriptor = extractor.generateSingleDescriptor(finalBitmap)

                    if (finalBitmap != scaledBitmap) finalBitmap.recycle()

                    if (foundDescriptor != null) {
                        Log.d(TAG, "Success! Face found at rotation: $angle degrees")
                        break
                    }
                }

                scaledBitmap.recycle()

                if (foundDescriptor == null) {
                    Log.e(TAG, "Failed: No face detected in any rotation.")
                }

                foundDescriptor
            }

            descriptors.takeIf { it.isNotEmpty() }

        } finally {
            extractor.close()
        }

    } catch (e: Exception) {
        Log.e(TAG, "Exception during face descriptor extraction.", e)
        null
    }
}