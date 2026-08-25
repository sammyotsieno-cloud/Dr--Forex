package com.example

import com.example.domain.engine.CsvInspector
import com.example.domain.engine.CsvInspectorImpl
import com.example.domain.engine.DataValidatorImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class CsvInspectionTest {

    private lateinit var inspector: CsvInspector
    private val validator = DataValidatorImpl()

    @Before
    fun setup() {
        inspector = CsvInspectorImpl(validator)
    }

    @Test
    fun `test standard CSV header extraction passes validation`() {
        val csvContent = """
            timestamp,open,high,low,close,volume
            1704067200000,1.0950,1.0980,1.0920,1.0960,150.0
            1704068100000,1.0960,1.0990,1.0950,1.0970,180.0
        """.trimIndent()

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "eurusd_m15.csv", fileSizeBytes = 2048L)

        assertEquals("eurusd_m15.csv", result.fileName)
        assertEquals(2048L, result.fileSizeBytes)
        assertEquals("2.0 KB", result.formattedFileSize)
        assertEquals(listOf("timestamp", "open", "high", "low", "close", "volume"), result.headers)
        assertTrue("Standard OHLC headers must be valid", result.isHeaderValid)
        assertEquals(0, result.headerErrors.size)
        assertFalse(result.isFileEmpty)
        assertEquals(null, result.errorMessage)
        assertEquals(2, result.samplePreviewRows.size)
    }

    @Test
    fun `test empty CSV file returns empty status and clear error`() {
        val stream = ByteArrayInputStream(ByteArray(0))
        val result = inspector.inspectStream(stream, fileName = "empty_file.csv", fileSizeBytes = 0L)

        assertEquals("empty_file.csv", result.fileName)
        assertTrue("Empty file must set isFileEmpty = true", result.isFileEmpty)
        assertFalse("Empty file must have isHeaderValid = false", result.isHeaderValid)
        assertTrue(result.headers.isEmpty())
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.contains("empty", ignoreCase = true))
    }

    @Test
    fun `test CSV with only whitespace and empty lines is safely treated as empty`() {
        val whitespaceContent = "\n  \n\r\n   \t \n"
        val stream = ByteArrayInputStream(whitespaceContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "blank.csv", fileSizeBytes = 12L)

        assertTrue(result.isFileEmpty)
        assertFalse(result.isHeaderValid)
        assertTrue(result.headers.isEmpty())
    }

    @Test
    fun `test CSV missing required column close reports validation error`() {
        val csvContent = """
            timestamp,open,high,low,vol
            1704067200000,1.0950,1.0980,1.0920,150.0
        """.trimIndent()

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "missing_close.csv")

        assertEquals(listOf("timestamp", "open", "high", "low", "vol"), result.headers)
        assertFalse("Missing 'close' column must be marked invalid", result.isHeaderValid)
        assertEquals(1, result.headerErrors.size)
        assertTrue(result.headerErrors.any { it.field == "close" })
    }

    @Test
    fun `test CSV with quoted headers extracts clean column names`() {
        val csvContent = """
            "Timestamp","Open","High","Low","Close","Volume"
            1704067200000,1.0950,1.0980,1.0920,1.0960,150.0
        """.trimIndent()

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "quoted.csv")

        assertEquals(listOf("Timestamp", "Open", "High", "Low", "Close", "Volume"), result.headers)
        assertTrue(result.isHeaderValid)
    }

    @Test
    fun `test CSV with UTF-8 BOM strips BOM character from first header`() {
        val csvContent = "\uFEFFtimestamp,open,high,low,close,volume\n1704067200000,1.0950,1.0980,1.0920,1.0960,100"
        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "bom.csv")

        assertEquals("timestamp", result.headers[0])
        assertFalse("First header must not contain BOM character", result.headers[0].startsWith("\uFEFF"))
        assertTrue(result.isHeaderValid)
    }

    @Test
    fun `test semicolon delimited CSV detection and extraction`() {
        val csvContent = """
            timestamp;open;high;low;close;volume
            1704067200000;1.0950;1.0980;1.0920;1.0960;100.0
        """.trimIndent()

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "european_format.csv")

        assertEquals(listOf("timestamp", "open", "high", "low", "close", "volume"), result.headers)
        assertTrue(result.isHeaderValid)
    }

    @Test
    fun `test tab delimited CSV detection and extraction`() {
        val csvContent = "timestamp\topen\thigh\tlow\tclose\tvolume\n1704067200000\t1.0950\t1.0980\t1.0920\t1.0960\t100.0"
        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "tab_separated.tsv")

        assertEquals(listOf("timestamp", "open", "high", "low", "close", "volume"), result.headers)
        assertTrue(result.isHeaderValid)
    }

    @Test
    fun `test unreadable or broken input stream handles error gracefully`() {
        val brokenStream = object : InputStream() {
            override fun read(): Int {
                throw IOException("Simulated disk read I/O failure")
            }
        }

        val result = inspector.inspectStream(brokenStream, fileName = "corrupt.csv")
        assertFalse(result.isHeaderValid)
        assertNotNull(result.errorMessage)
        assertTrue(result.errorMessage!!.contains("Simulated disk read I/O failure"))
    }

    @Test
    fun `test real-world CSV Etc UTC headers with mapping is valid and has no header errors`() {
        val csvContent = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
            2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
        """.trimIndent()

        val mapping = com.example.domain.model.CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = "Volume"
        )

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "eurusd_m15.csv", mapping = mapping)

        assertEquals(listOf("Etc/UTC", "Open", "High", "Low", "Close", "Volume"), result.headers)
        assertTrue("Real-world Etc/UTC mapped to Timestamp must be valid", result.isHeaderValid)
        assertEquals(0, result.headerErrors.size)
        assertFalse(result.isFileEmpty)
        assertEquals(null, result.errorMessage)
        assertEquals(2, result.samplePreviewRows.size)
    }

    @Test
    fun `test CSV missing required mapped field is rejected by downstream validation`() {
        val csvContent = """
            Etc/UTC,Open,High,Low,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,12231860000
        """.trimIndent()

        val mapping = com.example.domain.model.CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = null, // Close is missing
            volumeColumn = "Volume"
        )

        val stream = ByteArrayInputStream(csvContent.toByteArray(Charsets.UTF_8))
        val result = inspector.inspectStream(stream, fileName = "missing_close_mapped.csv", mapping = mapping)

        assertFalse("Missing required mapped field Close must fail validation", result.isHeaderValid)
        assertEquals(1, result.headerErrors.size)
        assertEquals("close", result.headerErrors[0].field)
    }

    @Test
    fun `test file size formatting helper`() {
        assertEquals("Unknown size", CsvInspectorImpl.formatFileSize(null))
        assertEquals("Unknown size", CsvInspectorImpl.formatFileSize(-1L))
        assertEquals("500 B", CsvInspectorImpl.formatFileSize(500L))
        assertEquals("1.0 KB", CsvInspectorImpl.formatFileSize(1024L))
        assertEquals("2.5 MB", CsvInspectorImpl.formatFileSize(2621440L))
        assertEquals("1.5 GB", CsvInspectorImpl.formatFileSize(1610612736L))
    }
}
