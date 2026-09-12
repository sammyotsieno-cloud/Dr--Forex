package com.drforex.researchlab.core.strategy

import com.drforex.researchlab.core.time.MarketTime
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class StrategyObservationValidatorTest {

    private val validator = StrategyObservationValidator()

    @Test
    fun `valid observation is accepted`() {
        val result = validator.validate(validObservation())

        assertEquals(
            StrategyObservationValidation.Valid,
            result
        )
    }

    @Test
    fun `observation with no values is rejected`() {
        val observation = StrategyObservation(
            observedAt = observedAt(),
            values = mapOf()
        )

        val result = validator.validate(observation)

        assertEquals(
            StrategyObservationValidation.Invalid(
                errors = listOf(
                    "Strategy observation must contain at least one value."
                )
            ),
            result
        )
    }

    @Test
    fun `observation with blank variable name is rejected`() {
        val observation = StrategyObservation(
            observedAt = observedAt(),
            values = mapOf(
                "" to 1.1050
            )
        )

        val result = validator.validate(observation)

        assertEquals(
            StrategyObservationValidation.Invalid(
                errors = listOf(
                    "Strategy observation variable names must not be blank."
                )
            ),
            result
        )
    }

    @Test
    fun `observation with non finite value is rejected`() {
        val observation = StrategyObservation(
            observedAt = observedAt(),
            values = mapOf(
                "close" to Double.NaN
            )
        )

        val result = validator.validate(observation)

        assertEquals(
            StrategyObservationValidation.Invalid(
                errors = listOf(
                    "Strategy observation values must be finite."
                )
            ),
            result
        )
    }

    @Test
    fun `observation with multiple invalid values reports all errors`() {
        val observation = StrategyObservation(
            observedAt = observedAt(),
            values = mapOf(
                "" to Double.POSITIVE_INFINITY
            )
        )

        val result = validator.validate(observation)

        assertEquals(
            StrategyObservationValidation.Invalid(
                errors = listOf(
                    "Strategy observation variable names must not be blank.",
                    "Strategy observation values must be finite."
                )
            ),
            result
        )
    }

    private fun validObservation(): StrategyObservation {
        return StrategyObservation(
            observedAt = observedAt(),
            values = mapOf(
                "close" to 1.1050,
                "volume" to 1000.0
            )
        )
    }

    private fun observedAt(): MarketTime {
        return MarketTime(
            Instant.parse("2026-01-01T10:00:00Z")
        )
    }
}
