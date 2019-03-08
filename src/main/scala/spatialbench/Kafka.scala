package spatialbench

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.clients.consumer.ConsumerConfig

object Kafka {
  def properties(options: LoaderOptions): Properties = {

    val kafkaSettings = new Properties()
    kafkaSettings.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, options.kafkaBroker)
    kafkaSettings.put(ProducerConfig.CLIENT_ID_CONFIG, "spatial-bench")
    kafkaSettings.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    kafkaSettings.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    kafkaSettings
  }

  def consumerProperties(options: LoaderOptions): Properties = {

    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, options.kafkaBroker)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "spatial-bench")
    props
  }
}
