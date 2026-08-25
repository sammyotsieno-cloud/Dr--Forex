package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DrForexDatabase
import com.example.data.local.dao.CandleDao
import com.example.data.local.dao.DatasetDao
import com.example.data.repository.DatasetRepository
import com.example.data.repository.DatasetRepositoryImpl
import com.example.domain.engine.CsvCandleImporter
import com.example.domain.engine.CsvCandleImporterImpl
import com.example.domain.engine.CsvColumnMapper
import com.example.domain.engine.CsvColumnMapperImpl
import com.example.domain.engine.DataValidatorImpl
import com.example.domain.model.CsvColumnMapping
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.ValidationStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CsvCandleImportTest {

    private lateinit var database: DrForexDatabase
    private lateinit var candleDao: CandleDao
    private lateinit var datasetDao: DatasetDao
    private lateinit var datasetRepository: DatasetRepository
    private lateinit var importer: CsvCandleImporter
    private lateinit var columnMapper: CsvColumnMapper

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, DrForexDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        candleDao = database.candleDao()
        datasetDao = database.datasetDao()
        datasetRepository = DatasetRepositoryImpl(datasetDao, candleDao)
        importer = CsvCandleImporterImpl(DataValidatorImpl())
        columnMapper = CsvColumnMapperImpl()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * TEST 1 — Normal valid CSV
     * Input:
     * Etc/UTC,Open,High,Low,Close,Volume
     * 2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
     * 2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
     * 2026-08-25T09:00:00+00:00,1.16672,1.16678,1.16659,1.16674,135880000
     * Expected: 3 valid candles.
     */
    @Test
    fun `TEST 1 - Normal valid CSV processes all rows into valid candles`() {
        val csv = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
            2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
            2026-08-25T09:00:00+00:00,1.16672,1.16678,1.16659,1.16674,135880000
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = "Volume"
        )

        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(3, result.validCandles.size)
        assertEquals(3, result.totalRowsRead)
        assertEquals(0, result.rejectedRows.size)

        val first = result.validCandles[0]
        assertEquals(1.16687, first.open, 0.00001)
        assertEquals(1.16687, first.high, 0.00001)
        assertEquals(1.16548, first.low, 0.00001)
        assertEquals(1.16557, first.close, 0.00001)
        assertEquals(12231860000.0, first.volume, 0.0001)
    }

    /**
     * TEST 2 — Volume absent
     * Input:
     * Etc/UTC,Open,High,Low,Close
     * 2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557
     * Expected: Valid candle with volume defaulted to 0.0.
     */
    @Test
    fun `TEST 2 - Volume absent produces valid candle with 0 volume`() {
        val csv = """
            Etc/UTC,Open,High,Low,Close
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = null
        )

        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(1, result.validCandles.size)
        assertEquals(0, result.rejectedRows.size)
        assertEquals(0.0, result.validCandles[0].volume, 0.00001)
    }

    /**
     * TEST 3 — Invalid timestamp
     * One row with an invalid timestamp.
     * Expected: Row rejected without crashing, remaining valid rows preserved.
     */
    @Test
    fun `TEST 3 - Invalid timestamp row is rejected without crashing`() {
        val csv = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,100
            INVALID_TIMESTAMP_STRING,1.16557,1.16677,1.16510,1.16673,200
            2026-08-25T09:00:00+00:00,1.16672,1.16678,1.16659,1.16674,300
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = "Volume"
        )

        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(2, result.validCandles.size)
        assertEquals(3, result.totalRowsRead)
        assertEquals(1, result.rejectedRows.size)
        assertEquals(3, result.rejectedRows[0].rowNumber) // Header is line 1, first data row 2, second row 3
        assertTrue(result.rejectedRows[0].reason.contains("timestamp", ignoreCase = true))
    }

    /**
     * TEST 4 — Invalid numeric OHLC
     * One row containing a non-numeric OHLC value.
     * Expected: Row rejected without crashing.
     */
    @Test
    fun `TEST 4 - Invalid numeric OHLC row is rejected without crashing`() {
        val csv = """
            timestamp,open,high,low,close,volume
            1704067200000,1.0950,1.0980,1.0920,1.0960,150.0
            1704068100000,NON_NUMERIC,1.0990,1.0950,1.0970,180.0
            1704069000000,1.0970,1.0995,1.0965,1.0985,200.0
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "timestamp",
            openColumn = "open",
            highColumn = "high",
            lowColumn = "low",
            closeColumn = "close",
            volumeColumn = "volume"
        )

        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(2, result.validCandles.size)
        assertEquals(1, result.rejectedRows.size)
        assertTrue(result.rejectedRows[0].reason.contains("Open", ignoreCase = true))
    }

    /**
     * TEST 5 — Missing required value
     * A row missing Close.
     * Expected: Row rejected.
     */
    @Test
    fun `TEST 5 - Row missing Close value is rejected`() {
        val csv = """
            timestamp,open,high,low,close,volume
            1704067200000,1.0950,1.0980,1.0920,,150.0
            1704068100000,1.0960,1.0990,1.0950,1.0970,180.0
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "timestamp",
            openColumn = "open",
            highColumn = "high",
            lowColumn = "low",
            closeColumn = "close",
            volumeColumn = "volume"
        )

        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(1, result.validCandles.size)
        assertEquals(1, result.rejectedRows.size)
        assertTrue(result.rejectedRows[0].reason.contains("Close", ignoreCase = true))
    }

    /**
     * TEST 6 — Multiple valid rows
     * Confirm all valid rows are converted correctly into MarketCandle objects.
     */
    @Test
    fun `TEST 6 - Multiple valid rows are converted into MarketCandle objects`() {
        val csv = """
            time,open,high,low,close,volume
            1704067200000,1.0950,1.0980,1.0920,1.0960,100
            1704068100000,1.0960,1.0990,1.0950,1.0970,110
            1704069000000,1.0970,1.0995,1.0965,1.0985,120
            1704069900000,1.0985,1.1010,1.0980,1.1005,130
        """.trimIndent()

        val mapping = columnMapper.autoDetectMapping(listOf("time", "open", "high", "low", "close", "volume"))
        val stream = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, mapping)

        assertEquals(4, result.validCandles.size)
        assertEquals(0, result.rejectedRows.size)
        assertEquals(1704067200000L, result.validCandles[0].timestamp)
        assertEquals(1704069900000L, result.validCandles[3].timestamp)
    }

    /**
     * TEST 7 — Duplicate and re-import behaviour
     * Verify that importing the same candle twice into the same dataset does not create unintended duplicates in Room.
     * 1. First import: 3 candles imported, 0 skipped.
     * 2. Exact same CSV imported again: 0 new candles, 3 duplicates skipped.
     * 3. Database candle count remains strictly 3.
     */
    @Test
    fun `TEST 7 - Re-importing identical candles does not duplicate records in Room`() = runBlocking {
        val csv = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
            2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
            2026-08-25T09:00:00+00:00,1.16672,1.16678,1.16659,1.16674,135880000
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "Etc/UTC",
            openColumn = "Open",
            highColumn = "High",
            lowColumn = "Low",
            closeColumn = "Close",
            volumeColumn = "Volume"
        )

        // 1. First import: 3 candles imported, 0 skipped
        val stream1 = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result1 = importer.parseAndValidateRows(stream1, mapping)
        val datasetId = "DS-EURUSD-M15-EURUSDM15"

        val (inserted1, skipped1) = datasetRepository.insertCandles(datasetId, result1.validCandles)
        assertEquals(3, inserted1)
        assertEquals(0, skipped1)
        assertEquals(3, datasetRepository.getCandleCountByDatasetId(datasetId))
        assertEquals(3, inserted1 + skipped1)

        // 2. Second import of exact same CSV: 0 new candles, 3 duplicates skipped
        val stream2 = ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8))
        val result2 = importer.parseAndValidateRows(stream2, mapping)
        val (inserted2, skipped2) = datasetRepository.insertCandles(datasetId, result2.validCandles)

        assertEquals(0, inserted2)
        assertEquals(3, skipped2)
        assertEquals(3, datasetRepository.getCandleCountByDatasetId(datasetId))
        assertEquals(3, inserted2 + skipped2)
    }

    /**
     * TEST 7b — Different dataset isolation
     * Verify that importing candles into a different dataset does NOT suppress candles
     * even if they share similar timestamps.
     */
    @Test
    fun `TEST 7b - Different datasets preserve legitimate candles without cross-dataset suppression`() = runBlocking {
        val csv = """
            timestamp,open,high,low,close,volume
            1704067200000,1.0950,1.0980,1.0920,1.0960,100
            1704068100000,1.0960,1.0990,1.0950,1.0970,110
        """.trimIndent()

        val mapping = CsvColumnMapping(
            timestampColumn = "timestamp",
            openColumn = "open",
            highColumn = "high",
            lowColumn = "low",
            closeColumn = "close",
            volumeColumn = "volume"
        )

        val resultA = importer.parseAndValidateRows(ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8)), mapping)
        val resultB = importer.parseAndValidateRows(ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8)), mapping)

        val datasetIdA = "DS-EURUSD-M15-SOURCEA"
        val datasetIdB = "DS-GBPUSD-M15-SOURCEB"

        // Insert into dataset A
        val (insertedA, skippedA) = datasetRepository.insertCandles(datasetIdA, resultA.validCandles)
        assertEquals(2, insertedA)
        assertEquals(0, skippedA)
        assertEquals(2, datasetRepository.getCandleCountByDatasetId(datasetIdA))

        // Insert into dataset B
        val (insertedB, skippedB) = datasetRepository.insertCandles(datasetIdB, resultB.validCandles)
        assertEquals(2, insertedB)
        assertEquals(0, skippedB)
        assertEquals(2, datasetRepository.getCandleCountByDatasetId(datasetIdB))

        // Total in dataset A remains 2, total in dataset B remains 2
        assertEquals(2, datasetRepository.getCandlesByDatasetId(datasetIdA).size)
        assertEquals(2, datasetRepository.getCandlesByDatasetId(datasetIdB).size)
    }

    /**
     * TEST 8 — Room persistence
     * Verify that successfully imported candles can actually be retrieved from the existing DAO/database.
     */
    @Test
    fun `TEST 8 - Successfully imported candles are persisted and retrievable from Room`() = runBlocking {
        val datasetId = "DS-EURUSD-TEST-ROOM"
        val dataset = DatasetMetadata(
            datasetId = datasetId,
            name = "EUR/USD M15 Real CSV",
            symbol = "EUR/USD",
            timeframe = "M15",
            startDate = 1704067200000L,
            endDate = 1704068100000L,
            rowCount = 2,
            source = "CSV File",
            timezone = "UTC",
            validationStatus = ValidationStatus.VALID,
            isDevelopmentSample = false
        )

        datasetRepository.insertDataset(dataset)

        val csv = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
            2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
        """.trimIndent()

        val mapping = columnMapper.autoDetectMapping(listOf("Etc/UTC", "Open", "High", "Low", "Close", "Volume"))
        val result = importer.parseAndValidateRows(ByteArrayInputStream(csv.toByteArray(Charsets.UTF_8)), mapping)

        datasetRepository.insertCandles(datasetId, result.validCandles)

        // Retrieve persisted candles
        val retrieved = datasetRepository.getCandlesByDatasetId(datasetId)
        assertEquals(2, retrieved.size)
        assertEquals(1.16687, retrieved[0].open, 0.00001)
        assertEquals(1.16673, retrieved[1].close, 0.00001)

        val retrievedDataset = datasetRepository.getDatasetById(datasetId)
        assertNotNull(retrievedDataset)
        assertEquals("EUR/USD M15 Real CSV", retrievedDataset?.name)
    }

    /**
     * REAL-WORLD CSV TEST
     * Exact structure from prompt.
     */
    @Test
    fun `REAL WORLD CSV - Processes the exact real-world CSV structure with UTC offsets`() {
        val realCsv = """
            Etc/UTC,Open,High,Low,Close,Volume
            2026-08-25T01:00:00+00:00,1.16687,1.16687,1.16548,1.16557,12231860000
            2026-08-25T05:00:00+00:00,1.16557,1.16677,1.16510,1.16673,21067200000
            2026-08-25T09:00:00+00:00,1.16672,1.16678,1.16659,1.16674,135880000
        """.trimIndent()

        val headers = listOf("Etc/UTC", "Open", "High", "Low", "Close", "Volume")
        val autoMapping = columnMapper.autoDetectMapping(headers)
        val validation = columnMapper.validateMapping(autoMapping, headers)

        assertTrue("Auto-detected mapping for real-world headers must be valid", validation.isValid)

        val stream = ByteArrayInputStream(realCsv.toByteArray(Charsets.UTF_8))
        val result = importer.parseAndValidateRows(stream, autoMapping)

        assertEquals(3, result.validCandles.size)
        assertEquals(0, result.rejectedRows.size)

        // Check chronological ordering
        assertTrue(result.validCandles[0].timestamp < result.validCandles[1].timestamp)
        assertTrue(result.validCandles[1].timestamp < result.validCandles[2].timestamp)
    }

    /**
     * TEST 9 — Deterministic Dataset ID generation
     * Verify that identical file names and symbol/timeframe pairs always produce
     * the exact same dataset ID, ensuring repeated imports map to the same logical dataset.
     */
    @Test
    fun `TEST 9 - Deterministic Dataset ID matches across repeated imports`() {
        val cleanSymbol = "EUR/USD".replace("/", "").replace("_", "").replace("-", "").uppercase()
        val baseName1 = "EURUSD_M15.csv".substringBeforeLast(".").replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
        val datasetId1 = "DS-$cleanSymbol-M15-$baseName1"

        val baseName2 = "EURUSD_M15.csv".substringBeforeLast(".").replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
        val datasetId2 = "DS-$cleanSymbol-M15-$baseName2"

        assertEquals(datasetId1, datasetId2)
        assertEquals("DS-EURUSD-M15-EURUSDM15", datasetId1)
    }
}
