package edu.watumull.presencify.core.presentation.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.write


actual object ShareUtils {

    
    actual suspend fun shareText(text: String) {
        val newFile = FileKit.openFileSaver(
            suggestedName = "text.txt",
        )
        newFile?.write(text.encodeToByteArray())
    }

    
    actual suspend fun shareFile(file: ShareFileModel) {
        val newFile = FileKit.openFileSaver(
            suggestedName = file.fileName,
        )
        newFile?.write(file.bytes)
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
}