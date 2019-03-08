import org.scalatest.FlatSpec
import spatialbench.storm.{DistanceCalculatorWithGeoToolsBolt, DistanceCalculatorWithSpatial4jBolt}

class CalculateDistanceTest extends FlatSpec {
  "A distance calculator" should "calculate distance from Sears Tower" in {
    val df = new DistanceCalculatorWithGeoToolsBolt()
    val dist = df.calculateDistance(-87.641175, 41.916466)
    println(dist)

    val calc2 = new DistanceCalculatorWithSpatial4jBolt()
    val dist2 = calc2.calculateDistance(-87.641175, 41.916466)

    println(dist2)
  }
}
