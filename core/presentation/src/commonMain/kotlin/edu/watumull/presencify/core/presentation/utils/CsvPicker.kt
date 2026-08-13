package edu.watumull.presencify.core.presentation.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes


object CsvPicker {
    
    
    suspend fun pickCsvFile(): ByteArray? {
        return try {
            val file = FileKit.openFilePicker()
            file?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    
    suspend fun pickCsvAsString(charset: String = "UTF-8"): String? {
        return try {
            val bytes = pickCsvFile()
            bytes?.decodeToString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    
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
