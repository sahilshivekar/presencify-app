package edu.watumull.presencify.core.presentation.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes


object ImagePicker {
    
    
    suspend fun pickImage(): ByteArray? {
        return try {
            val file = FileKit.openFilePicker(FileKitType.Image)
            file?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    
    suspend fun pickMultipleImages(): List<ByteArray> {
        return try {
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
