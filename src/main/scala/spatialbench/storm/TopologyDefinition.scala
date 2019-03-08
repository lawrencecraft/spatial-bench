package spatialbench.storm

import java.util.Properties

import org.apache.storm.{Config, StormSubmitter}
import org.apache.storm.kafka.bolt.KafkaBolt
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper
import org.apache.storm.kafka.spout.{KafkaSpout, KafkaSpoutConfig}
import org.apache.storm.topology.TopologyBuilder

object TopologyDefinition extends App {
  val builder = new TopologyBuilder()
  val spoutConfig = KafkaSpoutConfig
    .builder("kafkab1:9092", "spatial-bench-2")
    .setFirstPollOffsetStrategy(KafkaSpoutConfig.FirstPollOffsetStrategy.LATEST)
    .build()
  val spout = new KafkaSpout(spoutConfig)


  val props = new Properties()
  props.put("bootstrap.servers", "kafkab1:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val writer = new KafkaBolt()
    .withProducerProperties(props)
    .withTopicSelector("spatial-bench-output-2")
    .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("key", "value"))

  builder
    .setSpout("kafka-spout", spout, 9)
  builder.setBolt("distance-calculated", new DistanceCalculatorWithGeoToolsBolt(), 3).shuffleGrouping("kafka-spout")
  builder.setBolt("kafka-writer", writer, 6).shuffleGrouping("distance-calculated")

  val stormConf = new Config()
  stormConf.setNumWorkers(6)

  StormSubmitter.submitTopology("kafka-benchmark", stormConf, builder.createTopology())
}
