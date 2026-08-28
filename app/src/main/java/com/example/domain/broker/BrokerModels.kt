package com.example.domain.broker

import java.util.UUID

enum class BrokerPlatform(val displayName: String, val protocol: String, val avgSpreadPips: Double, val commPerLot: Double) {
    CTRADER("cTrader ECN", "Spotware Open API (OAuth 2.0)", 0.2, 3.50),
    METATRADER5("MetaTrader 5", "MT5 Cloud Gateway", 0.6, 0.0),
    OANDA("OANDA Institutional", "v20 Direct REST API", 0.8, 0.0),
    INTERACTIVE_BROKERS("Interactive Brokers", "TWS API Gateway", 0.3, 2.00)
}

enum class AccountEnvironment(val label: String) {
    DEMO("🧪 Demo / Research Sandbox"),
    LIVE("🛡️ Live / Real Capital")
}

data class BrokerAccount(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val platform: BrokerPlatform,
    val environment: AccountEnvironment,
    val accountNumber: String,
    val serverName: String,
    val currency: String = "USD",
    val balance: Double,
    val equity: Double,
    val freeMargin: Double,
    val leverage: Int = 100,
    val isConnected: Boolean = true,
    val pingMs: Int = 18
)

data class RouteEvaluation(
    val brokerAccount: BrokerAccount,
    val score: Int, // 0 - 100
    val spreadPips: Double,
    val estimatedFrictionUsd: Double,
    val executionLatencyMs: Int,
    val recommendationReason: String,
    val isOptimal: Boolean
)

data class TradeApprovalRequest(
    val symbol: String,
    val direction: String, // "BUY" or "SELL"
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val lotSize: Double,
    val riskAmountUsd: Double,
    val riskPercent: Double,
    val targetBroker: BrokerAccount,
    val reasonSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
