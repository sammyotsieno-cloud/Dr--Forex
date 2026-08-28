package com.example.domain.reasoning

import com.example.data.local.entity.CandleEntity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class MarketContext(
    val symbol: String,
    val timeframe: String,
    val latestPrice: Double,
    val regime: String, // "Bullish Trend", "Bearish Trend", "Ranging Expansion", "Consolidation"
    val ema20: Double,
    val ema50: Double,
    val rsi14: Double,
    val atr14: Double,
    val volatilityLevel: String, // "Low", "Normal", "High", "Extreme"
    val candleCount: Int
)

data class TradeHypothesis(
    val name: String,
    val setupRationale: String,
    val requiredDirection: String, // "BUY", "SELL", "NONE"
    val expectedRewardToRisk: Double
)

data class MarketEvidence(
    val confluenceScore: Int, // 0 - 100
    val trendAgreement: Boolean,
    val momentumAgreement: Boolean,
    val dataQualityScore: Double, // 0 - 100
    val supportingFactors: List<String>,
    val riskFactors: List<String>
)

data class TradeDecision(
    val action: String, // "BUY", "SELL", "WAIT"
    val confidencePercent: Int,
    val context: MarketContext,
    val hypothesis: TradeHypothesis,
    val evidence: MarketEvidence,
    val entryPrice: Double,
    val stopLossPrice: Double,
    val takeProfitPrice: Double,
    val stopLossPips: Double,
    val takeProfitPips: Double,
    val positionSizeUnits: Double,
    val riskAmountDollars: Double,
    val timestamp: Long = System.currentTimeMillis()
)

object ReasoningAndDecisionEngine {

    fun analyzeMarketAndDecide(
        symbol: String,
        timeframe: String,
        candles: List<CandleEntity>,
        accountBalance: Double = 20000.0,
        riskPercent: Double = 1.0,
        dataQuality: Double = 98.5
    ): TradeDecision {
        if (candles.size < 20) {
            val emptyContext = MarketContext(
                symbol = symbol,
                timeframe = timeframe,
                latestPrice = candles.lastOrNull()?.close ?: 1.0000,
                regime = "Insufficient Data",
                ema20 = 0.0,
                ema50 = 0.0,
                rsi14 = 50.0,
                atr14 = 0.0010,
                volatilityLevel = "Normal",
                candleCount = candles.size
            )
            return TradeDecision(
                action = "WAIT",
                confidencePercent = 0,
                context = emptyContext,
                hypothesis = TradeHypothesis("Data Insufficiency", "Minimum 20 candles required for statistical evaluation", "NONE", 1.0),
                evidence = MarketEvidence(0, false, false, dataQuality, emptyList(), listOf("Insufficient historical sample size")),
                entryPrice = emptyContext.latestPrice,
                stopLossPrice = emptyContext.latestPrice,
                takeProfitPrice = emptyContext.latestPrice,
                stopLossPips = 0.0,
                takeProfitPips = 0.0,
                positionSizeUnits = 0.0,
                riskAmountDollars = 0.0
            )
        }

        val closes = candles.map { it.close }
        val latestPrice = closes.last()
        val ema20 = calculateEma(closes, 20)
        val ema50 = calculateEma(closes, min(50, closes.size))
        val rsi14 = calculateRsi(closes, 14)
        val atr14 = calculateAtr(candles.takeLast(15))

        // Determine Market Regime
        val isBullishTrend = latestPrice > ema50 && ema20 > ema50
        val isBearishTrend = latestPrice < ema50 && ema20 < ema50
        val regime = when {
            isBullishTrend && rsi14 > 50 -> "Bullish Trend (Strong)"
            isBullishTrend && rsi14 <= 50 -> "Bullish Pullback"
            isBearishTrend && rsi14 < 50 -> "Bearish Trend (Strong)"
            isBearishTrend && rsi14 >= 50 -> "Bearish Pullback"
            else -> "Ranging Consolidation"
        }

        val pipMultiplier = if (symbol.contains("JPY", ignoreCase = true)) 100.0 else 10000.0
        val atrPips = atr14 * pipMultiplier
        val volatilityLevel = when {
            atrPips > 30.0 -> "High"
            atrPips < 8.0 -> "Low"
            else -> "Normal"
        }

        val context = MarketContext(
            symbol = symbol,
            timeframe = timeframe,
            latestPrice = latestPrice,
            regime = regime,
            ema20 = ema20,
            ema50 = ema50,
            rsi14 = rsi14,
            atr14 = atr14,
            volatilityLevel = volatilityLevel,
            candleCount = candles.size
        )

        // Evaluate Confluence & Evidence
        val supporting = mutableListOf<String>()
        val risks = mutableListOf<String>()
        var score = 50

        if (isBullishTrend) {
            supporting.add("Price trading above 50 EMA indicating upward momentum")
            score += 15
        } else if (isBearishTrend) {
            supporting.add("Price trading below 50 EMA confirming downward drift")
            score += 15
        }

        if (rsi14 in 35.0..48.0 && isBullishTrend) {
            supporting.add("RSI cooled down to oversold zone in macro uptrend (Prime Pullback)")
            score += 20
        } else if (rsi14 in 52.0..65.0 && isBearishTrend) {
            supporting.add("RSI bounced to overbought zone in macro downtrend (Prime Pullback)")
            score += 20
        } else if (rsi14 > 72.0) {
            risks.add("RSI severely overextended (>70) - potential mean reversion")
            score -= 15
        } else if (rsi14 < 28.0) {
            risks.add("RSI severely oversold (<30) - high exhaustion risk")
            score -= 15
        }

        if (volatilityLevel == "High") {
            risks.add("High ATR ($atrPips pips) requires wider safety buffers")
        }

        // Formulate Decision & Risk Parameters
        val action: String
        val hypothesis: TradeHypothesis
        val stopPips: Double
        val tpPips: Double
        val stopPrice: Double
        val tpPrice: Double

        if (score >= 70 && isBullishTrend) {
            action = "BUY"
            hypothesis = TradeHypothesis(
                name = "EMA 50 Confluence Pullback",
                setupRationale = "Bullish regime alignment with stabilized RSI pullback offering asymmetric reward-to-risk",
                requiredDirection = "BUY",
                expectedRewardToRisk = 2.2
            )
            stopPips = max(15.0, atrPips * 1.5)
            tpPips = stopPips * hypothesis.expectedRewardToRisk
            stopPrice = latestPrice - (stopPips / pipMultiplier)
            tpPrice = latestPrice + (tpPips / pipMultiplier)
        } else if (score >= 70 && isBearishTrend) {
            action = "SELL"
            hypothesis = TradeHypothesis(
                name = "EMA 50 Resistance Rejection",
                setupRationale = "Bearish regime alignment with rejection at dynamic moving average level",
                requiredDirection = "SELL",
                expectedRewardToRisk = 2.2
            )
            stopPips = max(15.0, atrPips * 1.5)
            tpPips = stopPips * hypothesis.expectedRewardToRisk
            stopPrice = latestPrice + (stopPips / pipMultiplier)
            tpPrice = latestPrice - (tpPips / pipMultiplier)
        } else {
            action = "WAIT"
            hypothesis = TradeHypothesis(
                name = "Neutral Range Filter",
                setupRationale = "Market structure is ranging without clean directional confluence",
                requiredDirection = "NONE",
                expectedRewardToRisk = 1.0
            )
            stopPips = 20.0
            tpPips = 20.0
            stopPrice = latestPrice
            tpPrice = latestPrice
        }

        val riskAmountDollars = accountBalance * (riskPercent / 100.0)
        val positionSizeUnits = if (stopPips > 0) (riskAmountDollars / (stopPips * (10.0 / pipMultiplier))) else 0.0

        val evidence = MarketEvidence(
            confluenceScore = score.coerceIn(10, 95),
            trendAgreement = isBullishTrend || isBearishTrend,
            momentumAgreement = rsi14 in 35.0..65.0,
            dataQualityScore = dataQuality,
            supportingFactors = supporting,
            riskFactors = risks
        )

        return TradeDecision(
            action = action,
            confidencePercent = score.coerceIn(10, 95),
            context = context,
            hypothesis = hypothesis,
            evidence = evidence,
            entryPrice = latestPrice,
            stopLossPrice = stopPrice,
            takeProfitPrice = tpPrice,
            stopLossPips = stopPips,
            takeProfitPips = tpPips,
            positionSizeUnits = positionSizeUnits,
            riskAmountDollars = riskAmountDollars
        )
    }

    private fun calculateEma(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 0.0
        val k = 2.0 / (period + 1.0)
        var ema = prices.first()
        for (i in 1 until prices.size) {
            ema = (prices[i] * k) + (ema * (1.0 - k))
        }
        return ema
    }

    private fun calculateRsi(prices: List<Double>, period: Int = 14): Double {
        if (prices.size <= period) return 50.0
        var gains = 0.0
        var losses = 0.0
        for (i in 1..period) {
            val diff = prices[i] - prices[i - 1]
            if (diff >= 0) gains += diff else losses += abs(diff)
        }
        var avgGain = gains / period
        var avgLoss = losses / period

        for (i in (period + 1) until prices.size) {
            val diff = prices[i] - prices[i - 1]
            val gain = if (diff > 0) diff else 0.0
            val loss = if (diff < 0) abs(diff) else 0.0
            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period
        }

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    private fun calculateAtr(candles: List<CandleEntity>): Double {
        if (candles.size < 2) return 0.0010
        var sumTr = 0.0
        for (i in 1 until candles.size) {
            val high = candles[i].high
            val low = candles[i].low
            val prevClose = candles[i - 1].close
            val tr = max(high - low, max(abs(high - prevClose), abs(low - prevClose)))
            sumTr += tr
        }
        return sumTr / (candles.size - 1)
    }
}
