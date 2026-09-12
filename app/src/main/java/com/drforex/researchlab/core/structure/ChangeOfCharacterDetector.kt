package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**
 * Detects confirmed Changes of Character (CHoCH) using only information
 * that was available at the time of the structural event.
 *
 * Forex interpretation:
 *
 * - In bullish structure, a CHoCH occurs when price closes below the
 *   relevant protected Higher Low.
 * - In bearish structure, a CHoCH occurs when price closes above the
 *   relevant protected Lower High.
 *
 * CHoCH represents a potential structural change. It is an observable
 * market event, not a trading signal.
 */
class ChangeOfCharacterDetector(
    private val swingPointDetector: SwingPointDetector = SwingPointDetector()
) {

    fun detect(
        series: MarketSeries,
        asOf: MarketTime,
        leftBars: Int = 2,
        rightBars: Int = 2
    ): List<ChangeOfCharacter> {

        val candles = series
            .availableAt(asOf)
            .sortedBy { it.closeTime.instant }

        if (candles.isEmpty()) {
            return emptyList()
        }

        val swings = swingPointDetector
            .detect(
                series = series,
                asOf = asOf,
                leftBars = leftBars,
                rightBars = rightBars
            )
            .filter { it.isConfirmedAt(asOf) }
            .sortedBy { it.confirmationTime.instant }

        if (swings.size < 3) {
            return emptyList()
        }

        val classified = classifySwings(swings)

        val events = mutableListOf<ChangeOfCharacter>()

        var structuralDirection = StructureDirection.UNKNOWN
        var protectedPoint: StructurePoint? = null

        var swingIndex = 0

        for (candle in candles) {

            while (
                swingIndex < classified.size &&
                !classified[swingIndex]
                    .swing
                    .confirmationTime
                    .isAfter(candle.closeTime)
            ) {
                val point = classified[swingIndex]

                val newDirection =
                    determineDirectionAtPoint(
                        points = classified,
                        index = swingIndex
                    )

                if (newDirection != StructureDirection.UNKNOWN &&
                    newDirection != StructureDirection.TRANSITION
                ) {
                    structuralDirection = newDirection
                }

                protectedPoint =
                    when (structuralDirection) {

                        StructureDirection.BULLISH ->
                            if (
                                point.classification ==
                                    StructureClassification.HIGHER_LOW
                            ) {
                                point
                            } else {
                                protectedPoint
                            }

                        StructureDirection.BEARISH ->
                            if (
                                point.classification ==
                                    StructureClassification.LOWER_HIGH
                            ) {
                                point
                            } else {
                                protectedPoint
                            }

                        else ->
                            protectedPoint
                    }

                swingIndex++
            }

            val protected = protectedPoint
                ?: continue

            if (
                candle.closeTime
                    .isBefore(protected.swing.confirmationTime)
            ) {
                continue
            }

            val breakDetected =
                when (structuralDirection) {

                    StructureDirection.BULLISH ->
                        candle.close < protected.swing.price

                    StructureDirection.BEARISH ->
                        candle.close > protected.swing.price

                    else ->
                        false
                }

            if (!breakDetected) {
                continue
            }

            val previousDirection = structuralDirection

            val newDirection =
                when (previousDirection) {
                    StructureDirection.BULLISH ->
                        StructureDirection.BEARISH

                    StructureDirection.BEARISH ->
                        StructureDirection.BULLISH

                    else ->
                        StructureDirection.UNKNOWN
                }

            if (newDirection == StructureDirection.UNKNOWN) {
                continue
            }

            events += ChangeOfCharacter(
                brokenPoint = protected.swing,
                eventTime = candle.closeTime,
                confirmationTime = candle.closeTime,
                previousDirection = previousDirection,
                newDirection = newDirection,
                breakPrice = candle.close
            )

            structuralDirection = newDirection
            protectedPoint = null
        }

        return events
            .distinctBy {
                Triple(
                    it.brokenPoint.time,
                    it.previousDirection,
                    it.newDirection
                )
            }
            .sortedBy {
                it.confirmationTime.instant
            }
    }

    private fun classifySwings(
        swings: List<SwingPoint>
    ): List<StructurePoint> {

        val previousHighs = mutableListOf<SwingPoint>()
        val previousLows = mutableListOf<SwingPoint>()

        return swings.map { swing ->

            val classification =
                when (swing.type) {

                    SwingPointType.HIGH -> {

                        val previous =
                            previousHighs.lastOrNull()

                        previousHighs += swing

                        when {
                            previous == null ->
                                StructureClassification.UNCLASSIFIED

                            swing.price > previous.price ->
                                StructureClassification.HIGHER_HIGH

                            swing.price < previous.price ->
                                StructureClassification.LOWER_HIGH

                            else ->
                                StructureClassification.UNCLASSIFIED
                        }
                    }

                    SwingPointType.LOW -> {

                        val previous =
                            previousLows.lastOrNull()

                        previousLows += swing

                        when {
                            previous == null ->
                                StructureClassification.UNCLASSIFIED

                            swing.price > previous.price ->
                                StructureClassification.HIGHER_LOW

                            swing.price < previous.price ->
                                StructureClassification.LOWER_LOW

                            else ->
                                StructureClassification.UNCLASSIFIED
                        }
                    }
                }

            StructurePoint(
                swing = swing,
                classification = classification
            )
        }
    }

    private fun determineDirectionAtPoint(
        points: List<StructurePoint>,
        index: Int
    ): StructureDirection {

        val recent =
            points
                .subList(0, index + 1)
                .filter {
                    it.classification !=
                        StructureClassification.UNCLASSIFIED
                }
                .takeLast(4)

        if (recent.isEmpty()) {
            return StructureDirection.UNKNOWN
        }

        val bullish =
            recent.count {
                it.classification ==
                    StructureClassification.HIGHER_HIGH ||
                    it.classification ==
                    StructureClassification.HIGHER_LOW
            }

        val bearish =
            recent.count {
                it.classification ==
                    StructureClassification.LOWER_HIGH ||
                    it.classification ==
                    StructureClassification.LOWER_LOW
            }

        return when {
            bullish >= 2 && bullish > bearish ->
                StructureDirection.BULLISH

            bearish >= 2 && bearish > bullish ->
                StructureDirection.BEARISH

            bullish > 0 && bearish > 0 ->
                StructureDirection.TRANSITION

            else ->
                StructureDirection.UNKNOWN
        }
    }
}
