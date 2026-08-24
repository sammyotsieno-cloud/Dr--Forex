package com.example.domain.engine

import com.example.domain.model.Experiment
import com.example.domain.model.MarketCandle
import com.example.domain.model.PerformanceMetrics
import com.example.domain.model.ResearchConfiguration
import com.example.domain.model.StrategyDefinition
import com.example.domain.model.Trade

/**
 * MODULE STATUS & EXTENSION POINT INTERFACES
 *
 * Dr. Forex is structured with clean modular interfaces for future phases.
 * In Phase 1, all future engines are defined as explicit contract interfaces
 * with clear scientific integrity constraints (no look-ahead bias, point-in-time state).
 */

enum class ModuleStatus(val label: String, val isReady: Boolean) {
    READY("READY", true),
    NOT_IMPLEMENTED("NOT IMPLEMENTED", false)
}

data class PipelineModuleInfo(
    val id: String,
    val name: String,
    val phase: Int,
    val status: ModuleStatus,
    val description: String,
    val scientificConstraint: String
)

/**
 * 1. Indicator Engine Interface (Phase 3)
 * Point-in-time technical indicators computed strictly on closed candles.
 */
interface IndicatorEngine {
    fun calculate(indicatorName: String, candles: List<MarketCandle>, parameters: Map<String, Any>): List<Double>
}

/**
 * 2. Market Structure Engine Interface (Phase 3)
 * Swings, Order Blocks, Liquidity Sweeps, Break of Structure (BOS) / Change of Character (CHoCH).
 */
interface MarketStructureEngine {
    fun identifyStructure(candles: List<MarketCandle>): Map<String, Any>
}

/**
 * 3. Strategy Engine Interface (Phase 4)
 * Evaluates strategy hypotheses and generates deterministic trade intent signals.
 */
interface StrategyEngine {
    fun evaluateSignal(strategy: StrategyDefinition, historicalWindow: List<MarketCandle>): TradeSignal?
}

data class TradeSignal(
    val timestamp: Long,
    val direction: String,
    val price: Double,
    val suggestedStopLoss: Double,
    val suggestedTakeProfit: Double,
    val reason: String
)

/**
 * 4. Backtest Engine Interface (Phase 5)
 * Event-driven historical simulation with strict look-ahead bias protection,
 * next-candle open execution, spread, and slippage modelling.
 */
interface BacktestEngine {
    suspend fun executeBacktest(
        strategy: StrategyDefinition,
        candles: List<MarketCandle>,
        config: ResearchConfiguration
    ): BacktestResult
}

data class BacktestResult(
    val experimentId: String,
    val trades: List<Trade>,
    val metrics: PerformanceMetrics,
    val executionLog: List<String>
)

/**
 * 5. Risk Engine Interface (Phase 6)
 * Position sizing, fractional risk limits, daily drawdown limits, correlation exposure.
 */
interface RiskEngine {
    fun calculatePositionSize(
        accountBalance: Double,
        riskPercent: Double,
        entryPrice: Double,
        stopLossPrice: Double,
        instrument: String
    ): Double
}

/**
 * 6. Performance Analyzer Interface (Phase 7)
 * Multi-dimensional statistical evaluation (Sharpe, Sortino, Expectancy, Max Drawdown, Recovery Factor).
 */
interface PerformanceAnalyzer {
    fun analyzeTrades(trades: List<Trade>, initialCapital: Double): PerformanceMetrics
}

/**
 * 7. Robustness Analyzer Interface (Phase 8)
 * Parameter sensitivity, permutation testing, slippage stress-testing.
 */
interface RobustnessAnalyzer {
    fun testRobustness(experiment: Experiment, candles: List<MarketCandle>): Map<String, Any>
}

/**
 * 8. Strategy Optimizer Interface (Phase 10)
 * Parameter grid/search with strict overfitting penalties.
 */
interface StrategyOptimizer {
    fun optimize(strategy: StrategyDefinition, trainingCandles: List<MarketCandle>): List<StrategyDefinition>
}

/**
 * 9. Walk-Forward Engine Interface (Phase 9)
 * Rolling and anchored in-sample optimization with out-of-sample forward testing.
 */
interface WalkForwardEngine {
    fun executeWalkForward(
        strategy: StrategyDefinition,
        candles: List<MarketCandle>,
        inSamplePeriods: Int,
        outOfSamplePeriods: Int
    ): Map<String, Any>
}

/**
 * 10. Monte Carlo Analyzer Interface (Phase 8)
 * Trade reshuffling and equity curve drawdown confidence intervals.
 */
interface MonteCarloAnalyzer {
    fun runSimulations(trades: List<Trade>, iterations: Int = 1000): Map<String, Double>
}

/**
 * 11. Regime Detector Interface (Phase 11)
 * Market state classification (Trending, Ranging, High Volatility, Low Liquidity).
 */
interface RegimeDetector {
    fun detectRegime(candles: List<MarketCandle>): String
}

/**
 * 12. Machine Learning Engine Interface (Phase 11)
 * Feature extraction and predictive hypothesis testing with walk-forward temporal splits.
 */
interface MachineLearningEngine {
    fun trainModel(features: List<List<Double>>, labels: List<Double>): Any?
    fun predict(features: List<Double>): Double
}
