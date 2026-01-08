package edu.watumull.presencify.core.presentation.utils

/**
 * Utility functions for CSV file handling and conversion
 */
object CsvUtils {
    
    /**
     * Converts a ByteArray to a string using the specified charset.
     * 
     * @param csvBytes The CSV file data as ByteArray
     * @param charset The character encoding (default: UTF-8)
     * @return String content of the CSV file
     */
    fun bytesToString(csvBytes: ByteArray, charset: String = "UTF-8"): String {
        return csvBytes.decodeToString()
    }
    
    /**
     * Converts a string to ByteArray using the specified charset.
     * 
     * @param csvString The CSV content as string
     * @return ByteArray of the CSV content
     */
    fun stringToBytes(csvString: String): ByteArray {
        return csvString.encodeToByteArray()
    }
    
    /**
     * Validates if the file content appears to be a valid CSV.
     * Performs basic validation by checking for common CSV characteristics.
     * 
     * @param csvContent The CSV content as string
     * @return true if the content appears to be valid CSV, false otherwise
     */
    fun validateCsvContent(csvContent: String): Boolean {
        if (csvContent.isBlank()) return false
        
        // Check if content has lines
        val lines = csvContent.lines()
        if (lines.isEmpty()) return false
        
        // Very basic validation - check if there are commas or tabs (common delimiters)
        val hasDelimiters = csvContent.contains(',') || csvContent.contains('\t') || csvContent.contains(';')
        
        return hasDelimiters || lines.size > 1
    }
    
    /**
     * Gets the number of rows in the CSV content.
     * 
     * @param csvContent The CSV content as string
     * @return Number of rows (including header)
     */
    fun getRowCount(csvContent: String): Int {
        return csvContent.lines().filter { it.isNotBlank() }.size
    }
    
    /**
     * Gets the number of columns in the CSV based on the first row.
     * Assumes comma as delimiter.
     * 
     * @param csvContent The CSV content as string
     * @param delimiter The delimiter used in the CSV (default: comma)
     * @return Number of columns, or 0 if no valid data
     */
    fun getColumnCount(csvContent: String, delimiter: Char = ','): Int {
        val firstLine = csvContent.lines().firstOrNull { it.isNotBlank() }
        return firstLine?.split(delimiter)?.size ?: 0
    }
    
    /**
     * Gets the header row (first line) of the CSV.
     * 
     * @param csvContent The CSV content as string
     * @return Header row as string, or null if no data
     */
    fun getHeader(csvContent: String): String? {
        return csvContent.lines().firstOrNull { it.isNotBlank() }
    }
    
    /**
     * Validates the CSV file size.
     * 
     * @param csvBytes The CSV file data as ByteArray
     * @param maxSizeInMB The maximum allowed size in megabytes (default: 10MB)
     * @return true if the file size is valid, false otherwise
     */
    fun validateFileSize(csvBytes: ByteArray, maxSizeInMB: Int = 10): Boolean {
        val maxSizeInBytes = maxSizeInMB * 1024 * 1024
        return csvBytes.size <= maxSizeInBytes
    }
    
    /**
     * Gets the file size in megabytes.
     * 
     * @param csvBytes The CSV file data as ByteArray
     * @return Size in megabytes
     */
    fun getFileSizeInMB(csvBytes: ByteArray): Double {
        return csvBytes.size.toDouble() / (1024 * 1024)
    }
    
    /**
     * Validates if the CSV has the expected number of columns.
     * 
     * @param csvContent The CSV content as string
     * @param expectedColumnCount The expected number of columns
     * @param delimiter The delimiter used in the CSV (default: comma)
     * @return true if all non-empty rows have the expected column count
     */
    fun validateColumnCount(
        csvContent: String,
        expectedColumnCount: Int,
        delimiter: Char = ','
    ): Boolean {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        return lines.all { line ->
            line.split(delimiter).size == expectedColumnCount
        }
    }
    
    /**
     * Parses CSV content into a list of rows, where each row is a list of values.
     * Basic CSV parser that handles simple cases.
     * 
     * @param csvContent The CSV content as string
     * @param delimiter The delimiter used in the CSV (default: comma)
     * @param skipHeader Whether to skip the first row (header)
     * @return List of rows, where each row is a list of string values
     */
    fun parseCsv(
        csvContent: String,
        delimiter: Char = ',',
        skipHeader: Boolean = false
    ): List<List<String>> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        val startIndex = if (skipHeader) 1 else 0
        
        return lines.drop(startIndex).map { line ->
            line.split(delimiter).map { it.trim() }
        }
    }
}

/**
 * Extension function to convert ByteArray to CSV string
 */
fun ByteArray.toCsvString(): String = CsvUtils.bytesToString(this)

/**
 * Extension function to validate CSV file size
 */
fun ByteArray.isValidCsvSize(maxSizeInMB: Int = 10): Boolean = 
    CsvUtils.validateFileSize(this, maxSizeInMB)

/**
 * Extension function to get CSV file size in MB
 */
fun ByteArray.csvSizeInMB(): Double = CsvUtils.getFileSizeInMB(this)

/**
 * Extension function to validate CSV content
 */
fun String.isValidCsv(): Boolean = CsvUtils.validateCsvContent(this)

/**
 * Extension function to get CSV row count
 */
fun String.csvRowCount(): Int = CsvUtils.getRowCount(this)

/**
 * Extension function to get CSV column count
 */
fun String.csvColumnCount(delimiter: Char = ','): Int = 
    CsvUtils.getColumnCount(this, delimiter)

/**
 * Extension function to get CSV header
 */
fun String.csvHeader(): String? = CsvUtils.getHeader(this)

/**
 * Extension function to parse CSV
 */
fun String.parseCsv(delimiter: Char = ',', skipHeader: Boolean = false): List<List<String>> =
    CsvUtils.parseCsv(this, delimiter, skipHeader)
