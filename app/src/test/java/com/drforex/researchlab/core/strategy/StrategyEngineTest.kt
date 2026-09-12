package com.drforex.researchlab.core.strategy

import com.drforex.researchlab.core.time.MarketTime
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class StrategyEngineTest {

    @Test
    fun `valid strategy is evaluated successfully`() {
        val strategy = validStrategy()
        val observation = validObservation()
        val expectedEvaluation = StrategyEvaluation(
            strategyId = strategy.id,
            observedAt = observation.observedAt,
            decision = StrategyDecision.LONG_SIGNAL,
            triggeredRules = listOf("entry"),
        )

        val engine = StrategyEngine(
            strategyEvaluator = FakeStrategyEvaluator(expectedEvaluation)
        )

        val result = engine.evaluate(
            strategy = strategy,
            observation = observation
        )

        assertEquals(
            StrategyEvaluationResult.Success(expectedEvaluation),
            result
        )
    }

    @Test
    fun `strategy with invalid definition is rejected before evaluator runs`() {
        val strategy = validStrategy()
        val invalidStrategy = strategy.copy(
            entryRules = listOf(
                StrategyRule(
                    name = "invalid-entry",
                    description = "Incorrect rule type for entry group.",
                    type = StrategyRuleType.EXIT
                )
            )
        )

        val engine = StrategyEngine(
            strategyEvaluator = FakeStrategyEvaluator(
                StrategyEvaluation(
                    strategyId = invalidStrategy.id,
                    observedAt = validObservation().observedAt,
                    decision = StrategyDecision.LONG_SIGNAL
                )
            )
        )

        val result = engine.evaluate(
            strategy = invalidStrategy,
            observation = validObservation()
        )

        assertEquals(
            StrategyEvaluationResult.InvalidDefinition(
                errors = listOf(
                    "entry strategy rule 'invalid-entry' must have type ENTRY."
                )
            ),
            result
        )
    }

    @Test
    fun `evaluation with mismatched strategy id is rejected`() {
        val strategy = validStrategy()
        val observation = validObservation()

        val evaluation = StrategyEvaluation(
            strategyId = "different-strategy",
            observedAt = observation.observedAt,
            decision = StrategyDecision.LONG_SIGNAL
        )

        val engine = StrategyEngine(
            strategyEvaluator = FakeStrategyEvaluator(evaluation)
        )

        val result = engine.evaluate(
            strategy = strategy,
            observation = observation
        )

        assertEquals(
            StrategyEvaluationResult.InvalidEvaluation(
                error = "Strategy evaluation id does not match strategy definition id."
            ),
            result
        )
    }

    @Test
    fun `evaluation with mismatched observation time is rejected`() {
        val strategy = validStrategy()
        val observation = validObservation()

        val evaluation = StrategyEvaluation(
            strategyId = strategy.id,
            observedAt = MarketTime(
                Instant.parse("2026-01-01T10:01:00Z")
            ),
            decision = StrategyDecision.LONG_SIGNAL
        )

        val engine = StrategyEngine(
            strategyEvaluator = FakeStrategyEvaluator(evaluation)
        )

        val result = engine.evaluate(
            strategy = strategy,
            observation = observation
        )

        assertEquals(
            StrategyEvaluationResult.InvalidEvaluation(
                error = "Strategy evaluation time does not match observation time."
            ),
            result
        )
    }

    @Test
    fun `evaluator argument error is converted into invalid evaluation`() {
        val strategy = validStrategy()
        val observation = validObservation()

        val engine = StrategyEngine(
            strategyEvaluator = ThrowingStrategyEvaluator(
                IllegalArgumentException("Observation is missing required market state.")
            )
        )

        val result = engine.evaluate(
            strategy = strategy,
            observation = observation
        )

        assertEquals(
            StrategyEvaluationResult.InvalidEvaluation(
                error = "Observation is missing required market state."
            ),
            result
        )
    }

    @Test
    fun `validate delegates strategy definition validation`() {
        val strategy = validStrategy()

        val engine = StrategyEngine(
            strategyEvaluator = FakeStrategyEvaluator(
                StrategyEvaluation(
                    strategyId = strategy.id,
                    observedAt = validObservation().observedAt,
                    decision = StrategyDecision.NO_ACTION
                )
            )
        )

        val result = engine.validate(strategy)

        assertEquals(
            StrategyValidation.Valid,
            result
        )
    }

    private fun validStrategy(): StrategyDefinition {
        return StrategyDefinition(
            id = "strategy-001",
            name = "Test Strategy",
            description = "Strategy used to verify engine behavior.",
            createdAt = MarketTime(
                Instant.parse("2026-01-01T00:00:00Z")
            ),
            instrument = "EURUSD",
            timeframe = "1H",
            entryRules = listOf(
                StrategyRule(
                    name = "entry",
                    description = "Test entry condition.",
                    type = StrategyRuleType.ENTRY
                )
            ),
            exitRules = listOf(
                StrategyRule(
                    name = "exit",
                    description = "Test exit condition.",
                    type = StrategyRuleType.EXIT
                )
            ),
            filters = listOf(
                StrategyRule(
                    name = "filter",
                    description = "Test filter condition.",
                    type = StrategyRuleType.FILTER
                )
            )
        )
    }

    private fun validObservation(): StrategyObservation {
        return StrategyObservation(
            observedAt = MarketTime(
                Instant.parse("2026-01-01T10:00:00Z")
            ),
            values = mapOf(
                "close" to 1.1050,
                "volume" to 1000.0
            )
        )
    }

    private class FakeStrategyEvaluator(
        private val evaluation: StrategyEvaluation
    ) : StrategyEvaluator {

        override fun evaluate(
            strategy: StrategyDefinition,
            observation: StrategyObservation
        ): StrategyEvaluation {
            return evaluation
        }
    }

    private class ThrowingStrategyEvaluator(
        private val exception: IllegalArgumentException
    ) : StrategyEvaluator {

        override fun evaluate(
            strategy: StrategyDefinition,
            observation: StrategyObservation
        ): StrategyEvaluation {
            throw exception
        }
    }
}
