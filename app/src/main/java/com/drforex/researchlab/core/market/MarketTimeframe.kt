package com.drforex.researchlab.core.market

/**

* Canonical timeframe definitions used throughout DrForex.

* 

* Timeframes are represented explicitly rather than as arbitrary strings

* so that multi-timeframe analysis remains deterministic and consistent.
  */
  enum class MarketTimeframe(
  val durationSeconds: Long
  ) {
  
  M1(60),
  M5(5 * 60),
  M15(15 * 60),
  M30(30 * 60),
  
  H1(60 * 60),
  H4(4 * 60 * 60),
  
  D1(24 * 60 * 60),
  W1(7 * 24 * 60 * 60),
  
  MN1(30 * 24 * 60 * 60);
  
  fun isHigherThan(other: MarketTimeframe): Boolean =
  durationSeconds > other.durationSeconds
  
  fun isLowerThan(other: MarketTimeframe): Boolean =
  durationSeconds < other.durationSeconds
  }
