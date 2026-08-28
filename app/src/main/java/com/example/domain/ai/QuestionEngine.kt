package com.example.domain.ai

import com.example.data.local.entity.LearnedPatternEntity
import com.example.data.local.entity.TradeObservationEntity
import com.example.domain.reasoning.TradeDecision

object QuestionEngine {

    fun processQuery(
        userQuery: String,
        tier: CognitiveTier,
        latestDecision: TradeDecision?,
        patterns: List<LearnedPatternEntity>,
        recentObservations: List<TradeObservationEntity>
    ): String {
        val q = userQuery.trim().lowercase()

        return when {
            q.contains("eli3") || q.contains("explain like i'm 3") || q.contains("explain like im 3") || q.contains("simple") -> {
                if (latestDecision != null) {
                    ExplanationEngine.formatMarketDecision(
                        tier = CognitiveTier.ELI3,
                        symbol = latestDecision.context.symbol,
                        decision = latestDecision.action,
                        regime = latestDecision.context.regime,
                        evidenceScore = latestDecision.evidence.confluenceScore,
                        rsiValue = latestDecision.context.rsi14,
                        price = latestDecision.entryPrice,
                        stopLoss = latestDecision.stopLossPrice,
                        takeProfit = latestDecision.takeProfitPrice,
                        rewardToRisk = latestDecision.hypothesis.expectedRewardToRisk
                    )
                } else {
                    "👶 The market is like a playground full of swings and slides. When lots of friends want to push a swing up, price climbs high! When everyone goes home for lunch, it rests."
                }
            }

            q.contains("predict") || q.contains("forecast") || q.contains("analyze") || q.contains("trade") || q.contains("signal") || q.contains("decision") || q.contains("market") -> {
                if (latestDecision != null) {
                    ExplanationEngine.formatMarketDecision(
                        tier = tier,
                        symbol = latestDecision.context.symbol,
                        decision = latestDecision.action,
                        regime = latestDecision.context.regime,
                        evidenceScore = latestDecision.evidence.confluenceScore,
                        rsiValue = latestDecision.context.rsi14,
                        price = latestDecision.entryPrice,
                        stopLoss = latestDecision.stopLossPrice,
                        takeProfit = latestDecision.takeProfitPrice,
                        rewardToRisk = latestDecision.hypothesis.expectedRewardToRisk
                    )
                } else {
                    "No active market dataset selected. Please load a dataset from the Data tab to run live reasoning."
                }
            }

            q.contains("why") && (q.contains("fail") || q.contains("loss") || q.contains("stop")) -> {
                val lastLoss = recentObservations.firstOrNull { it.outcome == "LOSS" }
                if (lastLoss != null) {
                    when (tier) {
                        CognitiveTier.ELI3 -> "👶 The last time we tried to climb the hill, a sudden gust of wind blew our hat off! The reason was: ${lastLoss.rootCause}."
                        CognitiveTier.TRADER -> "📉 Observation Post-Mortem on ${lastLoss.symbol}: The trade was stopped out due to: ${lastLoss.rootCause}. Max adverse excursion reached ${String.format("%.1f", lastLoss.maePips)} pips."
                        CognitiveTier.QUANT -> "🔬 Trade Attribution Audit: Loss of \$${String.format("%.2f", lastLoss.pnl)} recorded. Root cause: ${lastLoss.rootCause}. Regime state at execution: ${lastLoss.regime}."
                    }
                } else {
                    "No recent trade losses in memory. The system has maintained solid capital preservation."
                }
            }

            q.contains("learn") || q.contains("memory") || q.contains("pattern") || q.contains("strategy") -> {
                val topPattern = patterns.maxByOrNull { it.confidenceScore }
                if (topPattern != null) {
                    when (tier) {
                        CognitiveTier.ELI3 -> "🧠 My brain remembered that whenever the weather is '${topPattern.regime}', we win games ${topPattern.winRatePercent.toInt()}% of the time! Lesson: ${topPattern.keyLesson}"
                        CognitiveTier.TRADER -> "🧠 Learned Strategy Memory: Pattern '${topPattern.patternName}' in ${topPattern.regime} shows ${String.format("%.1f", topPattern.winRatePercent)}% win rate across ${topPattern.sampleSize} samples with Profit Factor ${String.format("%.2f", topPattern.avgProfitFactor)}. Key lesson: ${topPattern.keyLesson}"
                        CognitiveTier.QUANT -> "🔬 Knowledge Repository: Top pattern '${topPattern.id}' demonstrates confidence score ${String.format("%.2f", topPattern.confidenceScore)} with empirical expectancy +${String.format("%.2f", topPattern.avgProfitFactor)} PF."
                    }
                } else {
                    "Brain memory is recording ongoing simulations. Run a backtest in the Experiments workstation to feed the learning engine."
                }
            }

            q.contains("rsi") || q.contains("indicator") || q.contains("ema") || q.contains("risk") || q.contains("stop loss") -> {
                ExplanationEngine.explainConcept(userQuery, tier)
            }

            else -> {
                when (tier) {
                    CognitiveTier.ELI3 -> "👶 I am Dr. Forex! You can ask me 'What is our prediction?', 'Explain like I'm 3', or 'Why did the last trade stop out?'. I'll explain everything simply!"
                    CognitiveTier.TRADER -> "📈 Dr. Forex AI Analyst ready. Ask for a market diagnosis, trade decision breakdown (BUY/SELL/WAIT), technical indicator tutorial, or trade post-mortem."
                    CognitiveTier.QUANT -> "🔬 Dr. Forex Quantitative Reasoning Engine online. Ready to synthesize market context vectors, hypothesis confluence, ATR risk limits, and knowledge memory attribution."
                }
            }
        }
    }
}
