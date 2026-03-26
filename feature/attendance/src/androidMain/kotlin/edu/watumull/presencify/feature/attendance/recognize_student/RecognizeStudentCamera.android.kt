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
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
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
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
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
            val image = InputImage.fromBitmap(bitmap, 0)

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
                            Log.d(
                                "RecognizeStudentCam",
                                "Capture triggered. Processing embedding via shared extractor (original + mirrored)..."
                            )
                            isProcessing = true
                            hasCaptured = true

                            GlobalScope.launch(Dispatchers.IO) {
                                try {
                                    // Crop face once
                                    val faceBitmap = cropFace(bitmap, face.boundingBox)

                                    // Original orientation embedding
                                    val originalEmbedding = faceEmbeddingExtractor.extractFromFaceBitmap(faceBitmap)

                                    // Mirrored orientation embedding
                                    val mirroredBitmap = faceEmbeddingExtractor.mirrorBitmap(faceBitmap)
                                    val mirroredEmbedding = faceEmbeddingExtractor.extractFromFaceBitmap(mirroredBitmap)

                                    if (originalEmbedding != null && mirroredEmbedding != null) {
                                        Log.d(
                                            "RecognizeStudentCam",
                                            "Embeddings extracted. Original size=${originalEmbedding.size}, Mirrored size=${mirroredEmbedding.size}"
                                        )
                                        onEmbeddingExtracted(originalEmbedding, mirroredEmbedding)
                                    } else {
                                        Log.e(
                                            "RecognizeStudentCam",
                                            "Embedding extraction returned null (original or mirrored)"
                                        )
                                        hasCaptured = false
                                    }
                                } catch (e: Exception) {
                                    Log.e("RecognizeStudentCam", "Embedding Extraction Crash", e)
                                    hasCaptured = false
                                } finally {
                                    isProcessing = false
                                    imageProxy.close()
                                }
                            }
                            return@addOnSuccessListener
                        }
                    } else {
                        // No faces detected in this frame. If we are in critical liveness steps,
                        // invoke callback so VM can treat it as cheating.
                        if (!isLivenessCompleteProvider()) {
                            onFaceMissingDuringCriticalStep()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RecognizeStudentCam", "Face detection failed", e)
                }
                .addOnCompleteListener {
                    if (!isProcessing) {
                        imageProxy.close()
                    }
                }
        } catch (e: Exception) {
            Log.e("RecognizeStudentCam", "Failed to process image frame", e)
            imageProxy.close()
        }
    }

    private fun cropFace(bitmap: Bitmap, box: android.graphics.Rect): Bitmap {
        val padding = 10
        val x = (box.left - padding).coerceAtLeast(0)
        val y = (box.top - padding).coerceAtLeast(0)
        val width = (box.width() + padding * 2).coerceAtMost(bitmap.width - x)
        val height = (box.height() + padding * 2).coerceAtMost(bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, width, height)
    }
}