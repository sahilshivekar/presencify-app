package edu.watumull.presencify.core.presentation.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


object ImageUtils {
    
    
    @OptIn(ExperimentalEncodingApi::class)
    fun toBase64String(imageBytes: ByteArray): String {
        return Base64.encode(imageBytes)
    }
    
    
    @OptIn(ExperimentalEncodingApi::class)
    fun fromBase64String(base64String: String): ByteArray {
        return Base64.decode(base64String)
    }
    
    
    fun validateImageSize(imageBytes: ByteArray, maxSizeInMB: Int = 5): Boolean {
        val maxSizeInBytes = maxSizeInMB * 1024 * 1024
        return imageBytes.size <= maxSizeInBytes
    }
    
    
    fun getImageSizeInMB(imageBytes: ByteArray): Double {
        return imageBytes.size.toDouble() / (1024 * 1024)
    }
    
    
    fun validateImageFormat(imageBytes: ByteArray): Boolean {
        if (imageBytes.size < 4) return false
        
        return when {
            imageBytes[0] == 0xFF.toByte() && imageBytes[1] == 0xD8.toByte() -> true
            imageBytes[0] == 0x89.toByte() && imageBytes[1] == 0x50.toByte() && 
            imageBytes[2] == 0x4E.toByte() && imageBytes[3] == 0x47.toByte() -> true
            imageBytes[0] == 0x47.toByte() && imageBytes[1] == 0x49.toByte() && imageBytes[2] == 0x46.toByte() -> true
            imageBytes[0] == 0x42.toByte() && imageBytes[1] == 0x4D.toByte() -> true
            imageBytes[0] == 0x52.toByte() && imageBytes[1] == 0x49.toByte() && 
            imageBytes[2] == 0x46.toByte() && imageBytes[3] == 0x46.toByte() -> true
            else -> false
        }
    }
    
    
    fun getImageFormat(imageBytes: ByteArray): String {
        if (imageBytes.size < 4) return "Unknown"
        
        return when {
            imageBytes[0] == 0xFF.toByte() && imageBytes[1] == 0xD8.toByte() -> "JPEG"
            imageBytes[0] == 0x89.toByte() && imageBytes[1] == 0x50.toByte() && 
            imageBytes[2] == 0x4E.toByte() && imageBytes[3] == 0x47.toByte() -> "PNG"
            imageBytes[0] == 0x47.toByte() && imageBytes[1] == 0x49.toByte() && imageBytes[2] == 0x46.toByte() -> "GIF"
            imageBytes[0] == 0x42.toByte() && imageBytes[1] == 0x4D.toByte() -> "BMP"
            imageBytes[0] == 0x52.toByte() && imageBytes[1] == 0x49.toByte() && 
            imageBytes[2] == 0x46.toByte() && imageBytes[3] == 0x46.toByte() -> "WEBP"
            else -> "Unknown"
        }
    }
}


@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.toBase64(): String = ImageUtils.toBase64String(this)


fun ByteArray.isValidImageSize(maxSizeInMB: Int = 5): Boolean = ImageUtils.validateImageSize(this, maxSizeInMB)


fun ByteArray.imageSizeInMB(): Double = ImageUtils.getImageSizeInMB(this)


fun ByteArray.isValidImageFormat(): Boolean = ImageUtils.validateImageFormat(this)


fun ByteArray.imageFormat(): String = ImageUtils.getImageFormat(this)
