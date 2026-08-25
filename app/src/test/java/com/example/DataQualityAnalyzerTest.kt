package com.example

import com.example.domain.engine.DataQualityAnalyzer
import com.example.domain.engine.DataQualityAnalyzerImpl
import com.example.domain.model.DataQualityClassification
import com.example.domain.model.MarketCandle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DataQualityAnalyzerTest {

    private lateinit var analyzer: DataQualityAnalyzer

    @Before
    fun setup() {
        analyzer = DataQualityAnalyzerImpl()
    }

    /**
     * TEST 1 — Clean M15 Dataset
     * Verifies that a clean series with 15m intervals produces 0 abnormal gaps,
     * 100 quality score, and EXCELLENT classification.
     */
    @Test
    fun `TEST 1 - Clean M15 dataset produces 100 score and EXCELLENT classification`() {
        val baseTime = 1704096000000L // 2024-01-01 08:00:00 UTC
        val intervalMs = 15 * 60 * 1000L

        val candles = (0 until 20).map { i ->
            MarketCandle(
                timestamp = baseTime + (i * intervalMs),
                open = 1.1000 + (i * 0.0002),
                high = 1.1005 + (i * 0.0002),
                low = 1.0995 + (i * 0.0002),
                close = 1.1002 + (i * 0.0002),
                volume = 1000.0 + i
            )
        }

        val report = analyzer.analyzeQuality("DS-CLEAN-M15", candles)

        assertEquals("M15", report.detectedTimeframe)
        assertEquals(15L, report.expectedIntervalMinutes)
        assertEquals(20, report.totalCandleCount)
        assertEquals(0, report.abnormalGapCount)
        assertEquals(0, report.priceSpikeCount)
        assertEquals(0, report.flatlineCount)
        assertEquals(0, report.zeroVolumeCount)
        assertEquals(0, report.priceIntegrityIssueCount)
        assertEquals(100, report.overallQualityScore)
        assertEquals(DataQualityClassification.EXCELLENT, report.qualityClassification)
    }

    /**
     * TEST 2 — Missing Mid-Week Candle (Time Gap)
     * Timestamps: 09:00, 09:15, 09:30, 10:00 (missing 09:45)
     * Expected: M15 detected, abnormal gap count == 1, max gap duration == 30m.
     */
    @Test
    fun `TEST 2 - Missing mid-week candle detects abnormal time gap`() {
        val baseTime = 1704186000000L // Tuesday 2024-01-02 09:00:00 UTC
        val m15 = 15 * 60 * 1000L

        val candles = listOf(
            MarketCandle(baseTime + (0 * m15), 1.1000, 1.1010, 1.0990, 1.1005, 500.0), // 09:00
            MarketCandle(baseTime + (1 * m15), 1.1005, 1.1015, 1.0995, 1.1010, 520.0), // 09:15
            MarketCandle(baseTime + (2 * m15), 1.1010, 1.1020, 1.1000, 1.1015, 510.0), // 09:30
            MarketCandle(baseTime + (4 * m15), 1.1015, 1.1025, 1.1005, 1.1020, 530.0)  // 10:00 (gap!)
        )

        val report = analyzer.analyzeQuality("DS-GAP", candles)

        assertEquals("M15", report.detectedTimeframe)
        assertEquals(1, report.abnormalGapCount)
        assertEquals(30L, report.maxAbnormalGapDurationMinutes)
        assertTrue("Continuity score should be penalized for abnormal gap", report.continuityScore < 100)
    }

    /**
     * TEST 3 — Weekend Gap (Friday 21:00 UTC to Sunday 21:00 UTC)
     * Verifies that Forex market weekend closures are filtered conservatively
     * and NOT penalized as missing data gaps.
     */
    @Test
    fun `TEST 3 - Normal Forex weekend closure is filtered and not penalized`() {
        // Friday Jan 5, 2024 21:00:00 UTC
        val calFri = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2024, Calendar.JANUARY, 5, 21, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // Sunday Jan 7, 2024 21:00:00 UTC (48 hours later)
        val calSun = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(2024, Calendar.JANUARY, 7, 21, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val m15 = 15 * 60 * 1000L
        val tFri = calFri.timeInMillis
        val tSun = calSun.timeInMillis

        val candles = listOf(
            MarketCandle(tFri - (2 * m15), 1.0950, 1.0960, 1.0945, 1.0955, 300.0),
            MarketCandle(tFri - (1 * m15), 1.0955, 1.0965, 1.0950, 1.0958, 310.0),
            MarketCandle(tFri, 1.0958, 1.0962, 1.0952, 1.0957, 290.0), // Friday close
            MarketCandle(tSun, 1.0956, 1.0965, 1.0950, 1.0960, 320.0), // Sunday open (48h gap)
            MarketCandle(tSun + (1 * m15), 1.0960, 1.0970, 1.0955, 1.0965, 330.0),
            MarketCandle(tSun + (2 * m15), 1.0965, 1.0975, 1.0960, 1.0970, 340.0)
        )

        val report = analyzer.analyzeQuality("DS-WEEKEND", candles)

        assertEquals("M15", report.detectedTimeframe)
        assertEquals(0, report.abnormalGapCount)
        assertEquals(1, report.weekendGapCount)
        assertEquals(100, report.continuityScore)
        assertEquals(100, report.overallQualityScore)
    }

    /**
     * TEST 4 — Price Spike / Wick Anomaly Detection
     * Dataset with normal candles (range ~0.0010) and one extreme spike candle (range ~0.0200).
     */
    @Test
    fun `TEST 4 - Extreme range spike is flagged as price spike anomaly`() {
        val baseTime = 1704186000000L
        val m15 = 15 * 60 * 1000L

        val normalCandles = (0 until 10).map { i ->
            MarketCandle(baseTime + (i * m15), 1.1000, 1.1010, 1.0995, 1.1005, 500.0)
        }
        val spikeCandle = MarketCandle(baseTime + (10 * m15), 1.1000, 1.1250, 1.0900, 1.1010, 5000.0)
        val afterCandles = (11 until 15).map { i ->
            MarketCandle(baseTime + (i * m15), 1.1005, 1.1012, 1.0998, 1.1008, 500.0)
        }

        val all = normalCandles + spikeCandle + afterCandles
        val report = analyzer.analyzeQuality("DS-SPIKE", all)

        assertTrue("Should detect price spike", report.priceSpikeCount >= 1)
        assertTrue("Price integrity score should reflect spike penalty", report.priceIntegrityScore < 100)
    }

    /**
     * TEST 5 — Flatline Sequence Detection
     * Identifies >= 3 consecutive identical OHLC candles.
     */
    @Test
    fun `TEST 5 - Sequence of 3 or more identical OHLC bars detects flatline`() {
        val baseTime = 1704186000000L
        val m15 = 15 * 60 * 1000L

        val candles = listOf(
            MarketCandle(baseTime + (0 * m15), 1.1000, 1.1010, 1.0990, 1.1005, 500.0),
            MarketCandle(baseTime + (1 * m15), 1.1000, 1.1010, 1.0990, 1.1000, 500.0),
            MarketCandle(baseTime + (2 * m15), 1.1000, 1.1010, 1.0990, 1.1000, 500.0), // identical 1
            MarketCandle(baseTime + (3 * m15), 1.1000, 1.1010, 1.0990, 1.1000, 500.0), // identical 2
            MarketCandle(baseTime + (4 * m15), 1.1000, 1.1010, 1.0990, 1.1000, 500.0), // identical 3 (streak = 4)
            MarketCandle(baseTime + (5 * m15), 1.1005, 1.1015, 1.0995, 1.1010, 520.0)
        )

        val report = analyzer.analyzeQuality("DS-FLATLINE", candles)

        assertEquals(1, report.flatlineCount)
        assertEquals(4, report.longestFlatlineStreak)
        assertTrue("Flatline score should be penalized", report.flatlineScore < 100)
    }

    /**
     * TEST 6 — Zero-Volume Streak Detection
     * When volume is present in the dataset, zero volume bars are detected.
     */
    @Test
    fun `TEST 6 - Zero-volume bars in active volume series are flagged`() {
        val baseTime = 1704186000000L
        val m15 = 15 * 60 * 1000L

        val candles = listOf(
            MarketCandle(baseTime + (0 * m15), 1.1000, 1.1010, 1.0990, 1.1005, 500.0),
            MarketCandle(baseTime + (1 * m15), 1.1005, 1.1015, 1.0995, 1.1010, 0.0), // zero vol 1
            MarketCandle(baseTime + (2 * m15), 1.1010, 1.1020, 1.1000, 1.1015, 0.0), // zero vol 2
            MarketCandle(baseTime + (3 * m15), 1.1015, 1.1025, 1.1005, 1.1020, 600.0)
        )

        val report = analyzer.analyzeQuality("DS-ZERO-VOL", candles)

        assertTrue(report.isVolumeAvailable)
        assertEquals(2, report.zeroVolumeCount)
        assertEquals(2, report.zeroVolumeStreak)
        assertTrue("Volume score should be penalized for zero volume bars", report.volumeScore < 100)
    }

    /**
     * TEST 7 — Absent Volume (All 0.0 Volume)
     * Datasets without volume are not penalized; volume weight is redistributed cleanly.
     */
    @Test
    fun `TEST 7 - Dataset with no volume is not penalized`() {
        val baseTime = 1704186000000L
        val m15 = 15 * 60 * 1000L

        val candles = (0 until 10).map { i ->
            MarketCandle(
                timestamp = baseTime + (i * m15),
                open = 1.1000 + (i * 0.0002),
                high = 1.1008 + (i * 0.0002),
                low = 1.0995 + (i * 0.0002),
                close = 1.1004 + (i * 0.0002),
                volume = 0.0
            )
        }

        val report = analyzer.analyzeQuality("DS-NO-VOL", candles)

        assertFalse(report.isVolumeAvailable)
        assertEquals(0, report.zeroVolumeCount)
        assertEquals(100, report.overallQualityScore)
        assertEquals(DataQualityClassification.EXCELLENT, report.qualityClassification)
    }

    /**
     * TEST 8 — Price Integrity Violations
     * Checks High < Low, Close > High, Open < Low anomalies.
     */
    @Test
    fun `TEST 8 - Price integrity violations are detected and score penalized`() {
        val baseTime = 1704186000000L
        val m15 = 15 * 60 * 1000L

        val candles = listOf(
            MarketCandle(baseTime + (0 * m15), 1.1000, 1.1010, 1.0990, 1.1005, 500.0),
            MarketCandle(baseTime + (1 * m15), 1.1000, 1.0980, 1.1020, 1.1000, 500.0), // High < Low!
            MarketCandle(baseTime + (2 * m15), 1.1000, 1.1010, 1.0990, 1.1050, 500.0)  // Close > High!
        )

        val report = analyzer.analyzeQuality("DS-INTEGRITY", candles)

        assertTrue(report.priceIntegrityIssueCount >= 2)
        assertTrue("Price integrity score should be severely penalized", report.priceIntegrityScore <= 50)
    }

    /**
     * TEST 9 — Multi-Timeframe Detection Mode
     * Accurately detects M1, M5, M15, H1, H4, and D1 series.
     */
    @Test
    fun `TEST 9 - Dominant timeframe is correctly identified across standard resolutions`() {
        val baseTime = 1704186000000L

        // M1
        val m1Candles = (0 until 5).map { i ->
            MarketCandle(baseTime + (i * 60 * 1000L), 1.1000, 1.1010, 1.0990, 1.1005, 100.0)
        }
        assertEquals("M1", analyzer.analyzeQuality("M1", m1Candles).detectedTimeframe)

        // M5
        val m5Candles = (0 until 5).map { i ->
            MarketCandle(baseTime + (i * 5 * 60 * 1000L), 1.1000, 1.1010, 1.0990, 1.1005, 100.0)
        }
        assertEquals("M5", analyzer.analyzeQuality("M5", m5Candles).detectedTimeframe)

        // H1
        val h1Candles = (0 until 5).map { i ->
            MarketCandle(baseTime + (i * 60 * 60 * 1000L), 1.1000, 1.1010, 1.0990, 1.1005, 100.0)
        }
        assertEquals("H1", analyzer.analyzeQuality("H1", h1Candles).detectedTimeframe)

        // D1
        val d1Candles = (0 until 5).map { i ->
            MarketCandle(baseTime + (i * 24 * 60 * 60 * 1000L), 1.1000, 1.1010, 1.0990, 1.1005, 100.0)
        }
        assertEquals("D1", analyzer.analyzeQuality("D1", d1Candles).detectedTimeframe)
    }

    /**
     * TEST 10 — Quality Classification Ranges
     * Verifies deterministic mapping:
     * 90-100 -> EXCELLENT
     * 75-89  -> GOOD
     * 50-74  -> FAIR
     * 0-49   -> CRITICAL_ISSUES
     */
    @Test
    fun `TEST 10 - Quality classification correctly brackets scores`() {
        assertEquals(DataQualityClassification.EXCELLENT, DataQualityClassification.fromScore(100))
        assertEquals(DataQualityClassification.EXCELLENT, DataQualityClassification.fromScore(90))
        assertEquals(DataQualityClassification.GOOD, DataQualityClassification.fromScore(89))
        assertEquals(DataQualityClassification.GOOD, DataQualityClassification.fromScore(75))
        assertEquals(DataQualityClassification.FAIR, DataQualityClassification.fromScore(74))
        assertEquals(DataQualityClassification.FAIR, DataQualityClassification.fromScore(50))
        assertEquals(DataQualityClassification.CRITICAL_ISSUES, DataQualityClassification.fromScore(49))
        assertEquals(DataQualityClassification.CRITICAL_ISSUES, DataQualityClassification.fromScore(0))
    }
}
