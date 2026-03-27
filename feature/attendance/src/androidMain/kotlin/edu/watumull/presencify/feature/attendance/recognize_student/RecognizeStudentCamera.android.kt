package edu.watumull.presencify.feature.attendance.recognize_student

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

private const val TAG = "RecognizeStudentCam"

@Composable
actual fun RecognizeStudentCamera(
    modifier: Modifier,
    onFaceDetected: (Float) -> Unit,
    onEmbeddingExtracted: (FloatArray, FloatArray) -> Unit,
    isLivenessComplete: Boolean,
    shouldCaptureEmbedding: Boolean,
    cameraPermissionGranted: Boolean,
    onPermissionResult: (Boolean) -> Unit,
    onCheatingDetected: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentOnFaceDetected by rememberUpdatedState(onFaceDetected)
    val currentOnEmbeddingExtracted by rememberUpdatedState(onEmbeddingExtracted)
    val currentIsLivenessComplete by rememberUpdatedState(isLivenessComplete)
    val currentShouldCaptureEmbedding by rememberUpdatedState(shouldCaptureEmbedding)

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
        val analyzerState = remember { mutableStateOf<FaceAnalyzer?>(null) }

        val extractor = remember { FaceEmbeddingExtractor() }

        DisposableEffect(Unit) {
            onDispose {
                Log.d(TAG, "Disposing Camera Composable. Cleaning up ONNX and ML Kit memory.")
                extractor.close()
                analyzerState.value?.close()
            }
        }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Initializing FaceEmbeddingExtractor (ONNX Runtime)...")
                    extractor.initialize(context)
                    Log.d(TAG, "ONNX Environment and Sessions Initialized Successfully.")

                    val newAnalyzer = FaceAnalyzer(
                        faceEmbeddingExtractor = extractor,
                        onFaceDetected = { currentOnFaceDetected(it) },
                        onEmbeddingExtracted = { arr1, arr2 ->
                            currentOnEmbeddingExtracted(arr1, arr2)
                        },
                        isLivenessCompleteProvider = { currentIsLivenessComplete },
                        shouldCaptureEmbeddingProvider = { currentShouldCaptureEmbedding },
                        onFaceMissingDuringCriticalStep = { onCheatingDetected() }
                    )
                    analyzerState.value = newAnalyzer
                    Log.d(TAG, "FaceAnalyzer initialization complete.")
                } catch (e: Exception) {
                    Log.e(TAG, "FaceAnalyzer init failed", e)
                }
            }
        }

        val analyzer = analyzerState.value

        if (analyzer != null) {
            AndroidView(
                modifier = modifier,
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    val observer = LifecycleEventObserver { _, event ->
                        val cameraProvider = cameraProviderFuture.get()

                        if (event == Lifecycle.Event.ON_RESUME) {
                            Log.d(TAG, "ON_RESUME: Binding Camera")
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val imageAnalyzer = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_FRONT_CAMERA,
                                    preview,
                                    imageAnalyzer
                                )
                                Log.d(TAG, "Camera bound successfully to DEFAULT_FRONT_CAMERA.")
                            } catch (e: Exception) {
                                Log.e(TAG, "Camera Binding failed", e)
                            }
                        } else if (event == Lifecycle.Event.ON_PAUSE) {
                            Log.d(TAG, "ON_PAUSE: Unbinding Camera")
                            cameraProvider.unbindAll()
                        }
                    }

                    lifecycleOwner.lifecycle.addObserver(observer)
                    previewView
                },
                onRelease = {
                    Log.d(TAG, "Releasing AndroidView. Unbinding Camera.")
                    val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                    cameraProvider.unbindAll()
                }
            )
        }
    }
}

class FaceAnalyzer(
    private val faceEmbeddingExtractor: FaceEmbeddingExtractor,
    private val onFaceDetected: (Float) -> Unit,
    private val onEmbeddingExtracted: (FloatArray, FloatArray) -> Unit,
    private val isLivenessCompleteProvider: () -> Boolean,
    private val shouldCaptureEmbeddingProvider: () -> Boolean,
    private val onFaceMissingDuringCriticalStep: () -> Unit
) : ImageAnalysis.Analyzer {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .enableTracking()
            .build()
    )

    @Volatile
    private var isProcessing = false

    @Volatile
    private var hasCaptured = false
    private var lastLivenessReportTime = 0L

    private val analyzerScope = CoroutineScope(Dispatchers.IO)

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val shouldCapture = shouldCaptureEmbeddingProvider()

        if (!shouldCapture) {
            hasCaptured = false
        }

        if (isProcessing || hasCaptured) {
            imageProxy.close()
            return
        }

        try {
            val baseBitmap = imageProxy.toBitmap()
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()

            // Rotate bitmap so ONNX receives an upright image
            val uprightBitmap = if (rotationDegrees != 0f) {
                val matrix = Matrix().apply { postRotate(rotationDegrees) }
                Bitmap.createBitmap(baseBitmap, 0, 0, baseBitmap.width, baseBitmap.height, matrix, true)
            } else {
                baseBitmap.copy(Bitmap.Config.ARGB_8888, true)
            }

            // Pass upright bitmap with 0 rotation to ML Kit
            val image = InputImage.fromBitmap(uprightBitmap, 0)

            faceDetector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val face = faces.first()
                        val yaw = face.headEulerAngleY

                        if (!isLivenessCompleteProvider()) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastLivenessReportTime > 400) {
                                lastLivenessReportTime = currentTime
                                onFaceDetected(yaw)
                            }
                        } else if (shouldCapture && !hasCaptured) {

                            if (yaw in -12f..12f) {
                                Log.d(TAG, "---------------------------------------------------")
                                Log.d(TAG, "Head stable (Yaw: $yaw). Liveness Complete!")
                                Log.d(TAG, "Triggering ONNX Handoff. Image Size: ${uprightBitmap.width}x${uprightBitmap.height}")
                                Log.d(TAG, "---------------------------------------------------")

                                isProcessing = true
                                hasCaptured = true

                                analyzerScope.launch {
                                    try {
                                        // 1. Original Embedding
                                        Log.d(TAG, "Starting ONNX extraction for ORIGINAL embedding...")
                                        val originalEmbedding = faceEmbeddingExtractor.generateSingleDescriptor(uprightBitmap)

                                        if (originalEmbedding != null) {
                                            Log.d(TAG, "SUCCESS: Original Extracted. Array Sum: ${originalEmbedding.sum()} | First 3: ${originalEmbedding.take(3).joinToString(", ")}")
                                        } else {
                                            Log.e(TAG, "FAILED: ONNX Original Embedding came back null. (No face detected by BlazeFace)")
                                        }

                                        // 2. Mirrored Embedding
                                        Log.d(TAG, "Creating mirrored bitmap and starting MIRRORED extraction...")
                                        val mirrorMatrix = Matrix().apply { preScale(-1.0f, 1.0f) }
                                        val mirroredBitmap = Bitmap.createBitmap(
                                            uprightBitmap, 0, 0,
                                            uprightBitmap.width, uprightBitmap.height,
                                            mirrorMatrix, false
                                        )

                                        val mirroredEmbedding = faceEmbeddingExtractor.generateSingleDescriptor(mirroredBitmap)

                                        if (mirroredEmbedding != null) {
                                            Log.d(TAG, "SUCCESS: Mirrored Extracted. Array Sum: ${mirroredEmbedding.sum()} | First 3: ${mirroredEmbedding.take(3).joinToString(", ")}")
                                        } else {
                                            Log.e(TAG, "FAILED: ONNX Mirrored Embedding came back null.")
                                        }

                                        // 3. Final Handoff
                                        if (originalEmbedding != null && mirroredEmbedding != null) {
                                            Log.d(TAG, "Both ONNX embeddings extracted successfully. Passing to ViewModel...")
                                            onEmbeddingExtracted(originalEmbedding, mirroredEmbedding)
                                        } else {
                                            Log.e(TAG, "One or both ONNX embeddings returned null. Resetting capture flag.")
                                            hasCaptured = false
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "FATAL: ONNX Embedding Extraction Pipeline Crashed", e)
                                        hasCaptured = false
                                    } finally {
                                        isProcessing = false
                                        imageProxy.close()
                                    }
                                }
                                return@addOnSuccessListener
                            } else {
                                // Optional trace logging, usually too noisy but helpful if it gets stuck
                                // Log.v(TAG, "Waiting for head to stabilize... Current Yaw: $yaw")
                            }
                        }
                    } else {
                        if (!isLivenessCompleteProvider()) {
                            onFaceMissingDuringCriticalStep()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "ML Kit Face detection failed", e)
                }
                .addOnCompleteListener {
                    if (!isProcessing) {
                        imageProxy.close()
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process image frame", e)
            imageProxy.close()
        }
    }

    fun close() {
        faceDetector.close()
    }
}