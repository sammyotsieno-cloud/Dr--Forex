package com.drforex.researchlab.core.experiment

import com.drforex.researchlab.core.research.HypothesisRelationship
import com.drforex.researchlab.core.research.ResearchCondition
import com.drforex.researchlab.core.research.ResearchHypothesis
import com.drforex.researchlab.core.research.ResearchQuestion
import com.drforex.researchlab.core.research.ResearchQuestionMotivation
import com.drforex.researchlab.core.research.ResearchQuestionSource
import com.drforex.researchlab.core.research.ResearchVariable
import com.drforex.researchlab.core.research.ResearchVariableRole
import com.drforex.researchlab.core.time.MarketTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExperimentValidatorTest {

    private lateinit var validator: ExperimentValidator

    private val baseTime = MarketTime.fromEpochSeconds(1700000000L)
    private val scopeStartTime = MarketTime.fromEpochSeconds(1700001000L)
    private val scopeEndTime = MarketTime.fromEpochSeconds(1700005000L)

    @Before
    fun setUp() {
        validator = ExperimentValidator()
    }

    private fun createQuestion(id: String = "Q-1"): ResearchQuestion {
        return ResearchQuestion(
            id = id,
            title = "Momentum Drift Investigation",
            question = "Does short-term momentum predict forward return?",
            description = "Investigating momentum persistence in high-liquidity sessions.",
            createdAt = baseTime,
            source = ResearchQuestionSource.AUTOMATIC,
            variables = listOf(
                ResearchVariable(
                    name = "momentum",
                    role = ResearchVariableRole.INDEPENDENT,
                    description = "Short-term momentum metric"
                ),
                ResearchVariable(
                    name = "forward_return",
                    role = ResearchVariableRole.DEPENDENT,
                    description = "Subsequent candle return"
                )
            ),
            conditions = listOf(
                ResearchCondition(
                    name = "london_session",
                    description = "Active London trading session"
                )
            ),
            motivation = ResearchQuestionMotivation.MARKET_OBSERVATION
        )
    }

    private fun createHypothesis(
        id: String = "H-1",
        questionId: String = "Q-1"
    ): ResearchHypothesis {
        return ResearchHypothesis(
            id = id,
            questionId = questionId,
            statement = "Positive momentum in London session predicts positive forward return.",
            prediction = "Forward return will exceed zero following positive momentum.",
            relationship = HypothesisRelationship.POSITIVE,
            independentVariables = listOf("momentum"),
            dependentVariable = "forward_return",
            testConditions = listOf(
                ResearchCondition(
                    name = "london_session",
                    description = "Active London trading session"
                )
            ),
            outcomeDefinition = "Mean forward return greater than zero.",
            createdAt = baseTime
        )
    }

    private fun createExperiment(
        id: String = "E-1",
        questionId: String = "Q-1",
        hypothesisId: String = "H-1",
        variables: List<ExperimentVariable> = listOf(
            ExperimentVariable(
                name = "momentum",
                role = ExperimentVariableRole.INDEPENDENT,
                description = "Short-term momentum metric"
            ),
            ExperimentVariable(
                name = "forward_return",
                role = ExperimentVariableRole.DEPENDENT,
                description = "Subsequent candle return"
            )
        )
    ): ResearchExperiment {
        return ResearchExperiment(
            id = id,
            questionId = questionId,
            hypothesisId = hypothesisId,
            title = "London Momentum Forward Return Experiment",
            description = "Testing forward returns conditioned on momentum observations.",
            createdAt = baseTime,
            type = ExperimentType.OBSERVATIONAL,
            variables = variables,
            conditions = listOf(
                ResearchCondition(
                    name = "london_session",
                    description = "Active London trading session"
                )
            ),
            expectedOutcome = "Statistically significant positive mean return."
        )
    }

    private fun createDesign(): ExperimentDesign {
        return ExperimentDesign(
            designType = ExperimentDesignType.OBSERVATIONAL,
            observationUnit = ObservationUnit.CANDLE,
            repetitions = 1
        )
    }

    private fun createDataScope(
        cutoff: MarketTime? = null
    ): ExperimentDataScope {
        return ExperimentDataScope(
            instrument = "EUR_USD",
            timeframe = "H1",
            startTime = scopeStartTime,
            endTime = scopeEndTime,
            informationCutoff = cutoff
        )
    }

    private fun createProcedure(): ExperimentProcedure {
        return ExperimentProcedure(
            steps = listOf(
                ExperimentStep(
                    order = 1,
                    name = "SampleObservations",
                    description = "Record observations within data scope.",
                    action = ExperimentAction.RECORD_OBSERVATION
                )
            )
        )
    }

    private fun createObservation(
        sequence: Int,
        epochSeconds: Long
    ): ExperimentObservation {
        return ExperimentObservation(
            sequence = sequence,
            observedAt = MarketTime.fromEpochSeconds(epochSeconds),
            values = mapOf("momentum" to 1.25, "forward_return" to 0.0035)
        )
    }

    private fun createResult(
        experimentId: String = "E-1",
        status: ExperimentResultStatus = ExperimentResultStatus.COMPLETED,
        completedAt: MarketTime? = MarketTime.fromEpochSeconds(1700004500L),
        observations: List<ExperimentObservation> = listOf(
            createObservation(sequence = 1, epochSeconds = 1700002000L),
            createObservation(sequence = 2, epochSeconds = 1700003000L)
        ),
        errors: List<String> = emptyList()
    ): ExperimentResult {
        return ExperimentResult(
            experimentId = experimentId,
            startedAt = scopeStartTime,
            completedAt = completedAt,
            status = status,
            observations = observations,
            executionNotes = listOf("Execution completed normally."),
            errors = errors
        )
    }

    @Test
    fun validExperimentDefinitionIsAccepted() {
        val question = createQuestion()
        val hypothesis = createHypothesis()
        val experiment = createExperiment()
        val design = createDesign()
        val dataScope = createDataScope()
        val procedure = createProcedure()

        val validation = validator.validateDefinition(
            question = question,
            hypothesis = hypothesis,
            experiment = experiment,
            design = design,
            dataScope = dataScope,
            procedure = procedure
        )

        assertEquals(ExperimentValidation.Valid, validation)
    }

    @Test
    fun mismatchedExperimentQuestionIdIsRejected() {
        val question = createQuestion(id = "Q-1")
        val hypothesis = createHypothesis(questionId = "Q-1")
        val experiment = createExperiment(questionId = "Q-MISMATCH")
        val design = createDesign()
        val dataScope = createDataScope()
        val procedure = createProcedure()

        val validation = validator.validateDefinition(
            question = question,
            hypothesis = hypothesis,
            experiment = experiment,
            design = design,
            dataScope = dataScope,
            procedure = procedure
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("question id must match", ignoreCase = true) })
    }

    @Test
    fun mismatchedExperimentHypothesisIdIsRejected() {
        val question = createQuestion()
        val hypothesis = createHypothesis(id = "H-1")
        val experiment = createExperiment(hypothesisId = "H-MISMATCH")
        val design = createDesign()
        val dataScope = createDataScope()
        val procedure = createProcedure()

        val validation = validator.validateDefinition(
            question = question,
            hypothesis = hypothesis,
            experiment = experiment,
            design = design,
            dataScope = dataScope,
            procedure = procedure
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("hypothesis id must match", ignoreCase = true) })
    }

    @Test
    fun hypothesisIndependentVariableMissingFromExperimentIsRejected() {
        val question = createQuestion()
        val hypothesis = createHypothesis()
        // Omit the independent variable "momentum"
        val experiment = createExperiment(
            variables = listOf(
                ExperimentVariable(
                    name = "forward_return",
                    role = ExperimentVariableRole.DEPENDENT,
                    description = "Subsequent candle return"
                )
            )
        )
        val design = createDesign()
        val dataScope = createDataScope()
        val procedure = createProcedure()

        val validation = validator.validateDefinition(
            question = question,
            hypothesis = hypothesis,
            experiment = experiment,
            design = design,
            dataScope = dataScope,
            procedure = procedure
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("missing hypothesis independent variables", ignoreCase = true) })
    }

    @Test
    fun observationBeforeDataScopeIsRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700000500L) // before scopeStartTime (1700001000L)
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("occurs before the data scope", ignoreCase = true) })
    }

    @Test
    fun observationAfterDataScopeIsRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700006000L) // after scopeEndTime (1700005000L)
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("occurs after the data scope", ignoreCase = true) })
    }

    @Test
    fun observationAfterInformationCutoffIsRejected() {
        val experiment = createExperiment()
        val cutoff = MarketTime.fromEpochSeconds(1700002500L)
        val dataScope = createDataScope(cutoff = cutoff)
        val result = createResult(
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700002000L),
                createObservation(sequence = 2, epochSeconds = 1700003000L) // after cutoff (1700002500L)
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("occurs after the information cutoff", ignoreCase = true) })
    }

    @Test
    fun duplicateObservationSequenceNumbersAreRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700002000L),
                createObservation(sequence = 1, epochSeconds = 1700003000L) // duplicate sequence 1
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("sequence numbers must be unique", ignoreCase = true) })
    }

    @Test
    fun nonChronologicalObservationsAreRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700003000L),
                createObservation(sequence = 2, epochSeconds = 1700002000L) // earlier than previous
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("ordered chronologically", ignoreCase = true) })
    }

    @Test
    fun completedResultWithoutCompletedAtIsRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            status = ExperimentResultStatus.COMPLETED,
            completedAt = null
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("must have a completion time", ignoreCase = true) })
    }

    @Test
    fun failedResultWithoutErrorIsRejected() {
        val experiment = createExperiment()
        val dataScope = createDataScope()
        val result = createResult(
            status = ExperimentResultStatus.FAILED,
            completedAt = MarketTime.fromEpochSeconds(1700003000L),
            errors = emptyList()
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertTrue(validation is ExperimentValidation.Invalid)
        val errors = (validation as ExperimentValidation.Invalid).errors
        assertTrue(errors.any { it.contains("must contain at least one error", ignoreCase = true) })
    }

    @Test
    fun validResultWithinTemporalBoundariesIsAccepted() {
        val experiment = createExperiment()
        val dataScope = createDataScope(cutoff = MarketTime.fromEpochSeconds(1700004000L))
        val result = createResult(
            status = ExperimentResultStatus.COMPLETED,
            completedAt = MarketTime.fromEpochSeconds(1700004500L),
            observations = listOf(
                createObservation(sequence = 1, epochSeconds = 1700002000L),
                createObservation(sequence = 2, epochSeconds = 1700003000L)
            )
        )

        val validation = validator.validateResult(
            experiment = experiment,
            dataScope = dataScope,
            result = result
        )

        assertEquals(ExperimentValidation.Valid, validation)
    }
}
