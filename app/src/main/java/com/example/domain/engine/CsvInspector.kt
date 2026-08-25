package com.example.domain.engine

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.domain.model.CsvInspectionResult
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Locale

/**
 * Service for safely inspecting CSV files selected via Android Storage Access Framework (SAF).
 * Extracts metadata, raw header, parsed column headers, and preliminary OHLC header validity.
 */
interface CsvInspector {
    /**
     * Inspects a CSV input stream, extracting file metadata, raw header, parsed headers,
     * header validation errors against required OHLC columns, and sample preview rows.
     */
    fun inspectStream(
        inputStream: InputStream,
        fileName: String = "unknown.csv",
        fileSizeBytes: Long? = null
    ): CsvInspectionResult

    /**
     * Queries Android ContentResolver for file name and size metadata via SAF Uri,
     * then inspects the stream safely.
     */
    fun inspectUri(
        contentResolver: ContentResolver,
        uri: Uri
    ): CsvInspectionResult
}

class CsvInspectorImpl(
    private val dataValidator: DataValidator = DataValidatorImpl()
) : CsvInspector {

    override fun inspectStream(
        inputStream: InputStream,
        fileName: String,
        fileSizeBytes: Long?
    ): CsvInspectionResult {
        val formattedSize = formatFileSize(fileSizeBytes)
        return try {
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))

            // Read lines, skipping potential UTF-8 BOM on first line
            var firstLine: String? = null
            while (true) {
                val line = reader.readLine() ?: break
                val cleaned = line.removePrefix("\uFEFF").trim()
                if (cleaned.isNotEmpty()) {
                    firstLine = cleaned
                    break
                }
            }

            if (firstLine == null) {
                return CsvInspectionResult(
                    fileName = fileName,
                    fileSizeBytes = fileSizeBytes,
                    formattedFileSize = formattedSize,
                    isFileEmpty = true,
                    isHeaderValid = false,
                    errorMessage = "The selected CSV file is empty."
                )
            }

            // Detect delimiter: comma, semicolon, or tab
            val delimiter = detectDelimiter(firstLine)
            val rawTokens = firstLine.split(delimiter)
            val headers = rawTokens.map { it.trim().removeSurrounding("\"").removeSurrounding("'") }
                .filter { it.isNotEmpty() }

            if (headers.isEmpty()) {
                return CsvInspectionResult(
                    fileName = fileName,
                    fileSizeBytes = fileSizeBytes,
                    formattedFileSize = formattedSize,
                    rawHeaderLine = firstLine,
                    headers = emptyList(),
                    isHeaderValid = false,
                    errorMessage = "No valid column headers detected in the first row."
                )
            }

            // Validate headers against required columns (timestamp, open, high, low, close)
            val headerErrors = dataValidator.validateCsvHeaders(headers)
            val isHeaderValid = headerErrors.isEmpty()

            // Read up to 3 sample preview rows
            val previewRows = mutableListOf<List<String>>()
            var rowCount = 0
            while (rowCount < 3) {
                val line = reader.readLine() ?: break
                val trimmed = line.trim()
                if (trimmed.isNotEmpty()) {
                    val tokens = trimmed.split(delimiter).map { it.trim().removeSurrounding("\"") }
                    previewRows.add(tokens)
                    rowCount++
                }
            }

            CsvInspectionResult(
                fileName = fileName,
                fileSizeBytes = fileSizeBytes,
                formattedFileSize = formattedSize,
                headers = headers,
                rawHeaderLine = firstLine,
                headerErrors = headerErrors,
                isHeaderValid = isHeaderValid,
                samplePreviewRows = previewRows,
                isFileEmpty = false,
                errorMessage = null
            )
        } catch (e: Exception) {
            CsvInspectionResult(
                fileName = fileName,
                fileSizeBytes = fileSizeBytes,
                formattedFileSize = formattedSize,
                isFileEmpty = false,
                isHeaderValid = false,
                errorMessage = "Error reading CSV stream: ${e.localizedMessage ?: e.message ?: "Unknown I/O error"}"
            )
        }
    }

    override fun inspectUri(
        contentResolver: ContentResolver,
        uri: Uri
    ): CsvInspectionResult {
        var fileName = "selected_dataset.csv"
        var fileSize: Long? = null

        // 1. Query Display Name and Size from ContentResolver
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        val name = cursor.getString(nameIndex)
                        if (!name.isNullOrBlank()) {
                            fileName = name
                        }
                    }
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
            }
        } catch (_: Exception) {
            uri.lastPathSegment?.let { segment ->
                if (segment.isNotBlank()) fileName = segment
            }
        }

        // 2. Open stream and inspect
        return try {
            val stream = contentResolver.openInputStream(uri)
                ?: return CsvInspectionResult(
                    fileName = fileName,
                    fileSizeBytes = fileSize,
                    formattedFileSize = formatFileSize(fileSize),
                    isFileEmpty = false,
                    isHeaderValid = false,
                    errorMessage = "Unable to open input stream for the selected file."
                )

            stream.use { s ->
                inspectStream(s, fileName, fileSize)
            }
        } catch (e: Exception) {
            CsvInspectionResult(
                fileName = fileName,
                fileSizeBytes = fileSize,
                formattedFileSize = formatFileSize(fileSize),
                isFileEmpty = false,
                isHeaderValid = false,
                errorMessage = "Failed to access file: ${e.localizedMessage ?: e.message ?: "Access error"}"
            )
        }
    }

    private fun detectDelimiter(line: String): String {
        val commaCount = line.count { it == ',' }
        val semiCount = line.count { it == ';' }
        val tabCount = line.count { it == '\t' }

        return when {
            semiCount > commaCount && semiCount > tabCount -> ";"
            tabCount > commaCount && tabCount > semiCount -> "\t"
            else -> ","
        }
    }

    companion object {
        fun formatFileSize(bytes: Long?): String {
            if (bytes == null || bytes < 0) return "Unknown size"
            if (bytes < 1024) return "$bytes B"
            val kb = bytes / 1024.0
            if (kb < 1024) return String.format(Locale.US, "%.1f KB", kb)
            val mb = kb / 1024.0
            if (mb < 1024) return String.format(Locale.US, "%.1f MB", mb)
            val gb = mb / 1024.0
            return String.format(Locale.US, "%.1f GB", gb)
        }
    }
}
