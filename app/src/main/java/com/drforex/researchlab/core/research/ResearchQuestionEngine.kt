package com.drforex.researchlab.core.research

/**
 * Validates the structural integrity of research questions and hypotheses.
 *
 * This engine does not determine whether a research question is important
 * or whether a hypothesis is true.
 *
 * It verifies that research definitions are internally coherent before
 * they are passed to later research infrastructure.
 *
 * Research generation, experiment design, statistical evaluation, and
 * strategy validation belong to other parts of the research workflow.
 */
class ResearchQuestionEngine {

    /**
     * Validates whether a research question is structurally complete enough
     * to proceed to hypothesis development.
     */
    fun validateQuestion(
        question: ResearchQuestion
    ): ResearchDefinitionValidation {
        val errors = mutableListOf<String>()

        if (question.question.trim().endsWith("?").not()) {
            errors += "Research question should be phrased as a question."
        }

        if (question.variables.isEmpty()) {
            errors += "Research question must define at least one variable."
        }

        val duplicateVariables =
            question.variables
                .groupingBy { it.name }
                .eachCount()
                .filterValues { it > 1 }
                .keys

        if (duplicateVariables.isNotEmpty()) {
            errors += "Research question contains duplicate variables: " +
                duplicateVariables.sorted().joinToString(", ")
        }

        val duplicateConditions =
            question.conditions
                .groupingBy { it.name }
                .eachCount()
                .filterValues { it > 1 }
                .keys

        if (duplicateConditions.isNotEmpty()) {
            errors += "Research question contains duplicate conditions: " +
                duplicateConditions.sorted().joinToString(", ")
        }

        return validationResult(errors)
    }

    /**
     * Validates whether a hypothesis is structurally complete enough
     * to proceed to experiment design.
     *
     * This method does not test the hypothesis against market data.
     */
    fun validateHypothesis(
        hypothesis: ResearchHypothesis
    ): ResearchDefinitionValidation {
        val errors = mutableListOf<String>()

        if (hypothesis.independentVariables.isEmpty()) {
            errors += "Hypothesis must define at least one independent variable."
        }

        if (hypothesis.independentVariables.any { it.isBlank() }) {
            errors += "Hypothesis contains a blank independent variable."
        }

        if (hypothesis.dependentVariable.isBlank()) {
            errors += "Hypothesis must define a dependent variable."
        }

        if (hypothesis.testConditions.isEmpty()) {
            errors += "Hypothesis must define at least one test condition."
        }

        if (hypothesis.outcomeDefinition.isBlank()) {
            errors += "Hypothesis must define an outcome definition."
        }

        if (hypothesis.prediction.isBlank()) {
            errors += "Hypothesis must define a prediction."
        }

        val duplicateVariables =
            hypothesis.independentVariables
                .groupingBy { it }
                .eachCount()
                .filterValues { it > 1 }
                .keys

        if (duplicateVariables.isNotEmpty()) {
            errors += "Hypothesis contains duplicate independent variables: " +
                duplicateVariables.sorted().joinToString(", ")
        }

        return validationResult(errors)
    }

    /**
     * Validates that every variable referenced by a hypothesis has been
     * declared by its parent research question.
     *
     * This prevents a generated hypothesis from silently introducing
     * variables that were never part of the original research definition.
     */
    fun validateVariableReferences(
        question: ResearchQuestion,
        hypothesis: ResearchHypothesis
    ): ResearchDefinitionValidation {
        val declaredVariables =
            question.variables
                .map { it.name }
                .toSet()

        val referencedVariables =
            hypothesis.independentVariables
                .toSet() +
                hypothesis.dependentVariable

        val undeclaredVariables =
            referencedVariables
                .filter { it !in declaredVariables }
                .sorted()

        return if (undeclaredVariables.isEmpty()) {
            ResearchDefinitionValidation.Valid
        } else {
            ResearchDefinitionValidation.Invalid(
                undeclaredVariables.map {
                    "Hypothesis references undeclared variable: $it"
                }
            )
        }
    }

    /**
     * Validates the complete structural relationship between a research
     * question and its hypothesis.
     */
    fun validate(
        question: ResearchQuestion,
        hypothesis: ResearchHypothesis
    ): ResearchDefinitionValidation {
        val questionValidation = validateQuestion(question)

        if (questionValidation is ResearchDefinitionValidation.Invalid) {
            return questionValidation
        }

        if (hypothesis.questionId != question.id) {
            return ResearchDefinitionValidation.Invalid(
                listOf(
                    "Hypothesis question id does not match the parent research question."
                )
            )
        }

        val hypothesisValidation = validateHypothesis(hypothesis)

        if (hypothesisValidation is ResearchDefinitionValidation.Invalid) {
            return hypothesisValidation
        }

        return validateVariableReferences(
            question = question,
            hypothesis = hypothesis
        )
    }

    private fun validationResult(
        errors: List<String>
    ): ResearchDefinitionValidation {
        return if (errors.isEmpty()) {
            ResearchDefinitionValidation.Valid
        } else {
            ResearchDefinitionValidation.Invalid(errors)
        }
    }
}

/**
 * Result of structural validation of a research definition.
 */
sealed interface ResearchDefinitionValidation {

    /**
     * The research definition is structurally complete enough
     * to continue to the next stage.
     */
    data object Valid : ResearchDefinitionValidation

    /**
     * The research definition contains one or more structural problems.
     */
    data class Invalid(
        val errors: List<String>
    ) : ResearchDefinitionValidation
}
