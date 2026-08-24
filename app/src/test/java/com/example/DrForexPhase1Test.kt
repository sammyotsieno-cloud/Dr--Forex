package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DrForexDatabase
import com.example.data.repository.DatasetRepositoryImpl
import com.example.data.repository.ExperimentRepositoryImpl
import com.example.data.repository.ResearchConfigRepositoryImpl
import com.example.data.repository.SampleDataFactory
import com.example.domain.engine.DataValidatorImpl
import com.example.domain.experiment.ExperimentManager
import com.example.domain.model.DatasetMetadata
import com.example.domain.model.Experiment
import com.example.domain.model.ExperimentStatus
import com.example.domain.model.MarketCandle
import com.example.domain.model.ResearchConfiguration
import com.example.domain.model.ValidationStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DrForexPhase1Test {

    private lateinit var database: DrForexDatabase
    private lateinit var experimentRepo: ExperimentRepositoryImpl
    private lateinit var datasetRepo: DatasetRepositoryImpl
    private lateinit var configRepo: ResearchConfigRepositoryImpl
    private lateinit var experimentManager: ExperimentManager
    private val validator = DataValidatorImpl()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DrForexDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        experimentRepo = ExperimentRepositoryImpl(database.experimentDao())
        datasetRepo = DatasetRepositoryImpl(database.datasetDao())
        configRepo = ResearchConfigRepositoryImpl(database.researchConfigDao())
        experimentManager = ExperimentManager(experimentRepo)
    }

    @After
    fun teardown() {
        database.close()
    }

    // ==========================================
    // 1. Experiment ID Generation & Management Tests
    // ==========================================

    @Test
    fun `test initial experiment ID formatting starts at EXP-0001`() = runBlocking {
        val nextId = experimentManager.generateNextExperimentId()
        assertEquals("EXP-0001", nextId)
    }

    @Test
    fun `test sequential experiment ID generation`() = runBlocking {
        // Create EXP-0001
        val exp1 = experimentManager.createExperiment(
            strategyId = "STRAT-EMA",
            strategyName = "EMA 20/50 Cross",
            datasetId = "DS-EURUSD-M15",
            datasetName = "EUR/USD M15",
            instrument = "EUR/USD",
            timeframe = "M15",
            parametersSummary = "Fast=20, Slow=50"
        )
        assertEquals("EXP-0001", exp1.experimentId)

        // Check next ID
        val nextId = experimentManager.generateNextExperimentId()
        assertEquals("EXP-0002", nextId)

        // Create EXP-0002
        val exp2 = experimentManager.createExperiment(
            strategyId = "STRAT-RSI",
            strategyName = "RSI Mean Reversion",
            datasetId = "DS-EURUSD-M15",
            datasetName = "EUR/USD M15",
            instrument = "EUR/USD",
            timeframe = "M15",
            parametersSummary = "Period=14, Upper=70, Lower=30"
        )
        assertEquals("EXP-0002", exp2.experimentId)

        // Verify count and list
        val experiments = experimentRepo.allExperiments.first()
        assertEquals(2, experiments.size)
        assertEquals("EXP-0002", experiments[0].experimentId) // Sorted DESC by ID
        assertEquals("EXP-0001", experiments[1].experimentId)
    }

    @Test
    fun `test experiment deletion removes record correctly`() = runBlocking {
        val exp = experimentManager.createExperiment(
            strategyId = "STRAT-TEST",
            strategyName = "Temp Strategy",
            datasetId = "DS-TEST",
            datasetName = "Test Dataset",
            instrument = "EUR/USD",
            timeframe = "M15",
            parametersSummary = "None"
        )
        assertEquals(1, experimentRepo.getExperimentCount())

        experimentManager.deleteExperiment(exp.experimentId)
        assertEquals(0, experimentRepo.getExperimentCount())
    }

    // ==========================================
    // 2. Data Validation Engine Tests
    // ==========================================

    @Test
    fun `test valid candle sequence passes all integrity checks`() {
        val sampleCandles = SampleDataFactory.createSampleCandles()
        val result = validator.validateCandles(sampleCandles)

        assertTrue("Valid candles must pass validation", result.isValid)
        assertEquals(0, result.errors.size)
        assertEquals(100, result.validRows)
        assertNotNull(result.startDate)
        assertNotNull(result.endDate)
    }

    @Test
    fun `test validator rejects High lower than Low`() {
        val invalidCandle = MarketCandle(
            timestamp = 1704067200000L,
            open = 1.0950,
            high = 1.0920, // ERROR: High < Low
            low = 1.0960,
            close = 1.0940,
            volume = 100.0
        )
        val result = validator.validateCandles(listOf(invalidCandle))

        assertFalse("High < Low must fail validation", result.isValid)
        assertTrue(result.errors.any { it.field == "high_low" && it.message.contains("High (1.092) is lower than Low (1.096)") })
    }

    @Test
    fun `test validator rejects High lower than Open or Close`() {
        val invalidCandle = MarketCandle(
            timestamp = 1704067200000L,
            open = 1.0980,
            high = 1.0960, // ERROR: High < Open
            low = 1.0940,
            close = 1.0950,
            volume = 100.0
        )
        val result = validator.validateCandles(listOf(invalidCandle))

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "open_bounds" && it.message.contains("outside High-Low range") })
    }

    @Test
    fun `test validator rejects Low higher than Open or Close`() {
        val invalidCandle = MarketCandle(
            timestamp = 1704067200000L,
            open = 1.0920,
            high = 1.0980,
            low = 1.0950, // ERROR: Low > Open
            close = 1.0960,
            volume = 100.0
        )
        val result = validator.validateCandles(listOf(invalidCandle))

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "open_bounds" && it.message.contains("outside High-Low range") })
    }

    @Test
    fun `test validator rejects zero and negative prices`() {
        val zeroPriceCandle = MarketCandle(
            timestamp = 1704067200000L,
            open = 0.0,
            high = 1.0980,
            low = 0.0,
            close = 1.0960,
            volume = 100.0
        )
        val result = validator.validateCandles(listOf(zeroPriceCandle))

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "price" && it.message.contains("Non-positive price detected") })
    }

    @Test
    fun `test validator rejects empty dataset`() {
        val result = validator.validateCandles(emptyList())

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "dataset" && it.message.contains("Dataset contains zero candles") })
    }

    @Test
    fun `test validator detects out-of-order chronological timestamps`() {
        val candles = listOf(
            MarketCandle(1704067200000L, 1.0950, 1.0980, 1.0920, 1.0960, 100.0),
            MarketCandle(1704060000000L, 1.0960, 1.0990, 1.0950, 1.0970, 120.0) // earlier timestamp
        )
        val result = validator.validateCandles(candles)

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "timestamp_chronology" && it.message.contains("out of chronological order") })
    }

    @Test
    fun `test validator detects duplicate timestamps`() {
        val candles = listOf(
            MarketCandle(1704067200000L, 1.0950, 1.0980, 1.0920, 1.0960, 100.0),
            MarketCandle(1704067200000L, 1.0960, 1.0990, 1.0950, 1.0970, 120.0) // duplicate timestamp
        )
        val result = validator.validateCandles(candles)

        assertFalse(result.isValid)
        assertTrue(result.errors.any { it.field == "timestamp_duplicate" && it.message.contains("Duplicate timestamp") })
    }

    @Test
    fun `test CSV header validation catches missing columns`() {
        val invalidHeader = listOf("timestamp", "open", "high", "low") // missing close
        val errors = validator.validateCsvHeaders(invalidHeader)

        assertTrue("Should detect missing columns", errors.isNotEmpty())
        assertTrue(errors.any { it.message.contains("Missing required column: 'close'") })
    }

    @Test
    fun `test CSV header validation passes standard columns`() {
        val validHeader = listOf("timestamp", "open", "high", "low", "close", "volume")
        val errors = validator.validateCsvHeaders(validHeader)

        assertTrue("Valid headers should have no errors", errors.isEmpty())
    }

    // ==========================================
    // 3. Research Configuration & Persistence Tests
    // ==========================================

    @Test
    fun `test default research configuration values`() = runBlocking {
        val config = configRepo.getConfig()

        assertEquals("KSh", config.baseCurrency)
        assertEquals(20000.0, config.initialCapital, 0.001)
        assertEquals(1.0, config.defaultRiskPerTradePercent, 0.001)
        assertEquals("EUR/USD", config.defaultInstrument)
        assertEquals("M15", config.defaultExecutionTimeframe)
        assertEquals(1.5, config.defaultSpreadPips, 0.001)
        assertEquals(0.5, config.defaultSlippagePips, 0.001)
    }

    @Test
    fun `test saving custom research configuration persists properly`() = runBlocking {
        val custom = ResearchConfiguration(
            baseCurrency = "USD",
            initialCapital = 50000.0,
            defaultRiskPerTradePercent = 0.5,
            defaultInstrument = "GBP/USD",
            defaultExecutionTimeframe = "H1",
            contextTimeframes = listOf("H4", "D1"),
            defaultSpreadPips = 1.0,
            defaultSlippagePips = 0.2
        )
        configRepo.saveConfig(custom)

        val retrieved = configRepo.getConfig()
        assertEquals("USD", retrieved.baseCurrency)
        assertEquals(50000.0, retrieved.initialCapital, 0.001)
        assertEquals(0.5, retrieved.defaultRiskPerTradePercent, 0.001)
        assertEquals("GBP/USD", retrieved.defaultInstrument)
        assertEquals("H1", retrieved.defaultExecutionTimeframe)
    }

    // ==========================================
    // 4. Dataset Repository Persistence Tests
    // ==========================================

    @Test
    fun `test dataset repository saves and retrieves metadata`() = runBlocking {
        val dataset = DatasetMetadata(
            datasetId = "DS-EURUSD-M15-TEST",
            name = "EUR/USD Historical Test",
            symbol = "EUR/USD",
            timeframe = "M15",
            startDate = 1704067200000L,
            endDate = 1704157200000L,
            rowCount = 100,
            source = "Test Source",
            timezone = "UTC",
            validationStatus = ValidationStatus.VALID,
            isDevelopmentSample = true,
            validationSummary = "Test Validation Passed"
        )
        datasetRepo.insertDataset(dataset)

        val retrieved = datasetRepo.getDatasetById("DS-EURUSD-M15-TEST")
        assertNotNull(retrieved)
        assertEquals("EUR/USD", retrieved?.symbol)
        assertEquals(ValidationStatus.VALID, retrieved?.validationStatus)

        val all = datasetRepo.allDatasets.first()
        assertEquals(1, all.size)
    }
}
