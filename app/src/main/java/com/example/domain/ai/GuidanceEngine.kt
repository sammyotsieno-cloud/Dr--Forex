package com.example.domain.ai

import com.example.domain.reasoning.TradeDecision

data class GuidanceAlert(
    val level: String, // "INFO", "WARNING", "CRITICAL"
    val title: String,
    val message: String
)

object GuidanceEngine {

    fun generateGuidance(decision: TradeDecision?, spreadPips: Double, riskPercent: Double): List<GuidanceAlert> {
        val alerts = mutableListOf<GuidanceAlert>()

        if (riskPercent > 2.0) {
            alerts.add(
                GuidanceAlert(
                    level = "CRITICAL",
                    title = "Over-Leverage Risk Detected",
                    message = "Risking ${riskPercent}% per trade exceeds institutional safety bounds (max 1-2%). High probability of sequence risk."
                )
            )
        }

        if (spreadPips > 2.5) {
            alerts.add(
                GuidanceAlert(
                    level = "WARNING",
                    title = "High Market Friction",
                    message = "Active spread of $spreadPips pips will severely degrade short-term expectancy. Recommended execution on H1/H4 timeframes."
                )
            )
        }

        decision?.let { d ->
            if (d.context.volatilityLevel == "High") {
                alerts.add(
                    GuidanceAlert(
                        level = "WARNING",
                        title = "Volatility Expansion (ATR ${String.format("%.1f", d.stopLossPips)} Pips)",
                        message = "Wider price swings require reduced position lot sizing to strictly maintain the 1% risk rule."
                    )
                )
            }

            if (d.action == "WAIT") {
                alerts.add(
                    GuidanceAlert(
                        level = "INFO",
                        title = "Capital Preservation Active",
                        message = "Patience is a trading position. Current confluence (${d.evidence.confluenceScore}%) does not meet the hurdle rate."
                    )
                )
            }
        }

        return alerts
    }
}
