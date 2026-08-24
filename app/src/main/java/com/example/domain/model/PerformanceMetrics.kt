package com.example.domain.model

/**
 * Multi-dimensional strategy evaluation metrics.
 *
 * Scientific Principle:
 * A strategy must NEVER be evaluated solely on win rate or nominal profit.
 * Robustness requires examining risk-adjusted returns (Sharpe, Sortino),
 * tail risk (Maximum Drawdown, Recovery Factor), and mathematical expectancy.
 */
data class PerformanceMetrics(
    val netProfit: Double = 0.0,
    val returnPercent: Double = 0.0,
    val winRate: Double = 0.0,
    val profitFactor: Double = 0.0,
    val expectancy: Double = 0.0,
    val maximumDrawdown: Double = 0.0,
    val maximumDrawdownPercent: Double = 0.0,
    val recoveryFactor: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val sortinoRatio: Double = 0.0,
    val numberOfTrades: Int = 0,
    val winningTrades: Int = 0,
    val losingTrades: Int = 0,
    val averageWin: Double = 0.0,
    val averageLoss: Double = 0.0,
    val riskRewardRatio: Double = 0.0,
    val maxConsecutiveLosses: Int = 0
)
