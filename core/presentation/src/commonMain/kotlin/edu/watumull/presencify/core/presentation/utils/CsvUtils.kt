package edu.watumull.presencify.core.presentation.utils


object CsvUtils {
    
    
    fun bytesToString(csvBytes: ByteArray, charset: String = "UTF-8"): String {
        return csvBytes.decodeToString()
    }
    
    
    fun stringToBytes(csvString: String): ByteArray {
        return csvString.encodeToByteArray()
    }
    
    
    fun validateCsvContent(csvContent: String): Boolean {
        if (csvContent.isBlank()) return false
        
        val lines = csvContent.lines()
        if (lines.isEmpty()) return false
        
        val hasDelimiters = csvContent.contains(',') || csvContent.contains('\t') || csvContent.contains(';')
        
        return hasDelimiters || lines.size > 1
    }
    
    
    fun getRowCount(csvContent: String): Int {
        return csvContent.lines().filter { it.isNotBlank() }.size
    }
    
    
    fun getColumnCount(csvContent: String, delimiter: Char = ','): Int {
        val firstLine = csvContent.lines().firstOrNull { it.isNotBlank() }
        return firstLine?.split(delimiter)?.size ?: 0
    }
    
    
    fun getHeader(csvContent: String): String? {
        return csvContent.lines().firstOrNull { it.isNotBlank() }
    }
    
    
    fun validateFileSize(csvBytes: ByteArray, maxSizeInMB: Int = 10): Boolean {
        val maxSizeInBytes = maxSizeInMB * 1024 * 1024
        return csvBytes.size <= maxSizeInBytes
    }
    
    
    fun getFileSizeInMB(csvBytes: ByteArray): Double {
        return csvBytes.size.toDouble() / (1024 * 1024)
    }
    
    
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
    
    
    fun parseCsv(
        csvContent: String,
        delimiter: Char = ',',
        skipHeader: Boolean = false
    ): List<List<String>> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        val startIndex = if (skipHeader) 1 else 0
        
        return lines.drop(startIndex).map { line ->
            line.split(delimiter).map {
                it.trim().removeSurrounding("\"").trim()
            }
        }
    }
}


fun ByteArray.toCsvString(): String = CsvUtils.bytesToString(this)


fun ByteArray.isValidCsvSize(maxSizeInMB: Int = 10): Boolean = 
    CsvUtils.validateFileSize(this, maxSizeInMB)


fun ByteArray.csvSizeInMB(): Double = CsvUtils.getFileSizeInMB(this)


fun String.isValidCsv(): Boolean = CsvUtils.validateCsvContent(this)


fun String.csvRowCount(): Int = CsvUtils.getRowCount(this)


fun String.csvColumnCount(delimiter: Char = ','): Int = 
    CsvUtils.getColumnCount(this, delimiter)


fun String.csvHeader(): String? = CsvUtils.getHeader(this)


fun String.parseCsv(delimiter: Char = ',', skipHeader: Boolean = false): List<List<String>> =
    CsvUtils.parseCsv(this, delimiter, skipHeader)
