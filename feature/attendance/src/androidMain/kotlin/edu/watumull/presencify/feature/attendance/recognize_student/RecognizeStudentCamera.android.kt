package edu.watumull.presencify.feature.attendance.recognize_student

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import java.util.concurrent.Executors

private const val TAG = "[CameraX-Debug]"

@OptIn(ExperimentalResourceApi::class)
@Composable
actual fun RecognizeStudentCamera(
    modifier: Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    shouldCaptureEmbedding: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Log.d(TAG, "RecognizeStudentCamera RECOMPOSED. Perm: $cameraPermissionGranted | Liveness: $isLivenessComplete | Capture: $shouldCaptureEmbedding")

    val currentOnFaceDetected by rememberUpdatedState(onFaceDetected)
    val currentOnEmbeddingExtracted by rememberUpdatedState(onEmbeddingExtracted)
    val currentIsLivenessComplete by rememberUpdatedState(isLivenessComplete)
    val currentShouldCaptureEmbedding by rememberUpdatedState(shouldCaptureEmbedding)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permission request result: $isGranted")
        onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted) {
            val permission = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission already granted at Launch")
                onPermissionResult(true)
            } else {
                Log.d(TAG, "Launching permission request")
                launcher.launch(permission)
            }
        }
    }

    if (cameraPermissionGranted) {
        val analyzerState = remember { mutableStateOf<FaceAnalyzer?>(null) }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Starting to read MobileFaceNet.tflite...")
                    val modelBytes = Res.readBytes("files/MobileFaceNet.tflite")
                    Log.d(TAG, "Successfully read tflite model. Size: ${modelBytes.size} bytes")

                    val newAnalyzer = FaceAnalyzer(
                        modelBytes = modelBytes,
                        onFaceDetected = { currentOnFaceDetected(it) },
                        onEmbeddingExtracted = { currentOnEmbeddingExtracted(it) },
                        isLivenessCompleteProvider = { currentIsLivenessComplete },
                        shouldCaptureEmbeddingProvider = { currentShouldCaptureEmbedding }
                    )
                    Log.d(TAG, "FaceAnalyzer instance created successfully")
                    analyzerState.value = newAnalyzer
                } catch (e: Exception) {
                    Log.e(TAG, "FATAL: Failed to read tflite file or init FaceAnalyzer", e)
                }
            }
        }

        val analyzer = analyzerState.value

        if (analyzer != null) {
            AndroidView(
                modifier = modifier.onGloballyPositioned { logs ->
                    // Logs only if size changes to avoid spamming, but catches the initial layout
                    if (logs.size.width > 0 && logs.size.height > 0) {
                        Log.d(TAG, "PreviewView globally positioned. Size: ${logs.size.width} x ${logs.size.height}")
                    } else {
                        Log.w(TAG, "PreviewView globally positioned but size is 0x0!")
                    }
                },
                factory = { ctx ->
                    Log.d(TAG, "AndroidView Factory executing...")
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        Log.d(TAG, "CameraProviderFuture listener triggered")
                        try {
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also { analysisUseCase ->
                                    analysisUseCase.setAnalyzer(
                                        Executors.newSingleThreadExecutor(),
                                        analyzer
                                    )
                                }

                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                            Log.d(TAG, "Unbinding previous camera use cases...")
                            cameraProvider.unbindAll()

                            Log.d(TAG, "Attempting to bindToLifecycle...")
                            val camera = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalyzer
                            )
                            Log.d(TAG, "SUCCESS: Camera bound to lifecycle. Camera state: ${camera.cameraInfo.cameraState.value?.type}")

                        } catch (e: Exception) {
                            Log.e(TAG, "ERROR: Camera binding failed", e)
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        } else {
            Log.d(TAG, "Waiting for Analyzer to initialize...")
        }
    }
}

class FaceAnalyzer(
    private val modelBytes: ByteArray,
    private val onFaceDetected: (Float) -> Unit,
    private val onEmbeddingExtracted: (FloatArray) -> Unit,
    private val isLivenessCompleteProvider: () -> Boolean,
    private val shouldCaptureEmbeddingProvider: () -> Boolean
) : ImageAnalysis.Analyzer {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )

    private var interpreter: Interpreter? = null
    @Volatile private var isProcessing = false
    private var frameCount = 0

    init {
        loadModelAndInitInterpreter()
    }

    private fun loadModelAndInitInterpreter() {
        try {
            Log.d(TAG, "Allocating direct ByteBuffer for TFLite...")
            val buffer = ByteBuffer.allocateDirect(modelBytes.size)
            buffer.order(ByteOrder.nativeOrder())
            buffer.put(modelBytes)
            buffer.rewind()

            interpreter = Interpreter(buffer)
            Log.d(TAG, "TFLite Interpreter initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TFLite Interpreter", e)
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        frameCount++
        if (frameCount % 30 == 0) { // Log every ~1 second to prove feed is alive
            Log.d(TAG, "Analyze called. Frame #$frameCount. isProcessing=$isProcessing")
        }

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

                        // Log only periodically to avoid spam, unless a capture is triggered
                        if (frameCount % 30 == 0) {
                            Log.d(TAG, "Face detected. Yaw: $yaw. LivenessComplete: ${isLivenessCompleteProvider()}, ShouldCapture: ${shouldCaptureEmbeddingProvider()}")
                        }

                        if (!isLivenessCompleteProvider()) {
                            onFaceDetected(yaw)
                        } else if (shouldCaptureEmbeddingProvider()) {
                            Log.d(TAG, "CAPTURE TRIGGERED! Processing embedding...")
                            isProcessing = true
                            try {
                                if (interpreter != null) {
                                    val bitmap = imageProxy.toBitmap()
                                    val faceBitmap = cropFace(bitmap, face.boundingBox)
                                    val scaledBitmap = Bitmap.createScaledBitmap(faceBitmap, 112, 112, true)
                                    val input = preprocess(scaledBitmap)
                                    val output = Array(1) { FloatArray(128) }

                                    Log.d(TAG, "Running TFLite inference...")
                                    interpreter?.run(input, output)
                                    Log.d(TAG, "Inference successful. Extracting embedding...")
                                    onEmbeddingExtracted(output[0])
                                } else {
                                    Log.e(TAG, "Interpreter is NULL during capture!")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error during embedding extraction", e)
                            } finally {
                                isProcessing = false
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ML Kit Face Detection failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            Log.w(TAG, "mediaImage is null for frame #$frameCount")
            imageProxy.close()
        }
    }

    private fun cropFace(bitmap: Bitmap, box: android.graphics.Rect): Bitmap {
        val x = box.left.coerceAtLeast(0)
        val y = box.top.coerceAtLeast(0)
        val width = box.width().coerceAtMost(bitmap.width - x)
        val height = box.height().coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val inputSize = 112
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF)
            val g = (pixel shr 8 and 0xFF)
            val b = (pixel and 0xFF)

            buffer.putFloat((r - 127.5f) / 128.0f)
            buffer.putFloat((g - 127.5f) / 128.0f)
            buffer.putFloat((b - 127.5f) / 128.0f)
        }
        return buffer
    }
}