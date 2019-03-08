package spatialbench.storm

import org.apache.storm.topology.base.BaseBasicBolt
import org.apache.storm.topology.{BasicOutputCollector, OutputFieldsDeclarer}
import org.apache.storm.tuple.{Fields, Tuple}

import scala.collection.JavaConverters._
import scala.util.Try

trait DistanceCalculatorBolt extends BaseBasicBolt {
  override def execute(input: Tuple, collector: BasicOutputCollector): Unit = {
    val stuff = input.getStringByField("value")
    val k = input.getStringByField("key")

    val csvRow = stuff.split(",")

    for {
      long <- Try(csvRow(7).toDouble)
      lat <- Try(csvRow(8).toDouble)
    } {
      val distanceMeters = calculateDistance(long, lat)

      collector.emit(List[AnyRef](distanceMeters.asInstanceOf[AnyRef], stuff, k).asJava)
    }
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer): Unit =
    declarer.declare(new Fields("dist", "value", "key"))

  val fixedLatLong: (Double, Double) = (-87.6359, 41.8789)

  def calculateDistance(x: Double, y: Double): Double
}
