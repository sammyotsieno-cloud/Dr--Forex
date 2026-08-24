package com.example.domain.model

/**
 * Defines a strategy hypothesis for future testing in the laboratory.
 */
data class StrategyDefinition(
    val strategyId: String,
    val name: String,
    val description: String,
    val parameters: Map<String, String> = emptyMap(),
    val entryRules: List<String> = emptyList(),
    val exitRules: List<String> = emptyList(),
    val riskRules: List<String> = emptyList(),
    val version: String = "1.0.0"
)
