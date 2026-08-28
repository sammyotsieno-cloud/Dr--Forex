package com.example.domain.broker

object SmartOrderRoutingEngine {

    fun getDefaultAccounts(): List<BrokerAccount> = listOf(
        BrokerAccount(
            id = "acc_ctrader_demo",
            name = "Pepperstone cTrader",
            platform = BrokerPlatform.CTRADER,
            environment = AccountEnvironment.DEMO,
            accountNumber = "8894210",
            serverName = "Spotware-Live-01",
            currency = "USD",
            balance = 50000.0,
            equity = 50420.50,
            freeMargin = 48200.0,
            leverage = 200,
            isConnected = true,
            pingMs = 16
        ),
        BrokerAccount(
            id = "acc_mt5_demo",
            name = "IC Markets MT5",
            platform = BrokerPlatform.METATRADER5,
            environment = AccountEnvironment.DEMO,
            accountNumber = "5512903",
            serverName = "ICMarketsSC-Demo",
            currency = "USD",
            balance = 20000.0,
            equity = 19880.00,
            freeMargin = 19200.0,
            leverage = 500,
            isConnected = true,
            pingMs = 24
        ),
        BrokerAccount(
            id = "acc_oanda_live",
            name = "OANDA Institutional",
            platform = BrokerPlatform.OANDA,
            environment = AccountEnvironment.LIVE,
            accountNumber = "001-004-9812401",
            serverName = "api-fxtrade.oanda.com",
            currency = "USD",
            balance = 10000.0,
            equity = 10000.00,
            freeMargin = 10000.0,
            leverage = 50,
            isConnected = true,
            pingMs = 38
        )
    )

    fun evaluateRouting(
        symbol: String,
        lotSize: Double,
        accounts: List<BrokerAccount>,
        preferDemo: Boolean = true
    ): List<RouteEvaluation> {
        val targetAccounts = accounts.filter {
            if (preferDemo) it.environment == AccountEnvironment.DEMO else it.environment == AccountEnvironment.LIVE
        }.ifEmpty { accounts }

        val evaluations = targetAccounts.map { acc ->
            val spread = when (acc.platform) {
                BrokerPlatform.CTRADER -> 0.2
                BrokerPlatform.METATRADER5 -> 0.6
                BrokerPlatform.OANDA -> 0.8
                BrokerPlatform.INTERACTIVE_BROKERS -> 0.3
            }

            val comm = when (acc.platform) {
                BrokerPlatform.CTRADER -> 3.50 * lotSize
                BrokerPlatform.INTERACTIVE_BROKERS -> 2.00 * lotSize
                else -> 0.0
            }

            val frictionUsd = (spread * 10.0 * lotSize) + comm
            val latency = acc.pingMs

            // Scoring algorithm (lower friction + lower latency = higher score)
            val frictionPenalty = (frictionUsd * 4).toInt()
            val latencyPenalty = (latency / 3)
            val score = (100 - frictionPenalty - latencyPenalty).coerceIn(40, 99)

            val reason = when {
                acc.platform == BrokerPlatform.CTRADER -> "Tighest raw spread (0.2 pips) with 16ms FIX API execution."
                acc.platform == BrokerPlatform.METATRADER5 -> "Zero commission execution with high margin flexibility (1:500)."
                acc.platform == BrokerPlatform.OANDA -> "Institutional REST gateway with precision unit sizing and no requotes."
                else -> "Standard institutional order execution."
            }

            RouteEvaluation(
                brokerAccount = acc,
                score = score,
                spreadPips = spread,
                estimatedFrictionUsd = frictionUsd,
                executionLatencyMs = latency,
                recommendationReason = reason,
                isOptimal = false
            )
        }

        val best = evaluations.maxByOrNull { it.score }
        return evaluations.map {
            if (it == best) it.copy(isOptimal = true) else it
        }
    }
}
