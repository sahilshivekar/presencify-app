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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import edu.watumull.presencify.feature.attendance.add_student_biometrics.FaceEmbeddingExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.util.concurrent.Executors

private const val TAG = "RecognizeStudentCam"

@OptIn(ExperimentalResourceApi::class)
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

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "Initializing FaceEmbeddingExtractor...")
                    val extractor = FaceEmbeddingExtractor()
                    val newAnalyzer = FaceAnalyzer(
                        faceEmbeddingExtractor = extractor,
                        onFaceDetected = { currentOnFaceDetected(it) },
                        onEmbeddingExtracted = { arr1, arr2 ->
                            currentOnEmbeddingExtracted(arr1, arr2)
                        },
                        isLivenessCompleteProvider = { currentIsLivenessComplete },
                        shouldCaptureEmbeddingProvider = { currentShouldCaptureEmbedding },
                        onFaceMissingDuringCriticalStep = {
                            // We can't call ViewModel directly here, but we can signal via yaw=0
                            // and let VM interpret if needed, or handle in a dedicated action.
                        }
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
//                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                                .setTargetResolution(android.util.Size(1280, 720)) // High Res!
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
                                Log.d(TAG, "Camera bound successfully.")
                            } catch (e: Exception) {
                                Log.e(TAG, "Binding failed", e)
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
            val bitmap = imageProxy.toBitmap()
            val safeBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            val image = InputImage.fromBitmap(safeBitmap, 0) // Use safeBitmap here
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

                            // SYNC FIX: Relaxed back to -12..12 so it matches Liveness state perfectly.
                            if (yaw in -12f..12f) {
                                Log.d(
                                    TAG,
                                    "Head stable (Yaw: $yaw). Capture triggered. Proceeding to crop and extract..."
                                )
                                isProcessing = true
                                hasCaptured = true

                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        Log.d(TAG, "Full Camera Frame Size: ${safeBitmap.width}x${safeBitmap.height}, MLKit Face Box: ${face.boundingBox}")

                                        // Crop face once using the new dynamic padding
                                        val faceBitmap = faceEmbeddingExtractor.cropFace(safeBitmap, face.boundingBox)
                                        Log.d(TAG, "Cropped Face Bitmap Size: ${faceBitmap.width}x${faceBitmap.height}")

                                        // Original orientation embedding
                                        Log.d(TAG, "Starting extraction for ORIGINAL embedding...")
                                        val originalEmbedding = faceEmbeddingExtractor.extractFromFaceBitmap(faceBitmap)
                                        if (originalEmbedding != null) {
                                            Log.d(TAG, "SUCCESS: Original Embedding Extracted. Array Sum: ${originalEmbedding.sum()} | First 3: ${originalEmbedding.take(3)}")
                                        } else {
                                            Log.e(TAG, "FAILED: Original Embedding came back null.")
                                        }

                                        // Mirrored orientation embedding
                                        Log.d(TAG, "Starting extraction for MIRRORED embedding...")
                                        val mirroredBitmap = faceEmbeddingExtractor.mirrorBitmap(faceBitmap)
                                        val mirroredEmbedding = faceEmbeddingExtractor.extractFromFaceBitmap(mirroredBitmap)
                                        if (mirroredEmbedding != null) {
                                            Log.d(TAG, "SUCCESS: Mirrored Embedding Extracted. Array Sum: ${mirroredEmbedding.sum()} | First 3: ${mirroredEmbedding.take(3)}")
                                        } else {
                                            Log.e(TAG, "FAILED: Mirrored Embedding came back null.")
                                        }

                                        if (originalEmbedding != null && mirroredEmbedding != null) {
                                            Log.d(
                                                TAG,
                                                "Both embeddings extracted successfully. Passing to ViewModel..."
                                            )
                                            onEmbeddingExtracted(originalEmbedding, mirroredEmbedding)
                                        } else {
                                            Log.e(
                                                TAG,
                                                "One or both embeddings returned null. Resetting capture flag."
                                            )
                                            hasCaptured = false
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Embedding Extraction Pipeline Crash", e)
                                        hasCaptured = false
                                    } finally {
                                        isProcessing = false
                                        imageProxy.close()
                                    }
                                }
                                return@addOnSuccessListener
                            } else {
                                Log.d(TAG, "Waiting for head to stabilize... Current Yaw: $yaw")
                            }
                        }
                    } else {
                        if (!isLivenessCompleteProvider()) {
                            onFaceMissingDuringCriticalStep()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Face detection failed", e)
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
}