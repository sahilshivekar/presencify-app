package edu.watumull.presencify.feature.attendance.recognize_student

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.graphics.Bitmap

@Composable
actual fun RecognizeStudentCamera(
    modifier: Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted) {
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                 onPermissionResult(true)
            } else {
                 launcher.launch(permission)
            }
        }
    }

    if (cameraPermissionGranted) {
        // We use a FrameLayout container to ensure PreviewView measures correctly even inside AndroidView
        val previewView = remember {
            PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        }

        val analyzer = remember {
            FaceAnalyzer(
                context = context,
                onFaceDetected = onFaceDetected,
                onEmbeddingExtracted = onEmbeddingExtracted,
                initialIsLivenessComplete = isLivenessComplete
            )
        }

        LaunchedEffect(isLivenessComplete) {
            analyzer.updateLivenessComplete(isLivenessComplete)
        }

        LaunchedEffect(onFaceDetected, onEmbeddingExtracted) {
            analyzer.updateCallbacks(onFaceDetected, onEmbeddingExtracted)
        }

        // Bind camera lifecycle
        LaunchedEffect(cameraPermissionGranted) {
            val cameraProvider = context.getCameraProvider()

            try {
                // Must unbind before rebinding
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(previewView.surfaceProvider)

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                    }

                var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                if (!cameraProvider.hasCamera(cameraSelector)) {
                     cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                }

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("RecognizeStudentCamera", "Camera binding failed", e)
            }
        }

        AndroidView(
            modifier = modifier,
            factory = {
                FrameLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    addView(previewView)
                }
            }
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, ContextCompat.getMainExecutor(this))
    }
}


class FaceAnalyzer(
    private val context: Context,
    private var onFaceDetected: (Float) -> Unit,
    private var onEmbeddingExtracted: (FloatArray) -> Unit,
    initialIsLivenessComplete: Boolean
) : ImageAnalysis.Analyzer {

    private var isLivenessComplete = initialIsLivenessComplete

    fun updateLivenessComplete(isComplete: Boolean) {
        isLivenessComplete = isComplete
    }

    fun updateCallbacks(
        newOnFaceDetected: (Float) -> Unit,
        newOnEmbeddingExtracted: (FloatArray) -> Unit
    ) {
        onFaceDetected = newOnFaceDetected
        onEmbeddingExtracted = newOnEmbeddingExtracted
    }

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )

    private var interpreter: Interpreter? = null
    private var isProcessing = false

    init {
        loadModelAndInitInterpreter()
    }


    private fun loadModelAndInitInterpreter() {
        var modelBuffer: ByteBuffer? = null
        var lastError: Exception? = null

        // Potential paths where Compose Resources might place the file in assets
        val potentialPaths = listOf(
            "compose-resources/files/MobileFaceNet.tflite",
            "files/MobileFaceNet.tflite",
            "MobileFaceNet.tflite"
        )

        // 1. Try loading as FileDescriptor (Best for TFLite - Memory Mapping)
        for (path in potentialPaths) {
            try {
                modelBuffer = loadModelFile(context, path)
                Log.d("FaceAnalyzer", "Successfully loaded model from assets: $path")
                break
            } catch (e: Exception) {
                lastError = e
            }
        }

        // 2. Fallback: Try loading as Stream (In case asset is compressed)
        if (modelBuffer == null) {
            for (path in potentialPaths) {
                try {
                    modelBuffer = loadModelFromStream(context, path)
                    Log.d("FaceAnalyzer", "Successfully loaded model from stream: $path")
                    break
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }

        if (modelBuffer != null) {
            try {
                interpreter = Interpreter(modelBuffer)
            } catch (e: Exception) {
                Log.e("FaceAnalyzer", "Error initializing Interpreter with buffer", e)
            }
        } else {
             Log.e("FaceAnalyzer", "Failed to load MobileFaceNet.tflite. Assets: ${context.assets.list("")?.toList()}", lastError)
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val face = faces.first()
                        val yaw = face.headEulerAngleY

                        if (!isLivenessComplete) {
                            onFaceDetected(yaw)
                        } else {
                            // Run recognition
                            isProcessing = true
                            try {
                                if (interpreter != null) {
                                    // Extract bitmap, crop face, resize, normalize
                                    // Warning: mediaImage is in YUV. Need conversion to Bitmap.
                                    // Usually simplest is to use ToBitmap() extension or similar.
                                    // Since we don't have it handy without library, we rely on having one.
                                    // Or use imageProxy.toBitmap() (available in newer CameraX + core-ktx).

                                    val bitmap = imageProxy.toBitmap()
                                    val faceBitmap = cropFace(bitmap, face.boundingBox)
                                    val scaledBitmap = Bitmap.createScaledBitmap(faceBitmap, 112, 112, true)
                                    val input = preprocess(scaledBitmap)
                                    val output = Array(1) { FloatArray(128) } // MobileFaceNet output 192 or 128? Prompt says 128.

                                    interpreter?.run(input, output)
                                    onEmbeddingExtracted(output[0])
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                isProcessing = false // Or keep true to stop further processing?
                                // If successful, maybe stop.
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun loadModelFile(context: Context, path: String): ByteBuffer {
        val fileDescriptor = context.assets.openFd(path)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadModelFromStream(context: Context, path: String): ByteBuffer {
        val inputStream = context.assets.open(path)
        val bytes = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(bytes)
        buffer.rewind()
        return buffer
    }

    private fun cropFace(bitmap: Bitmap, box: android.graphics.Rect): Bitmap {
        // Ensure box is within bitmap bounds
        val x = box.left.coerceAtLeast(0)
        val y = box.top.coerceAtLeast(0)
        val width = box.width().coerceAtMost(bitmap.width - x)
        val height = box.height().coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val inputSize = 112
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4) // Float32: 4 bytes
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        // Normalize [-1.0, 1.0]
        for (pixel in intValues) {
            // RGB
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            // (value - 127.5) / 128.0 for [-1, 1] range commonly used with MobileFaceNet
            buffer.putFloat((r - 127.5f) / 128.0f)
            buffer.putFloat((g - 127.5f) / 128.0f)
            buffer.putFloat((b - 127.5f) / 128.0f)
        }
        return buffer
    }
}
