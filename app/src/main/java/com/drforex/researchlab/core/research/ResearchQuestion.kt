package com.drforex.researchlab.core.research

import com.drforex.researchlab.core.market.MarketInstrument
import com.drforex.researchlab.core.market.MarketTimeframe
import com.drforex.researchlab.core.time.MarketTime

/**
 * A formally defined research question that Dr. Forex intends to investigate.
 *
 * Research questions are normally generated automatically from market
 * observations, relationships, anomalies, knowledge gaps, previous findings,
 * or changing market conditions.
 *
 * A user may also provide a research objective or question, which can be
 * converted into this model by the research system.
 *
 * A ResearchQuestion is not:
 * - a trading signal
 * - a strategy
 * - a forecast
 * - a hypothesis
 * - evidence that a relationship is true
 *
 * It defines what the research process is trying to understand.
 */
data class ResearchQuestion(
    val id: String,
    val title: String,
    val question: String,
    val description: String,
    val createdAt: MarketTime,
    val source: ResearchQuestionSource,
    val instrument: MarketInstrument? = null,
    val timeframe: MarketTimeframe? = null,
    val variables: List<ResearchVariable> = emptyList(),
    val conditions: List<ResearchCondition> = emptyList(),
    val motivation: ResearchQuestionMotivation,
    val priority: ResearchQuestionPriority = ResearchQuestionPriority.NORMAL,
    val status: ResearchQuestionStatus = ResearchQuestionStatus.OPEN
) {
    init {
        require(id.isNotBlank()) {
            "Research question id must not be blank."
        }

        require(title.isNotBlank()) {
            "Research question title must not be blank."
        }

        require(question.isNotBlank()) {
            "Research question must not be blank."
        }

        require(description.isNotBlank()) {
            "Research question description must not be blank."
        }

        require(variables.map { it.name }.distinct().size == variables.size) {
            "Research question variables must have unique names."
        }

        require(conditions.map { it.name }.distinct().size == conditions.size) {
            "Research question conditions must have unique names."
        }
    }
}

/**
 * Describes where the research question originated.
 *
 * AUTOMATIC is the normal operating mode of Dr. Forex.
 *
 * USER_DIRECTED represents a question or research objective supplied
 * by the user and formalized by the research system.
 */
enum class ResearchQuestionSource {
    AUTOMATIC,
    USER_DIRECTED
}

/**
 * Describes why the research system considers a question worth investigating.
 *
 * These motivations allow autonomous research to remain grounded in
 * observable evidence rather than generating arbitrary questions.
 */
enum class ResearchQuestionMotivation {
    MARKET_OBSERVATION,
    STATISTICAL_RELATIONSHIP,
    ANOMALY,
    MARKET_REGIME_CHANGE,
    KNOWLEDGE_GAP,
    PREVIOUS_FINDING,
    DEGRADED_FINDING,
    INVALIDATED_FINDING,
    USER_OBJECTIVE,
    SYSTEM_REVIEW
}

/**
 * Priority assigned to a research question.
 *
 * Priority determines research ordering, not whether a question is true.
 */
enum class ResearchQuestionPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}

/**
 * A variable that may be observed, measured, controlled, or evaluated
 * during a future research experiment.
 *
 * This model defines the variable concept only.
 * It does not calculate or evaluate the variable.
 */
data class ResearchVariable(
    val name: String,
    val role: ResearchVariableRole,
    val description: String
) {
    init {
        require(name.isNotBlank()) {
            "Research variable name must not be blank."
        }

        require(description.isNotBlank()) {
            "Research variable description must not be blank."
        }
    }
}

/**
 * The role assigned to a research variable.
 */
enum class ResearchVariableRole {
    INDEPENDENT,
    DEPENDENT,
    CONTROL,
    OBSERVATIONAL,
    CONTEXT
}

/**
 * A condition describing when a research question is applicable.
 *
 * Conditions are declarative. They do not evaluate themselves.
 */
data class ResearchCondition(
    val name: String,
    val description: String
) {
    init {
        require(name.isNotBlank()) {
            "Research condition name must not be blank."
        }

        require(description.isNotBlank()) {
            "Research condition description must not be blank."
        }
    }
}

/**
 * Lifecycle status of a research question.
 */
enum class ResearchQuestionStatus {
    OPEN,
    HYPOTHESIS_FORMULATED,
    EXPERIMENT_READY,
    UNDER_RESEARCH,
    ANSWERED,
    INCONCLUSIVE,
    CLOSED
}
