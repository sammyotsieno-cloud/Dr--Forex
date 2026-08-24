package com.example.domain.model

enum class TradeDirection {
    BUY,
    SELL
}

enum class ExitReason {
    TAKE_PROFIT,
    STOP_LOSS,
    TRAILING_STOP,
    TIME_EXIT,
    SIGNAL_REVERSAL,
    MAX_HOLDING_PERIOD,
    RISK_STOP,
    MANUAL_CLOSE
}

/**
 * Record of an executed backtest trade.
 */
data class Trade(
    val tradeId: String,
    val entryTime: Long,
    val exitTime: Long? = null,
    val direction: TradeDirection,
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val positionSize: Double,
    val profitLoss: Double? = null,
    val returnPercent: Double? = null,
    val exitReason: ExitReason? = null,
    val spreadCost: Double = 0.0,
    val slippageCost: Double = 0.0,
    val commissionCost: Double = 0.0
)
