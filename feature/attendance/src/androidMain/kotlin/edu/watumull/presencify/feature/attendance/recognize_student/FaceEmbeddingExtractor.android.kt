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
import kotlin.math.roundToInt

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
            val detectorModelBytes = androidContext.assets.open("face_detection_yunet_2023mar.onnx").readBytes()
            detectorSession = ortEnvironment?.createSession(detectorModelBytes)
        }

        if (recognizerSession == null) {
            val recognizerModelBytes = androidContext.assets.open("face_recognition_sface_2021dec.onnx").readBytes()
            recognizerSession = ortEnvironment?.createSession(recognizerModelBytes)
        }
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

        val DETECT_SIZE = 640
        val detectScaledImage = Bitmap.createScaledBitmap(image, DETECT_SIZE, DETECT_SIZE, true)

        val detectTensor = imageToSFaceTensor(detectScaledImage)

        val inputName = detector.inputNames.iterator().next()
        val detectorResults = detector.run(mapOf(inputName to detectTensor))

        val outputs = detectorResults

        fun get(name: String): OnnxTensor {
            val optional = outputs.get(name)
                ?: throw IllegalStateException("Output $name not found")

            if (!optional.isPresent) {
                throw IllegalStateException("Output $name is empty")
            }

            return optional.get() as OnnxTensor
        }

        val scales = listOf(8, 16, 32)

        var bestScore = 0f
        var bestBox: FloatArray? = null
        var bestLandmarks: FloatArray? = null

        for (s in scales) {

            val cls = get("cls_$s").floatBuffer
            val obj = get("obj_$s").floatBuffer
            val bbox = get("bbox_$s").floatBuffer
            val kps = get("kps_$s").floatBuffer

            cls.rewind()
            obj.rewind()
            bbox.rewind()
            kps.rewind()

            val count = cls.capacity()

            for (i in 0 until count) {

                val score = cls.get(i) * obj.get(i)

                if (score > bestScore) {
                    bestScore = score

                    val bOffset = i * 4
                    val kOffset = i * 10

                    val cx = bbox.get(bOffset + 0)
                    val cy = bbox.get(bOffset + 1)
                    val w = bbox.get(bOffset + 2)
                    val h = bbox.get(bOffset + 3)

                    val landmarks = floatArrayOf(
                        kps.get(kOffset + 0), kps.get(kOffset + 1), // left eye
                        kps.get(kOffset + 2), kps.get(kOffset + 3), // right eye
                        kps.get(kOffset + 4), kps.get(kOffset + 5), // nose
                        kps.get(kOffset + 6), kps.get(kOffset + 7), // mouth left
                        kps.get(kOffset + 8), kps.get(kOffset + 9)  // mouth right
                    )

                    bestBox = floatArrayOf(cx, cy, w, h)
                    bestLandmarks = landmarks
                }
            }
        }

        if (bestBox == null || bestLandmarks == null || bestScore < 0.5f) {
            detectorResults.close()
            detectTensor.close()
            return null
        }

        val cx = bestBox[0]
        val cy = bestBox[1]
        val w = bestBox[2]
        val h = bestBox[3]

        val scaleFactor = DETECT_SIZE.toFloat()

        val x = (cx - w / 2f) * scaleFactor
        val y = (cy - h / 2f) * scaleFactor
        val width = w * scaleFactor
        val height = h * scaleFactor

        val faceBox = FaceBoundingBox(
            x / image.width,
            y / image.height,
            width / image.width,
            height / image.height
        )

        Log.d("RecognizeStudentCam", "Best score: $bestScore")

        detectorResults.close()
        detectTensor.close()

        // 🔥 IMPORTANT: pass landmarks
        val croppedFace = alignAndCrop(image, faceBox, bestLandmarks)

        val recognizeTensor = imageToSFaceTensor(croppedFace)
        val recognizeInputName = recognizer.inputNames.iterator().next()

        val recognizerResults = recognizer.run(mapOf(recognizeInputName to recognizeTensor))

        val embeddingTensor = recognizerResults.get(0) as? OnnxTensor
        val embeddingBuffer = embeddingTensor?.floatBuffer

        recognizeTensor.close()
        recognizerResults.close()

        if (embeddingBuffer == null) return null

        val embeddingArray = FloatArray(embeddingBuffer.capacity())
        embeddingBuffer.get(embeddingArray)

        var sumSq = 0.0f
        for (v in embeddingArray) sumSq += v * v
        val norm = kotlin.math.sqrt(sumSq)

        for (i in embeddingArray.indices) {
            embeddingArray[i] = embeddingArray[i] / norm
        }

        return embeddingArray
    }

    actual fun alignAndCrop(image: Bitmap, face: FaceBoundingBox, landmarks: FloatArray): Bitmap {

        val leftEyeX = landmarks[0]
        val leftEyeY = landmarks[1]
        val rightEyeX = landmarks[2]
        val rightEyeY = landmarks[3]

        val dx = rightEyeX - leftEyeX
        val dy = rightEyeY - leftEyeY

        val angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()

        val matrix = android.graphics.Matrix()
        matrix.postRotate(-angle)

        val rotated = Bitmap.createBitmap(
            image,
            0,
            0,
            image.width,
            image.height,
            matrix,
            true
        )

        // 👉 call NORMAL crop function (your old one)
        return cropOnly(rotated, face)
    }

    fun cropOnly(image: Bitmap, face: FaceBoundingBox): Bitmap {

        val imgW = image.width.toFloat()
        val imgH = image.height.toFloat()

        val absX = face.x * imgW
        val absY = face.y * imgH
        val absW = face.w * imgW
        val absH = face.h * imgH

        val cx = absX + absW / 2f
        val cy = absY + absH / 2f

        var size = maxOf(absW, absH) * 1.2f

        // Ensure crop does not exceed image bounds
        size = minOf(size, imgW, imgH)

        var newX = cx - size / 2f
        var newY = cy - size / 2f

        // Clamp inside image
        if (newX < 0f) newX = 0f
        if (newY < 0f) newY = 0f
        if (newX + size > imgW) newX = imgW - size
        if (newY + size > imgH) newY = imgH - size

        val roundedX = newX.roundToInt()
        val roundedY = newY.roundToInt()
        val roundedSize = size.roundToInt()

        Log.d(
            "RecognizeStudentCam",
            "CropOnly: X=$roundedX, Y=$roundedY, Size=$roundedSize on Image ${image.width}x${image.height}"
        )

        if (roundedSize <= 0) {
            Log.e("RecognizeStudentCam", "Crop error: size <= 0, fallback to full image")
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