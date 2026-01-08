package edu.watumull.presencify.core.presentation.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Utility functions for image handling and conversion
 */
object ImageUtils {
    
    /**
     * Converts a ByteArray to a Base64 encoded string.
     * Useful for sending images to server APIs.
     * 
     * @param imageBytes The image data as ByteArray
     * @return Base64 encoded string
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun toBase64String(imageBytes: ByteArray): String {
        return Base64.encode(imageBytes)
    }
    
    /**
     * Converts a Base64 encoded string to ByteArray.
     * Useful for receiving images from server APIs.
     * 
     * @param base64String The Base64 encoded image string
     * @return ByteArray of the image data
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun fromBase64String(base64String: String): ByteArray {
        return Base64.decode(base64String)
    }
    
    /**
     * Validates if the image size is within the allowed limit.
     * 
     * @param imageBytes The image data as ByteArray
     * @param maxSizeInMB The maximum allowed size in megabytes (default: 5MB)
     * @return true if the image size is valid, false otherwise
     */
    fun validateImageSize(imageBytes: ByteArray, maxSizeInMB: Int = 5): Boolean {
        val maxSizeInBytes = maxSizeInMB * 1024 * 1024
        return imageBytes.size <= maxSizeInBytes
    }
    
    /**
     * Gets the size of the image in megabytes.
     * 
     * @param imageBytes The image data as ByteArray
     * @return Size in megabytes
     */
    fun getImageSizeInMB(imageBytes: ByteArray): Double {
        return imageBytes.size.toDouble() / (1024 * 1024)
    }
    
    /**
     * Validates if the image format is supported based on file signature (magic numbers).
     * Supports: JPEG, PNG, GIF, BMP, WEBP
     * 
     * @param imageBytes The image data as ByteArray
     * @return true if the format is supported, false otherwise
     */
    fun validateImageFormat(imageBytes: ByteArray): Boolean {
        if (imageBytes.size < 4) return false
        
        return when {
            // JPEG
            imageBytes[0] == 0xFF.toByte() && imageBytes[1] == 0xD8.toByte() -> true
            // PNG
            imageBytes[0] == 0x89.toByte() && imageBytes[1] == 0x50.toByte() && 
            imageBytes[2] == 0x4E.toByte() && imageBytes[3] == 0x47.toByte() -> true
            // GIF
            imageBytes[0] == 0x47.toByte() && imageBytes[1] == 0x49.toByte() && imageBytes[2] == 0x46.toByte() -> true
            // BMP
            imageBytes[0] == 0x42.toByte() && imageBytes[1] == 0x4D.toByte() -> true
            // WEBP
            imageBytes[0] == 0x52.toByte() && imageBytes[1] == 0x49.toByte() && 
            imageBytes[2] == 0x46.toByte() && imageBytes[3] == 0x46.toByte() -> true
            else -> false
        }
    }
    
    /**
     * Gets the image format name based on file signature.
     * 
     * @param imageBytes The image data as ByteArray
     * @return Format name (JPEG, PNG, GIF, BMP, WEBP) or "Unknown"
     */
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

/**
 * Extension function to convert ByteArray to Base64 string
 */
@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.toBase64(): String = ImageUtils.toBase64String(this)

/**
 * Extension function to validate image size
 */
fun ByteArray.isValidImageSize(maxSizeInMB: Int = 5): Boolean = ImageUtils.validateImageSize(this, maxSizeInMB)

/**
 * Extension function to get image size in MB
 */
fun ByteArray.imageSizeInMB(): Double = ImageUtils.getImageSizeInMB(this)

/**
 * Extension function to validate image format
 */
fun ByteArray.isValidImageFormat(): Boolean = ImageUtils.validateImageFormat(this)

/**
 * Extension function to get image format
 */
fun ByteArray.imageFormat(): String = ImageUtils.getImageFormat(this)
