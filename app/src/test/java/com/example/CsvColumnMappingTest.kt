package com.example

import com.example.domain.engine.CsvColumnMapper
import com.example.domain.engine.CsvColumnMapperImpl
import com.example.domain.model.CsvColumnMapping
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Phase 2 — Milestone 2.2: Unit tests for CSV Column Mapping and Validation.
 */
class CsvColumnMappingTest {

    private lateinit var mapper: CsvColumnMapper

    @Before
    fun setup() {
        mapper = CsvColumnMapperImpl()
    }

    /**
     * TEST 1 — Exact real-world CSV:
     * Header: Etc/UTC,Open,High,Low,Close,Volume
     * Must automatically produce:
     * Timestamp -> Etc/UTC
     * Open -> Open
     * High -> High
     * Low -> Low
     * Close -> Close
     * Volume -> Volume
     */
    @Test
    fun `test real world CSV Etc UTC header mapping`() {
        val headers = listOf("Etc/UTC", "Open", "High", "Low", "Close", "Volume")
        val mapping = mapper.autoDetectMapping(headers)

        assertEquals("Etc/UTC", mapping.timestampColumn)
        assertEquals("Open", mapping.openColumn)
        assertEquals("High", mapping.highColumn)
        assertEquals("Low", mapping.lowColumn)
        assertEquals("Close", mapping.closeColumn)
        assertEquals("Volume", mapping.volumeColumn)

        val validation = mapper.validateMapping(mapping, headers)
        assertTrue("Mapping should be fully valid", validation.isValid)
        assertTrue("No missing required fields", validation.missingRequiredFields.isEmpty())
        assertTrue("No duplicate mappings", validation.duplicateColumns.isEmpty())
        assertTrue("No error messages", validation.errorMessages.isEmpty())
    }

    /**
     * TEST 2 — Case-insensitive matching:
     * Headers in ALL CAPS or mixed cases should map cleanly.
     */
    @Test
    fun `test case-insensitive header mapping`() {
        val headers = listOf("TIMESTAMP", "OPEN", "HIGH", "LOW", "CLOSE", "VOLUME")
        val mapping = mapper.autoDetectMapping(headers)

        assertEquals("TIMESTAMP", mapping.timestampColumn)
        assertEquals("OPEN", mapping.openColumn)
        assertEquals("HIGH", mapping.highColumn)
        assertEquals("LOW", mapping.lowColumn)
        assertEquals("CLOSE", mapping.closeColumn)
        assertEquals("VOLUME", mapping.volumeColumn)

        val validation = mapper.validateMapping(mapping, headers)
        assertTrue("Case-insensitive mapping must be valid", validation.isValid)
        assertTrue(validation.missingRequiredFields.isEmpty())
    }

    /**
     * TEST 3 — Common timestamp aliases:
     * Test: timestamp, datetime, date_time, time, Etc/UTC, Date
     */
    @Test
    fun `test common timestamp aliases detection`() {
        val testCases = listOf(
            listOf("timestamp", "open", "high", "low", "close") to "timestamp",
            listOf("datetime", "open", "high", "low", "close") to "datetime",
            listOf("date_time", "open", "high", "low", "close") to "date_time",
            listOf("time", "open", "high", "low", "close") to "time",
            listOf("Etc/UTC", "open", "high", "low", "close") to "Etc/UTC",
            listOf("Date", "open", "high", "low", "close") to "Date",
            listOf("Local_Time", "open", "high", "low", "close") to "Local_Time",
            listOf("Time (UTC)", "open", "high", "low", "close") to "Time (UTC)"
        )

        for ((headers, expectedTimestamp) in testCases) {
            val mapping = mapper.autoDetectMapping(headers)
            assertEquals("Failed alias match for headers: $headers", expectedTimestamp, mapping.timestampColumn)
            val validation = mapper.validateMapping(mapping, headers)
            assertTrue("Mapping for $headers should be valid", validation.isValid)
        }
    }

    /**
     * TEST 4 — Missing required field:
     * Header: Etc/UTC,Open,High,Low,Volume (Close is missing)
     * Must fail validation and identify missing Close field.
     */
    @Test
    fun `test missing required field Close fails validation`() {
        val headers = listOf("Etc/UTC", "Open", "High", "Low", "Volume")
        val mapping = mapper.autoDetectMapping(headers)

        assertEquals("Etc/UTC", mapping.timestampColumn)
        assertEquals("Open", mapping.openColumn)
        assertEquals("High", mapping.highColumn)
        assertEquals("Low", mapping.lowColumn)
        assertNull("Close should not be mapped", mapping.closeColumn)
        assertEquals("Volume", mapping.volumeColumn)

        val validation = mapper.validateMapping(mapping, headers)
        assertFalse("Mapping must be invalid when Close is missing", validation.isValid)
        assertTrue("missingRequiredFields must contain Close", validation.missingRequiredFields.contains("Close"))
        assertTrue("errorMessages must mention Close", validation.errorMessages.any { it.contains("Close") })
    }

    /**
     * TEST 5 — Missing multiple required fields:
     * Header: Timestamp, Volume
     * Must identify Open, High, Low, Close as missing.
     */
    @Test
    fun `test multiple missing required fields`() {
        val headers = listOf("Timestamp", "Volume")
        val mapping = mapper.autoDetectMapping(headers)

        val validation = mapper.validateMapping(mapping, headers)
        assertFalse(validation.isValid)
        assertEquals(4, validation.missingRequiredFields.size)
        assertTrue(validation.missingRequiredFields.containsAll(listOf("Open", "High", "Low", "Close")))
    }

    /**
     * TEST 6 — Duplicate mapping prevention:
     * Mapping the same CSV column to multiple required fields (e.g. Open and High) must fail.
     */
    @Test
    fun `test duplicate column mapping fails validation`() {
        val headers = listOf("Etc/UTC", "Price", "Low", "Close")
        val customMapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Price",
            highColumn = "Price", // Duplicate: same as open
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = null
        )

        val validation = mapper.validateMapping(customMapping, headers)
        assertFalse("Duplicate column mapping must be invalid", validation.isValid)
        assertTrue("duplicateColumns must contain 'Price'", validation.duplicateColumns.containsKey("Price"))
        val mappedFields = validation.duplicateColumns["Price"]
        assertTrue(mappedFields != null && mappedFields.containsAll(listOf("Open", "High")))
        assertTrue(validation.errorMessages.any { it.contains("Price") && it.contains("multiple") })
    }

    /**
     * TEST 7 — Optional Volume handling:
     * Header: Etc/UTC,Open,High,Low,Close (No Volume)
     * Must be valid because Volume is optional.
     */
    @Test
    fun `test optional volume absent is valid`() {
        val headers = listOf("Etc/UTC", "Open", "High", "Low", "Close")
        val mapping = mapper.autoDetectMapping(headers)

        assertEquals("Etc/UTC", mapping.timestampColumn)
        assertEquals("Open", mapping.openColumn)
        assertEquals("High", mapping.highColumn)
        assertEquals("Low", mapping.lowColumn)
        assertEquals("Close", mapping.closeColumn)
        assertNull("Volume should be null", mapping.volumeColumn)

        val validation = mapper.validateMapping(mapping, headers)
        assertTrue("Mapping without volume must be valid", validation.isValid)
        assertTrue("missingRequiredFields must be empty", validation.missingRequiredFields.isEmpty())
        assertTrue("duplicateColumns must be empty", validation.duplicateColumns.isEmpty())
    }

    /**
     * TEST 8 — User override behavior:
     * If user modifies any column mapping manually, validation correctly verifies the override.
     */
    @Test
    fun `test user override replaces automatic mapping`() {
        val headers = listOf("Date", "Time", "BidOpen", "BidHigh", "BidLow", "BidClose", "Volume")
        val autoMapping = mapper.autoDetectMapping(headers)

        // User overrides timestamp to "Time" and open to "BidOpen"
        val userOverride = autoMapping.copy(
            timestampColumn = "Time",
            openColumn = "BidOpen",
            highColumn = "BidHigh",
            lowColumn = "BidLow",
            closeColumn = "BidClose"
        )

        assertEquals("Time", userOverride.timestampColumn)
        assertEquals("BidOpen", userOverride.openColumn)
        assertEquals("BidHigh", userOverride.highColumn)
        assertEquals("BidLow", userOverride.lowColumn)
        assertEquals("BidClose", userOverride.closeColumn)

        val validation = mapper.validateMapping(userOverride, headers)
        assertTrue("User overridden mapping must be valid", validation.isValid)
    }

    /**
     * TEST 9 — Invalid column not in CSV headers:
     * Mapping a column name that does not exist in headers fails validation.
     */
    @Test
    fun `test mapping nonexistent column fails validation`() {
        val headers = listOf("Etc/UTC", "Open", "High", "Low", "Close")
        val invalidMapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "NonExistentColumn",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close"
        )

        val validation = mapper.validateMapping(invalidMapping, headers)
        assertFalse("Nonexistent column mapping must be invalid", validation.isValid)
        assertTrue(validation.errorMessages.any { it.contains("NonExistentColumn") })
    }

    /**
     * TEST 10 — Empty headers input handling:
     * Auto mapping with empty headers returns empty mapping and invalid validation.
     */
    @Test
    fun `test empty headers returns empty mapping and fails validation`() {
        val mapping = mapper.autoDetectMapping(emptyList())
        val validation = mapper.validateMapping(mapping, emptyList())

        assertFalse(validation.isValid)
        assertEquals(5, validation.missingRequiredFields.size)
    }
}
