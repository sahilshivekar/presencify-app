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


    /**
     * Shared, bitmap-based embedding extraction used by both add-biometrics and
     * the camera recognition flow to guarantee identical preprocessing.
     */
    suspend fun extractEmbeddingFromBitmap(bitmap: Bitmap, boundingBox: android.graphics.Rect): FloatArray? =
        withContext(Dispatchers.IO) {
            try {
                Log.d("FaceEmbeddingExt", "extractEmbeddingFromBitmap called.")
                initInterpreterIfNeeded()
                return@withContext extractEmbeddingFromBitmapInternal(bitmap, boundingBox)
            } catch (e: Exception) {
                Log.e("FaceEmbeddingExt", "Error extracting embedding from bitmap", e)
                return@withContext null
            }
        }

    private suspend fun initInterpreterIfNeeded() {
        if (interpreter != null) return

        Log.d("FaceEmbeddingExt", "Interpreter is null. Loading MobileFaceNet.tflite...")
        try {
            // Load model bytes via suspend API and configure interpreter
            val modelBytes = Res.readBytes("files/MobileFaceNet.tflite")
            Log.d("FaceEmbeddingExt", "Loaded ${modelBytes.size} bytes for TFLite model.")

            val buffer = ByteBuffer.allocateDirect(modelBytes.size)
            buffer.order(ByteOrder.nativeOrder())
            buffer.put(modelBytes)
            buffer.rewind()
            interpreter = Interpreter(buffer)
            Log.d("FaceEmbeddingExt", "Interpreter created successfully.")

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

            Log.d(
                "FaceEmbeddingExt",
                "Interpreter configured: Input [${inputImageWidth}x${inputImageHeight}], Bytes: $inputBufferSizeBytes | Output: Batch $outputBatchSize, Size $outputEmbeddingSize"
            )
        } catch (e: Exception) {
            Log.e("FaceEmbeddingExt", "Failed to initialize Interpreter", e)
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

    // CHANGED TO SUSPEND
    private suspend fun extractEmbeddingFromBitmapInternal(
        bitmap: Bitmap,
        boundingBox: android.graphics.Rect
    ): FloatArray? {
        val faceBitmap = cropFace(bitmap, boundingBox)
        return extractFromFaceBitmap(faceBitmap)
    }

    /**
     * Core embedding extraction from a face-only bitmap (already cropped to face).
     * Used by both the byte-array enrollment flow and the camera recognition flow
     * (including mirrored variants).
     */
    // CHANGED TO SUSPEND so we can force initialization!
    // Inside FaceEmbeddingExtractor.android.kt

    actual suspend fun extractEmbedding(imageBytes: ByteArray): FloatArray? = withContext(Dispatchers.IO) {
        try {
            initInterpreterIfNeeded()

            // 1. Force ARGB_8888 during decoding
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                ?: return@withContext null

            // 2. Ensure it's definitely ARGB_8888 (Factory.Options is just a "preference")
            val bitmap = if (originalBitmap.config != Bitmap.Config.ARGB_8888) {
                originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            } else {
                originalBitmap
            }

            val boundingBox = detectFirstFace(bitmap) ?: return@withContext null
            return@withContext extractEmbeddingFromBitmapInternal(bitmap, boundingBox)
        } catch (e: Exception) {
            return@withContext null
        }
    }

    suspend fun extractFromFaceBitmap(faceBitmap: Bitmap): FloatArray? {
        try {
            // Ensure the interpreter is initialized before attempting extraction
            initInterpreterIfNeeded()

            Log.d(
                "FaceEmbeddingExt",
                "extractFromFaceBitmap invoked. Input Bitmap Size: ${faceBitmap.width}x${faceBitmap.height}"
            )

            // Ensure the cropped face is standardized to ARGB_8888 before scaling.
            // This prevents bit-depth inconsistencies between gallery images and camera frames.
            val standardizedBitmap = if (faceBitmap.config != Bitmap.Config.ARGB_8888) {
                faceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            } else {
                faceBitmap
            }

            // Scale the bitmap to the specific dimensions required by the TFLite model (e.g., 112x112)
            val scaledBitmap = Bitmap.createScaledBitmap(standardizedBitmap, inputImageWidth, inputImageHeight, true)
            Log.d(
                "FaceEmbeddingExt",
                "Bitmap scaled to required input size: ${scaledBitmap.width}x${scaledBitmap.height}"
            )

            // Visual debugger: logs the scaled face image as a Base64 string for verification
            val baos = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)
            Log.d("FaceEmbeddingExt", "BASE64_IMAGE_START")
            Log.d("FaceEmbeddingExt", b64)
            Log.d("FaceEmbeddingExt", "BASE64_IMAGE_END")

            // Convert the Bitmap pixels into the normalized ByteBuffer required by the model
            val input = preprocess(scaledBitmap)
            Log.d("FaceEmbeddingExt", "Preprocessing complete. ByteBuffer ready. Invoking TFLite interpreter...")

            val output = Array(outputBatchSize) { FloatArray(outputEmbeddingSize) }

            if (interpreter == null) {
                Log.e("FaceEmbeddingExt", "CRITICAL ERROR: Interpreter is null during run phase!")
                return null
            }

            // Execute the model
            interpreter?.run(input, output)
            Log.d(
                "FaceEmbeddingExt",
                "Interpreter run successful! Extracted array size: ${output[0].size}, Array Sum: ${output[0].sum()}"
            )

            return output[0]
        } catch (e: Exception) {
            Log.e("FaceEmbeddingExt", "Crash during extractFromFaceBitmap", e)
            return null
        }
    }

    /**
     * Utility to create a horizontally mirrored version of a bitmap.
     */
    fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Shared cropping logic to ensure identical "zoom" levels for the AI model.
     * Uses 25% padding which is optimal for MobileFaceNet to see enough head shape.
     */
    fun cropFace(bitmap: Bitmap, box: android.graphics.Rect): Bitmap {
        val paddingPercentage = 0.25f // 25% padding
        val paddingX = (box.width() * paddingPercentage).toInt()
        val paddingY = (box.height() * paddingPercentage).toInt()

        val x = (box.left - paddingX).coerceAtLeast(0)
        val y = (box.top - paddingY).coerceAtLeast(0)

        // Ensure we don't exceed the bitmap boundaries
        val width = (box.width() + paddingX * 2).coerceAtMost(bitmap.width - x)
        val height = (box.height() + paddingY * 2).coerceAtMost(bitmap.height - y)

        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    // Your existing preprocess function is now safe because we forced ARGB_8888
    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(inputBufferSizeBytes)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputImageWidth * inputImageHeight)
        bitmap.getPixels(intValues, 0, inputImageWidth, 0, 0, inputImageWidth, inputImageHeight)

        for (pixel in intValues) {
            // These shifts ONLY work reliably if the Bitmap is ARGB_8888
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            buffer.putFloat((r - 127.5f) / 128.0f)
            buffer.putFloat((g - 127.5f) / 128.0f)
            buffer.putFloat((b - 127.5f) / 128.0f)
        }
        buffer.rewind()
        return buffer
    }
}