package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.time.MarketTime

/**

* Point-in-time snapshot of structural market information.

* 

* MarketContext deliberately contains observations from multiple

* structural capabilities rather than reducing the market to one

* indicator.

* 

* It is contextual evidence for research and strategy evaluation,

* not a trading signal.
  */
  data class MarketContext(
  val asOf: MarketTime,
  val structure: MarketStructure?,
  val recentBreaks: List<BreakOfStructure>,
  val recentChangeOfCharacters: List<ChangeOfCharacter>,
  val supportResistanceZones: List<SupportResistanceZone>,
  val liquidityEvents: List<LiquidityEvent>,
  val fairValueGaps: List<FairValueGap>,
  val displacements: List<Displacement>
  ) {
  
  /**
  
  * Returns only evidence that was already observable at [time].
  
  * 
  
  * Point-in-time projection must never expose structural or event
  
  * information that was confirmed after the requested time.
  
  * 
  
  * The context cannot manufacture evidence beyond the snapshot from
  
  * which it was created. Therefore [asOf] remains bounded by the
  
  * original context timestamp when [time] is later than this context.
    */
    fun knownAt(time: MarketTime): MarketContext =
    copy(
    asOf = if (asOf.isBefore(time)) asOf else time,
    
     structure = structure?.knownAt(time),

 recentBreaks =
     recentBreaks.filter {
         it.confirmationTime.isAtOrBefore(time)
     },

 recentChangeOfCharacters =
     recentChangeOfCharacters.filter {
         it.confirmationTime.isAtOrBefore(time)
     },

 supportResistanceZones =
     supportResistanceZones.filter {
         it.isKnownAt(time)
     },

 liquidityEvents =
     liquidityEvents.filter {
         it.isKnownAt(time)
     },

 fairValueGaps =
     fairValueGaps.filter {
         it.isKnownAt(time)
     },

 displacements =
     displacements.filter {
         it.isKnownAt(time)
     }
    
    )
  
  /**
  
  * Most recently confirmed structural break.
    */
    fun latestBreak(): BreakOfStructure? =
    recentBreaks.maxByOrNull {
    it.confirmationTime.instant
    }
  
  /**
  
  * Most recently confirmed CHoCH.
    */
    fun latestChangeOfCharacter(): ChangeOfCharacter? =
    recentChangeOfCharacters.maxByOrNull {
    it.confirmationTime.instant
    }
  
  /**
  
  * Most recent displacement event.
    */
    fun latestDisplacement(): Displacement? =
    displacements.maxByOrNull {
    it.confirmationTime.instant
    }
  
  /**
  
  * Most recent liquidity interaction.
    */
    fun latestLiquidityEvent(): LiquidityEvent? =
    liquidityEvents.maxByOrNull {
    it.confirmationTime.instant
    }
  
  /**
  
  * Returns support/resistance zones currently containing [price].
    */
    fun zonesContaining(price: Double): List<SupportResistanceZone> =
    supportResistanceZones.filter {
    it.contains(price)
    }
  
  /**
  
  * Returns FVGs currently containing [price].
    */
    fun gapsContaining(price: Double): List<FairValueGap> =
    fairValueGaps.filter {
    it.contains(price)
    }
  }
