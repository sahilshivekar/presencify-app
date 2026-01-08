package edu.watumull.presencify.core.presentation.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes

/**
 * Cross-platform image picker utility for selecting images from device storage.
 * Uses FileKit library to provide consistent API across Android, iOS, and Desktop platforms.
 */
object ImagePicker {
    
    /**
     * Opens a file picker dialog to select an image file.
     * 
     * @return ByteArray of the selected image, or null if no image was selected or an error occurred.
     */
    suspend fun pickImage(): ByteArray? {
        return try {
            val file = FileKit.openFilePicker(FileKitType.Image)
            file?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Opens a file picker dialog to select multiple image files.
     * Note: This is a placeholder as FileKit doesn't natively support multiple selections in the current version.
     * You may need to call pickImage() multiple times or implement platform-specific solutions.
     * 
     * @return List of ByteArrays of the selected images, or empty list if no images were selected or an error occurred.
     */
    suspend fun pickMultipleImages(): List<ByteArray> {
        return try {
            // Note: FileKit.openFilePicker doesn't support multiple=true directly
            // This would need to be implemented platform-specifically
            val file = FileKit.openFilePicker(FileKitType.Image)
            if (file != null) {
                listOf(file.readBytes())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

/**
 * Result wrapper for image picking operations
 */
sealed class ImagePickerResult {
    data class Success(val imageData: ByteArray) : ImagePickerResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Success

            return imageData.contentEquals(other.imageData)
        }

        override fun hashCode(): Int {
            return imageData.contentHashCode()
        }
    }
    
    data class MultipleSuccess(val imagesData: List<ByteArray>) : ImagePickerResult()
    data object Cancelled : ImagePickerResult()
    data class Error(val message: String) : ImagePickerResult()
}

/**
 * Extension function to pick an image with result wrapper
 */
suspend fun ImagePicker.pickImageWithResult(): ImagePickerResult {
    return try {
        val imageData = pickImage()
        if (imageData != null) {
            ImagePickerResult.Success(imageData)
        } else {
            ImagePickerResult.Cancelled
        }
    } catch (e: Exception) {
        ImagePickerResult.Error(e.message ?: "Unknown error occurred")
    }
}

/**
 * Extension function to pick multiple images with result wrapper
 */
suspend fun ImagePicker.pickMultipleImagesWithResult(): ImagePickerResult {
    return try {
        val imagesData = pickMultipleImages()
        if (imagesData.isNotEmpty()) {
            ImagePickerResult.MultipleSuccess(imagesData)
        } else {
            ImagePickerResult.Cancelled
        }
    } catch (e: Exception) {
        ImagePickerResult.Error(e.message ?: "Unknown error occurred")
    }
}
