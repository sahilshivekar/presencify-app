package edu.watumull.presencify.core.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.shareFile
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.write
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication


actual object ShareUtils {

    
    actual suspend fun shareText(text: String) {
        val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
        val activityViewController = UIActivityViewController(listOf(text), null)
        currentViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null,
        )
    }

    
    actual suspend fun shareFile(file: ShareFileModel) {
        try {
            val compressedBytes = if (file.mime == MimeType.IMAGE) {
                compressImage(file.bytes)
            } else {
                file.bytes
            }

            val fileToShare = saveFile(data = compressedBytes, fileName = file.fileName)
            FileKit.shareFile(fileToShare)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    
    actual suspend fun shareImage(title: String, image: ImageBitmap) {
        image.asSkiaBitmap().readPixels()?.let {
            FileKit.saveImageToGallery(
                bytes = it,
                filename = "$title.png",
            )
        }
    }

    
    actual suspend fun shareImage(title: String, byte: ByteArray) {
        FileKit.saveImageToGallery(
            bytes = byte,
            filename = "$title.png",
        )
    }

    
    private suspend fun saveFile(data: ByteArray, fileName: String): PlatformFile {
        val tempFile = PlatformFile(FileKit.cacheDir, fileName)
        tempFile.write(data)

        
        val nsUrl = NSURL.fileURLWithPath(tempFile.absolutePath())
        return PlatformFile(nsUrl)
    }

    
    private suspend fun compressImage(imageBytes: ByteArray): ByteArray {
        return FileKit.compressImage(
            bytes = imageBytes,
            quality = 100,
            maxWidth = 1024,
            maxHeight = 1024,
            imageFormat = ImageFormat.PNG,
        )
    }

    
    private fun MimeType.toIosUti(): String = when (this) {
        MimeType.PDF -> "com.adobe.pdf"
        MimeType.TEXT -> "public.plain-text"
        MimeType.IMAGE -> "public.image"
        MimeType.CSV -> "public.comma-separated-values-text"
    }
}