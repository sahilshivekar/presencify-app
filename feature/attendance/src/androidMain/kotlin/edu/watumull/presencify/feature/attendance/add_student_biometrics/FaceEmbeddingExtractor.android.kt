package edu.watumull.presencify.feature.attendance.add_student_biometrics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.tensorflow.lite.Interpreter
import presencify.feature.attendance.generated.resources.Res
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalResourceApi::class)
actual class FaceEmbeddingExtractor {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    private var interpreter: Interpreter? = null

    // Model specific constants (kept dynamic and shared)
    private var inputImageWidth: Int = 112
    private var inputImageHeight: Int = 112
    private var inputBufferSizeBytes: Int = 150528
    private var outputBatchSize: Int = 1
    private var outputEmbeddingSize: Int = 192

    actual suspend fun extractEmbedding(imageBytes: ByteArray): FloatArray? = withContext(Dispatchers.IO) {
        try {
            initInterpreterIfNeeded()

            // Decode image
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return@withContext null

            // Detect face
            val boundingBox = detectFirstFace(bitmap) ?: return@withContext null

            // Delegate to shared bitmap-based pipeline
            return@withContext extractEmbeddingFromBitmapInternal(bitmap, boundingBox)
        } catch (e: Exception) {
            Log.e("FaceEmbeddingExt", "Error extracting embedding", e)
            return@withContext null
        }
    }

    /**
     * Shared, bitmap-based embedding extraction used by both add-biometrics and
     * the camera recognition flow to guarantee identical preprocessing.
     */
    suspend fun extractEmbeddingFromBitmap(bitmap: Bitmap, boundingBox: android.graphics.Rect): FloatArray? =
        withContext(Dispatchers.IO) {
            try {
                initInterpreterIfNeeded()
                return@withContext extractEmbeddingFromBitmapInternal(bitmap, boundingBox)
            } catch (e: Exception) {
                Log.e("FaceEmbeddingExt", "Error extracting embedding from bitmap", e)
                return@withContext null
            }
        }

    private suspend fun initInterpreterIfNeeded() {
        if (interpreter != null) return

        // Load model bytes via suspend API and configure interpreter
        val modelBytes = Res.readBytes("files/MobileFaceNet.tflite")
        val buffer = ByteBuffer.allocateDirect(modelBytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(modelBytes)
        buffer.rewind()
        interpreter = Interpreter(buffer)

        // Configure based on model input/output
        val inputTensor = interpreter?.getInputTensor(0)
        val inputShape = inputTensor?.shape()
        if (inputShape != null && inputShape.size >= 3) {
            inputImageHeight = inputShape[1]
            inputImageWidth = inputShape[2]
        }
        inputBufferSizeBytes = inputTensor?.numBytes() ?: (inputImageWidth * inputImageHeight * 3 * 4)

        val outputTensor = interpreter?.getOutputTensor(0)
        val outShape = outputTensor?.shape()
        if (outShape != null && outShape.isNotEmpty()) {
            outputBatchSize = outShape[0]
            outputEmbeddingSize = if (outShape.size > 1) outShape[1] else 192
        }
    }

    private suspend fun detectFirstFace(bitmap: Bitmap): android.graphics.Rect? = suspendCoroutine { continuation ->
        val image = InputImage.fromBitmap(bitmap, 0)
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    continuation.resume(faces.first().boundingBox)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    private fun extractEmbeddingFromBitmapInternal(bitmap: Bitmap, boundingBox: android.graphics.Rect): FloatArray? {
        val faceBitmap = cropFace(bitmap, boundingBox)
        return extractFromFaceBitmap(faceBitmap)
    }

    /**
     * Core embedding extraction from a face-only bitmap (already cropped to face).
     * Used by both the byte-array enrollment flow and the camera recognition flow
     * (including mirrored variants).
     */
    fun extractFromFaceBitmap(faceBitmap: Bitmap): FloatArray? {
        val scaledBitmap = Bitmap.createScaledBitmap(faceBitmap, inputImageWidth, inputImageHeight, true)
        val input = preprocess(scaledBitmap)

        val output = Array(outputBatchSize) { FloatArray(outputEmbeddingSize) }
        interpreter?.run(input, output)

        return output[0]
    }

    /**
     * Utility to create a horizontally mirrored version of a bitmap.
     */
    fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun cropFace(bitmap: Bitmap, box: android.graphics.Rect): Bitmap {
        val padding = 10
        val x = (box.left - padding).coerceAtLeast(0)
        val y = (box.top - padding).coerceAtLeast(0)
        val width = (box.width() + padding * 2).coerceAtMost(bitmap.width - x)
        val height = (box.height() + padding * 2).coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(inputBufferSizeBytes)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(intValues, 0, inputImageWidth, 0, 0, inputImageWidth, inputImageHeight)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            // Normalize values from [0, 255] to [-1.0, 1.0]
            buffer.putFloat((r - 127.5f) / 128.0f)
            buffer.putFloat((g - 127.5f) / 128.0f)
            buffer.putFloat((b - 127.5f) / 128.0f)
        }

        buffer.rewind()
        return buffer
    }
}
