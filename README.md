Dr. Forex — Quantitative Trading Research Laboratory

Dr. Forex is an Android-based quantitative trading research laboratory designed to investigate financial markets scientifically.

It is not a live trading bot.

The purpose of Dr. Forex is to collect and validate market data, measure market behaviour and relationships, formulate hypotheses, design and run experiments, develop and evaluate trading strategies, generate evidence-based forecasts, test robustness, study changing market conditions, and progressively determine which findings deserve further investigation.

The guiding principle is:

«Evidence must come before belief, and validation must come before trading authority.»

---

1. Scientific Philosophy

Dr. Forex is built around several core scientific principles.

1.1 Zero Look-Ahead Bias

No information that would not have been available at the time of a historical decision may be used to make that decision.

Historical research must respect chronological information availability.

Future prices, future indicators, future events, future labels, or information derived from future observations must not leak into earlier decisions.

---

1.2 Realistic Market Friction

Research must account for the costs and limitations of actual markets wherever applicable.

This may include:

- Spread
- Slippage
- Commission
- Swap or financing costs
- Liquidity
- Execution limitations
- Trading-session restrictions
- Position-size limitations
- Broker or account constraints

A strategy should not appear successful simply because unrealistic market conditions were assumed.

---

1.3 Overfitting Rejection

A strategy that performs exceptionally well on historical data may simply be fitting historical noise.

Dr. Forex must therefore investigate:

- Parameter sensitivity
- Feature sensitivity
- Strategy complexity
- Data-snooping
- Multiple-testing effects
- Regime dependence
- Out-of-sample performance
- Walk-forward performance
- Stress-test behaviour
- Forward/demo behaviour

The objective is not to find the most impressive historical curve.

The objective is to determine whether the underlying finding remains credible outside the conditions that produced it.

---

1.4 Reproducible Experimentation

Research must be reproducible.

An experiment should record the conditions under which it was performed, including where applicable:

- Dataset and version
- Instrument
- Date range
- Timeframes
- Features
- Strategy/model version
- Parameters
- Initial capital
- Risk configuration
- Market-cost assumptions
- Execution assumptions
- Hypothesis
- Validation methodology
- Results

The same research configuration should be capable of reproducing the same research process and result.

---

1.5 Reproducibility Does Not Mean Repetition

Dr. Forex should not simply memorize previous conclusions and repeat them.

A reproducible methodology should be maintained while conclusions remain responsive to new evidence.

If new information materially changes the evidence, the reasoning and forecast should be allowed to change.

---

1.6 Evidence Before Belief

An observation is not automatically a fact.

A correlation is not automatically causation.

A successful backtest is not automatically a robust strategy.

A forecast is not certainty.

Dr. Forex should distinguish between:

- Observation
- Association
- Correlation
- Historical pattern
- Hypothesis
- Experimental result
- Robust evidence
- Forecast
- Research finding
- Strategy candidate
- Validated conclusion

---

1.7 Research and Execution Must Remain Separate

Research success must never automatically grant permission to trade real money.

A successful backtest does not automatically authorize live trading.

A strong forecast does not automatically become a trade.

A successful demo experiment does not automatically authorize real-account execution.

Real-money execution must remain a separate capability requiring appropriate validation, safeguards and explicit user authorization.

---

1.8 Continuous Re-evaluation

Financial markets evolve.

A strategy that worked previously may degrade.

A relationship that appeared stable may disappear.

A forecasting model may become poorly calibrated.

A feature that was once useful may lose predictive value.

Dr. Forex must therefore continuously compare new evidence against previous findings and identify:

- Confirmation
- Contradiction
- Degradation
- Regime change
- Invalidated assumptions
- New research opportunities

---

2. Core Research Cycle

The fundamental research process is:

MARKET DATA
    ↓
VALIDATE & CLEAN
    ↓
UNDERSTAND MARKET CONDITIONS
    ↓
MEASURE FEATURES & RELATIONSHIPS
    ↓
FORMULATE HYPOTHESIS
    ↓
DESIGN EXPERIMENT
    ↓
BACKTEST / SIMULATE
    ↓
ROBUSTNESS TESTING
    ↓
OUT-OF-SAMPLE VALIDATION
    ↓
FORECAST / STRATEGY EVALUATION
    ↓
FORWARD / DEMO EXPERIMENT
    ↓
NEW EVIDENCE
    ↓
RESEARCH AGAIN

This is a continuous loop rather than a one-time process.

---

3. Research Lifecycle

Individual research questions should follow a lifecycle such as:

OBSERVATION
    ↓
QUESTION
    ↓
HYPOTHESIS
    ↓
EXPERIMENT
    ↓
RESULT
    ↓
VALIDATION
    ↓
RESEARCH FINDING
    ↓
FOLLOW-UP QUESTION

A finding may generate another question rather than becoming a permanent conclusion.

---

4. Market Data Foundation

Reliable research begins with reliable data.

The data foundation should progressively support:

- Historical market data
- Point-in-time data
- Chronological integrity
- Multi-timeframe alignment
- Missing-data detection
- Duplicate detection
- Timestamp validation
- Market-session awareness
- Data-quality measurement
- Instrument metadata
- Volume-derived measurements where available
- Relevant economic/event information
- Data-source provenance

Research results should remain traceable to the data from which they were produced.

---

5. Quantitative Measurement Layer

Raw market data must be transformed into measurable research inputs.

The measurement layer may include:

- Returns
- Volatility
- Range
- Trend measurements
- Momentum
- Price location
- Statistical measurements
- Rolling relationships
- Correlations
- Regime measurements
- Volume-derived metrics
- Cross-asset relationships
- Market-structure measurements
- Quantitative feature snapshots
- Point-in-time feature calculations

Measurements must respect chronological information availability.

---

6. Research & Experimentation Engine

The research engine should allow Dr. Forex to investigate questions systematically.

An experiment may define:

- Research question
- Hypothesis
- Competing hypotheses
- Dataset
- Instrument
- Date range
- Timeframes
- Features
- Strategy/model
- Parameters
- Initial capital
- Risk configuration
- Market assumptions
- Execution assumptions
- Evaluation metrics
- Validation methodology
- Expected result
- Actual result
- Conclusion
- Follow-up research

Experiments should remain individually reproducible even when their configurations differ.

---

7. Competing Hypotheses

Dr. Forex should not simply search for evidence supporting the first explanation that appears plausible.

Where multiple explanations are possible, they should be represented as competing hypotheses.

For example:

Observation:
A currency pair behaves differently during high-volatility periods.

Possible hypotheses:

H1 — Volatility changes trend persistence.
H2 — Liquidity conditions change execution behaviour.
H3 — Economic events alter market structure.
H4 — The apparent relationship is statistical noise.

Research should attempt to distinguish between competing explanations.

---

8. Research Findings

Research findings should have explicit status.

Possible classifications include:

UNSUPPORTED
INCONCLUSIVE
INTERESTING
FRAGILE
CONDITIONALLY SUPPORTED
ROBUST RESEARCH FINDING
STRATEGY CANDIDATE
REQUIRES FURTHER VALIDATION
DEGRADED
INVALIDATED

A finding should retain the evidence and conditions supporting its classification.

---

9. Strategy Development

Strategies are research objects.

A strategy should be capable of being:

- Formulated
- Tested
- Compared
- Modified
- Combined
- Rejected
- Re-tested
- Forward-tested
- Monitored
- Suspended
- Returned to research

A strategy should never be considered valid solely because it produced a profitable historical backtest.

---

10. Strategy Composition & Feature Borrowing

Dr. Forex should be capable of investigating whether useful features from different strategies can be combined to formulate stronger strategies.

For example:

Strategy A
├── Strong trend detection
└── Weak in ranging markets

Strategy B
├── Strong range detection
└── Weak in strong trends

Strategy C
└── Strong volatility filtering

          ↓

COMBINATION HYPOTHESIS

Trend feature from A
+
Range behaviour from B
+
Volatility filter from C

          ↓

NEW STRATEGY CANDIDATE

The purpose is not to assume that combining successful strategies automatically produces a better strategy.

The combination itself becomes a new research hypothesis.

It must undergo independent:

- Backtesting
- Robustness testing
- Out-of-sample validation
- Stress testing
- Forward/demo experimentation

Feature borrowing also introduces additional overfitting risk.

Dr. Forex must therefore investigate whether the combined features provide genuine incremental value rather than merely producing a better historical fit.

---

11. Forecasting

Forecasting is a first-class research capability.

Dr. Forex should eventually be capable of generating coherent, evidence-based and repeatable forecasts.

Forecasts should preferably be probabilistic or scenario-based rather than simplistic unconditional BUY/SELL outputs.

A forecast may contain:

- Instrument
- Forecast timestamp
- Forecast horizon
- Current market conditions
- Market regime
- Primary scenario
- Probability/confidence
- Alternative scenarios
- Supporting evidence
- Contradictory evidence
- Important assumptions
- Invalidation conditions
- Model/reasoning version

A forecast should explain the reasoning and evidence behind its conclusion.

---

12. Forecast Evaluation

A forecast is itself a research object.

After its forecast horizon has elapsed, Dr. Forex should evaluate:

- Directional accuracy
- Magnitude error
- Scenario accuracy
- Probability calibration
- Confidence calibration
- Performance by instrument
- Performance by horizon
- Performance by market regime
- Performance under different information conditions

Forecast performance should feed back into the research process.

---

13. Forecasts Do Not Automatically Become Trades

A forecast is not automatically a trading instruction.

Even a highly confident forecast may result in:

NO TRADE

when:

- Evidence is insufficient
- Contradictory evidence is strong
- Expected edge is too small
- Risk is excessive
- Execution conditions are poor
- Account constraints are violated
- Strategy requirements are not satisfied

The ability to say NO TRADE is part of responsible research intelligence.

---

14. Laboratory Configuration

Dr. Forex should provide sensible default laboratory parameters.

However:

«A default is a starting assumption, not a fixed truth.»

Laboratory configuration must be customizable.

Example starting values:

Parameter| Example Default| Status
Base currency| KSh| Customizable
Initial research capital| KSh 20,000| Fully customizable
Risk per trade| 1%| Customizable
Execution timeframe| M15| Customizable
Context timeframes| H1 / H4| Customizable
Spread| 1.5 pips| Configurable / instrument-dependent
Slippage| 0.5 pips| Configurable / environment-dependent

These values are provided as sensible starting points for research.

They are not universal assumptions.

In particular, initial research capital must never be hard-coded to KSh 20,000.

---

15. Laboratory Parameters as Research Variables

Some laboratory parameters should themselves be available for experimentation.

For example:

- Timeframe
- Risk per trade
- Entry threshold
- Exit threshold
- Holding period
- Position-sizing method
- Strategy features
- Strategy combinations
- Forecast horizon
- Execution assumptions where scientifically appropriate

The system should distinguish between three types of configuration.

Research Variables

Variables that can legitimately be changed to answer a research question.

Environmental Parameters

Conditions that should be measured or realistically modelled.

Examples:

- Spread
- Slippage
- Commission
- Liquidity
- Execution latency

These should not be manipulated merely to produce better historical results.

Hard Constraints

Conditions imposed by the account, broker, instrument or safety system.

These should be respected rather than optimized away.

---

16. Capital as a Research Variable

Initial capital must be fully customizable.

One of the eventual practical questions Dr. Forex should answer is:

«If I had capital X, how much would it have grown or depreciated over timeframe W using strategy T?»

The research configuration should therefore be able to specify:

Initial Capital
Instrument
Strategy
Research Period
Execution Timeframe
Context Timeframes
Risk Model
Market Costs
Execution Assumptions

The resulting research output should be able to show:

Initial Capital
Final Capital
Net Profit / Loss
Percentage Return
Maximum Drawdown
Equity Curve
Risk Metrics
Trade Statistics
Winning / Losing Periods

The same strategy should be testable with different starting capital.

For example:

KSh 10,000
KSh 20,000
KSh 50,000
KSh 100,000

This allows Dr. Forex to investigate how capital size and risk interact with strategy performance.

The purpose is not merely to calculate profit.

It is to understand the relationship between:

CAPITAL
+
RISK
+
STRATEGY
+
MARKET CONDITIONS
+
TIME

---

17. Demo / Forward Experimentation

Demo experimentation should not merely be a final demonstration that a backtest worked.

It should be a controlled forward research stage.

The progression is:

HISTORICAL RESEARCH
        ↓
BACKTEST
        ↓
ROBUSTNESS TESTING
        ↓
OUT-OF-SAMPLE
        ↓
DEMO / PAPER EXPERIMENT
        ↓
FORWARD EVIDENCE
        ↓
RESEARCH UPDATE
        ↓
RETEST

Demo experimentation can investigate legitimate research variables such as:

- Risk configuration
- Timeframe
- Entry/exit behaviour
- Strategy combinations
- Execution behaviour
- Signal frequency
- Forecast horizon
- Position sizing

However, variables should normally be changed through controlled experiments.

For example:

DEMO EXPERIMENT D-001
Strategy T
M15
1% risk

DEMO EXPERIMENT D-002
Strategy T
M15
0.5% risk

DEMO EXPERIMENT D-003
Strategy T
M15
Alternative exit model

This makes it possible to determine what actually caused a change in behaviour.

Demo experimentation should also reveal issues that historical research cannot fully reproduce, such as:

- Actual execution behaviour
- Spread variation
- Slippage
- Latency
- Signal frequency
- Missed signals
- Changing market regimes
- Forecast calibration
- Strategy degradation
- Operational problems

---

18. Strategy Lifecycle

A strategy may progress through:

DISCOVERED
    ↓
RESEARCHING
    ↓
BACKTESTED
    ↓
ROBUSTNESS TESTING
    ↓
OUT-OF-SAMPLE
    ↓
DEMO / PAPER EXPERIMENT
    ↓
LIVE OBSERVATION
    ↓
REAL-ACCOUNT CANDIDATE
    ↓
USER APPROVED
    ↓
LIVE
    ↓
MONITORED

A strategy may also regress:

LIVE
 ↓
DEGRADING
 ↓
UNDER REVIEW
 ↓
SUSPENDED
 ↓
RESEARCH AGAIN

Previous success does not guarantee continued validity.

---

19. Adaptive Research Intelligence

The machine-learning and reasoning layer should maintain an up-to-date understanding of the Forex research environment.

Its objective is not simply to memorize historical information.

It should continuously investigate how the financial environment is evolving and whether those changes affect Dr. Forex's existing understanding.

Potential areas include:

- Monetary policy
- Interest rates
- Inflation
- Employment
- Central-bank behaviour
- Economic releases
- Volatility
- Liquidity
- Currency relationships
- Cross-asset relationships
- Market microstructure
- Geopolitical developments
- Quantitative research
- Academic research
- New modelling approaches
- Market-regime changes
- Strategy performance
- Forecast performance

The system should prioritize information that could materially affect existing hypotheses, relationships, forecasts, strategies, risk assumptions or conclusions.

The goal is relevant continuous research, not indiscriminate information collection.

---

20. Adaptive Reasoning

Dr. Forex should maintain a distinction between:

KNOWLEDGE
+
EVIDENCE
+
ASSUMPTIONS
+
HYPOTHESES
+
REASONING
+
EXPERIMENTAL RESULTS
+
CURRENT CONDITIONS

The system should be able to determine:

- What it previously believed
- Why it believed it
- What evidence supported that belief
- What evidence contradicted it
- Whether the conditions still apply
- What has changed
- Whether the conclusion should be retained
- Whether it should be weakened
- Whether it should be revised
- Whether it should be rejected

---

21. Knowledge & Reasoning Registry

Research knowledge should remain traceable.

A knowledge record may contain:

Knowledge Statement
Supporting Evidence
Contradictory Evidence
Applicable Conditions
Relevant Time Period
Confidence
Last Review
Knowledge Version
Reasoning Version
Related Experiments
Related Forecasts
Related Strategies
Current Status

Knowledge should therefore have a lifecycle rather than being treated as permanently correct.

---

22. Machine Learning & Adaptive Intelligence

Machine learning should be introduced as a research capability, not as a replacement for scientific validation.

Potential applications include:

- Pattern discovery
- Market-regime classification
- Forecasting
- Feature discovery
- Feature selection
- Relationship discovery
- Hypothesis generation
- Strategy evaluation
- Probability calibration
- Anomaly detection
- Strategy degradation detection
- Research prioritization
- Strategy-combination research

ML systems must remain subject to:

- Data separation
- Leakage prevention
- Out-of-sample testing
- Robustness testing
- Calibration
- Reproducibility
- Model/version tracking
- Appropriate baselines

A complex model that performs well historically is not automatically better than a simpler model.

---

23. Adaptive ML Objective

The long-term ML layer should help Dr. Forex continuously improve its ability to reason about its primary objective:

«Understand financial-market behaviour, discover useful evidence, formulate hypotheses, generate and evaluate forecasts, investigate strategies, and determine under what conditions those findings remain valid.»

The ML layer should progressively be capable of:

1. Learning from validated research data.
2. Identifying potentially useful relationships.
3. Generating hypotheses.
4. Comparing competing explanations.
5. Evaluating forecasts.
6. Identifying market regimes.
7. Detecting strategy degradation.
8. Suggesting new experiments.
9. Learning from failed experiments.
10. Re-evaluating previous conclusions when new evidence appears.

It must not simply memorize successful historical answers.

---

24. Risk & Safety Controls

Before any real-money execution capability is considered, Dr. Forex should support appropriate controls such as:

- Maximum risk per trade
- Maximum daily loss
- Maximum drawdown
- Maximum exposure
- Maximum simultaneous positions
- Instrument restrictions
- Strategy-specific limits
- Session restrictions
- Emergency stop
- Account-level kill switch
- Explicit user authorization

The system must be capable of refusing to trade when evidence, safety or operating conditions are insufficient.

---

25. Real-Account Execution

Real-account execution is separate from research.

A successful backtest does not automatically authorize live trading.

A successful demo experiment does not automatically authorize live trading.

A strong forecast does not automatically authorize live trading.

Potential prerequisites include:

- Research evidence
- Robustness evidence
- Out-of-sample evidence
- Forward/demo evidence
- Stable strategy behaviour
- Forecast evaluation
- Risk validation
- Operational validation
- Broker/account validation
- Exposure controls
- Emergency controls
- Explicit user authorization

---

26. Evidence Hierarchy

Evidence should generally become stronger as research progresses toward increasingly realistic conditions:

OBSERVATION
    ↓
ASSOCIATION / CORRELATION
    ↓
REPEATABLE HISTORICAL PATTERN
    ↓
CONTROLLED BACKTEST
    ↓
ROBUSTNESS EVIDENCE
    ↓
OUT-OF-SAMPLE EVIDENCE
    ↓
FORWARD / DEMO EVIDENCE
    ↓
CURRENT-MARKET EVIDENCE

No individual layer constitutes absolute proof.

---

27. Continuous Research Loop

The long-term laboratory should operate conceptually as:

OBSERVE
   ↓
MEASURE
   ↓
QUESTION
   ↓
HYPOTHESIZE
   ↓
EXPERIMENT
   ↓
VALIDATE
   ↓
LEARN
   ↓
FORECAST
   ↓
FORWARD TEST
   ↓
COMPARE WITH NEW EVIDENCE
   ↓
UPDATE KNOWLEDGE
   ↓
RESEARCH AGAIN

---

28. Progressive Development Roadmap

The original 13-phase roadmap remains the foundation of Dr. Forex.

New capabilities such as forecasting, adaptive research intelligence, machine learning, strategy composition and controlled forward experimentation extend the original roadmap rather than replacing it.

Phase 1 — Android Foundation & Data Ingestion

Establish the Android foundation and reliable market-data ingestion.

Phase 2 — Point-in-Time Historical Data

Establish chronologically correct historical datasets and prevent future-data contamination.

Phase 3 — Technical & Market Structure Engines

Build reusable technical measurements, indicators and market-structure analysis.

Phase 4 — Modular Strategy Framework

Create modular strategy components that can be independently tested, compared and combined.

Phase 5 — Event-Driven Backtesting Simulator

Build realistic historical simulation with chronological execution and market friction.

Phase 6 — Capital Preservation & Risk Engine

Implement risk, position sizing, drawdown and capital-preservation mechanisms.

Phase 7 — Performance Analytics

Measure returns, drawdowns, volatility, trade behaviour and risk-adjusted performance.

Phase 8 — Robustness & Anti-Overfitting

Test parameter sensitivity, feature sensitivity, complexity and data-snooping risks.

Phase 9 — Walk-Forward & Out-of-Sample

Evaluate strategies using information that was not used to formulate them.

Phase 10 — Research Dashboard & Visualizers

Make research results, relationships, experiments and strategy behaviour understandable.

Phase 11 — Automated Strategy Screening, Hypothesis Ranking & Forecasting

Support systematic comparison of strategies, hypotheses, features and forecasting approaches.

This phase may also investigate whether useful components from existing strategies can be combined into new strategy candidates.

Phase 12 — Comprehensive Verification, Stress Testing & Adaptive Research Intelligence

Integrate stronger verification, stress testing, adaptive knowledge, evolving reasoning, ML-assisted research, forecasting evaluation and strategy degradation detection.

Phase 13 — Export, Deployment & Forward Experimentation

Support controlled forward/demo experimentation, research export and carefully governed progression toward possible real-account use.

---

29. Research Output

Dr. Forex should ultimately produce more than a single profitability number.

A research result may contain:

Research Question
Hypothesis
Dataset
Experiment Configuration
Strategy / Model
Features
Parameters
Market Conditions
Initial Capital
Risk Configuration
Costs
Results
Drawdown
Robustness
Out-of-Sample Results
Forecast Performance
Forward Results
Contradictory Evidence
Research Finding
Confidence
Known Limitations
Recommended Follow-Up

---

30. Capital Growth / Depreciation Research

A central eventual capability of the laboratory is to answer practical questions using reproducible research configurations.

For example:

«If I had capital X, how much would it have grown or depreciated over timeframe W using strategy T?»

The laboratory should eventually be able to evaluate:

CAPITAL X
     +
STRATEGY T
     +
TIMEFRAME W
     +
INSTRUMENT
     +
MARKET CONDITIONS
     +
RISK CONFIGURATION
     +
TRADING COSTS
        ↓
HISTORICAL / SIMULATED OUTCOME

The output should include, where applicable:

- Starting capital
- Final capital
- Net change
- Percentage return
- Maximum drawdown
- Equity curve
- Risk metrics
- Trade statistics
- Winning and losing periods
- Conditions under which performance occurred

This should remain a research/simulation result, not a promise of future performance.

---

31. Research Traceability

Important outputs should be traceable back to their source.

A forecast should be traceable to:

FORECAST
   ↓
REASONING VERSION
   ↓
EVIDENCE
   ↓
FEATURES
   ↓
MARKET DATA

A strategy result should be traceable to:

STRATEGY RESULT
   ↓
STRATEGY VERSION
   ↓
EXPERIMENT
   ↓
CONFIGURATION
   ↓
DATASET

A research conclusion should be traceable to:

CONCLUSION
   ↓
RESEARCH FINDING
   ↓
EXPERIMENT(S)
   ↓
EVIDENCE
   ↓
DATA

---

32. What Dr. Forex Is Not

Dr. Forex is not intended to be:

- A guaranteed-profit system
- A simple BUY/SELL signal generator
- A black-box money-making machine
- A backtest optimizer designed solely to maximize historical returns
- A system that assumes one strategy works permanently
- A system that treats forecasts as certainty
- A system that automatically converts successful research into real-money trading

---

33. Default Laboratory Configuration

The laboratory should begin with sensible defaults while allowing the researcher to customize them.

Example starting configuration:

Base Currency:          KSh
Initial Capital:        KSh 20,000
Risk per Trade:         1%
Execution Timeframe:    M15
Context Timeframes:     H1 / H4
Spread:                 1.5 pips
Slippage:               0.5 pips

These are default starting values only.

They are not fixed system requirements.

Initial capital is fully customizable.

Risk, timeframes and other legitimate research parameters are customizable and may themselves become subjects of experimentation.

Market-cost assumptions should be made as realistic as available data and the research environment permit.

---

34. Scientific Disclaimer

Dr. Forex is a research project.

Historical, simulated, backtested, demo or forward results do not guarantee future performance.

Financial markets are uncertain and can change materially.

No research result should be interpreted as a guarantee of profit.

Any eventual real-money execution capability must be subject to appropriate validation, risk controls, operational safeguards and explicit user authorization.

---

35. Project Status

Dr. Forex is under active development.

Development prioritizes the research foundation before higher-level automation.

The roadmap describes the intended progressive development of the laboratory and should not be interpreted as a claim that every phase or capability is already complete.

The actual source code remains the authoritative reference for implementation status.

---

Final Principle

Dr. Forex is not being built to prove that a strategy works.

It is being built to discover:

«whether a strategy, forecast, relationship, feature, market explanation, or research finding deserves to be believed; under what conditions it remains valid; how sensitive it is to changing assumptions; and whether new evidence should strengthen, weaken, revise, or invalidate that belief.»

Ultimately, the laboratory should be capable of answering questions such as:

«If I had capital X, what would have happened to it over timeframe W using strategy T, under clearly defined and reproducible conditions?»

And when appropriate:

«Can the strongest evidence-supported characteristics of different strategies be combined into a new strategy that survives independent testing?»

The answer must come from research and evidence—not optimism.
