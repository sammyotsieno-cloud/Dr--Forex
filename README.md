Dr. Forex — Quantitative Trading Research Laboratory

Dr. Forex is an Android-based quantitative trading research laboratory designed to investigate financial markets scientifically.

It is not a live trading bot.

The laboratory is designed to:

- collect and validate market data
- measure market behaviour and relationships
- formulate and test hypotheses
- discover, develop, compare and reject trading strategies
- investigate market regimes and changing conditions
- perform realistic historical simulations
- evaluate robustness and resistance to overfitting
- perform out-of-sample and walk-forward validation
- generate and evaluate evidence-based market forecasts
- conduct controlled forward and demo experiments
- maintain research knowledge and reasoning
- identify strategy degradation
- determine whether a strategy has accumulated sufficient evidence to become a controlled live-trading candidate

The fundamental principle is:

«Evidence must come before belief, and validation must come before trading authority.»

A profitable backtest is not proof of a profitable strategy.

A forecast is not automatically a trade.

A strategy that passes one test is not automatically robust.

And a strategy approved for live trading is not considered proven to make money. Live approval means that sufficient evidence has accumulated to justify a defined level of controlled exposure under explicitly documented conditions.

---

1. Scientific Philosophy

Dr. Forex is built around the following principles.

1.1 Zero Look-Ahead Bias

No experiment may use information that would not have been available at the exact point in time at which a decision would have been made.

This applies to:

- prices
- indicators
- market regimes
- news
- economic releases
- forecasts
- machine-learning features
- labels
- model parameters
- strategy decisions

Future information must never influence a historical decision.

---

1.2 Realistic Market Friction

Research must account for the conditions under which trading would actually occur.

Where applicable, experiments should model:

- spread
- slippage
- commission
- swap/financing
- execution delay
- liquidity limitations
- position-size constraints
- entry and exit mechanics

A strategy that only works when unrealistic execution assumptions are used is not considered validated.

---

1.3 Overfitting Rejection

Dr. Forex must actively attempt to detect and reject strategies that have simply learned historical data.

Optimization is not evidence.

A strategy with exceptional historical performance but poor generalization should be treated as an overfitting candidate rather than a successful strategy.

---

1.4 Reproducible Experimentation

Research experiments should be reproducible.

The laboratory should preserve, where applicable:

- dataset/version
- instrument
- timeframe
- experiment period
- strategy version
- parameters
- assumptions
- costs
- execution model
- capital
- risk settings
- random seeds
- model versions
- results
- validation status

Another researcher should be able to understand how a result was produced.

---

1.5 Reproducibility Does Not Mean Repetition

The objective is not simply to repeatedly reproduce a historical result.

The stronger question is:

«Does the underlying relationship continue to hold when conditions, periods, parameters and market regimes change?»

A strategy that only works under one narrow historical configuration should be treated cautiously.

---

1.6 Research and Execution Must Remain Separate

Research must not automatically acquire authority to trade real money.

The laboratory may:

- discover strategies
- simulate strategies
- forecast markets
- evaluate strategies
- recommend research candidates
- identify live-trading candidates

But real-money execution must remain a separate controlled layer requiring explicit authorization and risk controls.

---

1.7 Continuous Re-evaluation

Validation is not permanent.

A strategy that previously worked may later:

- degrade
- become unstable
- lose its edge
- become unsuitable for a market regime
- experience increased execution costs
- become invalidated by structural market changes

Therefore:

«Approval is conditional and revocable.»

---

2. Core Research Cycle

The laboratory follows a continuous research cycle:

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
TEST ROBUSTNESS
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

The cycle does not terminate simply because a strategy becomes profitable.

New evidence can reopen previous conclusions.

---

3. Research Lifecycle

The fundamental research lifecycle is:

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

The laboratory should distinguish between what was:

- observed
- measured
- hypothesized
- tested
- supported
- contradicted
- inconclusive
- validated
- invalidated

This prevents assumptions from silently becoming facts.

---

4. Research Finding Status

Research findings may receive statuses such as:

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

A finding should not automatically progress from one status to another simply because a model or strategy produces a profitable result.

Evidence determines progression.

---

5. Market Data Foundation

Reliable research requires reliable data.

The data foundation should support, where applicable:

- historical market data
- point-in-time data
- multiple instruments
- multiple timeframes
- chronological integrity
- missing-data detection
- duplicate detection
- abnormal-data detection
- timestamp validation
- timeframe alignment
- market-session information
- volume information where available
- corporate/event information where relevant
- economic and fundamental information where available

Data quality problems must be detected before they contaminate research.

---

6. Quantitative Measurement Layer

The laboratory should transform raw market information into measurable features.

Examples include:

- price relationships
- returns
- volatility
- range
- momentum
- trend
- moving averages
- relative strength
- volume-derived measurements
- statistical relationships
- market structure
- price location
- timeframe relationships
- regime measurements

Measurements should remain distinct from interpretations.

A measurement is not automatically an explanation.

---

7. Market Understanding

Dr. Forex should attempt to understand the conditions in which observed relationships occur.

This includes investigation of:

- market regimes
- volatility conditions
- trend/range behaviour
- liquidity
- session behaviour
- monetary policy
- interest rates
- inflation
- employment
- central-bank behaviour
- economic releases
- geopolitical developments
- cross-asset relationships
- currency relationships
- market microstructure

The system should be capable of discovering that a relationship is:

- broad
- conditional
- temporary
- regime-dependent
- unstable
- or unsupported.

---

8. Hypothesis Development

A strategy should originate from a research question or hypothesis whenever practical.

For example:

«Does condition X combined with condition Y produce a statistically meaningful change in outcome Z under market regime R?»

The laboratory should record:

- hypothesis
- assumptions
- variables
- expected relationship
- experiment design
- evaluation criteria
- supporting evidence
- contradictory evidence

A strategy should not be considered scientifically validated simply because it was discovered through optimization.

---

9. Strategy Development

Strategies should be treated as research objects.

A strategy should have identifiable:

- entry conditions
- exit conditions
- position-sizing rules
- risk rules
- timeframe
- instruments
- market conditions
- assumptions
- parameters
- execution model
- version
- research origin
- validation history

Strategy development should distinguish between:

Research hypothesis

and

Trading implementation

The implementation must faithfully represent the hypothesis being tested.

---

10. Strategy Composition

Dr. Forex may investigate whether useful components from different strategies can be combined.

For example:

Strategy A
    +
Strategy B
    +
Feature C
    ↓
NEW COMPOSITE STRATEGY

However:

«A composite strategy is a new research hypothesis.»

The fact that Strategy A and Strategy B individually performed well does not prove that their combination will perform well.

The composite strategy must independently undergo:

- backtesting
- robustness testing
- out-of-sample testing
- walk-forward validation
- forward experimentation
- risk evaluation

No inherited validity is assumed.

---

11. Event-Driven Backtesting

Historical simulation should attempt to reproduce the sequence of information and decisions that would have existed in real time.

The simulator should account for:

- chronological information availability
- entries
- exits
- stops
- targets
- position sizing
- spread
- slippage
- commission
- swap
- capital changes
- concurrent positions
- exposure
- execution constraints

Backtesting should produce more than a final profit number.

---

12. Capital Growth and Depreciation Research

Dr. Forex should allow researchers to ask questions such as:

«If I had capital X, how would that capital have behaved over timeframe W using strategy T?»

Capital must therefore be customizable, rather than permanently fixed.

Experiments may compare:

KSh 10,000
KSh 20,000
KSh 50,000
KSh 100,000

or any other valid research capital.

Results may include:

- initial capital
- final capital
- absolute change
- percentage return
- equity curve
- maximum drawdown
- drawdown duration
- risk metrics
- trade statistics
- winning/losing periods
- exposure
- market conditions
- strategy behaviour

Capital itself can therefore become a research variable.

---

13. Risk and Capital Preservation

Profitability alone is insufficient.

The laboratory should evaluate:

- maximum drawdown
- drawdown duration
- losing streaks
- tail losses
- volatility
- exposure
- leverage
- risk per trade
- concentration
- risk of ruin
- recovery behaviour
- correlation with other strategies

The objective is not simply:

«Maximize return.»

It is:

«Understand the relationship between return, risk, uncertainty and survivability.»

---

14. Robustness and Anti-Overfitting

Strategies should be deliberately attacked.

Testing should investigate:

- parameter sensitivity
- execution sensitivity
- cost sensitivity
- timeframe sensitivity
- instrument sensitivity
- market-regime sensitivity
- sample-period sensitivity
- perturbation
- stress conditions
- alternative assumptions

The laboratory should prefer stable regions over isolated optimized parameter values.

For example:

Weak:

Parameter = 14
Result = excellent

13 → poor
15 → poor

versus:

Stronger:

11 → good
12 → good
13 → good
14 → good
15 → good
16 → good
17 → good

The objective is not to find a magical number.

---

15. Out-of-Sample and Walk-Forward Validation

Historical data should be separated into development and unseen evaluation periods where appropriate.

A simplified structure:

DEVELOPMENT DATA
       ↓
STRATEGY DEVELOPMENT
       ↓
LOCK STRATEGY
       ↓
UNSEEN DATA
       ↓
EVALUATION

Walk-forward testing should repeatedly simulate the process of learning from past information and evaluating subsequent unseen information.

TRAIN → TEST
   ↓
ROLL FORWARD
   ↓
TRAIN → TEST
   ↓
ROLL FORWARD
   ↓
TRAIN → TEST

A strategy that only succeeds inside its development sample should not receive strong validation status.

---

16. Forecasting

Forecasting is a research capability separate from automatic trade execution.

Forecasts may contain:

- instrument
- timestamp
- forecast horizon
- market regime
- primary scenario
- probability/confidence
- alternative scenarios
- supporting evidence
- contradictory evidence
- invalidation conditions
- model/reasoning version

The laboratory should be capable of producing:

«NO TRADE»

when evidence does not justify action.

Forecast confidence must not automatically translate into trading authority.

---

17. Forecast Evaluation

Forecasts must themselves be tested.

Evaluation may include:

- directional accuracy
- magnitude/error
- calibration
- confidence versus actual outcomes
- performance by forecast horizon
- performance by instrument
- performance by market regime
- performance under different information conditions

A forecasting system must be evaluated against reality rather than judged by how convincing its explanations sound.

---

18. Strategy Validation, Approval & Live-Readiness

A strategy must accumulate sufficient evidence before it can become a live-trading candidate.

Passing one backtest is not sufficient.

The strategy validation framework should evaluate, where applicable:

CLEAR HYPOTHESIS
        ↓
NO LOOK-AHEAD / NO DATA LEAKAGE
        ↓
RELIABLE DATA
        ↓
REALISTIC EXECUTION
        ↓
HISTORICAL BACKTEST
        ↓
ROBUSTNESS TESTING
        ↓
REGIME / STRESS TESTING
        ↓
WALK-FORWARD VALIDATION
        ↓
LOCKED OUT-OF-SAMPLE TESTING
        ↓
FORWARD / DEMO EXPERIMENT
        ↓
RISK REVIEW
        ↓
OPERATIONAL REVIEW
        ↓
LIVE-TRADING CANDIDATE

18.1 Mandatory Qualification Areas

A strategy should be evaluated for:

- clearly defined hypothesis
- data integrity
- zero look-ahead bias
- absence of information leakage
- realistic market friction
- realistic execution
- positive net performance where appropriate
- acceptable risk
- robustness
- parameter stability
- regime behaviour
- stress behaviour
- out-of-sample performance
- walk-forward performance
- forward/demo performance
- operational readiness
- monitoring requirements

Exact numerical thresholds should not be assumed to be universal.

A suitable threshold may depend on:

- instrument
- timeframe
- strategy class
- trading frequency
- market conditions
- capital
- execution environment
- research objective

Therefore, Dr. Forex should eventually support explicit, documented qualification criteria rather than one universal magic number.

---

18.2 Hard Vetoes

Some failures should prevent live approval regardless of profitability.

Examples include:

LOOK-AHEAD BIAS
        → REJECT

DATA LEAKAGE
        → REJECT

UNREALISTIC EXECUTION ASSUMPTIONS
        → REJECT / REWORK

CATASTROPHIC UNCONTROLLED RISK
        → REJECT

SEVERE OUT-OF-SAMPLE COLLAPSE
        → REJECT / RESEARCH AGAIN

UNEXPLAINABLE PERFORMANCE
        → HOLD / FURTHER INVESTIGATION

INSUFFICIENT EVIDENCE
        → NOT APPROVED

A strategy should never pass simply because its historical return is attractive.

---

18.3 Approval Is Conditional

Live approval must not mean:

«“This strategy is proven profitable.”»

Instead:

«Live approval means that the strategy has accumulated sufficient evidence to justify a defined level of controlled exposure under explicitly documented conditions.»

Those conditions may include:

- allowed instruments
- allowed timeframes
- permitted market regimes
- maximum allocation
- maximum risk per trade
- maximum exposure
- maximum drawdown
- maximum daily loss
- execution requirements
- data requirements
- monitoring frequency
- suspension conditions

---

18.4 Progressive Live Exposure

A strategy should not necessarily move directly from research into unrestricted live trading.

A controlled progression is:

RESEARCH CANDIDATE
        ↓
VALIDATED CANDIDATE
        ↓
FORWARD / DEMO APPROVED
        ↓
LIVE-TRADING CANDIDATE
        ↓
LIMITED LIVE EXPOSURE
        ↓
MONITORED LIVE
        ↓
FULLER DEPLOYMENT

The permitted exposure may increase only after additional evidence supports doing so.

---

18.5 Continuous Live Monitoring

A previously approved strategy must remain under observation.

Monitoring should detect:

- performance degradation
- unusual drawdown
- changing volatility
- regime change
- execution deterioration
- spread deterioration
- increased slippage
- unexpected behaviour
- model drift
- forecast degradation
- deviation from research assumptions

A strategy may move backwards:

LIVE
 ↓
DEGRADING
 ↓
UNDER REVIEW
 ↓
SUSPENDED
 ↓
RESEARCH AGAIN

This makes live trading another source of evidence rather than the end of research.

---

19. Evidence Hierarchy

Evidence should become progressively stronger:

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

No lower level of evidence should automatically be treated as equivalent to a higher level.

---

20. Knowledge and Reasoning Registry

Dr. Forex should eventually maintain a structured research knowledge base.

A knowledge record may include:

- knowledge statement
- supporting evidence
- contradictory evidence
- applicable conditions
- relevant period
- confidence
- last review
- knowledge version
- reasoning version
- related experiments
- related forecasts
- related strategies
- current status

Knowledge must remain revisable.

A previous conclusion may be:

- strengthened
- weakened
- restricted to certain conditions
- superseded
- invalidated

---

21. Adaptive Research Intelligence

The laboratory should eventually be capable of continuously identifying areas requiring investigation.

Potential research areas include:

- monetary policy
- interest rates
- inflation
- employment
- central-bank behaviour
- economic releases
- volatility
- liquidity
- currency relationships
- cross-asset relationships
- market microstructure
- geopolitical developments
- academic research
- quantitative research
- modelling techniques
- changing market behaviour
- strategy degradation
- forecasting performance

Adaptive intelligence should prioritize research, not blindly generate trades.

---

22. Machine Learning

Machine learning should be introduced only when the underlying research infrastructure is reliable enough to support it.

Potential uses include:

- pattern discovery
- regime identification
- forecasting
- feature selection
- relationship discovery
- hypothesis generation
- strategy evaluation
- probability calibration
- anomaly detection
- strategy degradation detection
- research prioritization
- strategy composition

Machine learning does not replace:

- data validation
- causal reasoning
- experimental design
- out-of-sample testing
- robustness testing
- risk management
- human oversight

The laboratory should treat ML output as research evidence or a research hypothesis, not unquestionable truth.

---

23. Research Variables, Environmental Parameters and Hard Constraints

Not every parameter should be optimized in the same way.

Research Variables

These may legitimately be varied to answer research questions.

Examples:

- starting capital
- risk per trade
- timeframe
- entry conditions
- exit conditions
- forecast horizon
- strategy components
- feature combinations
- strategy composition

Environmental Parameters

These represent the trading environment and should be modeled realistically.

Examples:

- spread
- slippage
- commission
- liquidity
- latency
- financing costs

They should not simply be optimized to make a strategy look better.

Hard Constraints

These represent boundaries that should not be optimized away.

Examples:

- account limitations
- instrument restrictions
- execution limitations
- safety limits
- risk limits
- broker constraints

---

24. Demo and Forward Experimentation

Forward/demo testing is a controlled research stage.

It is not merely a demonstration.

The purpose is to compare:

EXPECTED BEHAVIOUR
        vs
OBSERVED BEHAVIOUR

The experiment should record:

- signals
- execution
- spread
- slippage
- results
- drawdown
- market regime
- forecast performance
- deviations from historical expectations
- unexpected behaviour

Parameters may be varied deliberately when doing so answers a research question.

Where practical, controlled experiments should change limited variables at a time so that observed effects can be interpreted.

---

25. Strategy Lifecycle

The strategy lifecycle is:

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
VALIDATED CANDIDATE
    ↓
LIVE-TRADING REVIEW
    ↓
LIMITED LIVE EXPOSURE
    ↓
MONITORED LIVE
    ↓
FULLER DEPLOYMENT

A strategy may regress at any stage:

LIVE
 ↓
DEGRADING
 ↓
UNDER REVIEW
 ↓
SUSPENDED
 ↓
RESEARCH AGAIN

The lifecycle is therefore not strictly linear.

---

26. Official 13-Phase Roadmap

The official Dr. Forex roadmap remains:

Phase 1 — Android Foundation & Data Ingestion

Establish the Android research environment, application foundation and market-data ingestion capabilities.

Phase 2 — Point-in-Time Historical Data

Build reliable historical datasets that preserve chronological information availability.

Phase 3 — Technical & Market Structure Engines

Build quantitative measurements, technical analysis and market-structure understanding.

Phase 4 — Modular Strategy Framework

Create the infrastructure for defining, versioning and evaluating strategies.

Phase 5 — Event-Driven Backtesting Simulator

Build realistic historical market simulation.

Phase 6 — Capital Preservation & Risk Engine

Model position sizing, capital preservation and risk controls.

Phase 7 — Performance Analytics

Measure strategy performance, risk and behaviour.

Phase 8 — Robustness & Anti-Overfitting

Attempt to invalidate strategies through sensitivity, perturbation, stress and regime testing.

Phase 9 — Walk-Forward & Out-of-Sample

Evaluate generalization on unseen data.

Phase 10 — Research Dashboard & Visualizers

Provide interfaces for understanding experiments, markets, strategies and results.

Phase 11 — Automated Strategy Screening, Hypothesis Ranking & Forecasting

Allow the laboratory to evaluate research candidates, prioritize hypotheses and generate/evaluate forecasts.

Phase 12 — Verification, Stress Testing & Adaptive Research Intelligence

Integrate comprehensive validation, degradation detection and adaptive research capabilities.

Phase 13 — Export, Deployment & Forward Experimentation

Provide research export, controlled deployment pathways and forward/demo experimentation.

Real-money execution remains a separate controlled layer rather than the core purpose of the research laboratory.

---

27. Current Default Research Configuration

The laboratory may begin with the following defaults:

Base Currency: KSh

Initial Capital: KSh 20,000
                 (Example/default starting value; fully customizable)

Risk per Trade: 1%

Execution Timeframe: M15

Context Timeframes: H1 / H4

Spread: 1.5 pips

Slippage: 0.5 pips

These values are defaults for research and demonstration.

They are not universal claims about the optimal trading environment.

Research should allow appropriate parameters to be changed and tested.

---

28. The Research Question Dr. Forex Must Eventually Answer

The laboratory should ultimately allow questions such as:

«Given capital X, strategy T, market M, timeframe W, risk configuration R and realistic execution conditions C, how did the strategy perform historically, how robust was that result, how did it perform on unseen data, and what evidence exists that its behaviour may persist under current conditions?»

The answer should contain evidence rather than a simple:

«“BUY.”»

The system should be able to answer:

- What happened?
- Why might it have happened?
- How strong is the evidence?
- Under what conditions did it happen?
- When did it fail?
- Could the result be overfit?
- Does it survive unseen data?
- Does it survive realistic costs?
- Does it survive different regimes?
- Does forward behaviour resemble historical expectations?
- What evidence contradicts the conclusion?
- What should be researched next?
- Is there sufficient evidence for controlled live exposure?

And sometimes the correct answer must be:

«NO TRADE.»

---

29. Research Laboratory Principle

Dr. Forex is not designed to manufacture confidence.

It is designed to earn confidence through evidence.

The ultimate research loop is therefore:

OBSERVE
   ↓
MEASURE
   ↓
QUESTION
   ↓
HYPOTHESIZE
   ↓
TEST
   ↓
TRY TO DISPROVE
   ↓
VALIDATE
   ↓
FORWARD TEST
   ↓
CONTROLLED EXPOSURE
   ↓
MONITOR
   ↓
LEARN
   ↓
RESEARCH AGAIN

The laboratory should continuously prefer:

evidence over intuition,

robustness over optimization,

generalization over memorization,

risk awareness over return chasing,

uncertainty over false certainty,

and

research authority over automatic trading authority.
