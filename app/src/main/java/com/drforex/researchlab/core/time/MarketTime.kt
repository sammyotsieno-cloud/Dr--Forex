package com.drforex.researchlab.core.time

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**

* Canonical representation of a point in market time.

* 

* DrForex treats time as first-class research data. Every observation,

* market event, news item, economic release, hypothesis, and research

* result must ultimately be traceable to a precise instant.

* 

* The canonical storage representation is UTC.
  */
  @JvmInline
  value class MarketTime(
  val instant: Instant
  ) {
  
  fun toUtc(): ZonedDateTime =
  instant.atZone(ZoneOffset.UTC)
  
  fun isBefore(other: MarketTime): Boolean =
  instant.isBefore(other.instant)
  
  fun isAfter(other: MarketTime): Boolean =
  instant.isAfter(other.instant)
  
  fun isAtOrBefore(other: MarketTime): Boolean =
  !instant.isAfter(other.instant)
  
  fun isAtOrAfter(other: MarketTime): Boolean =
  !instant.isBefore(other.instant)
  
  fun plusSeconds(seconds: Long): MarketTime =
  MarketTime(instant.plusSeconds(seconds))
  
  fun minusSeconds(seconds: Long): MarketTime =
  MarketTime(instant.minusSeconds(seconds))
  
  companion object {
  
   fun now(): MarketTime =
     MarketTime(Instant.now())

 fun fromEpochMillis(epochMillis: Long): MarketTime =
     MarketTime(Instant.ofEpochMilli(epochMillis))

 fun fromEpochSeconds(epochSeconds: Long): MarketTime =
     MarketTime(Instant.ofEpochSecond(epochSeconds))
  
  }
  }
