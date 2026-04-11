package edu.watumull.presencify.feature.attendance.recognize_student

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.LongBuffer
import kotlin.math.roundToInt
import kotlin.math.sqrt

actual typealias PlatformImage = Bitmap

actual class FaceEmbeddingExtractor actual constructor() {

    private var ortEnvironment: OrtEnvironment? = null
    private var detectorSession: OrtSession? = null
    private var recognizerSession: OrtSession? = null

    actual fun initialize(context: Any) {
        val androidContext = context as? Context
            ?: throw IllegalArgumentException("Context must be provided on Android")

        if (ortEnvironment == null) {
            ortEnvironment = OrtEnvironment.getEnvironment()
        }

        if (detectorSession == null) {
            val detectorModelBytes = androidContext.assets.open("blaze.onnx").readBytes()
            detectorSession = ortEnvironment?.createSession(detectorModelBytes)
        }

        if (recognizerSession == null) {
            val recognizerModelBytes = androidContext.assets.open("face_recognition_sface_2021dec.onnx").readBytes()
            recognizerSession = ortEnvironment?.createSession(recognizerModelBytes)
        }
    }

    private fun imageToBlazeTensor(bitmap: Bitmap): OnnxTensor {
        val env = ortEnvironment ?: throw IllegalStateException("OrtEnvironment not initialized")
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height
//        val floatBuffer = FloatBuffer.allocate(3 * size)
        // Replace FloatBuffer.allocate(3 * size) with:
        val byteBuffer = ByteBuffer.allocateDirect(3 * size * 4) // 4 bytes per float
        byteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer = byteBuffer.asFloatBuffer()
        val pixels = IntArray(size)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in 0 until size) {
            val pixel = pixels[i]
            // BlazeFace: RGB Order, Normalized to [-1, 1]
            floatBuffer.put(i, (Color.red(pixel) / 127.5f) - 1.0f)
            floatBuffer.put(size + i, (Color.green(pixel) / 127.5f) - 1.0f)
            floatBuffer.put(size * 2 + i, (Color.blue(pixel) / 127.5f) - 1.0f)
        }
        floatBuffer.rewind()
        return OnnxTensor.createTensor(env, floatBuffer, longArrayOf(1, 3, height.toLong(), width.toLong()))
    }

    private fun imageToSFaceTensor(bitmap: Bitmap): OnnxTensor {
        val env = ortEnvironment ?: throw IllegalStateException("OrtEnvironment not initialized")
        val width = bitmap.width
        val height = bitmap.height
        val size = width * height

        // ✅ DIRECT NATIVE MEMORY ALLOCATION (Replaced FloatBuffer.allocate)
        val byteBuffer = ByteBuffer.allocateDirect(3 * size * 4) // 4 bytes per float
        byteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer = byteBuffer.asFloatBuffer()

        val pixels = IntArray(size)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in 0 until size) {
            val pixel = pixels[i]
            // SFace: BGR Order, Raw Pixels [0, 255] (NO DIVISION)
            floatBuffer.put(i, Color.blue(pixel).toFloat())
            floatBuffer.put(size + i, Color.green(pixel).toFloat())
            floatBuffer.put(size * 2 + i, Color.red(pixel).toFloat())
        }
        floatBuffer.rewind()
        return OnnxTensor.createTensor(env, floatBuffer, longArrayOf(1, 3, height.toLong(), width.toLong()))
    }

    actual fun generateSingleDescriptor(image: PlatformImage): FloatArray? {
        val env = ortEnvironment ?: throw IllegalStateException("ONNX Env not initialized")
        val detector = detectorSession ?: throw IllegalStateException("Detector not initialized")
        val recognizer = recognizerSession ?: throw IllegalStateException("Recognizer not initialized")

        // 1. Resize for Detection (128x128)
        val detectScaledImage = Bitmap.createScaledBitmap(image, 128, 128, true)
        val detectTensor = imageToBlazeTensor(detectScaledImage) // Make sure you are using the BlazeTensor method!

        // 2. Prepare Detector Configuration Tensors
        val confTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(floatArrayOf(0.75f)), longArrayOf(1))
        val iouTensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(floatArrayOf(0.3f)), longArrayOf(1))
        val maxDetTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(longArrayOf(1L)), longArrayOf(1))

        val detectorInputs = mapOf(
            "image" to detectTensor,
            "conf_threshold" to confTensor,
            "iou_threshold" to iouTensor,
            "max_detections" to maxDetTensor
        )

        // 3. Run Detection
        val detectorResults = detector.run(detectorInputs)

        val boxesTensor = detectorResults.get(0) as? OnnxTensor
        val boxesBuffer = boxesTensor?.floatBuffer

        detectTensor.close()
        confTensor.close()
        iouTensor.close()
        maxDetTensor.close()
        detectorResults.close()

        if (boxesBuffer == null || boxesBuffer.capacity() < 4) return null

        // 4. Extract Face Coordinates

        var x1 = boxesBuffer.get(0) // xmin
        var y1 = boxesBuffer.get(1) // ymin
        var x2 = boxesBuffer.get(2) // xmax
        var y2 = boxesBuffer.get(3) // ymax

        // Normalize absolute coords to 0.0 - 1.0 percentages
        if (x2 > 1.0f || y2 > 1.0f) {
            x1 /= 128.0f
            y1 /= 128.0f
            x2 /= 128.0f
            y2 /= 128.0f
        }

        val faceBox = FaceBoundingBox(x1, y1, x2 - x1, y2 - y1)
        Log.d("RecognizeStudentCam", "BlazeFace Box (Normalized): X=$x1, Y=$y1, W=${faceBox.w}, H=${faceBox.h}")

        // 5. Crop and Align
        val croppedFace = alignAndCrop(image, faceBox)

        // 6. Recognition
        val recognizeTensor = imageToSFaceTensor(croppedFace) // Make sure you are using the SFace method!
        val recognizeInputName = recognizer.inputNames.iterator().next()
        val recognizerResults = recognizer.run(mapOf(recognizeInputName to recognizeTensor))

        val embeddingTensor = recognizerResults.get(0) as? OnnxTensor
        val embeddingBuffer = embeddingTensor?.floatBuffer

        recognizeTensor.close()
        recognizerResults.close()

        if (embeddingBuffer == null) return null

        val embeddingArray = FloatArray(embeddingBuffer.capacity())
        embeddingBuffer.get(embeddingArray)

        // 7. L2 Normalization
        var sumSq = 0.0f
        for (v in embeddingArray) sumSq += v * v
        val norm = kotlin.math.sqrt(sumSq)
        for (i in embeddingArray.indices) embeddingArray[i] = embeddingArray[i] / norm

        return embeddingArray
    }

    actual fun alignAndCrop(image: PlatformImage, face: FaceBoundingBox): PlatformImage {
        val imgW = image.width.toFloat()
        val imgH = image.height.toFloat()

        val absX = face.x * imgW
        val absY = face.y * imgH
        val absW = face.w * imgW
        val absH = face.h * imgH

        val cx = absX + absW / 2f
        val cy = absY + absH / 2f

        var size = maxOf(absW, absH) * 1.1f

        // 1. Guarantee the crop box is never larger than the image itself
        size = minOf(size, imgW, imgH)

        var newX = cx - size / 2f
        var newY = cy - size / 2f

        // 2. Shift the bounding box back into frame
        if (newX < 0f) newX = 0f
        if (newY < 0f) newY = 0f
        if (newX + size > imgW) newX = imgW - size
        if (newY + size > imgH) newY = imgH - size

        val roundedX = newX.roundToInt()
        val roundedY = newY.roundToInt()
        val roundedSize = size.roundToInt()

        Log.d(
            "RecognizeStudentCam",
            "Actual Crop Execution: X=$roundedX, Y=$roundedY, Size=$roundedSize on Image ${image.width}x${image.height}"
        )

        if (roundedSize <= 0) {
            Log.e("RecognizeStudentCam", "FATAL CROP ERROR: Calculated size is <= 0. Returning full uncropped image!")
            return Bitmap.createScaledBitmap(image, 112, 112, true)
        }

        val cropped = Bitmap.createBitmap(image, roundedX, roundedY, roundedSize, roundedSize)
        return Bitmap.createScaledBitmap(cropped, 112, 112, true)
    }

    actual fun close() {
        detectorSession?.close()
        recognizerSession?.close()
        ortEnvironment?.close()
        detectorSession = null
        recognizerSession = null
        ortEnvironment = null
    }
}