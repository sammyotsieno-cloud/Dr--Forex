package com.drforex.researchlab.core.indicator

import kotlin.math.sqrt

/**

* General-purpose statistical measurements used by the research

* and quantitative feature layers.

* 

* These functions are descriptive only. They do not make trading

* decisions.
  */
  object StatisticalMetrics {
  
  /**
  
  * Arithmetic mean.
    */
    fun mean(
    values: List<Double>
    ): Double? {
    
    val valid =
    finiteValues(values)
    
    if (valid.isEmpty()) {
    return null
    }
    
    return valid.average()
    }
  
  /**
  
  * Population variance.
    */
    fun variance(
    values: List<Double>
    ): Double? {
    
    val valid =
    finiteValues(values)
    
    if (valid.isEmpty()) {
    return null
    }
    
    val average =
    valid.average()
    
    return valid
    .map {
    val difference =
    it - average
    
         difference * difference
 }
 .average()
  
  }
  
  /**
  
  * Population standard deviation.
    */
    fun standardDeviation(
    values: List<Double>
    ): Double? =
    variance(values)
    ?.let { sqrt(it) }
  
  /**
  
  * Sample standard deviation.
  
  * 
  
  * Returns null when fewer than two valid observations exist.
    */
    fun sampleStandardDeviation(
    values: List<Double>
    ): Double? {
    
    val valid =
    finiteValues(values)
    
    if (valid.size < 2) {
    return null
    }
    
    val average =
    valid.average()
    
    val squaredDifferences =
    valid.sumOf {
    val difference =
    it - average
    
         difference * difference
 }
    
    return sqrt(
    squaredDifferences /
    (valid.size - 1)
    )
    }
  
  /**
  
  * Minimum value.
    */
    fun minimum(
    values: List<Double>
    ): Double? =
    finiteValues(values)
    .minOrNull()
  
  /**
  
  * Maximum value.
    */
    fun maximum(
    values: List<Double>
    ): Double? =
    finiteValues(values)
    .maxOrNull()
  
  /**
  
  * Range between minimum and maximum values.
    */
    fun range(
    values: List<Double>
    ): Double? {
    
    val min =
    minimum(values)
    ?: return null
    
    val max =
    maximum(values)
    ?: return null
    
    return max - min
    }
  
  /**
  
  * Median value.
    */
    fun median(
    values: List<Double>
    ): Double? {
    
    val sorted =
    finiteValues(values)
    .sorted()
    
    if (sorted.isEmpty()) {
    return null
    }
    
    val middle =
    sorted.size / 2
    
    return if (sorted.size % 2 == 0) {
    (
    sorted[middle - 1] +
    sorted[middle]
    ) / 2.0
    } else {
    sorted[middle]
    }
    }
  
  /**
  
  * Population covariance between two aligned series.
    */
    fun covariance(
    first: List<Double>,
    second: List<Double>
    ): Double? {
    
    val pairs =
    alignedFinitePairs(
    first,
    second
    )
    
    if (pairs.isEmpty()) {
    return null
    }
    
    val firstMean =
    pairs.map { it.first }.average()
    
    val secondMean =
    pairs.map { it.second }.average()
    
    return pairs
    .sumOf {
    (it.first - firstMean) *
    (it.second - secondMean)
    } / pairs.size
    }
  
  /**
  
  * Pearson correlation coefficient.
  
  * 
  
  * Returns a value between -1.0 and +1.0 when calculable.
    */
    fun correlation(
    first: List<Double>,
    second: List<Double>
    ): Double? {
    
    val pairs =
    alignedFinitePairs(
    first,
    second
    )
    
    if (pairs.size < 2) {
    return null
    }
    
    val firstMean =
    pairs.map { it.first }.average()
    
    val secondMean =
    pairs.map { it.second }.average()
    
    var numerator = 0.0
    var firstSquared = 0.0
    var secondSquared = 0.0
    
    for ((x, y) in pairs) {
    
     val xDifference =
     x - firstMean

 val yDifference =
     y - secondMean

 numerator +=
     xDifference * yDifference

 firstSquared +=
     xDifference * xDifference

 secondSquared +=
     yDifference * yDifference
    
    }
    
    val denominator =
    sqrt(
    firstSquared *
    secondSquared
    )
    
    if (denominator == 0.0) {
    return null
    }
    
    return numerator / denominator
    }
  
  /**
  
  * Population percentile using linear interpolation.
  
  * 
  
  * [percentile] must be between 0.0 and 1.0.
    */
    fun percentile(
    values: List<Double>,
    percentile: Double
    ): Double? {
    
    require(
    percentile in 0.0..1.0
    ) {
    "Percentile must be between 0.0 and 1.0."
    }
    
    val sorted =
    finiteValues(values)
    .sorted()
    
    if (sorted.isEmpty()) {
    return null
    }
    
    if (sorted.size == 1) {
    return sorted.first()
    }
    
    val position =
    percentile *
    (sorted.size - 1)
    
    val lowerIndex =
    position.toInt()
    
    val upperIndex =
    minOf(
    lowerIndex + 1,
    sorted.lastIndex
    )
    
    val fraction =
    position - lowerIndex
    
    val lower =
    sorted[lowerIndex]
    
    val upper =
    sorted[upperIndex]
    
    return lower +
    ((upper - lower) * fraction)
    }
  
  /**
  
  * Percentile rank of [value] relative to [values].
  
  * 
  
  * Returns a value between 0.0 and 1.0.
    */
    fun percentileRank(
    values: List<Double>,
    value: Double
    ): Double? {
    
    if (!value.isFinite()) {
    return null
    }
    
    val valid =
    finiteValues(values)
    
    if (valid.isEmpty()) {
    return null
    }
    
    val count =
    valid.count {
    it <= value
    }
    
    return count.toDouble() /
    valid.size.toDouble()
    }
  
  /**
  
  * Z-score of a value relative to a series.
    */
    fun zScore(
    values: List<Double>,
    value: Double
    ): Double? {
    
    if (!value.isFinite()) {
    return null
    }
    
    val average =
    mean(values)
    ?: return null
    
    val deviation =
    standardDeviation(values)
    ?: return null
    
    if (deviation == 0.0) {
    return null
    }
    
    return (
    value - average
    ) / deviation
    }
  
  private fun finiteValues(
  values: List<Double>
  ): List<Double> =
  values.filter {
  it.isFinite()
  }
  
  private fun alignedFinitePairs(
  first: List<Double>,
  second: List<Double>
  ): List<Pair<Double, Double>> {
  
   val size =
     minOf(
         first.size,
         second.size
     )

 val pairs =
     ArrayList<Pair<Double, Double>>()

 for (index in 0 until size) {

     val firstValue =
         first[index]

     val secondValue =
         second[index]

     if (
         firstValue.isFinite() &&
         secondValue.isFinite()
     ) {
         pairs +=
             firstValue to secondValue
     }
 }

 return pairs
  
  }
  }
