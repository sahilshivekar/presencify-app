package edu.watumull.presencify.feature.attendance.add_student_biometrics

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class FaceEmbeddingExtractor {

    // JVM implementation currently does not run the TFLite model.
    // We keep the same API but always return null so that the
    // ViewModel falls back to showing an error dialog on desktop.

    actual suspend fun extractEmbedding(imageBytes: ByteArray): FloatArray? = withContext(Dispatchers.IO) {
        try {
            // Attempt to decode the image just to validate it's a proper image.
            val bais = ByteArrayInputStream(imageBytes)
            val originalImage: BufferedImage? = ImageIO.read(bais)
            if (originalImage == null) {
                return@withContext null
            }

            // TODO: If you want full JVM support, wire a JVM-compatible
            // TensorFlow Lite (or other) runtime here and mirror the
            // Android pipeline. For now we return null so the
            // AddStudentBiometricsViewModel shows an error dialog.
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
