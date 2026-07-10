package edu.watumull.presencify.feature.attendance.recognize_student

import android.R.attr.src
import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.objdetect.FaceDetectorYN
import org.opencv.objdetect.FaceRecognizerSF
import org.opencv.core.Size
import java.io.File
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

actual typealias PlatformImage = Bitmap


actual class FaceEmbeddingExtractor {

    private lateinit var detector: FaceDetectorYN
    private lateinit var recognizer: FaceRecognizerSF

    private var initialized = false

    actual fun initialize(context: Any) {

        if (initialized) return
        val androidContext = requireNotNull(context as? Context) {
            "Android Context expected."
        }

        check(OpenCVLoader.initLocal()) {
            "Failed to initialize OpenCV ${OpenCVLoader.OPENCV_VERSION}"
        }

        val detectorPath =
            copyAsset(
                androidContext,
                "face_detection_yunet_2023mar_int8.onnx"
            )

        val recognizerPath =
            copyAsset(
                androidContext,
                "face_recognition_sface_2021dec_int8.onnx"
            )

        detector =
            FaceDetectorYN.create(
                detectorPath,
                "",
                Size(1.0, 1.0)
            )
        detector.scoreThreshold = 0.5f
        recognizer =
            FaceRecognizerSF.create(
                recognizerPath,
                ""
            )

        initialized = true
    }

    actual fun generateSingleDescriptor(image: Bitmap): FloatArray? {
        check(initialized) {
            "FaceEmbeddingExtractor is not initialized."
        }

        val bgr = Mat()
        return try {
            Utils.bitmapToMat(image, bgr)
            Imgproc.cvtColor(bgr, bgr, Imgproc.COLOR_RGBA2BGR)
            val face = detectBestFace(bgr) ?: return null
            extractDescriptor(bgr, face)
        } finally {
            bgr.release()
        }
    }

    private fun detectBestFace(bgr: Mat): Mat? {

        detector.setInputSize(
            Size(bgr.cols().toDouble(), bgr.rows().toDouble())
        )

        val faces = Mat()

        try {

            if (detector.detect(bgr, faces) <= 0 || faces.empty()) {
                return null
            }

            var bestIndex = 0
            var bestScore = Double.NEGATIVE_INFINITY

            for (i in 0 until faces.rows()) {
                val score = faces.get(i, 14)[0]

                if (score > bestScore) {
                    bestScore = score
                    bestIndex = i
                }
            }

            return faces.row(bestIndex).clone()

        } finally {
            faces.release()
        }
    }

    private fun extractDescriptor(
        src: Mat,
        face: Mat
    ): FloatArray {

        val aligned = Mat()
        val embedding = Mat()

        try {

            recognizer.alignCrop(src, face, aligned)
            recognizer.feature(aligned, embedding)

            return FloatArray(embedding.cols()).also {
                embedding.get(0, 0, it)
            }

        } finally {
            embedding.release()
            aligned.release()
            face.release()
        }
    }

    actual fun compare(
        descriptor1: FloatArray,
        descriptor2: FloatArray
    ): Float {
        require(descriptor1.size == 128) {
            "Expected 128-dimensional SFace descriptor."
        }

        require(descriptor1.size == descriptor2.size) {
            "Face descriptors must have the same size."
        }

        val feature1 = Mat(1, descriptor1.size, CvType.CV_32FC1)
        val feature2 = Mat(1, descriptor2.size, CvType.CV_32FC1)

        feature1.put(0, 0, descriptor1)
        feature2.put(0, 0, descriptor2)

        return recognizer.match(
            feature1,
            feature2,
            FaceRecognizerSF.FR_COSINE
        ).toFloat()
    }

    actual fun close() {
        if (!initialized) return
        initialized = false
    }

    companion object {
        private fun copyAsset(
            context: Context,
            assetName: String
        ): String {

            val file = File(context.filesDir, assetName)

            if (!file.exists() || file.length() == 0L) {

                context.assets.open(assetName).use { input ->

                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }

            return file.absolutePath
        }
    }
}