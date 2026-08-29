package com.drforex.researchlab.core.structure

import com.drforex.researchlab.core.market.MarketSeries
import com.drforex.researchlab.core.time.MarketTime

/**

* Orchestrates the structural analysis capabilities into a single

* point-in-time MarketContext.

* 

* This class does not create trading signals. Its responsibility is

* to assemble independently derived market observations so that

* higher-level research components can reason over the complete

* structural state.
  */
  class MarketContextAnalyzer(
  private val swingPointDetector: SwingPointDetector = SwingPointDetector(),
  private val marketStructureAnalyzer: MarketStructureAnalyzer =
  MarketStructureAnalyzer(),
  private val breakOfStructureDetector: BreakOfStructureDetector =
  BreakOfStructureDetector(),
  private val changeOfCharacterDetector: ChangeOfCharacterDetector =
  ChangeOfCharacterDetector(),
  private val supportResistanceDetector: SupportResistanceDetector =
  SupportResistanceDetector(),
  private val liquidityDetector: LiquidityDetector =
  LiquidityDetector(),
  private val fairValueGapDetector: FairValueGapDetector =
  FairValueGapDetector(),
  private val displacementDetector: DisplacementDetector =
  DisplacementDetector()
  ) {
  
  /**
  
  * Produces a complete structural market snapshot using only
  
  * information available at [asOf].
    */
    fun analyze(
    series: MarketSeries,
    asOf: MarketTime
    ): MarketContext {
    
    val swings =
    swingPointDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val structure =
    marketStructureAnalyzer.analyze(
    series = series,
    asOf = asOf
    )
    
    val breaks =
    breakOfStructureDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val changes =
    changeOfCharacterDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val supportResistance =
    supportResistanceDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val liquidity =
    liquidityDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val fairValueGaps =
    fairValueGapDetector.detect(
    series = series,
    asOf = asOf
    )
    
    val displacements =
    displacementDetector.detect(
    series = series,
    asOf = asOf
    )
    
    return MarketContext(
    asOf = asOf,
    structure = structure,
    recentBreaks = breaks,
    recentChangeOfCharacters = changes,
    supportResistanceZones = supportResistance,
    liquidityEvents = liquidity,
    fairValueGaps = fairValueGaps,
    displacements = displacements
    ).knownAt(asOf)
    }
    }
