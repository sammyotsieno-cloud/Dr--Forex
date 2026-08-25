package com.example.domain.engine

import com.example.domain.model.DataQualityClassification
import com.example.domain.model.DataQualityReport
import com.example.domain.model.MarketCandle
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

interface DataQualityAnalyzer {
    fun analyzeQuality(datasetId: String, candles: List<MarketCandle>): DataQualityReport
}

class DataQualityAnalyzerImpl : DataQualityAnalyzer {

    override fun analyzeQuality(datasetId: String, candles: List<MarketCandle>): DataQualityReport {
        if (candles.isEmpty()) {
            return DataQualityReport(
                datasetId = datasetId,
                detectedTimeframe = "M15",
                expectedIntervalMinutes = 15L,
                totalCandleCount = 0,
                firstTimestamp = 0L,
                lastTimestamp = 0L,
                gapCount = 0,
                abnormalGapCount = 0,
                maxAbnormalGapDurationMinutes = 0L,
                weekendGapCount = 0,
                priceSpikeCount = 0,
                flatlineCount = 0,
                longestFlatlineStreak = 0,
                isVolumeAvailable = false,
                zeroVolumeCount = 0,
                zeroVolumeStreak = 0,
                priceIntegrityIssueCount = 1,
                continuityScore = 0,
                priceIntegrityScore = 0,
                flatlineScore = 100,
                volumeScore = 100,
                overallQualityScore = 0,
                qualityClassification = DataQualityClassification.CRITICAL_ISSUES,
                summaryIssues = listOf("Dataset is empty (0 candles)")
            )
        }

        val totalCount = candles.size
        val firstTs = candles.first().timestamp
        val lastTs = candles.last().timestamp
        val issues = mutableListOf<String>()

        // 1. Price Integrity & Ordering
        var priceIntegrityIssues = 0
        for (i in candles.indices) {
            val c = candles[i]
            if (c.open <= 0.0 || c.high <= 0.0 || c.low <= 0.0 || c.close <= 0.0) {
                priceIntegrityIssues++
            }
            if (c.high < c.low) {
                priceIntegrityIssues++
            }
            if (c.open < c.low || c.open > c.high) {
                priceIntegrityIssues++
            }
            if (c.close < c.low || c.close > c.high) {
                priceIntegrityIssues++
            }
            if (i > 0 && c.timestamp <= candles[i - 1].timestamp) {
                priceIntegrityIssues++
            }
        }
        if (priceIntegrityIssues > 0) {
            issues.add("$priceIntegrityIssues price integrity or chronological order anomaly(s) found")
        }

        // 2. Timeframe & Interval Detection (Dominant Mode)
        val (detectedTimeframe, expectedIntervalMinutes) = detectTimeframe(candles)
        val expectedIntervalMs = expectedIntervalMinutes * 60 * 1000L

        // 3. Gap Detection with Conservative Weekend Filtering
        var totalGaps = 0
        var abnormalGaps = 0
        var weekendGaps = 0
        var maxAbnormalGapDurationMinutes = 0L

        for (i in 1 until totalCount) {
            val tPrev = candles[i - 1].timestamp
            val tCurr = candles[i].timestamp
            val diffMs = tCurr - tPrev

            // Threshold for gap: anything exceeding 1.5x expected interval
            if (diffMs > (expectedIntervalMs * 1.5).toLong()) {
                totalGaps++
                val gapMinutes = diffMs / (60 * 1000L)

                if (isWeekendClosure(tPrev, tCurr, diffMs)) {
                    weekendGaps++
                } else {
                    abnormalGaps++
                    if (gapMinutes > maxAbnormalGapDurationMinutes) {
                        maxAbnormalGapDurationMinutes = gapMinutes
                    }
                }
            }
        }
        if (abnormalGaps > 0) {
            issues.add("$abnormalGaps abnormal time gap(s) detected (longest: ${maxAbnormalGapDurationMinutes}m)")
        }

        // 4. Price Spike / Wick Anomaly Detection (using MAD statistics)
        val priceSpikeCount = detectPriceSpikes(candles)
        if (priceSpikeCount > 0) {
            issues.add("$priceSpikeCount price range spike/wick anomaly(s) detected (>5 MAD from median)")
        }

        // 5. Flatline Detection (>= 3 consecutive identical OHLC candles)
        val (flatlineCount, longestFlatlineStreak) = detectFlatlines(candles)
        if (flatlineCount > 0) {
            issues.add("$flatlineCount flatline sequence(s) detected (longest streak: $longestFlatlineStreak bars)")
        }

        // 6. Volume Availability & Zero-Volume Detection
        val isVolumeAvailable = candles.any { it.volume > 0.0 }
        var zeroVolumeCount = 0
        var longestZeroVolumeStreak = 0

        if (isVolumeAvailable) {
            var currentZeroStreak = 0
            for (c in candles) {
                if (c.volume <= 0.0) {
                    zeroVolumeCount++
                    currentZeroStreak++
                    if (currentZeroStreak > longestZeroVolumeStreak) {
                        longestZeroVolumeStreak = currentZeroStreak
                    }
                } else {
                    currentZeroStreak = 0
                }
            }
            if (zeroVolumeCount > 0) {
                issues.add("$zeroVolumeCount zero-volume bar(s) detected (longest streak: $longestZeroVolumeStreak)")
            }
        }

        // 7. Weighted Component Scores (0 to 100)
        // Continuity Score
        val gapPenalty = min(100, abnormalGaps * 15 + (maxAbnormalGapDurationMinutes / max(1L, expectedIntervalMinutes * 2)).toInt() * 5)
        val continuityScore = max(0, 100 - gapPenalty)

        // Price Integrity Score
        val integrityPenalty = min(100, priceIntegrityIssues * 30 + priceSpikeCount * 12)
        val priceIntegrityScore = max(0, 100 - integrityPenalty)

        // Flatline Score
        val flatlinePenalty = min(100, flatlineCount * 15 + (if (longestFlatlineStreak >= 3) (longestFlatlineStreak - 2) * 5 else 0))
        val flatlineScore = max(0, 100 - flatlinePenalty)

        // Volume Score
        val volumeScore: Int = if (isVolumeAvailable) {
            val zeroVolRatio = zeroVolumeCount.toDouble() / max(1, totalCount)
            val volPenalty = min(100, (zeroVolRatio * 80).toInt() + longestZeroVolumeStreak * 5)
            max(0, 100 - volPenalty)
        } else {
            100 // Neutral default when volume is absent
        }

        // Composite Quality Score (0 - 100)
        val rawOverallScore: Double = if (isVolumeAvailable) {
            0.40 * continuityScore + 0.30 * priceIntegrityScore + 0.20 * flatlineScore + 0.10 * volumeScore
        } else {
            // Re-distribute volume weight proportionally without penalizing absent volume
            0.45 * continuityScore + 0.35 * priceIntegrityScore + 0.20 * flatlineScore
        }

        val overallQualityScore = rawOverallScore.roundToInt().coerceIn(0, 100)
        val classification = DataQualityClassification.fromScore(overallQualityScore)

        return DataQualityReport(
            datasetId = datasetId,
            detectedTimeframe = detectedTimeframe,
            expectedIntervalMinutes = expectedIntervalMinutes,
            totalCandleCount = totalCount,
            firstTimestamp = firstTs,
            lastTimestamp = lastTs,
            gapCount = totalGaps,
            abnormalGapCount = abnormalGaps,
            maxAbnormalGapDurationMinutes = maxAbnormalGapDurationMinutes,
            weekendGapCount = weekendGaps,
            priceSpikeCount = priceSpikeCount,
            flatlineCount = flatlineCount,
            longestFlatlineStreak = longestFlatlineStreak,
            isVolumeAvailable = isVolumeAvailable,
            zeroVolumeCount = zeroVolumeCount,
            zeroVolumeStreak = longestZeroVolumeStreak,
            priceIntegrityIssueCount = priceIntegrityIssues,
            continuityScore = continuityScore,
            priceIntegrityScore = priceIntegrityScore,
            flatlineScore = flatlineScore,
            volumeScore = volumeScore,
            overallQualityScore = overallQualityScore,
            qualityClassification = classification,
            summaryIssues = issues
        )
    }

    /**
     * Determines dominant interval and timeframe code using statistical mode.
     */
    private fun detectTimeframe(candles: List<MarketCandle>): Pair<String, Long> {
        if (candles.size < 2) return Pair("M15", 15L)

        val intervalCounts = mutableMapOf<Long, Int>()
        for (i in 1 until candles.size) {
            val diffMs = candles[i].timestamp - candles[i - 1].timestamp
            if (diffMs > 0) {
                // Round to nearest 30 seconds to absorb minor jitter
                val roundedMinutes = max(1L, ((diffMs + 30000L) / 60000L))
                intervalCounts[roundedMinutes] = (intervalCounts[roundedMinutes] ?: 0) + 1
            }
        }

        if (intervalCounts.isEmpty()) return Pair("M15", 15L)

        // Find mode (most frequent interval in minutes)
        val dominantMinutes = intervalCounts.maxByOrNull { it.value }?.key ?: 15L

        val timeframeCode = when (dominantMinutes) {
            1L -> "M1"
            5L -> "M5"
            15L -> "M15"
            30L -> "M30"
            60L -> "H1"
            240L -> "H4"
            1440L -> "D1"
            else -> "${dominantMinutes}m"
        }

        return Pair(timeframeCode, dominantMinutes)
    }

    /**
     * Identifies legitimate Forex weekend market closures.
     * Forex markets typically close Friday evening (~21:00 UTC) and reopen Sunday evening (~21:00 UTC).
     */
    private fun isWeekendClosure(tPrev: Long, tCurr: Long, diffMs: Long): Boolean {
        val minWeekendMs = 36 * 3600 * 1000L // 36 hours
        val maxWeekendMs = 76 * 3600 * 1000L // 76 hours

        if (diffMs !in minWeekendMs..maxWeekendMs) {
            return false
        }

        val calPrev = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = tPrev }
        val calCurr = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = tCurr }

        val dayPrev = calPrev.get(Calendar.DAY_OF_WEEK)
        val dayCurr = calCurr.get(Calendar.DAY_OF_WEEK)

        // Normal closure: Friday (or early Saturday) to Sunday (or early Monday)
        val isPrevFridayOrSaturday = dayPrev == Calendar.FRIDAY || dayPrev == Calendar.SATURDAY
        val isCurrSundayOrMonday = dayCurr == Calendar.SUNDAY || dayCurr == Calendar.MONDAY

        return isPrevFridayOrSaturday && isCurrSundayOrMonday
    }

    /**
     * Detects suspicious price spikes/wicks using Median Absolute Deviation (MAD).
     */
    private fun detectPriceSpikes(candles: List<MarketCandle>): Int {
        if (candles.size < 4) return 0

        val ranges = candles.map { it.high - it.low }
        val sortedRanges = ranges.sorted()
        val medianRange = median(sortedRanges)

        val absDeviations = ranges.map { abs(it - medianRange) }.sorted()
        val mad = median(absDeviations)

        // Threshold: 5 MAD above median range (or 5x median range if MAD is very small)
        val threshold = if (mad > 0.00001) {
            medianRange + (5.0 * mad)
        } else if (medianRange > 0.00001) {
            max(0.0005, 5.0 * medianRange)
        } else {
            Double.MAX_VALUE
        }

        var spikes = 0
        for (r in ranges) {
            if (r > threshold && r > 0.0005) {
                spikes++
            }
        }
        return spikes
    }

    /**
     * Detects consecutive flatline sequences (>= 3 consecutive identical OHLC candles).
     */
    private fun detectFlatlines(candles: List<MarketCandle>): Pair<Int, Int> {
        var flatlineSequences = 0
        var longestStreak = 0
        var currentStreak = 1

        for (i in 1 until candles.size) {
            val prev = candles[i - 1]
            val curr = candles[i]

            val isIdentical = curr.open == prev.open &&
                curr.high == prev.high &&
                curr.low == prev.low &&
                curr.close == prev.close

            if (isIdentical) {
                currentStreak++
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                }
            } else {
                if (currentStreak >= 3) {
                    flatlineSequences++
                }
                currentStreak = 1
            }
        }

        if (currentStreak >= 3) {
            flatlineSequences++
        }

        return Pair(flatlineSequences, longestStreak)
    }

    private fun median(sortedList: List<Double>): Double {
        if (sortedList.isEmpty()) return 0.0
        val size = sortedList.size
        return if (size % 2 == 0) {
            (sortedList[size / 2 - 1] + sortedList[size / 2]) / 2.0
        } else {
            sortedList[size / 2]
        }
    }
}
