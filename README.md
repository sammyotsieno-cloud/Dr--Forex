# Dr. Forex — Quantitative Trading Research Laboratory

> **Dr. Forex is NOT a live trading bot.**  
> It is an Android-based quantitative trading research laboratory whose long-term purpose is to discover, test, compare, reject, refine, and progressively improve trading strategies using historical market data with uncompromising scientific discipline.

---

## 1. Research Philosophy & Scientific Integrity

The objective of Dr. Forex is to determine whether a trading strategy holds **genuine predictive edge** or is simply an artifact of data mining and curve-fitting.

### Core Scientific Mandates:
1. **Zero Look-Ahead Bias**: Point-in-time calculation rules. All decisions and indicator calculations are strictly executed on closed candles available at $t=0$. Future candle prices are strictly inaccessible.
2. **Realistic Market Friction**: Backtests must penalize trades with realistic bid/ask spreads, slippage, and swap costs.
3. **Overfitting Rejection**: Strategies are evaluated on out-of-sample data splits, parameter stability, and Monte Carlo robustness testing, rather than isolated high win-rates.
4. **Reproducible Experimentation**: Every test is an immutable, sequentially identified research experiment (`EXP-0001`, `EXP-0002`...) stored in a local Room database with its exact parameter matrix and dataset fingerprint.

---

## 2. 13-Phase Progressive Development Roadmap

- [x] **Phase 1: Android Research Lab Foundation & Data Ingestion** *(Current)*
  - Modern Jetpack Compose & Material 3 architecture
  - Room Database local persistence for Experiments, Datasets, and Research Configurations
  - Point-in-time Data Validation engine with physical OHLC and chronological checks
  - Quantitative engine interfaces (`IndicatorEngine`, `StrategyEngine`, `BacktestEngine`, etc.)
- [ ] **Phase 2: Point-in-Time Historical Data Engine** (Multi-timeframe synchronization, gap detection)
- [ ] **Phase 3: Quantitative Technical & Market Structure Engines** (EMAs, ATR, RSI, FVG, Swing Highs/Lows)
- [ ] **Phase 4: Modular Strategy Framework** (Hypothesis definition, deterministic rule evaluation)
- [ ] **Phase 5: Event-Driven Backtesting Simulator** (Next-candle open execution, spread, slippage modeling)
- [ ] **Phase 6: Capital Preservation & Risk Engine** (Position sizing, drawdown caps, risk per trade)
- [ ] **Phase 7: Performance Analytics & Strategy Comparison** (Sharpe, Sortino, Expectancy, Max Drawdown)
- [ ] **Phase 8: Robustness & Anti-Overfitting Lab** (Monte Carlo shuffling, parameter plateau sensitivity)
- [ ] **Phase 9: Walk-Forward & Out-of-Sample Validation** (Anchored and rolling split validation)
- [ ] **Phase 10: Quantitative Research Dashboard & Equity Visualizers**
- [ ] **Phase 11: Automated Strategy Screening & Hypothesis Ranking**
- [ ] **Phase 12: Comprehensive Verification & Stress Testing**
- [ ] **Phase 13: Export, Deployment & Forward Paper Testing Preparation**

---

## 3. Phase 1 Architecture Overview

```
com.example
├── data
│   ├── local
│   │   ├── dao           // ExperimentDao, DatasetDao, ResearchConfigDao
│   │   ├── entity        // Room entities with toDomain/fromDomain mapping
│   │   └── DrForexDatabase.kt
│   └── repository        // Clean repository interfaces and implementations
├── domain
│   ├── engine            // DataValidator, Quantitative Engine interfaces
│   ├── experiment        // ExperimentManager (Sequential ID generation EXP-XXXX)
│   └── model             // Domain models (MarketCandle, Experiment, Strategy, etc.)
└── ui
    ├── components        // Status badges, metric cards, scientific banners
    ├── navigation        // 5-tab Bottom Navigation Host (Home, Research, Data, Experiments, Settings)
    ├── screens
    │   ├── home          // Laboratory overview & system readiness
    │   ├── research      // 10-module pipeline architecture & scientific constraints
    │   ├── data          // Dataset ingestion, validation diagnostics, dev sample generator
    │   ├── experiments   // Auditable experiment registry (EXP-0001...)
    │   └── settings      // Capital, risk (KSh 20,000 / 1%), spread, and timeframe defaults
    └── theme             // Obsidian dark-navy quantitative laboratory styling
```

---

## 4. Default Laboratory Settings

- **Base Currency**: KSh (Kenyan Shillings)
- **Initial Research Capital**: 20,000 KSh
- **Default Risk Per Trade**: 1.0%
- **Default Execution Timeframe**: M15
- **Context Timeframes**: H1, H4
- **Default Spread / Slippage**: 1.5 pips / 0.5 pips
