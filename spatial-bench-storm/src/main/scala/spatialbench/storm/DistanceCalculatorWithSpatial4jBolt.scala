package spatialbench.storm

import com.spatial4j.core.context.SpatialContext
import com.spatial4j.core.distance.DistanceUtils

class DistanceCalculatorWithSpatial4jBolt extends DistanceCalculatorBolt {
  override def calculateDistance(x: Double, y: Double): Double = {
    val context = SpatialContext.GEO
    val fixedPoint = context.makePoint(fixedLatLong._1, fixedLatLong._2)
    val degreeDistance = context.calcDistance(fixedPoint, x, y)
    DistanceUtils.DEG_TO_KM * 1000 * degreeDistance
  }
}
