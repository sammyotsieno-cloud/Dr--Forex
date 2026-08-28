package com.example.domain.ai

enum class CognitiveTier(val id: String, val title: String, val emoji: String, val description: String) {
    ELI3("ELI3", "Explain Like I'm 3", "👶", "Simple analogies, friendly metaphors, zero jargon"),
    TRADER("TRADER", "Practical Trader", "📈", "Support/Resistance, EMAs, RSI, and actionable trade setups"),
    QUANT("QUANT", "Quantitative Pro", "🔬", "Statistical confidence, ATR distributions, Sharpe ratio, and alpha decay")
}

object ExplanationEngine {

    fun formatMarketDecision(
        tier: CognitiveTier,
        symbol: String,
        decision: String, // "BUY", "SELL", "WAIT"
        regime: String,
        evidenceScore: Int,
        rsiValue: Double,
        price: Double,
        stopLoss: Double,
        takeProfit: Double,
        rewardToRisk: Double
    ): String {
        return when (tier) {
            CognitiveTier.ELI3 -> {
                when (decision.uppercase()) {
                    "BUY" -> "🟢 Green Light! The market for $symbol wants to climb like a rocket. We saw friends helping it push up. We put a safety cushion at ${String.format("%.4f", stopLoss)} just in case it falls."
                    "SELL" -> "🔴 Red Light! The market for $symbol is sliding downhill like a slide. We are waiting safely with our helmet on."
                    else -> "🟡 Yellow Light! Hold your horses! The market for $symbol is playing hide and seek and cannot decide where to go. We stay seated and watch patiently."
                }
            }
            CognitiveTier.TRADER -> {
                when (decision.uppercase()) {
                    "BUY" -> "📈 Bullish Opportunity on $symbol at ${String.format("%.4f", price)}. Market structure is in a $regime with RSI at ${String.format("%.1f", rsiValue)}. Stop Loss: ${String.format("%.4f", stopLoss)} | Target: ${String.format("%.4f", takeProfit)} (R:R ${String.format("%.1f", rewardToRisk)}:1). Confidence: $evidenceScore%."
                    "SELL" -> "📉 Bearish Setup on $symbol at ${String.format("%.4f", price)}. Resistance held firmly in $regime. Stop Loss: ${String.format("%.4f", stopLoss)} | Target: ${String.format("%.4f", takeProfit)}. Confluence: $evidenceScore%."
                    else -> "⏸️ Neutral Stance (WAIT) on $symbol. Market is consolidating with mixed signals (RSI ${String.format("%.1f", rsiValue)}). No high-probability edge present. Capital preservation is priority."
                }
            }
            CognitiveTier.QUANT -> {
                when (decision.uppercase()) {
                    "BUY" -> "🔬 LONG Execution Order: $symbol @ ${String.format("%.5f", price)}. Regime: $regime. Confluence Score: $evidenceScore/100. RSI(14): ${String.format("%.2f", rsiValue)}. Risk Invariant: 1.0% NAV. Stop: ${String.format("%.5f", stopLoss)}, TP: ${String.format("%.5f", takeProfit)}, Expected R:R = ${String.format("%.2f", rewardToRisk)}."
                    "SELL" -> "🔬 SHORT Execution Order: $symbol @ ${String.format("%.5f", price)}. Regime: $regime. Stat confluence: $evidenceScore%. Stop: ${String.format("%.5f", stopLoss)}, TP: ${String.format("%.5f", takeProfit)}."
                    else -> "🔬 INSUFFICIENT CONFLUENCE ($evidenceScore/100). Regime: $regime. Expectancy is below statistical hurdle rate. Preserving liquidity in state WAIT."
                }
            }
        }
    }

    fun explainConcept(topic: String, tier: CognitiveTier): String {
        val lower = topic.lowercase()
        return when {
            lower.contains("rsi") -> {
                when (tier) {
                    CognitiveTier.ELI3 -> "RSI is like a speedometer on a bicycle! When the rider pedals too fast (above 70), they get tired and slow down. When they pedal too slow (below 30), they catch their breath and speed up!"
                    CognitiveTier.TRADER -> "RSI (Relative Strength Index) measures price momentum on a scale of 0 to 100. Above 70 suggests overbought (potential pullback), while below 30 signals oversold (potential bounce)."
                    CognitiveTier.QUANT -> "RSI is a bounded momentum oscillator calculating the ratio of exponential average gains to average losses over 14 periods: RSI = 100 - (100 / (1 + RS)). Mean-reverting extremes exceed +/- 1.5 standard deviations."
                }
            }
            lower.contains("ema") || lower.contains("moving average") -> {
                when (tier) {
                    CognitiveTier.ELI3 -> "An EMA is like drawing a smooth line following the path of footprints on a beach, showing you where the waves are really flowing!"
                    CognitiveTier.TRADER -> "An EMA (Exponential Moving Average) gives higher weight to recent prices, smoothing noise and clearly displaying the trend direction and dynamic support/resistance."
                    CognitiveTier.QUANT -> "EMA applies a recursive weighting multiplier α = 2 / (N + 1) to recent price ticks, reducing lag relative to SMA while functioning as a low-pass digital filter."
                }
            }
            lower.contains("risk") || lower.contains("stop loss") -> {
                when (tier) {
                    CognitiveTier.ELI3 -> "A stop loss is your superhero seatbelt! If the ride goes the wrong way, the seatbelt clicks and saves all your candy!"
                    CognitiveTier.TRADER -> "A stop loss is an automated exit order placed at an invalidation level to cap maximum downside risk to 1% of your total equity per trade."
                    CognitiveTier.QUANT -> "Stop loss establishes a strict mathematical loss constraint defined by ATR multiples (e.g. 1.5 * ATR(14)), preventing gambler's ruin and bounding downside tail risk."
                }
            }
            else -> {
                when (tier) {
                    CognitiveTier.ELI3 -> "Trading is simply finding when things are on sale at a discount and sharing them when people really want them, always wearing safety pads!"
                    CognitiveTier.TRADER -> "Quantitative trading replaces emotional gambling with verified rules, statistical confluence, and disciplined risk management."
                    CognitiveTier.QUANT -> "Systematic algorithmic trading extracts positive mathematical expectancy from non-stationary financial time series using deterministic edge validation."
                }
            }
        }
    }
}
