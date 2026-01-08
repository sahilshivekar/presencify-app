package edu.watumull.presencify.core.presentation.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes

/**
 * Cross-platform CSV file picker utility for selecting CSV files from device storage.
 * Uses FileKit library to provide consistent API across Android, iOS, and Desktop platforms.
 */
object CsvPicker {
    
    /**
     * Opens a file picker dialog to select a CSV file.
     * 
     * @return ByteArray of the selected CSV file, or null if no file was selected or an error occurred.
     */
    suspend fun pickCsvFile(): ByteArray? {
        return try {
            // FileKit will show all files, user should select CSV
            val file = FileKit.openFilePicker()
            file?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Opens a file picker dialog to select a CSV file and converts it to a string.
     * 
     * @param charset The character encoding to use (default: UTF-8)
     * @return String content of the CSV file, or null if no file was selected or an error occurred.
     */
    suspend fun pickCsvAsString(charset: String = "UTF-8"): String? {
        return try {
            val bytes = pickCsvFile()
            bytes?.decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Opens a file picker dialog to select multiple CSV files.
     * 
     * @return List of ByteArrays of the selected CSV files, or empty list if no files were selected.
     */
    suspend fun pickMultipleCsvFiles(): List<ByteArray> {
        return try {
            val file = FileKit.openFilePicker()
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
 * Result wrapper for CSV file picking operations
 */
sealed class CsvPickerResult {
    data class Success(val csvData: ByteArray) : CsvPickerResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Success

            return csvData.contentEquals(other.csvData)
        }

        override fun hashCode(): Int {
            return csvData.contentHashCode()
        }
    }
    
    data class SuccessAsString(val csvContent: String) : CsvPickerResult()
    data class MultipleSuccess(val csvFiles: List<ByteArray>) : CsvPickerResult()
    data object Cancelled : CsvPickerResult()
    data class Error(val message: String) : CsvPickerResult()
}

/**
 * Extension function to pick a CSV file with result wrapper
 */
suspend fun CsvPicker.pickCsvFileWithResult(): CsvPickerResult {
    return try {
        val csvData = pickCsvFile()
        if (csvData != null) {
            CsvPickerResult.Success(csvData)
        } else {
            CsvPickerResult.Cancelled
        }
    } catch (e: Exception) {
        CsvPickerResult.Error(e.message ?: "Unknown error occurred")
    }
}

/**
 * Extension function to pick a CSV file as string with result wrapper
 */
suspend fun CsvPicker.pickCsvAsStringWithResult(): CsvPickerResult {
    return try {
        val csvContent = pickCsvAsString()
        if (csvContent != null) {
            CsvPickerResult.SuccessAsString(csvContent)
        } else {
            CsvPickerResult.Cancelled
        }
    } catch (e: Exception) {
        CsvPickerResult.Error(e.message ?: "Unknown error occurred")
    }
}

/**
 * Extension function to pick multiple CSV files with result wrapper
 */
suspend fun CsvPicker.pickMultipleCsvFilesWithResult(): CsvPickerResult {
    return try {
        val csvFiles = pickMultipleCsvFiles()
        if (csvFiles.isNotEmpty()) {
            CsvPickerResult.MultipleSuccess(csvFiles)
        } else {
            CsvPickerResult.Cancelled
        }
    } catch (e: Exception) {
        CsvPickerResult.Error(e.message ?: "Unknown error occurred")
    }
}
