package com.drforex.researchlab.core.market

/**
 * Quality state assigned to market data before it is consumed by
 * research engines.
 *
 * Research code must be able to distinguish valid observations from
 * observations that require investigation.
 */
enum class MarketDataQuality {
    UNKNOWN,
    VALIDATED,
    SUSPECT,
    INVALID
}

/**
 * Describes a validation finding attached to market data.
 *
 * A finding contains both a machine-readable code and a human-readable
 * explanation so that validation results can be consumed by research
 * engines, diagnostics, logs, and future UI layers.
 */
data class MarketDataQualityFinding(
    val quality: MarketDataQuality,
    val code: String,
    val message: String
) {

    init {
        require(code.isNotBlank()) {
            "Quality finding code must not be blank."
        }

        require(message.isNotBlank()) {
            "Quality finding message must not be blank."
        }
    }
}

/**
 * Result of validating a market-data object.
 *
 * This is deliberately separate from the validation engine itself.
 * The validator determines the result; this model carries the result
 * through the rest of the research pipeline.
 */
data class MarketDataValidationResult(
    val quality: MarketDataQuality,
    val findings: List<MarketDataQualityFinding> = emptyList()
) {

    /**
     * True only when the data has explicitly passed validation and may
     * be consumed as reliable research input.
     */
    val isUsableForResearch: Boolean
        get() = quality == MarketDataQuality.VALIDATED

    /**
     * True when the data has a quality state that requires investigation
     * before it can be trusted.
     */
    val requiresInvestigation: Boolean
        get() = quality == MarketDataQuality.SUSPECT ||
            quality == MarketDataQuality.INVALID
}
