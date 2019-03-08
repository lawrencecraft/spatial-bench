package spatialbench.storm

import org.geotools.referencing.GeodeticCalculator


class DistanceCalculatorWithGeoToolsBolt extends DistanceCalculatorBolt {
  override def calculateDistance(x: Double, y: Double): Double = {
    val dt = new GeodeticCalculator()
    dt.setStartingGeographicPoint(fixedLatLong._1, fixedLatLong._2)
    dt.setDestinationGeographicPoint(x, y)
    dt.getOrthodromicDistance
  }
}
