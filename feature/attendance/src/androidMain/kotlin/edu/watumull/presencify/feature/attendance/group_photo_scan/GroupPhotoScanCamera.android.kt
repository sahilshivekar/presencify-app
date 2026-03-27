package edu.watumull.presencify.feature.attendance.group_photo_scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.camera.view.PreviewView
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "GroupPhotoScanCamera"

@Composable
actual fun GroupPhotoScanCamera(
    onImageCaptured: (ByteArray) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            // Best-effort: if permission is not granted, just log. In a full
            // implementation you would request permission from an Activity.
            Log.w(TAG, "Camera permission not granted for GroupPhotoScanCamera")
        }
    }

    if (!hasCameraPermission) {
        // Do not render camera if we don't have permission.
        return
    }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { ImageCapture.Builder().build() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            imageCapture = imageCapture,
            lifecycleOwner = lifecycleOwner,
            context = context,
        )

        FloatingActionButton(
            onClick = {
                takePhoto(
                    context = context,
                    imageCapture = imageCapture,
                    cameraExecutor = cameraExecutor,
                    onImageCaptured = onImageCaptured,
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Capture classroom photo",
            )
        }
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner,
    context: Context,
) {
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    cameraExecutor: ExecutorService,
    onImageCaptured: (ByteArray) -> Unit,
) {
    val photoFile = File.createTempFile("group_photo_", ".jpg", context.cacheDir)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                try {
                    val uri: Uri? = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    val file = uri?.toFile() ?: photoFile
                    val bytes = file.readBytes()
                    onImageCaptured(bytes)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to read captured image bytes", e)
                }
            }
        }
    )
}
